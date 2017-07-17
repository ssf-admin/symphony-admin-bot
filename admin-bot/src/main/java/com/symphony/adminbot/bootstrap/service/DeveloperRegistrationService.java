package com.symphony.adminbot.bootstrap.service;

import com.symphony.adminbot.bootstrap.model.DeveloperBootstrapState;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.clients.ApplicationClient;
import com.symphony.api.clients.UsersClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.model.ApplicationDetail;
import com.symphony.api.pod.model.ApplicationInfo;
import com.symphony.api.pod.model.Feature;
import com.symphony.api.pod.model.FeatureList;
import com.symphony.api.pod.model.UserAppEntitlement;
import com.symphony.api.pod.model.UserAppEntitlementList;
import com.symphony.api.pod.model.UserAttributes;
import com.symphony.api.pod.model.UserCreate;
import com.symphony.api.pod.model.UserDetail;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/2/17.
 */
public class DeveloperRegistrationService {
  private static final Logger LOG = LoggerFactory.getLogger(DeveloperRegistrationService.class);
  enum FeaturesEnum{
    EXTERNAL("isExternalIMEnabled"),
    SHARE_FILES_EXTERNAL("canShareFilesExternally"),
    CREATE_PUBLIC_ROOM("canCreatePublicRoom"),
    SEND_FILES("sendFilesEnabled");

    private String entitlement;

    FeaturesEnum(String entitlement){
      this.entitlement = entitlement;
    }

    public Feature enabled(){
      Feature feature = new Feature();
      feature.setEnabled(true);
      feature.setEntitlment(entitlement);
      return feature;
    }

    public Feature disabled(){
      Feature feature = new Feature();
      feature.setEnabled(false);
      feature.setEntitlment(entitlement);
      return feature;
    }
  }

  private UsersClient usersClient;
  private ApplicationClient applicationClient;

  public DeveloperRegistrationService(UsersClient usersClient, ApplicationClient applicationClient){
    this.usersClient = usersClient;
    this.applicationClient = applicationClient;
  }

  /**
   * Creates a symphony user for the developer.
   * @param state the developers's current state in the bootstrap process
   */
  public void registerDeveloperUser(DeveloperBootstrapState state) throws ApiException {
    UserDetail userDetail = usersClient.createUser(state.getUserCreate());
    state.setUserDetail(userDetail);
    LOG.info("Registered new user " + userDetail.getUserAttributes().getUserName() + " with pod.");

    FeatureList features = new FeatureList();

    features.add(FeaturesEnum.CREATE_PUBLIC_ROOM.enabled());
    features.add(FeaturesEnum.EXTERNAL.enabled());
    features.add(FeaturesEnum.SHARE_FILES_EXTERNAL.enabled());
    features.add(FeaturesEnum.SEND_FILES.enabled());

    usersClient.updateEntitlements(userDetail.getUserSystemInfo().getId(), features);
    LOG.info("Updated " + userDetail.getUserAttributes().getUserName() + " entitlements on pod.");
  }

  /**
   * Registers a symphony service bot
   * @param signUpForm the sign up form to base the bot on
   */
  public UserDetail registerBot(String botUsername, DeveloperSignUpForm signUpForm) throws ApiException {
    UserCreate userCreate = new UserCreate();
    UserAttributes userAttributes = new UserAttributes();
    userAttributes.setAccountType(UserAttributes.AccountTypeEnum.SYSTEM);
    userAttributes.setUserName(botUsername);
    userAttributes.displayName(signUpForm.getBotName());
    userAttributes.setEmailAddress(signUpForm.getBotEmail());
    userAttributes.setDepartment(signUpForm.getAppCompanyName());
    userCreate.setUserAttributes(userAttributes);

    List<String> roles = new ArrayList<>();
    roles.add("INDIVIDUAL");
    userCreate.setRoles(roles);

    UserDetail userDetail = usersClient.createUser(userCreate);
    LOG.info("Registered new bot service user " + userDetail.getUserAttributes().getUserName() + " with pod.");

    FeatureList features = new FeatureList();

    features.add(FeaturesEnum.CREATE_PUBLIC_ROOM.enabled());
    features.add(FeaturesEnum.EXTERNAL.enabled());
    features.add(FeaturesEnum.SHARE_FILES_EXTERNAL.enabled());
    features.add(FeaturesEnum.SEND_FILES.enabled());

    usersClient.updateEntitlements(userDetail.getUserSystemInfo().getId(), features);
    LOG.info("Updated service user " + userDetail.getUserAttributes().getUserName() + " entitlements on pod.");
    return userDetail;
  }

