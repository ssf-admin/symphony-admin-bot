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

import com.symphony.api.adminbot.api.SignUpApi;
import com.symphony.api.adminbot.client.ApiException;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.adminbot.model.DeveloperWelcomeDetail;
import com.symphony.api.adminbot.model.WelcomeSettings;

import org.apache.commons.lang.RandomStringUtils;
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

  @When("the admin user creates a valid form for the bootstrap process")
  public void createForm() {
    DeveloperSignUpForm developerSignUpForm = new DeveloperSignUpForm();
    developerSignUpForm.setAppId(RandomStringUtils.random(40));
    developerSignUpForm.setAppCompanyName(RandomStringUtils.random(30));
    developerSignUpForm.setAppDescription(RandomStringUtils.random(100));

    String domain = RandomStringUtils.random(20) + ".com";
    developerSignUpForm.setAppUrl("https://" + domain);
    developerSignUpForm.setAppDomain(domain);

    developerSignUpForm.setAppName(RandomStringUtils.random(20));
    developerSignUpForm.setBotEmail(RandomStringUtils.random(10) + "@gmail.com");
    developerSignUpForm.setBotName(RandomStringUtils.random(10));

    Developer developer = new Developer();
    developer.setFirstName(RandomStringUtils.random(10));
    developer.setLastName(RandomStringUtils.random(10));
    developer.setEmail(RandomStringUtils.random(10) + "@gmail.com");
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

  @When("the admin user modifies the $field field to a random string value of length $value")
  public void randomStringOfLengthModifyForm(String field, int value) {
    if(field.equals(FormEnum.APP_ID.getName())) {
      context.getDeveloperSignUpForm().setAppId(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.APP_COMPANY.getName())) {
      context.getDeveloperSignUpForm().setAppCompanyName(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.APP_DESCRIPTION.getName())) {
      context.getDeveloperSignUpForm().setAppDescription(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.APP_ICON_URL.getName())) {
      context.getDeveloperSignUpForm().setAppIconUrl(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.APP_DOMAIN.getName())) {
      context.getDeveloperSignUpForm().setAppDomain(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.APP_NAME.getName())) {
      context.getDeveloperSignUpForm().setAppName(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.APP_URL.getName())) {
      context.getDeveloperSignUpForm().setAppUrl(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.BOT_EMAIL.getName())) {
      context.getDeveloperSignUpForm().setBotEmail(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.BOT_NAME.getName())) {
      context.getDeveloperSignUpForm().setBotName(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.FIRST_NAME.getName())) {
      context.getDeveloperSignUpForm().getCreator().setFirstName(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.LAST_NAME.getName())) {
      context.getDeveloperSignUpForm().getCreator().setLastName(RandomStringUtils.random(value));
    } else if (field.equals(FormEnum.EMAIL.getName())) {
      context.getDeveloperSignUpForm().getCreator().setEmail(RandomStringUtils.random(value));
    }
  }

  @When("the admin user adds a team member $first $last with $email as the email to the sign up form")
  public void addTeamMember(String first, String last, String email) {
    Developer developer = new Developer();
    developer.setFirstName(first);
    developer.setLastName(last);
    developer.setEmail(email);

    context.getDeveloperSignUpForm().addTeamItem(developer);
  }

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

  @Then("the admin user sends a developer welcome with automatic bootstrap set to $auto, fail on $exception")
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
