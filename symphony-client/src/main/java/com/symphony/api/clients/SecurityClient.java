package com.symphony.api.clients;


import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.pod.api.SecurityApi;
import com.symphony.api.pod.client.ApiClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.client.Configuration;
import com.symphony.api.pod.model.CompanyCert;
import com.symphony.api.pod.model.CompanyCertDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class SecurityClient {
  private final Logger LOG = LoggerFactory.getLogger(SecurityClient.class);

  private final ApiClient apiClient;
  private SymphonyAuth symAuth;

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
  public CompanyCertDetail createCert(CompanyCert cert) throws ApiException {
    SecurityApi securityApi = new SecurityApi(apiClient);

    CompanyCertDetail response;
    try {
      response = securityApi.v2CompanycertCreatePost(symAuth.getSessionToken().getToken(), cert);
    } catch (ApiException e) {
      LOG.error("Create cert failed: ", e);
      throw new ApiException("Create cert failed: " + e.getStackTrace());
    }

    return response;
  }

  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
