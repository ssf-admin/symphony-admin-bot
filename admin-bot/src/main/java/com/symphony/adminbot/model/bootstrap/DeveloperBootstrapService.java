package com.symphony.adminbot.model.bootstrap;

import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.model.bootstrap.setup.DeveloperCompanyCertSetup;
import com.symphony.adminbot.model.bootstrap.setup.DeveloperMessageSetup;
import com.symphony.adminbot.model.bootstrap.setup.DeveloperUserSetup;
import com.symphony.adminbot.util.file.ExpiringFileLoaderCache;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperBootstrapInfo;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.clients.SymphonyClient;
import com.symphony.adminbot.bots.AdminBot;

import com.symphony.adminbot.model.bootstrap.setup.DeveloperConfirmationSetup;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.pod.invoker.ApiException;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public class DeveloperBootstrapService {
  private static final Logger LOG = LoggerFactory.getLogger(AdminBot.class);

  private ExpiringFileLoaderCache<Developer, DeveloperState> partnerStateCache;

  private DeveloperUserSetup developerUserSetup;
  private DeveloperMessageSetup developerMessageSetup;
  private DeveloperCompanyCertSetup developerCompanyCertSetup;
  private DeveloperConfirmationSetup developerConfirmationSetup;

  public DeveloperBootstrapService(SymphonyClient symClient){
    partnerStateCache = new ExpiringFileLoaderCache<>(
        System.getProperty(BotConfig.JSON_DIR),
        (partner) -> partner.getEmail(),
        BotConstants.EXPIRE_TIME_DAYS,
        TimeUnit.DAYS,
        DeveloperState.class);

    developerConfirmationSetup = new DeveloperConfirmationSetup();
    developerUserSetup = new DeveloperUserSetup(symClient.getUsersClient());
    developerMessageSetup = new DeveloperMessageSetup(symClient.getMessagesClient(),
        symClient.getStreamsClient());
    developerCompanyCertSetup = new DeveloperCompanyCertSetup(symClient.getSecurityClient(),
        symClient.getAttachmentsClient());

  }

  /**
   * Generate bot cert, register with pod, create bot user.
   * Generate app cert, register with pod, create app.
   * Update all team member states so they know certs were created.
   * @param developer the developer to base the bootstrap on
   */
  public void bootstrapPartner(Developer developer) throws ApiException, ExecutionException {
    DeveloperState developerState = partnerStateCache.get(developer);
    DeveloperSignUpForm signUpForm = developerState.getDeveloperSignUpForm();

    if(developerState.getCertAttachmentInfo() == null) {
      String password = developerState.getPassword();

      String botUsername = developerCompanyCertSetup.getDefaultBotUsername();
      botUsername = developerCompanyCertSetup.generateAndRegisterCert(botUsername, password);

      String appUsername = null;
      if(StringUtils.isNotBlank(signUpForm.getAppName())) {
        appUsername = developerCompanyCertSetup.generateAndRegisterCert(signUpForm.getAppName(), password);
      }

      developerUserSetup.createBot(signUpForm);

      DeveloperBootstrapInfo DeveloperBootstrapInfo = new DeveloperBootstrapInfo();
      DeveloperBootstrapInfo.setBotUsername(botUsername);
      DeveloperBootstrapInfo.setAppId(appUsername);
      DeveloperBootstrapInfo.setAppName(signUpForm.getAppName());
      DeveloperBootstrapInfo.setBotEmail(signUpForm.getBotEmail());
      for(Developer teamMember: developerState.getTeamMembers()){
        DeveloperState teamMemberState = partnerStateCache.get(teamMember);
        teamMemberState.setCertAttachmentInfo(developerState.getCertAttachmentInfo());
        teamMemberState.setBootstrapInfo(DeveloperBootstrapInfo);
      }
    }
  }

  /**
   * Send symphony message containing the bootstrap package.
   * Package contains bot, app certs and info.
   * @param developer the developer to send bootstrap package
   * @return the bootstrap info
   */
  public DeveloperBootstrapInfo sendBootstrapPackage(Developer developer) throws ExecutionException {
    DeveloperState developerState = partnerStateCache.get(developer);
    developerCompanyCertSetup.uploadCerts(developerState);
    developerMessageSetup.sendBootstrapMessage(developerState);

    return developerState.getBootstrapInfo();
  }

  /**
   * Creates partner symphony user with random temp password.
   * Sends welcome email and message in symphony to partner.
   * @param signUpForm the partner sign up form
   */
  public void welcomePartner(DeveloperSignUpForm signUpForm) throws ApiException {
    Set<DeveloperState> welcomeStates = developerUserSetup.getPartnerSetupStates(signUpForm);
    for(DeveloperState developerState : welcomeStates){
      partnerStateCache.put(developerState.getDeveloper(), developerState);

      developerUserSetup.createPartnerUser(developerState);
      developerConfirmationSetup.sendWelcomeEmail(developerState);
      developerMessageSetup.sendDirectionalMessage(developerState);
    }
  }

  /**
   * Validates that all the fields in the sign up form are valid
   * @param signUpForm the sign up form
   */
  public void validateSignUpForm(DeveloperSignUpForm signUpForm) throws ApiException {
    if(!developerUserSetup.allPartnersDoNotExist(signUpForm)){
      throw new BadRequestException(BotConstants.USER_EXISTS);
    }

    if(developerUserSetup.botAndAppDoNotExist(signUpForm)){
      throw new BadRequestException(BotConstants.BOT_EXISTS);
    }
  }

}
