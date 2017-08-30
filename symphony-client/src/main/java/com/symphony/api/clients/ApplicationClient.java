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
