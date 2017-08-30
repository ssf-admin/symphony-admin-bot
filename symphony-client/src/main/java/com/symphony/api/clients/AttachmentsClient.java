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
