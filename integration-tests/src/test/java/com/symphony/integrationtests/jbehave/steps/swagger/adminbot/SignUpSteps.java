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
import static org.junit.Assert.assertNotNull;

import com.symphony.api.adminbot.api.SignUpApi;
import com.symphony.api.adminbot.client.ApiException;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.adminbot.model.DeveloperWelcomeDetail;
import com.symphony.api.adminbot.model.DeveloperWelcomeResponse;
import com.symphony.api.adminbot.model.WelcomeSettings;

import org.apache.commons.lang.RandomStringUtils;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 8/27/17.
 */
public class SignUpSteps extends BaseApiSteps {
  enum FormEnum {
    APP_ID("app id"),
    APP_COMPANY("app company"),
    APP_DESCRIPTION("app description"),
    APP_URL("app url"),
    APP_ICON_URL("app icon url"),
    APP_DOMAIN("app domain"),
    APP_NAME("app name"),
    BOT_EMAIL("bot email"),
    BOT_NAME("bot name"),
    FIRST_NAME("first name"),
    LAST_NAME("last name"),
    EMAIL("email");

    private String name;

    FormEnum(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }

  @Given("a valid form for the bootstrap process")
  public void createForm() {
    DeveloperSignUpForm developerSignUpForm = new DeveloperSignUpForm();
    developerSignUpForm.setAppId(RandomStringUtils.randomAlphabetic(40));
    developerSignUpForm.setAppCompanyName(RandomStringUtils.randomAlphabetic(30));
    developerSignUpForm.setAppDescription(RandomStringUtils.randomAlphabetic(100));

    String domain = RandomStringUtils.randomAlphabetic(20) + ".com";
    developerSignUpForm.setAppUrl("https://" + domain);
    developerSignUpForm.setAppDomain(domain);

    developerSignUpForm.setAppName(RandomStringUtils.randomAlphabetic(20));
    developerSignUpForm.setBotEmail(RandomStringUtils.randomAlphabetic(10) + "@gmail.com");
    developerSignUpForm.setBotName(RandomStringUtils.randomAlphabetic(10));

    Developer developer = new Developer();
    developer.setFirstName(RandomStringUtils.randomAlphabetic(10));
    developer.setLastName(RandomStringUtils.randomAlphabetic(10));
    developer.setEmail(RandomStringUtils.randomAlphabetic(10) + "@gmail.com");
    developerSignUpForm.setCreator(developer);

    context.setDeveloperSignUpForm(developerSignUpForm);
  }

  @When("the admin user modifies the $field field on the sign up form to $value")
  public void modifyForm(String field, String value) {
    if(field.equals(FormEnum.APP_ID.getName())) {
      context.getDeveloperSignUpForm().setAppId(value);
    } else if (field.equals(FormEnum.APP_COMPANY.getName())) {
      context.getDeveloperSignUpForm().setAppCompanyName(value);
    } else if (field.equals(FormEnum.APP_DESCRIPTION.getName())) {
      context.getDeveloperSignUpForm().setAppDescription(value);
    } else if (field.equals(FormEnum.APP_ICON_URL.getName())) {
      context.getDeveloperSignUpForm().setAppIconUrl(value);
    } else if (field.equals(FormEnum.APP_DOMAIN.getName())) {
      context.getDeveloperSignUpForm().setAppDomain(value);
    } else if (field.equals(FormEnum.APP_NAME.getName())) {
      context.getDeveloperSignUpForm().setAppName(value);
    } else if (field.equals(FormEnum.APP_URL.getName())) {
      context.getDeveloperSignUpForm().setAppUrl(value);
    } else if (field.equals(FormEnum.BOT_EMAIL.getName())) {
      context.getDeveloperSignUpForm().setBotEmail(value);
    } else if (field.equals(FormEnum.BOT_NAME.getName())) {
      context.getDeveloperSignUpForm().setBotName(value);
    } else if (field.equals(FormEnum.FIRST_NAME.getName())) {
      context.getDeveloperSignUpForm().getCreator().setFirstName(value);
    } else if (field.equals(FormEnum.LAST_NAME.getName())) {
      context.getDeveloperSignUpForm().getCreator().setLastName(value);
    } else if (field.equals(FormEnum.EMAIL.getName())) {
      context.getDeveloperSignUpForm().getCreator().setEmail(value);
    }
  }

