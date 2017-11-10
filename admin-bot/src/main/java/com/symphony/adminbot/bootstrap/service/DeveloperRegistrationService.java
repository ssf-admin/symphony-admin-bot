/*
 * Copyright 2017 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.symphony.adminbot.bootstrap.service;

import com.symphony.adminbot.bootstrap.model.DeveloperBootstrapState;
import com.symphony.adminbot.bootstrap.model.DeveloperClientHash;
import com.symphony.adminbot.bootstrap.model.template.BootstrapTemplateData;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.util.file.FileUtil;
import com.symphony.adminbot.util.template.MessageTemplate;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.clients.ApplicationClient;
import com.symphony.api.clients.UsersClient;
import com.symphony.api.clients.model.SymphonyUser;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.model.ApplicationDetail;
import com.symphony.api.pod.model.ApplicationInfo;
import com.symphony.api.pod.model.Feature;
import com.symphony.api.pod.model.FeatureList;
import com.symphony.api.pod.model.Password;
import com.symphony.api.pod.model.PodAppEntitlement;
import com.symphony.api.pod.model.PodAppEntitlementList;
import com.symphony.api.pod.model.UserAppEntitlement;
import com.symphony.api.pod.model.UserAppEntitlementList;
import com.symphony.api.pod.model.UserAttributes;
import com.symphony.api.pod.model.UserCreate;
import com.symphony.api.pod.model.UserDetail;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;

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

  private int botId = -1;

  public DeveloperRegistrationService(UsersClient usersClient, ApplicationClient applicationClient){
    this.usersClient = usersClient;
    this.applicationClient = applicationClient;
  }

  /**
   * Creates a symphony user for the developer.
   * @param bootstrapState the developers's current state in the bootstrap process
   */
  public void registerDeveloperUser(DeveloperBootstrapState bootstrapState, String developerPassword) throws ApiException {
    UserCreate userCreate = new UserCreate();
    UserAttributes userAttributes = new UserAttributes();
    userAttributes.setAccountType(UserAttributes.AccountTypeEnum.NORMAL);
    userAttributes.setEmailAddress(bootstrapState.getDeveloper().getEmail());
    userAttributes.setFirstName(bootstrapState.getDeveloper().getFirstName());
    userAttributes.setLastName(bootstrapState.getDeveloper().getLastName());
    userAttributes.setUserName((bootstrapState.getDeveloper().getFirstName().toLowerCase() +
      WordUtils.capitalize(bootstrapState.getDeveloper().getLastName())).replace(" ", ""));
    userAttributes.displayName(bootstrapState.getDeveloper().getFirstName() +
        " " + bootstrapState.getDeveloper().getLastName());
    userAttributes.setDepartment(bootstrapState.getDeveloperSignUpForm().getAppCompanyName());
    userCreate.setUserAttributes(userAttributes);

    DeveloperClientHash clientHash = new DeveloperClientHash();
    final SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    String saltString = Base64.encodeBase64String(salt);
    String clientHashedPassword = clientHash.getClientHashedPassword(developerPassword, saltString);

    Password pass = new Password();
    pass.setHPassword(clientHashedPassword);
    pass.setHSalt(saltString);
    pass.setKhPassword(clientHashedPassword);
    pass.setKhSalt(saltString);
    userCreate.setPassword(pass);

    List<String> roles = new ArrayList<>();
    roles.add("INDIVIDUAL");
    userCreate.setRoles(roles);

    UserDetail userDetail = null;
    int usernameIndex = 1;
    String baseUsername = userCreate.getUserAttributes().getUserName();
    boolean usernameExists = true;
    while (usernameExists) {
      try {
        userDetail = usersClient.createUser(userCreate);
        usernameExists = false;
      } catch (ApiException e) {
        if (e.getMessage().equals(BotConstants.USERS_EXIST)) {
          baseUsername += usernameIndex;
          while (usersClient.userExistsByUsername(baseUsername)) {
            baseUsername = baseUsername.replace("" + usernameIndex, "" + (usernameIndex + 1));
            usernameIndex++;
          }
          userCreate.getUserAttributes().setUserName(baseUsername);
        } else {
          LOG.error("Create developer failed: ", e);
          throw new InternalServerErrorException("Create developer failed: " + e);
        }
      }
    }

    bootstrapState.setUserDetail(userDetail);
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
   * @param bootstrapState the developers's current state in the bootstrap process
   */
  public UserDetail registerBot(DeveloperBootstrapState bootstrapState) throws ApiException {
    UserCreate userCreate = new UserCreate();
    UserAttributes userAttributes = new UserAttributes();
    userAttributes.setAccountType(UserAttributes.AccountTypeEnum.SYSTEM);
    userAttributes.setUserName(bootstrapState.getBootstrapInfo().getBotUsername());
    userAttributes.displayName(bootstrapState.getDeveloperSignUpForm().getBotName());
    userAttributes.setEmailAddress(bootstrapState.getDeveloperSignUpForm().getBotEmail());
    userAttributes.setDepartment(bootstrapState.getDeveloperSignUpForm().getAppCompanyName());
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
   * @param bootstrapState the developers's current state in the bootstrap process
   */
  public ApplicationDetail registerApp(DeveloperBootstrapState bootstrapState)
      throws ApiException {
    DeveloperSignUpForm signUpForm = bootstrapState.getDeveloperSignUpForm();

    ApplicationDetail applicationDetail = new ApplicationDetail();
    applicationDetail.setDescription(signUpForm.getAppDescription());

    ApplicationInfo applicationInfo = new ApplicationInfo();
    applicationInfo.setPublisher(signUpForm.getCreator().getFirstName().toLowerCase()
        + WordUtils.capitalize(signUpForm.getCreator().getLastName()));
    applicationInfo.setAppId(bootstrapState.getDeveloperSignUpForm().getAppId());
    applicationInfo.setAppUrl(signUpForm.getAppUrl());
    applicationInfo.setDomain(signUpForm.getAppDomain());
    applicationInfo.setName(signUpForm.getAppName());

    if(StringUtils.isNotBlank(signUpForm.getAppIconUrl())) {
      applicationDetail.setIconUrl(signUpForm.getAppIconUrl());
    } else {
      String iconUrl = "";
      try {
        iconUrl = FileUtil.readFile(System.getProperty(BotConfig.BOOTSTRAP_ICON_URL_TEMPLATE));
      } catch(IOException e) {
        LOG.error("Read from icon url template file failed: ", e);
      }
      BootstrapTemplateData templateData = new BootstrapTemplateData(bootstrapState);
      MessageTemplate iconTemplate = new MessageTemplate(iconUrl);
      applicationDetail.setIconUrl(iconTemplate.buildFromData(templateData).replace(" ", ""));
      LOG.info("Using icon url: " + applicationDetail.getIconUrl());
    }

    applicationDetail.setCert(bootstrapState.getCompanyCertMap()
        .get(bootstrapState.getDeveloperSignUpForm().getAppId())
        .getPem());

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
    userAppEntitlement.appId(bootstrapState.getBootstrapInfo().getAppId());
    userAppEntitlement.appName(bootstrapState.getDeveloperSignUpForm().getAppName());
    userAppEntitlement.setListed(true);
    userAppEntitlement.setInstall(true);

    userAppEntitlements.add(userAppEntitlement);
    usersClient.updateUserApps(userId, userAppEntitlements);
    LOG.info("Installed app " + bootstrapState.getDeveloperSignUpForm().getAppName() + " for user "
        + bootstrapState.getUserDetail().getUserAttributes().getUserName() + ".");
  }

  /**
   * Gets a default bot username
   * @return a default bot username
   */
  public String getDefaultBotUsername() {
    try {
      String path = System.getProperty(BotConfig.BOOTSTRAP_BOT_ID);
      if (botId == -1) {
        String text = FileUtil.readFile(path).replace("\n", "");
        botId = Integer.parseInt(text);
      }
      botId += 1;
      FileUtil.writeFile("" + botId, path);
    } catch (IOException e) {
      LOG.error("Failed to retrieve bot sign up id: ", e);
    }

    return BotConstants.BOT_USERNAME + botId;
  }

  /**
   * Gets a developer's username.
   */
  public String getUsername(String email) throws ApiException {
    return usersClient.userSearchByEmail(email).getUsername();
  }

  public void setDeveloperUserDetail(DeveloperBootstrapState developerBootstrapState) throws ApiException {
    SymphonyUser symphonyUser = usersClient.userSearchByEmail(developerBootstrapState.getDeveloper().getEmail());
    developerBootstrapState.setUserDetail(usersClient.getUserDetail(symphonyUser.getId()));
  }

  /**
   * Checks if partners already exist as symphony users.
   * @param signUpForm the sign up form containing the partners
   * @return if all partners do not exist as symphony users
   */
  public boolean oneDeveloperExists(DeveloperSignUpForm signUpForm) throws ApiException {
    Set<String> allEmails = new HashSet<>();
    String creatorEmail = signUpForm.getCreator().getEmail();
    for(Developer developer : signUpForm.getTeam()) {
      allEmails.add(developer.getEmail());
    }
    allEmails.add(creatorEmail);

    for (String email : allEmails) {
      if (usersClient.userExistsByEmail(email)) {
        return true;
      }
    }
    return false;
  }

  public boolean developerExists(Developer developer) throws ApiException {
    if (usersClient.userExistsByEmail(developer.getEmail())) {
      return true;
    }
    return false;
  }

  /**
   * Checks if bot or app already exists
   * @param developerSignUpForm the sign up form to base the bot and app info on
   * @return if the bot and app does not exist
   */
  public boolean botOrAppExist(DeveloperSignUpForm developerSignUpForm) throws ApiException {
    if(StringUtils.isNotBlank(developerSignUpForm.getAppId())) {
      PodAppEntitlementList podAppEntitlements = usersClient.listPodApps();
      for (PodAppEntitlement appEntitlement : podAppEntitlements) {
        if (appEntitlement.getAppId().equals(developerSignUpForm.getAppId())) {
          return true;
        }
      }
    }
    return usersClient.userExistsByEmail(developerSignUpForm.getBotEmail());
  }

}
