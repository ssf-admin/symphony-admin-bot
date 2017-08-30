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
import com.symphony.api.pod.api.RoomMembershipApi;
import com.symphony.api.pod.api.StreamsApi;
import com.symphony.api.pod.client.ApiClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.client.Configuration;
import com.symphony.api.pod.model.RoomCreate;
import com.symphony.api.pod.model.RoomDetail;
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.SuccessResponse;
import com.symphony.api.pod.model.UserId;
import com.symphony.api.pod.model.UserIdList;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class StreamsClient {
  private final ApiClient apiClient;
  private SymphonyAuth symAuth;

  public StreamsClient(SymphonyAuth symAuth, String serviceUrl) {
    this.symAuth = symAuth;

    //Get Service clients to query for userID.
    apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(serviceUrl);

    apiClient.addDefaultHeader(symAuth.getSessionToken().getName(), symAuth.getSessionToken().getToken());
    apiClient.addDefaultHeader(symAuth.getKeyToken().getName(), symAuth.getKeyToken().getToken());
  }

  public Stream createIM(UserIdList userIdList) throws ApiException {
    StreamsApi streamsApi = new StreamsApi(apiClient);

    Stream stream = null;
    try {
      stream = streamsApi.v1ImCreatePost(userIdList, symAuth.getSessionToken().getToken());
    } catch (ApiException e) {
      throw new ApiException("Could not create IM: " + e.getStackTrace());
    }

    return stream;
  }

  public RoomDetail createRoom(RoomCreate roomCreate) throws ApiException {
    StreamsApi streamsApi = new StreamsApi(apiClient);

    RoomDetail roomDetail = null;
    try {
      roomDetail = streamsApi.v1RoomCreatePost(roomCreate, symAuth.getSessionToken().getToken());
    } catch (ApiException e) {
      throw new ApiException("Could not create room: " + e.getStackTrace());
    }

    return roomDetail;
  }

  public SuccessResponse addMemberToRoom(String id, UserId payload) throws ApiException {
    RoomMembershipApi streamsApi = new RoomMembershipApi(apiClient);

    SuccessResponse successResponse = null;
    try {
      successResponse = streamsApi.v1RoomIdMembershipAddPost(id, payload, symAuth.getSessionToken().getToken());
    } catch (ApiException e) {
      throw new ApiException("Could not create room: " + e.getStackTrace());
    }

    return successResponse;
  }


  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
