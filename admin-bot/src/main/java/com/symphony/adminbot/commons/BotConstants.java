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

package com.symphony.adminbot.commons;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class BotConstants {
  //For error messages
  public static final String NOT_ENTITLED = "User is not entitled to use these endpoints.";
  public static final String INTERNAL_ERROR = "Internal server error.";
  public static final String NO_CERT = "Please provide a cert with request.";

  //Symphony API Errors
  public static final String USERS_EXIST = "Could not create user: {\"code\":0,\"message\":\"User already exists.\"}";

  //For cert generation
  public static final String BOT_USERNAME = "BotUser";

  //Cache
  public static final int VALID_DURATION = 7200;
  public static final long EXPIRE_TIME_DAYS = 1;
  public static final long MANAGER_EXPIRE_MINUTES = 30;

  //For Email Confirmation
  public static final String ADMIN_BOT_NAME = "Admin Bot";

  //Success Messages
  public static final String DEVELOPER_WELCOME_SUCCESS = "Developer welcome succeeded.";

  //Bootstrap Error Messages
  public static final String BOT_APP_EXISTS = "Bot or app already exists.";
  public static final String DEVELOPER_REQUIRED = "All developer fields are required.";
  public static final String APP_NAME_REQUIRED = "App name is required.";
  public static final String APP_DESCRIPTION_REQUIRED = "App description is required.";
  public static final String APP_DOMAIN_REQUIRED = "App domain is required.";
  public static final String APP_COMPANY_REQUIRED = "App company name is required.";
  public static final String APP_URL_REQUIRED = "App url is required.";
  public static final String BOT_EMAIL_REQUIRED = "Bot email is required.";
  public static final String BOT_NAME_REQUIRED = "Bot name is required.";
  public static final String ICON_START_WITH_HTTPS = "Icon url must start with \"https://\".";
  public static final String APP_START_WITH_HTTPS = "App url must start with \"https://\".";
  public static final String DOMAIN_MUST_MATCH = "Domain must match app url domain.";
  public static final String DEVELOPER_NOT_FOUND = "Developer not found.";
  public static final String DUPLICATE_DEVELOPER = "Developers cannot have same emails.";
  public static final String DEVELOPER_EXISTS = "One or more developers already exists on pod.";
  public static final String INVALID_APP_ICON_URL = "The app icon url is invalid.";
  public static final String INVALID_APP_URL = "The app url is invalid.";
  public static final String BOT_DEVELOPER_EMAIL_SAME = "The bot email must be different than developers' emails.";
}
