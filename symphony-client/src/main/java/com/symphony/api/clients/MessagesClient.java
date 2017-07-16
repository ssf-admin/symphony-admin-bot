package com.symphony.api.clients;

import com.symphony.api.agent.api.MessagesApi;
import com.symphony.api.agent.client.ApiClient;
import com.symphony.api.agent.client.ApiException;
import com.symphony.api.agent.client.Configuration;
import com.symphony.api.agent.model.V2Message;
import com.symphony.api.agent.model.V2MessageSubmission;
import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.pod.model.Stream;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class MessagesClient {
  private final ApiClient apiClient;
  private SymphonyAuth symAuth;

  public MessagesClient(SymphonyAuth symAuth, String agentUrl) {
    this.symAuth = symAuth;

    //Get Service clients to query for userID.
    apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(agentUrl);
  }

  /**
   * Send message to stream
   * @param stream Stream to send message to
   * @param message Message to send
   * @return Message sent
   */
  public V2Message sendMessage(Stream stream, V2Message message,
      V2MessageSubmission.FormatEnum formatEnum) throws ApiException {
    if (stream == null || message == null) {
      throw new NullPointerException("Stream or message submission was not provided..");
    }

    MessagesApi messagesApi = new MessagesApi(apiClient);

    V2MessageSubmission messageSubmission = new V2MessageSubmission();

    messageSubmission.setMessage(message.getMessage());
    messageSubmission.setFormat(formatEnum);
    messageSubmission.setAttachments(message.getAttachments());

    V2Message v2Message;
    try {
      v2Message = messagesApi.v3StreamSidMessageCreatePost(stream.getId(),
          symAuth.getSessionToken().getToken(),
          messageSubmission, symAuth.getKeyToken().getToken());
    } catch (ApiException e) {
      throw new ApiException("Message send failed: " + e);
    }

    return v2Message;
  }

  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
