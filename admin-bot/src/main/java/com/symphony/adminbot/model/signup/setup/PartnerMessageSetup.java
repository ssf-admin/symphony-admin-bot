package com.symphony.adminbot.model.signup.setup;

import com.symphony.clients.MessagesClient;
import com.symphony.clients.StreamsClient;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.util.template.MessageTemplate;
import com.symphony.adminbot.model.signup.PartnerState;
import com.symphony.adminbot.model.signup.template.PartnerTemplateData;
import com.symphony.adminbot.util.file.FileUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.agent.model.V2Message;
import org.symphonyoss.symphony.agent.model.V2MessageSubmission;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.UserIdList;

import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 7/3/17.
 */
public class PartnerMessageSetup {
  private static final Logger LOG = LoggerFactory.getLogger(PartnerMessageSetup.class);

  private MessagesClient messagesClient;
  private StreamsClient streamsClient;

  private String directionalUrl;
  private String directionalMessage;
  private String welcomeMessage;

  public PartnerMessageSetup(MessagesClient messagesClient, StreamsClient streamsClient){
    this.messagesClient = messagesClient;
    this.streamsClient = streamsClient;

    this.directionalUrl = System.getProperty(BotConfig.DIRECTIONAL_URL);

    try {
      this.directionalMessage =
          FileUtil.readFile(System.getProperty(BotConfig.MESSAGE_DIRECTIONAL_TEMPLATE));
      LOG.info("Loaded directional message template: " + directionalMessage);
      this.welcomeMessage =
          FileUtil.readFile(System.getProperty(BotConfig.MESSAGE_WELCOME_TEMPLATE));
      LOG.info("Loaded welcome message template: " + welcomeMessage);
    } catch (Exception e){
      LOG.error("Error occurred when loading message templates: ", e);
    }
  }

  /**
   * Sends a directional message to the symphony user
   * Should specify how to reset password and finish sign up process
   * @param partnerState the current state of the partner in the sign up process
   */
  public void sendDirectionalMessage(PartnerState partnerState) {
    try {
      PartnerTemplateData partnerTemplateData =
          new PartnerTemplateData(partnerState.getPartner(), null, null,
              directionalUrl);
      MessageTemplate partnerDocumentTemplate = new MessageTemplate(directionalMessage);
      String message = partnerDocumentTemplate.buildFromData(partnerTemplateData);

      Long partnerId = partnerState.getUserDetail().getUserSystemInfo().getId();
      UserIdList userIdList = new UserIdList();
      userIdList.add(partnerId);

      V2Message v2Message = new V2Message();
      v2Message.setMessage(message);

      Stream stream = streamsClient.createIM(userIdList);
      partnerState.setPartnerIM(stream);
      messagesClient.sendMessage(stream, v2Message, V2MessageSubmission.FormatEnum.MESSAGEML);
    } catch (Exception e) {
      LOG.error("Error occurred when sending directional message: ", e);
      throw new InternalServerErrorException(BotConstants.INTERNAL_ERROR);
    }
  }

  /**
   * Sends message containing bootstrap package.
   * @param partnerState the current state of the user in the sign up process
   */
  public void sendBootstrapMessage(PartnerState partnerState) {
    try {
      PartnerTemplateData partnerTemplateData = new PartnerTemplateData(partnerState.getPartner(),
          partnerState.getPartnerSignUpForm(), null, System.getProperty(BotConfig.DIRECTIONAL_URL));
      MessageTemplate partnerDocumentTemplate = new MessageTemplate(welcomeMessage);
      String message = partnerDocumentTemplate.buildFromData(partnerTemplateData);

      Long partnerId = partnerState.getUserDetail().getUserSystemInfo().getId();
      UserIdList userIdList = new UserIdList();
      userIdList.add(partnerId);

      V2Message v2Message = new V2Message();
      v2Message.setMessage(message);

      messagesClient.sendMessage(partnerState.getPartnerIM(), v2Message, V2MessageSubmission.FormatEnum.MESSAGEML);
    } catch (Exception e) {
      LOG.error("Error occurred when sending directional message: ", e);
      throw new InternalServerErrorException(BotConstants.INTERNAL_ERROR);
    }
  }
}
