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

package com.symphony.api.clients.model;

import com.symphony.api.auth.model.Token;

import javax.ws.rs.client.Client;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class SymphonyAuth {
  private Token sessionToken;
  private Token keyToken;
  private String email;
  private String sessionUrl;
  private String keyUrl;
  private Client httpClient;

  public Token getSessionToken() {
    return sessionToken;
  }

  public void setSessionToken(Token sessionToken) {
    this.sessionToken = sessionToken;
  }

  public Token getKeyToken() {
    return keyToken;
  }

  public void setKeyToken(Token keyToken) {
    this.keyToken = keyToken;
  }


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSessionUrl() {
    return sessionUrl;
  }

  public void setSessionUrl(String sessionUrl) {
    this.sessionUrl = sessionUrl;
  }

  public String getKeyUrl() {
    return keyUrl;
  }

  public void setKeyUrl(String keyUrl) {
    this.keyUrl = keyUrl;
  }

  public Client getClient() {
    return httpClient;
  }

  public void setClient(Client httpClient) {
    this.httpClient = httpClient;
  }
}