  /**
   * Registers a app with pod.
   * @param appId the app id
   * @param bootstrapState the developers's current state in the bootstrap process
   */
  public ApplicationDetail registerApp(String appId, DeveloperBootstrapState bootstrapState)
      throws ApiException {
    DeveloperSignUpForm signUpForm = bootstrapState.getDeveloperSignUpForm();

    ApplicationDetail applicationDetail = new ApplicationDetail();

    ApplicationInfo applicationInfo = new ApplicationInfo();
    applicationInfo.setPublisher(signUpForm.getCreator().getFirstName().toLowerCase()
        + WordUtils.capitalize(signUpForm.getCreator().getLastName()));
    applicationInfo.setAppId(appId);
    applicationInfo.setAppUrl(signUpForm.getAppUrl());
    applicationInfo.setDescription(signUpForm.getAppDescription());
    applicationInfo.setDomain(signUpForm.getAppDomain());
    applicationInfo.setName(signUpForm.getAppName());

    if(StringUtils.isNotBlank(signUpForm.getAppIconUrl())) {
      applicationInfo.setIconUrl(signUpForm.getAppIconUrl());
    } else {
      applicationInfo.setIconUrl(System.getProperty(BotConfig.BOOTSTRAP_ICON_URL));
    }

    applicationDetail.setCert(bootstrapState.getCompanyCertMap().get(appId).getPem());

    applicationDetail.setApplicationInfo(applicationInfo);

    applicationDetail = applicationClient.createApplication(applicationDetail);
    LOG.info("Registered new app " + applicationDetail.getApplicationInfo().getAppId() + " with pod.");
    return applicationDetail;
  }

  /**
   * Installs an app for a developer on pod.
   * @param bootstrapState the developers's current state in the bootstrap process
   */
  public void installApp(DeveloperBootstrapState bootstrapState) throws ApiException {
    Long userId = bootstrapState.getUserDetail().getUserSystemInfo().getId();
    UserAppEntitlementList userAppEntitlements = new UserAppEntitlementList();

    UserAppEntitlement userAppEntitlement = new UserAppEntitlement();
    userAppEntitlement.appId(bootstrapState.getApplicationDetail().getId());
    userAppEntitlement.appName(bootstrapState.getDeveloperSignUpForm().getAppName());
    userAppEntitlement.setListed(false);
    userAppEntitlement.setInstall(true);

    userAppEntitlements.add(userAppEntitlement);
    usersClient.updateUserApps(userId, userAppEntitlements);
    LOG.info("Installed app " + bootstrapState.getDeveloperSignUpForm().getAppName() + " for user "
        + bootstrapState.getUserDetail().getUserAttributes().getUserName() + ".");
  }

  /**
   * Checks if partners already exist as symphony users.
   * @param signUpForm the sign up form containing the partners
   * @return if all partners do not exist as symphony users
   */
  public boolean allDevelopersDoNotExist(DeveloperSignUpForm signUpForm) throws ApiException {
    Set<String> allEmails = new HashSet<>();
    String creatorEmail = signUpForm.getCreator().getEmail();
    for(Developer developer : signUpForm.getTeam()) {
      allEmails.add(developer.getEmail());
    }
    allEmails.add(creatorEmail);

    for (String email : allEmails) {
      if (usersClient.userExistsByEmail(email)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if bot or app already exists
   * @param developerSignUpForm the sign up form to base the bot and app info on
   * @return if the bot and app does not exist
   */
  public boolean botAndAppDoNotExist(DeveloperSignUpForm developerSignUpForm) throws ApiException {
    return usersClient.userExistsByEmail(developerSignUpForm.getBotEmail());
  }

}
