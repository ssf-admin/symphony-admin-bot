package com.symphony.adminbot.api.impl;

import com.symphony.adminbot.model.core.AdminSession;
import com.symphony.api.adminbot.api.V1ApiService;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;

import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public abstract  class AbstractV1AdminService implements V1ApiService {
  public abstract Response bootstrapDeveloper(AdminSession adminSession, Developer developer);

  public abstract Response sendDeveloperWelcome(AdminSession adminSession, DeveloperSignUpForm signUpForm);

  public abstract AdminSession getAdminSession(String sessionToken, String keyManagerToken);

  @Override
  public Response v1BootstrapDeveloperPost(String sessionToken, String keyManagerToken, Developer
      developer) {
    AdminSession adminSession = getAdminSession(sessionToken, keyManagerToken);
    return bootstrapDeveloper(adminSession, developer);
  }

  @Override
  public Response v1SendDeveloperWelcomePost(String sessionToken, String keyManagerToken, DeveloperSignUpForm signUpForm){
    AdminSession adminSession = getAdminSession(sessionToken, keyManagerToken);
    return sendDeveloperWelcome(adminSession, signUpForm);
  }
}
