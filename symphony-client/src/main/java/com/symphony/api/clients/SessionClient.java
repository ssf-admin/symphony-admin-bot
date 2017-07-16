package com.symphony.api.clients;

import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.clients.model.SymphonyUser;
import com.symphony.api.pod.api.SessionApi;
import com.symphony.api.pod.client.ApiClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.client.Configuration;

/**
 * Created by nick.tarsillo on 7/6/17.
 */
public class SessionClient {
  private final ApiClient apiClient;
  private SymphonyAuth symAuth;

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



  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
