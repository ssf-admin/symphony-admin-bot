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
import com.symphony.adminbot.clients.GoogleEmailClient;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.util.file.FileUtil;
import com.symphony.adminbot.util.template.MessageTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.InternalServerErrorException;

/**
 * Created by nick.tarsillo on 7/3/17.
 */
public class DeveloperEmailService {
  private static final Logger LOG = LoggerFactory.getLogger(DeveloperEmailService.class);
  private String subjectTemplate;
  private String messageTemplate;
  private GoogleEmailClient googleEmailClient;

  public DeveloperEmailService() {
    try {
      googleEmailClient = GoogleEmailClient.getInstance(
          BotConstants.ADMIN_BOT_NAME,
          System.getProperty(BotConfig.GMAIL_ADDRESS),
          System.getProperty(BotConfig.GOOGLE_SERVICE_ID),
          System.getProperty(BotConfig.GOOGLE_CRED_FILE));
      LOG.info("Gmail client is looking good.");

      subjectTemplate = FileUtil.readFile(System.getProperty(BotConfig.BOOTSTRAP_EMAIL_SUBJECT_TEMPLATE));
      LOG.info("Loaded subject template: " + subjectTemplate);

      messageTemplate = FileUtil.readFile(System.getProperty(BotConfig.BOOTSTRAP_EMAIL_MESSAGE_TEMPLATE));
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
  public void sendWelcomeEmail(DeveloperBootstrapState developerState, String developerPassword) {
    try {
      BootstrapTemplateData developerTemplateData = new BootstrapTemplateData(developerState);
      developerTemplateData.addPassword(developerPassword);
      MessageTemplate subTemplate = new MessageTemplate(subjectTemplate);
      MessageTemplate emailTemplate = new MessageTemplate(messageTemplate);

      String newSubject = subTemplate.buildFromData(developerTemplateData);
      String newEmail = emailTemplate.buildFromData(developerTemplateData);
      googleEmailClient.sendEmail(developerState.getDeveloper().getEmail(),  newSubject, newEmail);
      LOG.info("Sent welcome email to " + developerState.getDeveloper().getEmail() + ".");
    } catch(Exception e){
      LOG.error("Error occurred when sending confirmation email: ", e);
      throw new InternalServerErrorException(BotConstants.INTERNAL_ERROR);
    }
  }
}
