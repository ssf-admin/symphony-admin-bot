package com.symphony.adminbot.bootstrap.service;

import com.symphony.adminbot.bootstrap.model.DeveloperBootstrapState;
import com.symphony.adminbot.bots.AdminBot;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.util.file.ExpiringFileLoaderCache;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperBootstrapInfo;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.clients.SymphonyClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.model.ApplicationDetail;
import com.symphony.api.pod.model.CompanyCertDetail;
import com.symphony.api.pod.model.Password;
import com.symphony.api.pod.model.UserAttributes;
import com.symphony.api.pod.model.UserCreate;
import com.symphony.api.pod.model.UserDetail;
import com.symphony.security.hash.ClientHash;
import com.symphony.security.hash.IClientHash;
import com.symphony.security.utils.CryptoGenerator;

import com.sun.jndi.toolkit.url.Uri;
import javafx.application.Application;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public class DeveloperBootstrapService {
  private static final Logger LOG = LoggerFactory.getLogger(AdminBot.class);

  private ExpiringFileLoaderCache<Developer, DeveloperBootstrapState> partnerStateCache;

  private DeveloperRegistrationService developerRegistrationService;
  private DeveloperMessageService developerMessageService;
  private DeveloperCertService developerCertService;
  private DeveloperEmailService developerEmailService;

  public DeveloperBootstrapService(SymphonyClient symClient){
    partnerStateCache = new ExpiringFileLoaderCache<>(
        System.getProperty(BotConfig.JSON_DIR),
        (developer) -> developer.getEmail(),
        BotConstants.EXPIRE_TIME_DAYS,
        TimeUnit.DAYS,
        DeveloperBootstrapState.class);

    developerEmailService = new DeveloperEmailService();
    developerRegistrationService = new DeveloperRegistrationService(symClient.getUsersClient(),
        symClient.getApplicationClient());
    developerMessageService = new DeveloperMessageService(symClient.getMessagesClient(),
        symClient.getStreamsClient());
    developerCertService = new DeveloperCertService(symClient.getSecurityClient(),
        symClient.getAttachmentsClient());
  }

  /**
   * Generate bot cert, register with pod, create bot user.
   * Generate app cert, register with pod, create app.
   * Update all team member states so they know certs were created.
   * Send symphony message containing the bootstrap package.
   * Package contains bot, app certs and info.
   * @param developer the bootstrap to base the bootstrap on
   * @return bootstrap info
   */
  public DeveloperBootstrapInfo bootstrapPartner(Developer developer) throws ApiException, ExecutionException {
    DeveloperBootstrapState developerState = partnerStateCache.get(developer);
    DeveloperSignUpForm signUpForm = developerState.getDeveloperSignUpForm();

    if(developerState.getBootstrapInfo() == null) {
      DeveloperBootstrapInfo developerBootstrapInfo = new DeveloperBootstrapInfo();

      //Register bot cert
      String botUsername = developerCertService.getDefaultBotUsername();
      CompanyCertDetail botCertDetail =
          developerCertService.generateAndRegisterCert(botUsername, "", developerState);
      botUsername = botCertDetail.getCompanyCertInfo().getCommonName();
      //Register bot
      developerBootstrapInfo.setBotUsername(botUsername);
      UserDetail botDetail = developerRegistrationService.registerBot(botUsername, signUpForm);
      //Save bot detail for all team members
      for(Developer teamMember: developerState.getTeamMembers()){
        DeveloperBootstrapState teamMemberState = partnerStateCache.get(teamMember);
        teamMemberState.setBotDetail(botDetail);
      }
      developerState.setBotDetail(botDetail);

      if (StringUtils.isNotBlank(signUpForm.getAppName())) {
        //Register app cert
        CompanyCertDetail appCertDetail = developerCertService.generateAndRegisterCert(
            signUpForm.getAppName(), "", developerState);

        //Register app
        String appId = appCertDetail.getCompanyCertInfo().getCommonName();
        developerBootstrapInfo.setAppId(appId);
        ApplicationDetail applicationDetail = developerRegistrationService.registerApp(appId, developerState);
        //Save app detail for all team members
        for(Developer teamMember: developerState.getTeamMembers()){
          DeveloperBootstrapState teamMemberState = partnerStateCache.get(teamMember);
          teamMemberState.setApplicationDetail(applicationDetail);
        }
        developerState.setApplicationDetail(applicationDetail);
      }

      developerBootstrapInfo.setAppName(signUpForm.getAppName());
      developerBootstrapInfo.setBotEmail(signUpForm.getBotEmail());
      //Set bootstrap info for team members (So they know app and bot were already created)
      for(Developer teamMember: developerState.getTeamMembers()){
        DeveloperBootstrapState teamMemberState = partnerStateCache.get(teamMember);
        teamMemberState.setBootstrapInfo(developerBootstrapInfo);
      }
      developerState.setBootstrapInfo(developerBootstrapInfo);
    }

    developerRegistrationService.installApp(developerState);
    developerCertService.uploadCerts(developerState);
    developerMessageService.sendBootstrapMessage(developerState);

    return developerState.getBootstrapInfo();
  }

  /**
   * Validates that all the fields in the sign up form are valid
   * Creates partner symphony user with random temp password.
   * Sends welcome email and message in symphony to partner.
   * @param signUpForm the partner sign up form
   */
  public void welcomeDeveloper(DeveloperSignUpForm signUpForm) throws ApiException {
    validateSignUpForm(signUpForm);
    Set<DeveloperBootstrapState> bootstrapStates = getInitialBootstrapStates(signUpForm);
    for(DeveloperBootstrapState developerState : bootstrapStates){
      partnerStateCache.put(developerState.getDeveloper(), developerState);

      developerRegistrationService.registerDeveloperUser(developerState);
      developerEmailService.sendWelcomeEmail(developerState);
      developerMessageService.sendDirectionalMessage(developerState);
    }
  }

  private void validateSignUpForm(DeveloperSignUpForm signUpForm) throws ApiException {
    if(!developerRegistrationService.allDevelopersDoNotExist(signUpForm)){
      throw new BadRequestException(BotConstants.USER_EXISTS);
    }
    if(developerRegistrationService.botAndAppDoNotExist(signUpForm)){
      throw new BadRequestException(BotConstants.BOT_EXISTS);
    }
    if (StringUtils.isBlank(signUpForm.getCreator().getFirstName()) ||
        StringUtils.isBlank(signUpForm.getCreator().getLastName()) ||
        StringUtils.isBlank(signUpForm.getCreator().getEmail())) {
      throw new BadRequestException(BotConstants.DEVELOPER_REQUIRED);
    }
    if (StringUtils.isBlank(signUpForm.getAppName())) {
      throw new BadRequestException(BotConstants.APP_NAME_REQUIRED);
    }
    if (StringUtils.isBlank(signUpForm.getAppDescription())) {
      throw new BadRequestException(BotConstants.APP_DESCRIPTION_REQUIRED);
    }
    if (StringUtils.isBlank(signUpForm.getAppDomain())) {
      throw new BadRequestException(BotConstants.APP_DOMAIN_REQUIRED);
    }
    if (StringUtils.isBlank(signUpForm.getAppCompanyName())) {
      throw new BadRequestException(BotConstants.APP_COMPANY_REQUIRED);
    }
    if (StringUtils.isBlank(signUpForm.getAppIconUrl())) {
      throw new BadRequestException(BotConstants.APP_ICON_REQUIRED);
    }
    if(!signUpForm.getAppIconUrl().startsWith("https://")){
      throw new BadRequestException(BotConstants.ICON_START_WITH_HTTPS);
    }
    if (StringUtils.isBlank(signUpForm.getAppUrl())) {
      throw new BadRequestException(BotConstants.APP_URL_REQUIRED);
    }
    if(!signUpForm.getAppUrl().startsWith("https://")){
      throw new BadRequestException(BotConstants.APP_START_WITH_HTTPS);
    }
    if (StringUtils.isBlank(signUpForm.getBotEmail())) {
      throw new BadRequestException(BotConstants.BOT_EMAIL_REQUIRED);
    }
    if (StringUtils.isBlank(signUpForm.getBotName())) {
      throw new BadRequestException(BotConstants.BOT_NAME_REQUIRED);
    }

    try {
      validateDomain(signUpForm.getAppUrl(), signUpForm.getAppDomain());
    } catch (MalformedURLException e) {
      throw new BadRequestException(BotConstants.BAD_APP_URL);
    }
  }

  /**
   * Creates initial states for developer within sign up form. (Team members and creator)
   * @param signUpForm the sign up form
   * @return the initial partner states
   */
  private Set<DeveloperBootstrapState> getInitialBootstrapStates(DeveloperSignUpForm signUpForm){
    Set<Developer> developerSet = new HashSet<>();
    developerSet.add(signUpForm.getCreator());
    developerSet.addAll(signUpForm.getTeam());

    Set<DeveloperBootstrapState> developerStates = new HashSet<>();
    for(Developer developer : developerSet) {
      UserCreate userCreate = new UserCreate();
      UserAttributes userAttributes = new UserAttributes();
      userAttributes.setAccountType(UserAttributes.AccountTypeEnum.NORMAL);
      userAttributes.setEmailAddress(developer.getEmail());
      userAttributes.setFirstName(developer.getFirstName());
      userAttributes.setLastName(developer.getLastName());
      userAttributes.setUserName(developer.getFirstName().toLowerCase()
          + WordUtils.capitalize(developer.getLastName()));
      userAttributes.displayName(developer.getFirstName() + " " + developer.getLastName());
      userAttributes.setDepartment(signUpForm.getAppCompanyName());
      userCreate.setUserAttributes(userAttributes);

      String randomPassword = UUID.randomUUID().toString().replace("-", "");
      int randomBegin = (int)(Math.random() * (randomPassword.length() - 3));
      int randomEnd = ThreadLocalRandom.current().nextInt(randomBegin, randomPassword.length());
      randomPassword = randomPassword.replace(randomPassword.substring(randomBegin, randomEnd),
          randomPassword.substring(randomBegin, randomEnd).toUpperCase());

      IClientHash clientHash = new ClientHash();
      String salt = CryptoGenerator.generateBase64Salt();
      String clientHashedPassword = clientHash.getClientHashedPassword(randomPassword, salt);

      Password pass = new Password();
      pass.setHPassword(clientHashedPassword);
      pass.setHSalt(salt);
      pass.setKhPassword(clientHashedPassword);
      pass.setKhSalt(salt);
      userCreate.setPassword(pass);

      List<String> roles = new ArrayList<>();
      roles.add("INDIVIDUAL");
      userCreate.setRoles(roles);

      DeveloperBootstrapState developerState = new DeveloperBootstrapState();
      developerState.setDeveloper(developer);
      developerState.setDeveloperSignUpForm(signUpForm);
      developerState.setUserCreate(userCreate);
      developerState.setPassword(randomPassword);

      Set<Developer> teamMembers = new HashSet<>(developerSet);
      teamMembers.remove(developer);
      developerState.setTeamMembers(teamMembers);

      developerState.setCompanyCertMap(new HashMap<>());

      developerStates.add(developerState);
    }

    return developerStates;
  }

  private void validateDomain(String url, String domain) throws MalformedURLException {
    Uri uri = new Uri(url);
    String appDomain = uri.getHost();
    appDomain = appDomain.startsWith("www.") ? appDomain.substring(4) : appDomain;
    String[] splitAppDomains = appDomain.split("\\.");
    String[] splitSubDomain = domain.split("\\.");
    for (int appIndex = 0; appIndex + splitSubDomain.length <= splitAppDomains.length;
        appIndex++) {
      for (int subIndex = 0; subIndex < splitAppDomains.length; subIndex++) {
        if (!splitAppDomains[appIndex + subIndex].equals(splitSubDomain[subIndex])) {
          break;
        }
        if (subIndex + 1 == splitSubDomain.length) {
          return;
        }
      }
    }
    throw new BadRequestException(BotConstants.DOMAIN_MUST_MATCH);
  }

}
