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

package com.symphony.adminbot.api;

import com.symphony.adminbot.api.impl.AbstractV1AdminService;
import com.symphony.adminbot.bootstrap.service.DeveloperBootstrapService;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.health.HealthCheckFailedException;
import com.symphony.adminbot.health.HealthcheckHelper;
import com.symphony.adminbot.model.session.AdminBotSession;
import com.symphony.adminbot.model.session.AdminBotUserSession;
import com.symphony.adminbot.model.session.AdminBotUserSessionManager;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperBootstrapInfo;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.adminbot.model.HealthcheckResponse;
import com.symphony.api.pod.client.ApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;


/**
 * Created by nick.tarsillo on 7/5/17.
 */
public class V1AdminApi extends AbstractV1AdminService {
  private static final Logger LOG = LoggerFactory.getLogger(V1AdminApi.class);

  private AdminBotUserSessionManager adminSessionManager;
  private AdminBotSession adminBotSession;

  public V1AdminApi(AdminBotUserSessionManager adminSessionManager, AdminBotSession adminBotSession){
    this.adminSessionManager = adminSessionManager;
    this.adminBotSession = adminBotSession;
  }

  @Override
  public DeveloperBootstrapInfo bootstrapDeveloper(Developer developer) {
    DeveloperBootstrapService signUpService = adminBotSession.getBootstrapService();
    try {
      DeveloperBootstrapInfo developerBootstrapInfo = signUpService.bootstrapDeveloper(developer);
      return developerBootstrapInfo;
    } catch (ApiException e) {
      LOG.error("Bootstrap partner welcome failed:", e);
      throw new InternalServerErrorException(BotConstants.INTERNAL_ERROR);
    }
  }

  @Override
  public DeveloperBootstrapInfo bootstrapDevelopers(DeveloperSignUpForm signUpForm) {
    DeveloperBootstrapService signUpService = adminBotSession.getBootstrapService();
    try {
      DeveloperBootstrapInfo developerBootstrapInfo = signUpService.bootstrapDevelopers(signUpForm);
      return developerBootstrapInfo;
    } catch (ApiException e) {
      LOG.error("Bootstrap partner welcome failed:", e);
      throw new InternalServerErrorException(BotConstants.INTERNAL_ERROR);
    }
  }

  @Override
  public String sendDeveloperWelcome(DeveloperSignUpForm signUpForm) {
    DeveloperBootstrapService signUpService = adminBotSession.getBootstrapService();
    try {
      signUpService.welcomeDeveloper(signUpForm);
    } catch (ApiException e) {
      LOG.error("Send partner welcome failed:", e);
      throw new InternalServerErrorException(BotConstants.INTERNAL_ERROR);
    }

    return BotConstants.DEVELOPER_WELCOME_SUCCESS;
  }

  @Override
  public AdminBotUserSession getAdminUserSession(String sessionToken) {
    AdminBotUserSession adminSession = adminSessionManager.getAdminSession(sessionToken);
    if(adminSession == null) {
      throw new BadRequestException("Admin session not found.");
    }

    return adminSession;
  }

  @Override
  public HealthcheckResponse healthcheck() {
    String agentUrl = System.getProperty(BotConfig.SYMPHONY_AGENT);
    String podUrl = System.getProperty(BotConfig.SYMPHONY_POD);
    HealthcheckHelper healthcheckHelper = new HealthcheckHelper(podUrl, agentUrl);

    HealthcheckResponse response = new HealthcheckResponse();
    try {
      healthcheckHelper.checkPodConnectivity();
      response.setPodConnectivityCheck(true);
    } catch (HealthCheckFailedException e) {
      response.setPodConnectivityCheck(false);
      response.setPodConnectivityError(e.getMessage());
    }

    try {
      healthcheckHelper.checkAgentConnectivity();
      response.setAgentConnectivityCheck(true);
    } catch (HealthCheckFailedException e) {
      response.setAgentConnectivityCheck(false);
      response.setAgentConnectivityError(e.getMessage());
    }

    return response;
  }
}
