package com.symphony.adminbot.api;

import com.symphony.adminbot.api.impl.AbstractV1AdminService;
import com.symphony.adminbot.bootstrap.service.DeveloperBootstrapService;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.model.session.AdminSession;
import com.symphony.adminbot.model.session.AdminSessionManager;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperBootstrapInfo;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.pod.client.ApiException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;


/**
 * Created by nick.tarsillo on 7/5/17.
 */
public class V1AdminApi extends AbstractV1AdminService {
  private static final Logger LOG = LoggerFactory.getLogger(V1AdminApi.class);
  private AdminSessionManager adminSessionManager;

  public V1AdminApi(AdminSessionManager adminSessionManager){
    this.adminSessionManager = adminSessionManager;
  }

  @Override
  public Response bootstrapDeveloper(AdminSession adminSession, Developer developer) {
    DeveloperBootstrapService signUpService = adminSession.getBootstrapService();
    try {
      DeveloperBootstrapInfo partnerBootstrapInfo = signUpService.bootstrapPartner(developer);
      return Response.ok(partnerBootstrapInfo).build();
    } catch (Exception e) {
      LOG.error("Bootstrap partner welcome failed:", e);
      return handleError(Response.Status.INTERNAL_SERVER_ERROR, BotConstants.INTERNAL_ERROR);
    }
  }

  @Override
  public Response sendDeveloperWelcome(AdminSession adminSession, DeveloperSignUpForm signUpForm) {
    DeveloperBootstrapService signUpService = adminSession.getBootstrapService();
    try {
      signUpService.welcomePartner(signUpForm);
    } catch (ApiException e) {
      LOG.error("Send partner welcome failed:", e);
      return handleError(Response.Status.INTERNAL_SERVER_ERROR, BotConstants.INTERNAL_ERROR);
    }

    return Response.ok("{\"message\":\"Developer welcome succeeded.\"}").build();
  }

  @Override
  public AdminSession getAdminSession(String sessionToken, String keyManagerToken) {
    AdminSession adminSession = adminSessionManager.getAdminSession(sessionToken, keyManagerToken);
    if(adminSession == null) {
      throw new BadRequestException("Admin session not found.");
    }

    return adminSession;
  }

  private Response handleError(Response.Status status, String responseBody){
    return Response.status(status).entity("{\"code\":" + status.getStatusCode()
        + ", \"message\": \"" + responseBody + "\"}").build();
  }
}
