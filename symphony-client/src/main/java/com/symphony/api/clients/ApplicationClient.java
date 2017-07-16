package com.symphony.api.clients;

import com.symphony.api.clients.model.SymphonyAuth;

import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.Configuration;

/**
 * Created by nick.tarsillo on 7/14/17.
 */
public class ApplicationClient {
  private final ApiClient apiClient;
  private SymphonyAuth symAuth;

  public ApplicationClient(SymphonyAuth symAuth, String agentUrl) {
    this.symAuth = symAuth;

    //Get Service clients to query for userID.
    apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(agentUrl);
  }
}
