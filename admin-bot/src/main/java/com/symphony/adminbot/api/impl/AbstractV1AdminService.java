package com.symphony.adminbot.api.impl;

import com.symphony.adminbot.model.session.AdminBotUserSession;
import com.symphony.api.adminbot.api.V1ApiService;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;

import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public abstract  class AbstractV1AdminService implements V1ApiService {
  public abstract Response bootstrapDeveloper(Developer developer);

  public abstract Response sendDeveloperWelcome(DeveloperSignUpForm signUpForm);

  public abstract AdminBotUserSession getAdminUserSession(String sessionToken);

  @Override
  public Response v1BootstrapDeveloperPost(String sessionToken, Developer
      developer) {
    getAdminUserSession(sessionToken);
    return bootstrapDeveloper(developer);
  }

  @Override
  public Response v1SendDeveloperWelcomePost(String sessionToken, DeveloperSignUpForm signUpForm){
    getAdminUserSession(sessionToken);
    return sendDeveloperWelcome(signUpForm);
  }
}
