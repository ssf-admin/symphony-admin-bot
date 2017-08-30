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
