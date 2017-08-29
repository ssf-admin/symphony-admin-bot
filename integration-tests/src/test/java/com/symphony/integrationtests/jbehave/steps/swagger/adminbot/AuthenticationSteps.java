package com.symphony.integrationtests.jbehave.steps.swagger.adminbot;

import com.symphony.api.adminbot.api.AuthenticationApi;
import com.symphony.api.adminbot.client.ApiException;
import com.symphony.api.adminbot.model.SessionToken;

import org.jbehave.core.annotations.When;

/**
 * Created by nick.tarsillo on 8/27/17.
 */
public class AuthenticationSteps extends BaseApiSteps {
  @When("the admin user authenticates using a certificate")
  public void authenticate() throws ApiException {
    AuthenticationApi authenticationApi = new AuthenticationApi(getAuthClient());
    SessionToken tokenResponse = authenticationApi.v1AuthenticatePost();
    context.setAdminSessionToken(tokenResponse.getSessionToken());
  }
}
