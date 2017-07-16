package com.symphony.api.clients;

import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.pod.api.ApplicationApi;
import com.symphony.api.pod.client.ApiClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.client.Configuration;
import com.symphony.api.pod.model.ApplicationDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nick.tarsillo on 7/14/17.
 */
public class ApplicationClient {
  private final Logger LOG = LoggerFactory.getLogger(ApplicationClient.class);

  private final ApiClient apiClient;
  private SymphonyAuth symAuth;

  public ApplicationClient(SymphonyAuth symAuth, String agentUrl) {
    this.symAuth = symAuth;

    //Get Service clients to query for userID.
    apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(agentUrl);
  }

  public ApplicationDetail createApplication(ApplicationDetail applicationDetail) throws ApiException {
    ApplicationApi applicationApi = new ApplicationApi(apiClient);

    ApplicationDetail applicationDetail1;
    try {
      applicationDetail = applicationApi.v1AdminAppCreatePost(symAuth.getSessionToken().getToken(), applicationDetail);
    } catch(ApiException e) {
      LOG.error("Create application failed: ", e);
      throw new ApiException("Create application failed: " + e);
    }

    return applicationDetail;
  }

  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
