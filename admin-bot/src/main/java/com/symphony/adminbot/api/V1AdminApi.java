package com.symphony.adminbot.api;

import com.symphony.adminbot.bots.AdminBot;
import com.symphony.adminbot.model.core.AdminSession;
import com.symphony.adminbot.model.core.AdminSessionManager;
import com.symphony.adminbot.api.impl.AbstractV1AdminService;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.model.signup.PartnerSignUpService;
import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerBootstrapInfo;
import com.symphony.api.adminbot.model.PartnerSignUpForm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.StringUtil;

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
  public Response bootstrapPartner(AdminSession adminSession, Partner partner) {
    PartnerSignUpService signUpService = adminSession.getSignUpService();
    try {
      signUpService.bootstrapPartner(partner);
      PartnerBootstrapInfo partnerBootstrapInfo = signUpService.sendBootstrapPackage(partner);

      return Response.ok().entity(partnerBootstrapInfo).build();
    } catch (Exception e) {
      return handleError(Response.Status.INTERNAL_SERVER_ERROR, BotConstants.INTERNAL_ERROR);
    }
  }

  @Override
  public Response sendPartnerWelcome(AdminSession adminSession, PartnerSignUpForm signUpForm) {
    PartnerSignUpService signUpService = adminSession.getSignUpService();
    try {
      signUpService.validateSignUpForm(signUpForm);
      signUpService.welcomePartner(signUpForm);
    } catch (BadRequestException e) {
      return handleError(Response.Status.BAD_REQUEST, e.getMessage());
    } catch (ApiException e) {
      LOG.error("Send partner welcome failed:", e);
      return handleError(Response.Status.INTERNAL_SERVER_ERROR, BotConstants.INTERNAL_ERROR);
    }

    return Response.ok().build();
  }

  @Override
  public AdminSession getAdminSession(String sessionToken, String keyManagerToken) {
    return adminSessionManager.getAdminSession(sessionToken, keyManagerToken);
  }

  private Response handleError(Response.Status status, String responseBody){
    return Response.status(status).entity("{\"code\":" + status.getStatusCode()
        + "\"message\": \"" + responseBody + "\"}").build();
  }
}
