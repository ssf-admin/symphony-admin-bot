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

package com.symphony.integrationtests.lib.config;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by nick.tarsillo on 8/27/17.
 */
public class IntegrationTestConfig {
  private static final Logger LOG = LoggerFactory.getLogger(IntegrationTestConfig.class);
  private static final Set<EnvironmentConfigProperty> PROPERTY_SET = new HashSet<>();

  public final static String CONFIG_DIR_ENV = "CONFIG_DIR";
  public final static String CONFIG_FILE_ENV = "CONFIG_FILE";
  public final static String AUTH_BASE_URL_ENV = "AUTH_BASE_URL";
  public final static String BOT_BASE_URL_ENV = "BOT_BASE_URL";
  public final static String BOT_TRUSTORE_FILE_ENV = "BOT_TRUSTORE_FILE";
  public final static String BOT_KEYSTORE_FILE_ENV = "BOT_KEYSTORE_FILE";
  public final static String BOT_TRUSTORE_PASSWORD_ENV = "BOT_TRUSTORE_PASSWORD";
  public final static String BOT_KEYSTORE_PASSWORD_ENV = "BOT_KEYSTORE_PASSWORD";
  //_____________________________Properties_____________________________//
  /**
   * Config
   */
  public final static String CONFIG_DIR = "test.config.dir";
  public final static String CONFIG_FILE = "test.properties.file";

  /**
   * Url
   */
  public final static String AUTH_BASE_URL = "auth.base.url";
  public final static String BOT_BASE_URL = "bot.base.url";

  /**
   * Cert
   */
  public final static String BOT_TRUSTORE_FILE = "bot.truststore.file";
  public final static String BOT_KEYSTORE_FILE = "bot.keystore.file";

  /**
   * Passwords
   */
  public final static String BOT_TRUSTORE_PASSWORD = "bot.truststore.password";
  public final static String BOT_KEYSTORE_PASSWORD = "bot.keystore.password";

  static {
    PROPERTY_SET.add(new EnvironmentConfigProperty(AUTH_BASE_URL_ENV, AUTH_BASE_URL));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOT_BASE_URL_ENV, BOT_BASE_URL));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOT_TRUSTORE_FILE_ENV, BOT_TRUSTORE_FILE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOT_KEYSTORE_FILE_ENV, BOT_KEYSTORE_FILE));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOT_TRUSTORE_PASSWORD_ENV, BOT_TRUSTORE_PASSWORD));
    PROPERTY_SET.add(new EnvironmentConfigProperty(BOT_KEYSTORE_PASSWORD_ENV, BOT_KEYSTORE_PASSWORD));
  }

  /**
   * Init properties and envs
   */
  public static void init(){
    String configDir = null;
    String propFile = null;

    Configuration c = null;
    try {
      if (propFile == null) {
        propFile = System.getProperty(CONFIG_FILE);
        if (propFile == null) {
          propFile = "test.properties";
        }
      }
      if (configDir == null) {
        configDir = System.getProperty(CONFIG_DIR);
        if (configDir == null) {
          configDir = Thread.currentThread().getContextClassLoader().getResource("config/" + propFile).getPath().replace(propFile, "");
        }
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
