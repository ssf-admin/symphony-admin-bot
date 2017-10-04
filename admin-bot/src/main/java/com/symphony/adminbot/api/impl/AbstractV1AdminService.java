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

package com.symphony.adminbot.api.impl;

import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.model.session.AdminBotUserSession;
import com.symphony.api.adminbot.api.V1ApiService;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperBootstrapInfo;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.adminbot.model.DeveloperWelcomeDetail;
import com.symphony.api.adminbot.model.DeveloperWelcomeResponse;
import com.symphony.api.adminbot.model.HealthcheckResponse;
import com.symphony.api.adminbot.model.NewTeamMembersDetail;
import com.symphony.api.adminbot.model.WelcomeSettings;

import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public abstract class AbstractV1AdminService implements V1ApiService {
  public abstract DeveloperBootstrapInfo bootstrapDeveloper(Developer developer);

  public abstract DeveloperBootstrapInfo bootstrapDevelopers(DeveloperSignUpForm signUpForm);

  public abstract String sendDeveloperWelcome(DeveloperSignUpForm signUpForm);

  public abstract AdminBotUserSession getAdminUserSession(String sessionToken);

  protected abstract DeveloperBootstrapInfo addTeamMembers(NewTeamMembersDetail newTeamMembersDetail);

  public abstract HealthcheckResponse healthcheck();

  @Override
  public Response v1HealthcheckGet() {
    return Response.ok(healthcheck()).build();
  }

  @Override
  public Response v1BootstrapDeveloperPost(String sessionToken, Developer
      developer) {
    getAdminUserSession(sessionToken);
    return Response.ok(bootstrapDeveloper(developer)).build();
  }

  @Override
  public Response v1SendDeveloperWelcomePost(String sessionToken, DeveloperWelcomeDetail welcomeDetail) {
    getAdminUserSession(sessionToken);
    DeveloperWelcomeResponse developerWelcomeResponse = new DeveloperWelcomeResponse();

    String message = sendDeveloperWelcome(welcomeDetail.getSignUpForm());
    developerWelcomeResponse.setMessage(message);

    if(welcomeDetail.getWelcomeSettings() == null) {
      welcomeDetail.setWelcomeSettings(new WelcomeSettings());
    }

    if(welcomeDetail.getWelcomeSettings().getAutoBootstrap()) {
      DeveloperBootstrapInfo bootstrapInfo = bootstrapDevelopers(welcomeDetail.getSignUpForm());
      developerWelcomeResponse.setBootstrapInfo(bootstrapInfo);
    }

    return Response.ok(developerWelcomeResponse).build();
  }

  @Override
  public Response v1AddTeamMemberPost(String sessionToken, NewTeamMembersDetail newTeamMembersDetail) {
    getAdminUserSession(sessionToken);
    DeveloperBootstrapInfo developerBootstrapInfo = addTeamMembers(newTeamMembersDetail);
    DeveloperWelcomeResponse developerWelcomeResponse = new DeveloperWelcomeResponse();
    developerWelcomeResponse.setMessage(BotConstants.DEVELOPER_WELCOME_SUCCESS);
    developerWelcomeResponse.setBootstrapInfo(developerBootstrapInfo);
    return Response.ok(developerWelcomeResponse).build();
  }
}
