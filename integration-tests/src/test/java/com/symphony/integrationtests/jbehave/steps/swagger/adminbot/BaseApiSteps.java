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

package com.symphony.integrationtests.jbehave.steps.swagger.adminbot;

import com.symphony.api.adminbot.client.ApiClient;
import com.symphony.integrationtests.lib.TestContext;
import com.symphony.integrationtests.lib.config.IntegrationTestConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by nick.tarsillo on 8/27/17.
 */
public class BaseApiSteps {
  private static final Logger LOG = LoggerFactory.getLogger(BaseApiSteps.class);

  protected TestContext context;

  public BaseApiSteps() {
    context = TestContext.getInstance();
  }

  protected ApiClient getApiClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(System.getProperty(IntegrationTestConfig.BOT_BASE_URL));
    return apiClient;
  }

  protected ApiClient getAuthClient() {
    ApiClient apiClient = new ApiClient();
    apiClient.setBasePath(System.getProperty(IntegrationTestConfig.AUTH_BASE_URL));

    KeyStore cks;
    KeyStore tks;
    try {
      cks = KeyStore.getInstance("PKCS12");
      tks = KeyStore.getInstance("JKS");

      loadKeyStore(cks, System.getProperty(IntegrationTestConfig.BOT_KEYSTORE_FILE),
          System.getProperty(IntegrationTestConfig.BOT_KEYSTORE_PASSWORD));
      loadKeyStore(tks, System.getProperty(IntegrationTestConfig.BOT_TRUSTORE_FILE),
          System.getProperty(IntegrationTestConfig.BOT_TRUSTORE_PASSWORD));

      Client httpClient = ClientBuilder.newBuilder()
          .keyStore(cks,
              System.getProperty(IntegrationTestConfig.BOT_KEYSTORE_PASSWORD).toCharArray())
          .trustStore(tks)
          .hostnameVerifier((string, sll) -> true)
          .build();

      apiClient.setHttpClient(httpClient);
    } catch (Exception e) {
      LOG.error("Auth client creation failed: ", e);
    }

    return apiClient;
  }

  /**
   * Internal keystore loader
   *
   * @param ks     Keystore object which defines the expected type (PKCS12, JKS)
   * @param ksFile Keystore file to process
   * @param ksPass Keystore password for file to process
   * @throws Exception Generally IOExceptions generated from file read
   */
  private static void loadKeyStore(KeyStore ks, String ksFile, String ksPass) throws Exception {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(ksFile);
      ks.load(fis, ksPass.toCharArray());
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
  }
}
