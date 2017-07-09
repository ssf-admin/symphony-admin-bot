package com.symphony.clients;

import com.symphony.clients.model.SymphonyAuth;
import com.symphony.clients.model.SymphonyUser;

import org.symphonyoss.symphony.pod.api.SessionApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;
import org.symphonyoss.symphony.pod.model.UserV2;

/**
 * Created by nick.tarsillo on 7/6/17.
 */
public class SessionClient {
  private final ApiClient apiClient;
  private final SymphonyAuth symAuth;

  public SessionClient(SymphonyAuth symAuth, String serviceUrl) {
    this.symAuth = symAuth;

    //Get Service clients to query for userID.
    apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(serviceUrl);
  }

  public SymphonyUser getSessionInfo() throws ApiException {
    SessionApi sessionApi = new SessionApi(apiClient);

    SymphonyUser symUser;
    try {
      symUser =  new SymphonyUser(sessionApi.v2SessioninfoGet(symAuth.getSessionToken().getToken()));
    } catch (ApiException e) {
      throw new ApiException("Could not get session info: " + e.getStackTrace());
    }

    return symUser;
  }
}