  @When("the admin user sets the $field field to null")
  public void nullForm(String field) {
    modifyForm(field, null);
  }

  @When("the admin user modifies the $field field to a random string value of length $value")
  public void randomStringOfLengthModifyForm(String field, int value) {
    modifyForm(field, RandomStringUtils.random(value));
  }

  @When("the admin user adds a team member to the form")
  public void addTeamMember() {
    Developer developer = new Developer();
    developer.setFirstName(RandomStringUtils.randomAlphabetic(40));
    developer.setLastName(RandomStringUtils.randomAlphabetic(40));
    developer.setEmail(RandomStringUtils.randomAlphabetic(40) + "@gmail.com");

    context.getDeveloperSignUpForm().addTeamItem(developer);
  }

  @When("the admin user sends a developer welcome with automatic bootstrap set to $auto")
  @Then("the admin user sends a developer welcome with automatic bootstrap set to $auto")
  public void developerWelcome(boolean autoBootstrap) throws ApiException {
    SignUpApi signUpApi = new SignUpApi(getApiClient());

    DeveloperWelcomeDetail developerWelcomeDetail = new DeveloperWelcomeDetail();
    developerWelcomeDetail.setSignUpForm(context.getDeveloperSignUpForm());

    WelcomeSettings welcomeSettings = new WelcomeSettings();
    welcomeSettings.setAutoBootstrap(autoBootstrap);
    developerWelcomeDetail.setWelcomeSettings(welcomeSettings);
    context.setDeveloperWelcomeResponse(signUpApi.v1SendDeveloperWelcomePost(context.getAdminSessionToken(), developerWelcomeDetail));
  }

  @Then("the welcome response contains valid bootstrap data")
  public void bootstrapValid() throws ApiException {
    DeveloperWelcomeResponse welcomeResponse = context.getDeveloperWelcomeResponse();
    DeveloperSignUpForm developerSignUpForm = context.getDeveloperSignUpForm();
    assertNotNull(welcomeResponse.getBootstrapInfo());
    assertNotNull(welcomeResponse.getBootstrapInfo().getBotUsername());
    assertEquals("Bot email response", developerSignUpForm.getBotEmail(),
        welcomeResponse.getBootstrapInfo().getBotEmail());

    if(developerSignUpForm.getAppName() != null) {
      assertNotNull(welcomeResponse.getBootstrapInfo().getAppId());
      assertEquals("App name response", developerSignUpForm.getAppName(),
          welcomeResponse.getBootstrapInfo().getAppName());
      if(developerSignUpForm.getAppId() != null) {
        assertEquals("App name response", developerSignUpForm.getAppId(),
            welcomeResponse.getBootstrapInfo().getAppId());
      }
    }
  }

  @Then("the admin user cannot send a developer welcome with automatic bootstrap set to $auto, fail on $exception")
  public void developerWelcomeFail(boolean autoBootstrap, Response.Status exception) {
    SignUpApi signUpApi = new SignUpApi(getApiClient());

    DeveloperWelcomeDetail developerWelcomeDetail = new DeveloperWelcomeDetail();
    developerWelcomeDetail.setSignUpForm(context.getDeveloperSignUpForm());

    WelcomeSettings welcomeSettings = new WelcomeSettings();
    welcomeSettings.setAutoBootstrap(autoBootstrap);
    developerWelcomeDetail.setWelcomeSettings(welcomeSettings);
    try {
      signUpApi.v1SendDeveloperWelcomePost(context.getAdminSessionToken(), developerWelcomeDetail);
    } catch (ApiException e) {
      //Expected
      assertEquals("Exception return", exception.getStatusCode(), e.getCode());
    }
  }
}
