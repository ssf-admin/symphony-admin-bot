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

package com.symphony.adminbot.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class BotConfig {
  private static final Logger LOG = LoggerFactory.getLogger(BotConfig.class);
  private static final Set<EnvironmentConfigProperty> PROPERTY_SET = new HashSet<>();

  //Env
  public final static String AUTH_TRUSTSTORE_PASSWORD_ENV = "AUTH_TRUSTSTORE_PASSWORD";
  public final static String AUTH_KEYSTORE_PASSWORD_ENV = "AUTH_KEYSTORE_PASSWORD";
  public final static String BOT_KEYSTORE_FILE_NAME_ENV = "BOT_KEYSTORE_FILE_NAME";
  public final static String BOT_KEYSTORE_PASSWORD_ENV = "BOT_KEYSTORE_PASSWORD";
  public final static String KEYS_PASSWORD_FILE_ENV = "KEYS_PASSWORD_FILE";
  public final static String SESSIONAUTH_URL_ENV = "SESSION_AUTH";
  public final static String KEYAUTH_URL_ENV = "KEY_AUTH";
  public final static String SYMPHONY_POD_ENV = "SYMPHONY_POD";
  public final static String SYMPHONY_AGENT_ENV = "SYMPHONY_AGENT";
  public final static String CERTS_DIR_ENV = "CERTS";
  public final static String AUTH_KEYSTORE_FILE_ENV = "AUTH_KEYSTORE_FILE";
  public final static String AUTH_TRUSTSTORE_FILE_ENV = "AUTH_TRUSTSTORE_FILE";
  public final static String GOOGLE_CRED_FILE_ENV = "GOOGLE_CRED";
  public final static String GOOGLE_SERVICE_ID_ENV = "GOOGLE_SERVICE_ID";
  public final static String GMAIL_ADDRESS_ENV = "GMAIL_ADDRESS";
  public final static String DEVELOPER_JSON_DIR_ENV = "DEVELOPER_JSON_DIR";
  public final static String USER_JSON_DIR_ENV = "USER_JSON_DIR";
  public final static String DEVELOPER_P12_DIR_ENV = "DEVELOPER_P12_DIR";
  public static final String BOOTSTRAP_EMAIL_SUBJECT_TEMPLATE_ENV = "BOOTSTRAP_EMAIL_SUBJECT_TEMPLATE";
  public static final String BOOTSTRAP_EMAIL_MESSAGE_TEMPLATE_ENV = "BOOTSTRAP_EMAIL_MESSAGE_TEMPLATE";
  public static final String BOOTSTRAP_MESSAGE_DIRECTIONAL_TEMPLATE_ENV = "BOOTSTRAP_MESSAGE_DIRECTIONAL_TEMPLATE";
  public static final String BOOTSTRAP_MESSAGE_WELCOME_TEMPLATE_ENV = "BOOTSTRAP_MESSAGE_WELCOME_TEMPLATE";
  public static final String BOOTSTRAP_ICON_URL_TEMPLATE_ENV = "BOOTSTRAP_ICON_URL_TEMPLATE";
  public static final String BOOTSTRAP_BOT_ID_ENV = "BOOTSTRAP_BOT_ID";
  public static final String AUTH_PORT_ENV = "AUTH_PORT";
  public static final String SWAGGER_PORT_ENV = "SWAGGER_PORT";

  //_____________________________Properties_____________________________//
  /**
   * Config
   */
  public final static String CONFIG_DIR = "bot.config.dir";
  public final static String CONFIG_FILE = "bot.properties";

  /**
   * Passwords
   */
  public final static String BOT_KEYSTORE_PASSWORD = "bot.keystore.password";
  public final static String AUTH_KEYSTORE_PASSWORD = "auth.keystore.password";
  public final static String AUTH_TRUSTSTORE_PASSWORD = "auth.truststore.password";
  public final static String KEYS_PASSWORD_FILE = "keys.password.file";

  /**
   * URLS
   */
  public final static String SESSIONAUTH_URL = "sessionauth.url";
  public final static String KEYAUTH_URL = "keyauth.url";
  public final static String SYMPHONY_POD = "symphony.agent.pod.url";
  public final static String SYMPHONY_AGENT = "symphony.agent.agent.url";

  /**
   * DIRECTORIES
   */
  public final static String CERTS_DIR = "certs.dir";
  public final static String DEVELOPER_JSON_DIR = "adminbot.developer.json.dir";
  public final static String DEVELOPER_P12_DIR = "adminbot.developer.p12.dir";
  public final static String USER_JSON_DIR = "adminbot.user.json.dir";

  /**
   * FILES
   */
  public final static String BOT_KEYSTORE_FILE_NAME = "bot.keystore.file.name";
  public final static String AUTH_KEYSTORE_FILE = "auth.keystore.file";
  public final static String AUTH_TRUSTSTORE_FILE = "auth.truststore.file";
  public final static String GOOGLE_CRED_FILE = "google.cred.file";
  public final static String BOOTSTRAP_EMAIL_SUBJECT_TEMPLATE = "adminbot.bootstrap.email.subject.template";
  public final static String BOOTSTRAP_EMAIL_MESSAGE_TEMPLATE = "adminbot.bootstrap.email.message.template";
  public final static String BOOTSTRAP_MESSAGE_DIRECTIONAL_TEMPLATE = "adminbot.bootstrap.message.directional.template";
  public final static String BOOTSTRAP_MESSAGE_WELCOME_TEMPLATE = "adminbot.bootstrap.message.welcome.template";
  public final static String BOOTSTRAP_ICON_URL_TEMPLATE = "adminbot.bootstrap.icon.url.template";

  /**
   * IDs
   */
  public final static String GOOGLE_SERVICE_ID = "google.service.id";
  public final static String BOOTSTRAP_BOT_ID = "adminbot.bootstrap.bot.id";

  /**
   * EMAILS
   */
  public final static String GMAIL_ADDRESS = "adminbot.gmail.address";

  /**
   * Ports
   */
  public final static String AUTH_PORT = "adminbot.auth.port";
  public final static String SWAGGER_PORT = "adminbot.swagger.port";

  static {
    PROPERTY_SET.add(new EnvironmentConfigProperty(AUTH_KEYSTORE_PASSWORD_ENV,
        AUTH_KEYSTORE_PASSWORD));
    PROPERTY_SET.add(new EnvironmentConfigProperty(AUTH_TRUSTSTORE_PASSWORD_ENV,
        AUTH_TRUSTSTORE_PASSWORD));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOT_KEYSTORE_FILE_NAME_ENV,
        BOT_KEYSTORE_FILE_NAME));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOT_KEYSTORE_PASSWORD_ENV, BOT_KEYSTORE_PASSWORD));
    PROPERTY_SET.add(new EnvironmentConfigProperty(KEYS_PASSWORD_FILE_ENV, KEYS_PASSWORD_FILE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(SESSIONAUTH_URL_ENV, SESSIONAUTH_URL));
    PROPERTY_SET.add(new EnvironmentConfigProperty(KEYAUTH_URL_ENV, KEYAUTH_URL));
    PROPERTY_SET.add(new EnvironmentConfigProperty(SYMPHONY_POD_ENV, SYMPHONY_POD));
    PROPERTY_SET.add(new EnvironmentConfigProperty(SYMPHONY_AGENT_ENV, SYMPHONY_AGENT));
    PROPERTY_SET.add(new EnvironmentConfigProperty(CERTS_DIR_ENV, CERTS_DIR));
    PROPERTY_SET.add(new EnvironmentConfigProperty(AUTH_KEYSTORE_FILE_ENV, AUTH_KEYSTORE_FILE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(AUTH_TRUSTSTORE_FILE_ENV, AUTH_TRUSTSTORE_FILE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(GOOGLE_CRED_FILE_ENV, GOOGLE_CRED_FILE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(GOOGLE_SERVICE_ID_ENV, GOOGLE_SERVICE_ID));
    PROPERTY_SET.add(new EnvironmentConfigProperty(GMAIL_ADDRESS_ENV, GMAIL_ADDRESS));
    PROPERTY_SET.add(new EnvironmentConfigProperty(DEVELOPER_JSON_DIR_ENV, DEVELOPER_JSON_DIR));
    PROPERTY_SET.add(new EnvironmentConfigProperty(USER_JSON_DIR_ENV, USER_JSON_DIR));
    PROPERTY_SET.add(new EnvironmentConfigProperty(DEVELOPER_P12_DIR_ENV, DEVELOPER_P12_DIR));
    PROPERTY_SET.add(new EnvironmentConfigProperty(AUTH_PORT_ENV, AUTH_PORT));
    PROPERTY_SET.add(new EnvironmentConfigProperty(SWAGGER_PORT_ENV, SWAGGER_PORT));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOOTSTRAP_BOT_ID_ENV, BOOTSTRAP_BOT_ID));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOOTSTRAP_ICON_URL_TEMPLATE_ENV,
        BOOTSTRAP_ICON_URL_TEMPLATE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOOTSTRAP_EMAIL_SUBJECT_TEMPLATE_ENV,
        BOOTSTRAP_EMAIL_SUBJECT_TEMPLATE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOOTSTRAP_EMAIL_MESSAGE_TEMPLATE_ENV,
        BOOTSTRAP_EMAIL_MESSAGE_TEMPLATE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOOTSTRAP_MESSAGE_DIRECTIONAL_TEMPLATE_ENV,
        BOOTSTRAP_MESSAGE_DIRECTIONAL_TEMPLATE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOOTSTRAP_MESSAGE_WELCOME_TEMPLATE_ENV,
        BOOTSTRAP_MESSAGE_WELCOME_TEMPLATE));
  }

  /**
   * Init properties and envs
   */
  public static void init(){
    String configDir = null;
    String propFile = null;

    Configuration c = null;
    try {
      if (configDir == null) {
        configDir = System.getProperty(CONFIG_DIR);
        if (configDir == null) {
          configDir = "com/symphony/adminbot/quickstart";
        }
      }

      if (propFile == null) {
        propFile = CONFIG_FILE;
      }
      propFile = configDir + "/" + propFile;

      LOG.info("Using bot.properties file location: {}", propFile);

      c = new PropertiesConfiguration(propFile);

      for(EnvironmentConfigProperty property: PROPERTY_SET){
        property.initProperty(c);
      }
    } catch (ConfigurationException e) {
      LOG.error("Configuration init exception: ", e);
    }
  }

  /**
   * If env exists, use env, otherwise use default config property
   */
  static class EnvironmentConfigProperty {
    private String envName;
    private String propertyName;

    EnvironmentConfigProperty(String envName, String propertyName){
      this.envName = envName;
      this.propertyName = propertyName;
    }

    void initProperty(Configuration configuration){
      if (System.getProperty(propertyName) == null) {
        if (System.getenv(envName) != null) {
          System.setProperty(propertyName, System.getenv(envName));
        } else {
          System.setProperty(propertyName, configuration.getString(propertyName));
        }
      }
    }
  }

}
