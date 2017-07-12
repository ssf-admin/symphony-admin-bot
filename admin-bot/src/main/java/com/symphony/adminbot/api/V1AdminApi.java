package com.symphony.adminbot.api;

import com.symphony.adminbot.api.impl.AbstractV1AdminService;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.model.core.AdminSession;
import com.symphony.adminbot.model.core.AdminSessionManager;
import com.symphony.adminbot.model.bootstrap.DeveloperBootstrapService;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperBootstrapInfo;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.pod.invoker.ApiException;

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
    DeveloperBootstrapService signUpService = adminSession.getSignUpService();
    try {
      signUpService.bootstrapPartner(developer);
      DeveloperBootstrapInfo partnerBootstrapInfo = signUpService.sendBootstrapPackage(developer);

      return Response.ok().entity(partnerBootstrapInfo).build();
    } catch (Exception e) {
      return handleError(Response.Status.INTERNAL_SERVER_ERROR, BotConstants.INTERNAL_ERROR);
    }
  }

  @Override
  public Response sendDeveloperWelcome(AdminSession adminSession, DeveloperSignUpForm signUpForm) {
    DeveloperBootstrapService signUpService = adminSession.getSignUpService();
    try {
      signUpService.validateSignUpForm(signUpForm);
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
