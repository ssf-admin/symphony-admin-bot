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

package com.symphony.adminbot.model.session;

import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.model.user.AdminBotUser;
import com.symphony.adminbot.util.file.ExpiringFileLoaderCache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by nick.tarsillo on 7/6/17.
 */
public class AdminBotUserSessionManager {
  private final Logger LOG = LoggerFactory.getLogger(AdminBotUserSessionManager.class);

  private ExpiringFileLoaderCache<String, AdminBotUser> adminUserCache;
  private Cache<SessionCacheKey, AdminBotUserSession> adminSessionCache;
  private SecureRandom random = new SecureRandom();

  public AdminBotUserSessionManager(){
    adminUserCache = new ExpiringFileLoaderCache<>(
        System.getProperty(BotConfig.USER_JSON_DIR),
        (adminName) -> adminName,
        BotConstants.EXPIRE_TIME_DAYS,
        TimeUnit.DAYS,
        AdminBotUser.class);

    adminSessionCache = CacheBuilder.newBuilder()
        .concurrencyLevel(4)
        .maximumSize(10000)
        .expireAfterAccess(BotConstants.MANAGER_EXPIRE_MINUTES, TimeUnit.MINUTES)
        .build();
  }

  public String getSessionToken(String adminName) {
    AdminBotUser adminBotUser = null;
    try {
      adminBotUser = adminUserCache.get(adminName);
    } catch (Exception e) {
      LOG.warn("Could not load user " + adminName + " from file.");
    }
    if(adminBotUser == null) {
      adminBotUser = new AdminBotUser(adminName);
    }
    AdminBotUserSession adminBotUserSession = new AdminBotUserSession(adminBotUser);
    SessionCacheKey sessionCacheKey = new SessionCacheKey();
    adminSessionCache.put(sessionCacheKey, adminBotUserSession);

    return sessionCacheKey.getSessionToken();
  }

  public AdminBotUserSession getAdminSession(String sessionToken) {
    SessionCacheKey sessionCacheKey = new SessionCacheKey(sessionToken);
    return adminSessionCache.getIfPresent(sessionCacheKey);
  }

  class SessionCacheKey {
    private String sessionToken;

    SessionCacheKey () {
      this.sessionToken = new BigInteger(256, random).toString(32);;
    }
    SessionCacheKey (String sessionToken) {
      this.sessionToken = sessionToken;
    }

    @Override
    public boolean equals(Object other) {
      return (other instanceof SessionCacheKey
          && Objects.equals(sessionToken, ((SessionCacheKey) other).sessionToken));
    }

    @Override
    public int hashCode() {
      return Objects.hash(sessionToken);
    }

    public String getSessionToken() {
      return sessionToken;
    }
  }
}
