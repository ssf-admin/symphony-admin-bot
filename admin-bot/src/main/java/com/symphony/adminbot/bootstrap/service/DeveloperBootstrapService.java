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
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.UserDetail;
import com.symphony.api.pod.model.UserIdList;
import com.symphony.api.pod.model.V2RoomDetail;

import com.sun.jndi.toolkit.url.Uri;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public class DeveloperBootstrapService {
  private static final Logger LOG = LoggerFactory.getLogger(AdminBot.class);

  private ExpiringFileLoaderCache<Developer, DeveloperBootstrapState> developerStateCache;
  private Set<String> reservedContent = new HashSet<>();

  private DeveloperRegistrationService developerRegistrationService;
  private DeveloperMessageService developerMessageService;
  private DeveloperCertService developerCertService;
  private DeveloperEmailService developerEmailService;

  public DeveloperBootstrapService(SymphonyClient symClient){
    developerStateCache = new ExpiringFileLoaderCache<>(
        System.getProperty(BotConfig.DEVELOPER_JSON_DIR),
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
  public DeveloperBootstrapInfo bootstrapDeveloper(Developer developer) throws ApiException {
    DeveloperBootstrapState developerState = getDeveloperState(developer);
    bootstrap(developerState);

    if(StringUtils.isNotBlank(developerState.getDeveloperSignUpForm().getAppId())) {
      developerRegistrationService.installApp(developerState);
    }

    developerCertService.uploadCerts(developerState);
    developerMessageService.sendBootstrapMessage(developerState);

    LOG.info("Bootstraped user " + developerState.getUserDetail().getUserAttributes().getUserName() + ".");

    return developerState.getBootstrapInfo();
  }

  public DeveloperBootstrapInfo bootstrapDevelopers(DeveloperSignUpForm signUpForm) throws ApiException {
    DeveloperBootstrapState developerState = getDeveloperState(signUpForm.getCreator());
    bootstrap(developerState);

    UserIdList userIdList = new UserIdList();
    userIdList.add(developerState.getUserDetail().getUserSystemInfo().getId());
    for (Developer teamMember : signUpForm.getTeam()) {
      developerState = getDeveloperState(teamMember);
      userIdList.add(developerState.getUserDetail().getUserSystemInfo().getId());
    }

    if(StringUtils.isNotBlank(signUpForm.getAppId())) {
      developerState = getDeveloperState(signUpForm.getCreator());
      developerRegistrationService.installApp(developerState);
      for (Developer teamMember : signUpForm.getTeam()) {
        developerState = getDeveloperState(teamMember);
        developerRegistrationService.installApp(developerState);
      }
    }

    developerState = getDeveloperState(signUpForm.getCreator());
    V2RoomDetail roomDetail = developerMessageService.createDeveloperRoom(
        "Team Development Room (" + developerState.getUserDetail().getUserAttributes().getUserName()
            + ")", userIdList);
    Stream stream = new Stream();
    stream.setId(roomDetail.getRoomSystemInfo().getId());
    developerState.setDeveloperRoom(stream);
    for(Developer teamMember : developerState.getTeamMembers()) {
      developerState = getDeveloperState(teamMember);
      developerState.setDeveloperRoom(stream);
    }

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

    if(StringUtils.isBlank(signUpForm.getAppId())) {
      signUpForm.setAppId(RandomStringUtils.randomAlphanumeric(40).toUpperCase());
    }

    Set<Developer> developerSet = new HashSet<>();
    developerSet.add(signUpForm.getCreator());
    developerSet.addAll(signUpForm.getTeam());
    Set<DeveloperBootstrapState> bootstrapStates = getInitialBootstrapStates(developerSet, signUpForm);
    welcome(bootstrapStates);
  }

  /**
   * Adds a developer to a developer team that was already created.
   * @param creator the creator of the team.
   * @param newTeamMembers the team members to add and create.
   * @return the bootstrap info, if any.
   */
  public DeveloperBootstrapInfo addTeamMembers(Developer creator, List<Developer> newTeamMembers) throws ApiException {
    DeveloperBootstrapState developerState = getDeveloperState(creator);
    Set<DeveloperBootstrapState> bootstrapStates = getInitialBootstrapStates(new HashSet<>(newTeamMembers),
        developerState.getDeveloperSignUpForm());
    welcome(bootstrapStates);

    if(developerRegistrationService.botOrAppExist(developerState.getDeveloperSignUpForm())) {
      String roomId = developerMessageService.getTeamRoomId("Team Development Room (" +
          developerState.getUserDetail().getUserAttributes().getUserName() + ")");
      if(roomId != null) {
        Stream stream = new Stream();
        stream.setId(roomId);
        for(DeveloperBootstrapState bootstrapState : bootstrapStates) {
          bootstrapState.setDeveloperRoom(stream);
        }
      }

      DeveloperBootstrapInfo developerBootstrapInfo = null;
      for(DeveloperBootstrapState bootstrapState : bootstrapStates) {
        developerBootstrapInfo = bootstrapDeveloper(bootstrapState.getDeveloper());
      }

      return developerBootstrapInfo;
    }

    DeveloperSignUpForm developerSignUpForm =  developerState.getDeveloperSignUpForm();
    developerSignUpForm.getTeam().addAll(newTeamMembers);
    for(Developer developer: developerSignUpForm.getTeam()) {
      developerState = getDeveloperState(developer);
      developerState.setDeveloperSignUpForm(developerSignUpForm);
      developerStateCache.put(developer, developerState);
    }

    return null;
  }

  private void bootstrap(DeveloperBootstrapState developerState) throws ApiException {
    DeveloperSignUpForm signUpForm = developerState.getDeveloperSignUpForm();
    if(developerState.getBootstrapInfo() == null) {
      DeveloperBootstrapInfo developerBootstrapInfo = new DeveloperBootstrapInfo();
      developerState.setBootstrapInfo(developerBootstrapInfo);

      //Register bot cert
      String botUsername = developerRegistrationService.getDefaultBotUsername();
      developerCertService.generateAndRegisterCert(botUsername, "", developerState);
      developerBootstrapInfo.setBotUsername(botUsername);
      developerBootstrapInfo.setBotEmail(signUpForm.getBotEmail());
      //Register bot
      UserDetail botDetail = developerRegistrationService.registerBot(developerState);
      //Save bot detail for all team members
      for(Developer teamMember: developerState.getTeamMembers()){
        DeveloperBootstrapState teamMemberState = getDeveloperState(teamMember);
        teamMemberState.setBotDetail(botDetail);
      }
      developerState.setBotDetail(botDetail);

      if (StringUtils.isNotBlank(signUpForm.getAppId())) {
        //Register app cert
        developerCertService.generateAndRegisterCert(signUpForm.getAppId(), "", developerState);
        developerBootstrapInfo.setAppId(signUpForm.getAppId());
        developerBootstrapInfo.setAppName(signUpForm.getAppName());

        //Register app
        ApplicationDetail applicationDetail = developerRegistrationService.registerApp(developerState);
        //Save app detail for all team members
        for(Developer teamMember: developerState.getTeamMembers()){
          DeveloperBootstrapState teamMemberState = getDeveloperState(teamMember);
          teamMemberState.setApplicationDetail(applicationDetail);
        }
        developerState.setApplicationDetail(applicationDetail);
      }

      //Set bootstrap info for team members (So they know app and bot were already created)
      for(Developer teamMember: developerState.getTeamMembers()){
        DeveloperBootstrapState teamMemberState = getDeveloperState(teamMember);
        teamMemberState.setBootstrapInfo(developerBootstrapInfo);
      }
    }
  }

  /**
   * Validates sign up form.
   * @param signUpForm the sign up form to validate
   */
  private void validateSignUpForm(DeveloperSignUpForm signUpForm) throws ApiException {
    if (StringUtils.isBlank(signUpForm.getCreator().getFirstName()) ||
        StringUtils.isBlank(signUpForm.getCreator().getLastName()) ||
        StringUtils.isBlank(signUpForm.getCreator().getEmail())) {
      throw new BadRequestException(BotConstants.DEVELOPER_REQUIRED);
    }
    for(Developer teamMember: signUpForm.getTeam()) {
      if (StringUtils.isBlank(teamMember.getFirstName()) ||
          StringUtils.isBlank(teamMember.getLastName()) ||
          StringUtils.isBlank(teamMember.getEmail())) {
        throw new BadRequestException(BotConstants.DEVELOPER_REQUIRED);
      }
    }

    Set<String> developerEmails = new HashSet<>();
    developerEmails.add(signUpForm.getCreator().getEmail().replace(" ", ""));
    for(Developer teamMember: signUpForm.getTeam()) {
      developerEmails.add(teamMember.getEmail().replace(" ", ""));
    }
    for(String developerEmail : developerEmails) {
      if(developerEmail.equals(signUpForm.getBotEmail())) {
        throw new BadRequestException(BotConstants.BOT_DEVELOPER_EMAIL_SAME);
      }
    }

    if (StringUtils.isBlank(signUpForm.getBotEmail())) {
      throw new BadRequestException(BotConstants.BOT_EMAIL_REQUIRED);
    }
    if (StringUtils.isBlank(signUpForm.getBotName())) {
      throw new BadRequestException(BotConstants.BOT_NAME_REQUIRED);
    }
    if(developerEmails.size() != signUpForm.getTeam().size() + 1) {
      throw new BadRequestException(BotConstants.DUPLICATE_DEVELOPER);
    }
    if(developerRegistrationService.oneDeveloperExists(signUpForm)){
      throw new BadRequestException(BotConstants.DEVELOPER_EXISTS);
    }
    if(developerRegistrationService.botOrAppExist(signUpForm) ||
        (StringUtils.isNotBlank(signUpForm.getAppId()) && reservedContent.contains(signUpForm.getAppId())) ||
        reservedContent.contains(signUpForm.getBotEmail().replace(" ", ""))){
      throw new BadRequestException(BotConstants.BOT_APP_EXISTS);
    }

    if(signUpFormContainsApp(signUpForm)) {
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
      if(StringUtils.isNotBlank(signUpForm.getAppIconUrl()) &&
          !signUpForm.getAppIconUrl().startsWith("https://")) {
        throw new BadRequestException(BotConstants.ICON_START_WITH_HTTPS);
      }
      if(StringUtils.isNotBlank(signUpForm.getAppIconUrl()) &&
          !validateUrl(signUpForm.getAppIconUrl())) {
        throw new BadRequestException(BotConstants.INVALID_APP_ICON_URL);
      }
      if (StringUtils.isBlank(signUpForm.getAppUrl())) {
        throw new BadRequestException(BotConstants.APP_URL_REQUIRED);
      }
      if(!signUpForm.getAppUrl().startsWith("https://")) {
        throw new BadRequestException(BotConstants.APP_START_WITH_HTTPS);
      }
      if(StringUtils.isNotBlank(signUpForm.getAppUrl()) &&
          !validateUrl(signUpForm.getAppUrl())) {
        throw new BadRequestException(BotConstants.INVALID_APP_URL);
      }
      try {
        validateDomain(signUpForm.getAppUrl(), signUpForm.getAppDomain());
      } catch (Exception e) {
        throw new BadRequestException(BotConstants.INVALID_APP_URL);
      }
    }
  }

  /**
   * Creates initial states for developer within sign up form. (Team members and creator)
   * @param signUpForm the sign up form
   * @return the initial partner states
   */
  private Set<DeveloperBootstrapState> getInitialBootstrapStates(Set<Developer> developerSet,
      DeveloperSignUpForm signUpForm) throws ApiException {
    Set<DeveloperBootstrapState> developerStates = new HashSet<>();
    for(Developer developer : developerSet) {
      DeveloperBootstrapState developerState = new DeveloperBootstrapState();
      developerState.setDeveloper(developer);
      developerState.setDeveloperSignUpForm(signUpForm);

      Set<Developer> teamMembers = new HashSet<>(developerSet);
      teamMembers.remove(developer);
      developerState.setTeamMembers(teamMembers);

      developerState.setCompanyCertMap(new HashMap<>());

      developerStates.add(developerState);
    }

    return developerStates;
  }

  private void welcome(Set<DeveloperBootstrapState> bootstrapStates) throws ApiException {
    for(DeveloperBootstrapState developerState : bootstrapStates) {
      developerStateCache.put(developerState.getDeveloper(), developerState);

      String randomPassword = UUID.randomUUID().toString().replace("-", "");
      int randomBegin = (int)(Math.random() * (randomPassword.length() - 3));
      int randomEnd = ThreadLocalRandom.current().nextInt(randomBegin, randomPassword.length());
      randomPassword = randomPassword.replace(randomPassword.substring(randomBegin, randomEnd),
          randomPassword.substring(randomBegin, randomEnd).toUpperCase());

      developerRegistrationService.registerDeveloperUser(developerState, randomPassword);
      developerEmailService.sendWelcomeEmail(developerState, randomPassword);
      developerMessageService.sendDirectionalMessage(developerState);

      reservedContent.add(developerState.getDeveloperSignUpForm().getAppId());
      reservedContent.add(developerState.getDeveloperSignUpForm().getBotEmail().replace(" ", ""));

      LOG.info("Welcomed developer " + developerState.getUserDetail().getUserAttributes().getUserName() + ".");
    }
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

  private boolean validateUrl(String url){
    UrlValidator urlValidator = new UrlValidator();
    return urlValidator.isValid(url);
  }

  private boolean signUpFormContainsApp(DeveloperSignUpForm signUpForm) {
    return StringUtils.isNotBlank(signUpForm.getAppId()) || StringUtils.isNotBlank(signUpForm.getAppName()) ||
        StringUtils.isNotBlank(signUpForm.getAppIconUrl()) || StringUtils.isNotBlank(signUpForm.getAppUrl()) ||
        StringUtils.isNotBlank(signUpForm.getAppCompanyName()) || StringUtils.isNotBlank(signUpForm.getAppDomain()) ||
        StringUtils.isNotBlank(signUpForm.getAppDescription());
  }

  private DeveloperBootstrapState getDeveloperState(Developer developer) {
    DeveloperBootstrapState developerState;
    try {
      developerState = developerStateCache.get(developer);
    } catch (Exception e) {
      LOG.warn("Get developer state failed: ", e);
      throw new BadRequestException(BotConstants.DEVELOPER_NOT_FOUND);
    }

    return developerState;
  }

}
