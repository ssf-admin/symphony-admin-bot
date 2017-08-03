package com.symphony.adminbot.api.impl;

import com.symphony.adminbot.model.session.AdminBotUserSession;
import com.symphony.api.adminbot.api.V1ApiService;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperBootstrapInfo;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.adminbot.model.DeveloperWelcomeDetail;
import com.symphony.api.adminbot.model.DeveloperWelcomeResponse;
import com.symphony.api.adminbot.model.WelcomeSettings;

import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public abstract  class AbstractV1AdminService implements V1ApiService {
  public abstract DeveloperBootstrapInfo bootstrapDeveloper(Developer developer);

  public abstract DeveloperBootstrapInfo bootstrapDevelopers(DeveloperSignUpForm signUpForm);

  public abstract String sendDeveloperWelcome(DeveloperSignUpForm signUpForm);

  public abstract AdminBotUserSession getAdminUserSession(String sessionToken);

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
}
