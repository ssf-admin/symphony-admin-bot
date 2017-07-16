package com.symphony.api.clients;

import com.symphony.api.agent.api.AttachmentsApi;
import com.symphony.api.agent.client.ApiClient;
import com.symphony.api.agent.client.ApiException;
import com.symphony.api.agent.client.Configuration;
import com.symphony.api.agent.model.AttachmentInfo;
import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.pod.model.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class AttachmentsClient {
  private final ApiClient apiClient;
  private SymphonyAuth symAuth;

  public AttachmentsClient(SymphonyAuth symAuth, String agentUrl) {
    this.symAuth = symAuth;

    //Get Service clients to query for userID.
    apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(agentUrl);
  }

  public List<AttachmentInfo> uploadAttachments(Stream stream, Set<File> attachments)
      throws ApiException {
    AttachmentsApi attachmentsApi = new AttachmentsApi(apiClient);

    List<AttachmentInfo> attachmentInfoList = new ArrayList<>();
    try {
      for (File file : attachments) {
        attachmentInfoList.add(attachmentsApi.v3StreamSidAttachmentCreatePost(stream.getId(),
            symAuth.getSessionToken().getToken(), file, symAuth.getKeyToken().getToken()));
      }
    } catch (ApiException e) {
      throw new ApiException("Upload attachments failed: " + e);
    }

    return attachmentInfoList;
  }

  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
