package com.symphony.clients;

import com.symphony.clients.model.SymphonyAuth;

import org.symphonyoss.symphony.agent.api.AttachmentsApi;
import org.symphonyoss.symphony.agent.invoker.ApiClient;
import org.symphonyoss.symphony.agent.invoker.ApiException;
import org.symphonyoss.symphony.agent.invoker.Configuration;
import org.symphonyoss.symphony.agent.model.AttachmentInfo;
import org.symphonyoss.symphony.pod.model.Stream;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class AttachmentsClient {
  private final ApiClient apiClient;
  private final SymphonyAuth symAuth;

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
}
