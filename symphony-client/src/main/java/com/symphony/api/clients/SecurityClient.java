/*
 * Copyright 2017 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.symphony.api.clients;


import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.pod.api.SecurityApi;
import com.symphony.api.pod.client.ApiClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.client.Configuration;
import com.symphony.api.pod.model.CompanyCert;
import com.symphony.api.pod.model.CompanyCertDetail;
import com.symphony.api.pod.model.CompanyCertType;

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
