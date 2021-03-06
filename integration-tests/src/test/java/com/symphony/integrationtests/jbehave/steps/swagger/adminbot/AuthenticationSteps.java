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

package com.symphony.integrationtests.jbehave.steps.swagger.adminbot;

import static org.junit.Assert.assertEquals;

import com.symphony.api.adminbot.api.AuthenticationApi;
import com.symphony.api.adminbot.api.SignUpApi;
import com.symphony.api.adminbot.client.ApiException;
import com.symphony.api.adminbot.model.DeveloperWelcomeDetail;
import com.symphony.api.adminbot.model.SessionToken;

import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

/**
 * Created by nick.tarsillo on 8/27/17.
 */
public class AuthenticationSteps extends BaseApiSteps {
  @When("the admin user authenticates using a certificate")
  @Then("the admin user authenticates using a certificate, receives valid token back")
  public void authenticate() throws ApiException {
    AuthenticationApi authenticationApi = new AuthenticationApi(getAuthClient());
    SessionToken tokenResponse = authenticationApi.v1AuthenticatePost();
    context.setAdminSessionToken(tokenResponse.getSessionToken());
  }

  @Then("the admin user can not use an invalid session token to identify their session")
  public void authenticateInvalidToken() {
    SignUpApi signUpApi = new SignUpApi(getApiClient());
    try {
      signUpApi.v1SendDeveloperWelcomePost("Invalid", new DeveloperWelcomeDetail());
    } catch (ApiException e) {
      assertEquals("Error code return", 403, e.getCode());
      assertEquals("Error message return", "{\"code\":403, \"message\": \"Admin session not found"
          + ".\"}", e.getMessage());
    }
  }
}
