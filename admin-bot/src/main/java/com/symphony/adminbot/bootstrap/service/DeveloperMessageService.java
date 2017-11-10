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

package com.symphony.adminbot.bootstrap.service;

import com.symphony.adminbot.bootstrap.model.DeveloperBootstrapState;
import com.symphony.adminbot.bootstrap.model.template.BootstrapTemplateData;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.util.file.FileUtil;
import com.symphony.adminbot.util.template.MessageTemplate;
import com.symphony.api.agent.model.V2Message;
import com.symphony.api.agent.model.V2MessageSubmission;
import com.symphony.api.clients.MessagesClient;
import com.symphony.api.clients.StreamsClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.UserId;
import com.symphony.api.pod.model.UserIdList;
import com.symphony.api.pod.model.V2RoomAttributes;
import com.symphony.api.pod.model.V2RoomDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 7/3/17.
 */
public class DeveloperMessageService {
  private static final Logger LOG = LoggerFactory.getLogger(DeveloperMessageService.class);

  private MessagesClient messagesClient;
  private StreamsClient streamsClient;

  private String directionalMessage;
  private String welcomeMessage;

  public DeveloperMessageService(MessagesClient messagesClient, StreamsClient streamsClient){
    this.messagesClient = messagesClient;
    this.streamsClient = streamsClient;

    try {
      this.directionalMessage =
          FileUtil.readFile(System.getProperty(BotConfig.BOOTSTRAP_MESSAGE_DIRECTIONAL_TEMPLATE));
      LOG.info("Loaded directional message template: " + directionalMessage);
      this.welcomeMessage =
          FileUtil.readFile(System.getProperty(BotConfig.BOOTSTRAP_MESSAGE_WELCOME_TEMPLATE));
      LOG.info("Loaded welcome message template: " + welcomeMessage);
    } catch (Exception e){
      LOG.error("Error occurred when loading message templates: ", e);
    }
  }

  /**
   * Sends a directional message to the symphony user
   * Should specify how to reset password and finish sign up process
   * @param developerState the current state of the partner in the sign up process
   */
  public void sendDirectionalMessage(DeveloperBootstrapState developerState) {
    try {
      BootstrapTemplateData developerTemplateData = new BootstrapTemplateData(developerState.getDeveloper());
      MessageTemplate partnerDocumentTemplate = new MessageTemplate(directionalMessage);
      String message = partnerDocumentTemplate.buildFromData(developerTemplateData);

      Long partnerId = developerState.getUserDetail().getUserSystemInfo().getId();
      UserIdList userIdList = new UserIdList();
      userIdList.add(partnerId);

      V2Message v2Message = new V2Message();
      v2Message.setMessage(message);

      Stream stream = streamsClient.createIM(userIdList);
      developerState.setDeveloperIM(stream);
      messagesClient.sendMessage(stream, v2Message, V2MessageSubmission.FormatEnum.MESSAGEML);
      LOG.info("Sent directional message to user " + developerState.getUserDetail().getUserAttributes().getUserName() + ".");
    } catch (Exception e) {
      LOG.error("Error occurred when sending directional message: ", e);
      throw new InternalServerErrorException(BotConstants.INTERNAL_ERROR);
    }
  }

  /**
   * Sends message containing bootstrap package.
   * If developer room is null, will send to developer IM instead.
   * @param developerState the current state of the user in the sign up process
   */
  public void sendBootstrapMessage(DeveloperBootstrapState developerState) {
    try {
      BootstrapTemplateData developerTemplateData = new BootstrapTemplateData(developerState);
      MessageTemplate partnerDocumentTemplate = new MessageTemplate(welcomeMessage);
      String message = partnerDocumentTemplate.buildFromData(developerTemplateData);

      Long partnerId = developerState.getUserDetail().getUserSystemInfo().getId();
      UserIdList userIdList = new UserIdList();
      userIdList.add(partnerId);

      V2Message v2Message = new V2Message();
      v2Message.setMessage(message);
      v2Message.setAttachments(developerState.getCertAttachmentInfo());

      Stream stream = developerState.getDeveloperIM();
      if(developerState.getDeveloperRoom() != null) {
        stream = developerState.getDeveloperRoom();
      }

      messagesClient.sendMessage(stream, v2Message, V2MessageSubmission.FormatEnum.MESSAGEML);
      LOG.info("Sent bootstrap message to user " + developerState.getUserDetail().getUserAttributes().getUserName() + ".");
    } catch (Exception e) {
      LOG.error("Error occurred when sending directional message: ", e);
      throw new InternalServerErrorException(BotConstants.INTERNAL_ERROR);
    }
  }

  /**
   * Creates room for all developers specified in sign up form. (Bot is also in room.)
   * @param roomName the name to create the room with
   * @param userIdList the list of user id's to add to the room
   * @return details about the room
   * @throws ApiException
   */
  public V2RoomDetail createDeveloperRoom(String roomName, UserIdList userIdList) throws ApiException {
    V2RoomAttributes roomAttributes = new V2RoomAttributes();
    roomAttributes.setDescription("Room for developers to collaborate.");
    roomAttributes.setName(roomName);

    roomAttributes.setDiscoverable(false);
    roomAttributes.setMembersCanInvite(true);

    V2RoomDetail roomDetail = streamsClient.createRoom(roomAttributes);

    for(Long uid : userIdList) {
      UserId userId = new UserId();
      userId.setId(uid);
      streamsClient.addMemberToRoom(roomDetail.getRoomSystemInfo().getId(), userId);
    }

    return roomDetail;
  }

  public void addDevelopersToTeamRoom(String teamRoomId, Set<DeveloperBootstrapState> developerSet) throws ApiException {
    for(DeveloperBootstrapState developerState: developerSet) {
      UserId userId = new UserId();
      userId.setId(developerState.getUserDetail().getUserSystemInfo().getId());
      streamsClient.addMemberToRoom(teamRoomId, userId);
    }
  }

  public String getTeamRoomId(String roomName) throws ApiException {
    V2RoomDetail v2RoomDetail = streamsClient.getRoomByName(roomName);

    if(v2RoomDetail != null) {
      return v2RoomDetail.getRoomSystemInfo().getId();
    } else {
      return null;
    }
  }

  public void setDeveloperStream(DeveloperBootstrapState developerBootstrapState) throws ApiException {
    Long partnerId = developerBootstrapState.getUserDetail().getUserSystemInfo().getId();
    UserIdList userIdList = new UserIdList();
    userIdList.add(partnerId);

    Stream stream = streamsClient.createIM(userIdList);
    developerBootstrapState.setDeveloperIM(stream);
  }
}
