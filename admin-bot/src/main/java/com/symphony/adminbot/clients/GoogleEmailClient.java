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

package com.symphony.adminbot.clients;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by nick.tarsillo on 7/3/17.
 */
public class GoogleEmailClient {
  private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  private static LoadingCache<String, GoogleEmailClient> clientLoadingCache;

  private Gmail service;
  private String from;

  public static GoogleEmailClient getInstance(String appName, String email, String serviceId,
      String credPath) throws ExecutionException {
    if (clientLoadingCache == null) {
      clientLoadingCache = CacheBuilder.newBuilder().build(
          new CacheLoader<String, GoogleEmailClient>() {
            @Override
            public GoogleEmailClient load(String s) throws Exception {
              return new GoogleEmailClient(appName, email, serviceId, credPath);
            }
          });
    }

    return clientLoadingCache.get(serviceId);
  }

  /**
   * Authenticate with gmail api's and create gmail service
   */
  public GoogleEmailClient(String appName, String email, String serviceId, String credPath)
      throws GeneralSecurityException, IOException {
    this.from = email;
    Set<String> scopes = new HashSet<>();
    scopes.add(GmailScopes.GMAIL_SEND);
    scopes.add(GmailScopes.GMAIL_COMPOSE);
    scopes.add(GmailScopes.GMAIL_MODIFY);
    scopes.add(GmailScopes.MAIL_GOOGLE_COM);

    HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    GoogleCredential credential = new GoogleCredential.Builder()
        .setTransport(httpTransport)
        .setJsonFactory(JSON_FACTORY)
        .setServiceAccountId(serviceId)
        .setServiceAccountPrivateKeyFromP12File(new File(credPath))
        .setServiceAccountScopes(scopes)
        .setServiceAccountUser(email)
        .build();

    credential.refreshToken();

    service = new Gmail.Builder(httpTransport,
        JSON_FACTORY, credential).setApplicationName(appName).build();
  }

  /**
   * Create a MimeMessage using the parameters provided.
   *
   * @param to email address of the receiver
   * @param subject subject of the email
   * @param bodyText body text of the email
   * @return the MimeMessage to be used to send email
   */
  public MimeMessage createEmail(String to, String subject, String bodyText)
      throws MessagingException {
    Properties props = new Properties();
    Session session = Session.getDefaultInstance(props, null);

    MimeMessage email = new MimeMessage(session);

    email.setFrom(new InternetAddress(from));
    email.addRecipient(javax.mail.Message.RecipientType.TO,
        new InternetAddress(to));
    email.setSubject(subject);
    email.setText(bodyText);
    return email;
  }


  /**
   * Create a message from an email.
   *
   * @param emailContent Email to be set to raw of message
   * @return a message containing a base64url encoded email
   */
  public Message createMessageWithEmail(MimeMessage emailContent)
      throws MessagingException, IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    emailContent.writeTo(buffer);
    byte[] bytes = buffer.toByteArray();
    String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
    Message message = new Message();
    message.setRaw(encodedEmail);
    return message;
  }

  /**
   * Sends a email
   * @param recipientEmail the email to send to
   * @param subject the subject of the email
   * @param message the message to send in the body of the email
   */
  public void sendEmail(String recipientEmail, String subject, String message)
      throws IOException, MessagingException {
    Message m = createMessageWithEmail(createEmail(recipientEmail, subject, message));
    service.users().messages().send(from, m).execute();
  }
}
