package com.symphony.adminbot.bootstrap.service;

import com.symphony.adminbot.bootstrap.model.DeveloperBootstrapState;
import com.symphony.adminbot.bootstrap.model.template.BootstrapTemplateData;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.util.file.FileUtil;
import com.symphony.adminbot.util.template.MessageTemplate;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.agent.model.V2Message;
import com.symphony.api.agent.model.V2MessageSubmission;
import com.symphony.api.clients.MessagesClient;
import com.symphony.api.clients.StreamsClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.model.ImmutableRoomAttributes;
import com.symphony.api.pod.model.RoomAttributes;
import com.symphony.api.pod.model.RoomCreate;
import com.symphony.api.pod.model.RoomDetail;
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.UserId;
import com.symphony.api.pod.model.UserIdList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

  public RoomDetail createDeveloperRoom(DeveloperBootstrapState bootstrapState, UserIdList userIdList) throws ApiException {
    RoomCreate roomCreate = new RoomCreate();
    RoomAttributes roomAttributes = new RoomAttributes();
    roomAttributes.setDescription("Room for developers to collaborate.");
    roomAttributes.setName(" Team Development Room ("
        + bootstrapState.getUserDetail().getUserAttributes().getUserName() + ")");

    roomAttributes.setDiscoverable(false);
    roomAttributes.setMembersCanInvite(true);
    roomCreate.setRoomAttributes(roomAttributes);

    RoomDetail roomDetail = streamsClient.createRoom(roomCreate);

    for(Long uid : userIdList) {
      UserId userId = new UserId();
      userId.setId(uid);
      streamsClient.addMemberToRoom(roomDetail.getRoomSystemInfo().getId(), userId);
    }

    return roomDetail;
  }
}
