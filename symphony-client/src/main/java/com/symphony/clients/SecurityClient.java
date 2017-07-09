package com.symphony.clients;


import com.symphony.clients.model.SymphonyAuth;

import org.symphonyoss.symphony.pod.api.SecurityApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;
import org.symphonyoss.symphony.pod.model.CompanyCert;
import org.symphonyoss.symphony.pod.model.SuccessResponse;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class SecurityClient {
  private final SymphonyAuth symAuth;
  private final ApiClient apiClient;

  public SecurityClient(SymphonyAuth symAuth, String serviceUrl){
    this.symAuth = symAuth;

    //Get Service clients to query for userID.
    apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(serviceUrl);

    apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
    apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());
  }

  /**
   * Creates a company cert
   * @param cert
   * @return
   * @throws ApiException
   */
  public SuccessResponse createCert(CompanyCert cert) throws ApiException {
    SecurityApi securityApi = new SecurityApi(apiClient);

    SuccessResponse response;
    try {
      response = securityApi.v1CompanycertCreatePost(symAuth.getSessionToken().getToken(), cert);
    } catch (ApiException e) {
      throw new ApiException("Create cert failed: " + e.getStackTrace());
    }

    return response;
  }
}
