package com.symphony.api.clients;

import com.symphony.api.clients.model.SymphonyAuth;

import org.symphonyoss.symphony.pod.api.StreamsApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.UserIdList;

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

  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
