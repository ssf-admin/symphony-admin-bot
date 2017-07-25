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
