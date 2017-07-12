package com.symphony.adminbot.model.bootstrap.setup;

import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.model.bootstrap.DeveloperState;
import com.symphony.adminbot.model.bootstrap.template.DeveloperTemplateData;
import com.symphony.adminbot.util.file.FileUtil;
import com.symphony.adminbot.util.template.MessageTemplate;

import com.symphony.adminbot.clients.GoogleEmailClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 7/3/17.
 */
public class DeveloperConfirmationSetup {
  private static final Logger LOG = LoggerFactory.getLogger(DeveloperConfirmationSetup.class);
  private String subjectTemplate;
  private String messageTemplate;
  private GoogleEmailClient googleEmailClient;

  public DeveloperConfirmationSetup() {
    try {
      googleEmailClient = GoogleEmailClient.getInstance(
          BotConstants.ADMIN_BOT_NAME,
          System.getProperty(BotConfig.GMAIL_ADDRESS),
          System.getProperty(BotConfig.GOOGLE_SERVICE_ID),
          System.getProperty(BotConfig.GOOGLE_CRED_FILE));
      LOG.info("Gmail client is looking good.");

      subjectTemplate = FileUtil.readFile(System.getProperty(BotConfig.EMAIL_SUBJECT_TEMPLATE));
      LOG.info("Loaded subject template: " + subjectTemplate);

      messageTemplate = FileUtil.readFile(System.getProperty(BotConfig.EMAIL_MESSAGE_TEMPLATE));
      LOG.info("Loaded message template: " + messageTemplate);
    }catch(Exception e){
      LOG.error("Developer email confirmation setup failed: ", e);
    }
  }

  /**
   * Sends welcome email
   * Should contain username and temporary password
   * @param developerState the current state of the partner in the sign up process
   */
  public void sendWelcomeEmail(DeveloperState developerState) {
    try {
      String url = System.getProperty(BotConfig.POD_URL);
      DeveloperTemplateData developerTemplateData = new DeveloperTemplateData(developerState.getDeveloper(),
          developerState.getDeveloperSignUpForm(), developerState.getPassword(), url);
      MessageTemplate subTemplate = new MessageTemplate(subjectTemplate);
      MessageTemplate emailTemplate = new MessageTemplate(messageTemplate);

      String newSubject = subTemplate.buildFromData(developerTemplateData);
      String newEmail = emailTemplate.buildFromData(developerTemplateData);
      googleEmailClient.sendEmail(developerState.getDeveloper().getEmail(),  newSubject, newEmail);
    } catch(Exception e){
      LOG.error("Error occurred when sending confirmation email: ", e);
      throw new InternalServerErrorException(BotConstants.INTERNAL_ERROR);
    }
  }
}
