package com.symphony.adminbot.api.impl;

import com.symphony.adminbot.model.core.AdminSession;
import com.symphony.api.adminbot.api.NotFoundException;
import com.symphony.api.adminbot.api.V1ApiService;
import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerSignUpForm;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public abstract  class AbstractV1AdminService extends V1ApiService {
  public abstract Response bootstrapPartner(AdminSession adminSession, Partner partner);

  public abstract Response sendPartnerWelcome(AdminSession adminSession, PartnerSignUpForm signUpForm);

  public abstract AdminSession getAdminSession(String sessionToken, String keyManagerToken);

  @Override
  public Response v1BootstrapPartnerPost(String sessionToken, String keyManagerToken, Partner partner, SecurityContext securityContext)
      throws NotFoundException {
    AdminSession adminSession = getAdminSession(sessionToken, keyManagerToken);
    return bootstrapPartner(adminSession, partner);
  }

  @Override
  public Response v1SendPartnerWelcomePost(String sessionToken, String keyManagerToken, PartnerSignUpForm signUpForm,
      SecurityContext securityContext) throws NotFoundException{
    AdminSession adminSession = getAdminSession(sessionToken, keyManagerToken);
    return sendPartnerWelcome(adminSession, signUpForm);
  }
}
