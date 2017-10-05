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
import com.symphony.api.pod.model.RoomSearchCriteria;
import com.symphony.api.pod.model.RoomSearchResults;
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.SuccessResponse;
import com.symphony.api.pod.model.UserId;
import com.symphony.api.pod.model.UserIdList;
import com.symphony.api.pod.model.V2RoomAttributes;
import com.symphony.api.pod.model.V2RoomDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class StreamsClient {
  private final Logger LOG = LoggerFactory.getLogger(ApplicationClient.class);

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

  public V2RoomDetail createRoom(V2RoomAttributes roomCreate) throws ApiException {
    StreamsApi streamsApi = new StreamsApi(apiClient);

    V2RoomDetail roomDetail = null;
    try {
      roomDetail = streamsApi.v2RoomCreatePost(roomCreate, symAuth.getSessionToken().getToken());
    } catch (ApiException e) {
      throw new ApiException("Could not create room: " + e.getStackTrace());
    }

    return roomDetail;
  }

  public V2RoomDetail getRoomByName(String roomName) throws ApiException {
    StreamsApi streamsApi = new StreamsApi(apiClient);

    RoomSearchResults results = null;
    try {
      RoomSearchCriteria roomSearchCriteria = new RoomSearchCriteria();
      roomSearchCriteria.setQuery(roomName);
      results = streamsApi.v2RoomSearchPost(symAuth.getSessionToken().getToken(), roomSearchCriteria, 0, 1);
    } catch (ApiException e) {
      throw new ApiException("Could not search for room: " + e.getStackTrace());
    }

    LOG.info("Room search results: " + results);

    if (!results.getRooms().isEmpty()
        && results.getRooms().get(0).getRoomAttributes().getName().equals(roomName)) {
      return results.getRooms().get(0);
    } else {
      return null;
    }
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
