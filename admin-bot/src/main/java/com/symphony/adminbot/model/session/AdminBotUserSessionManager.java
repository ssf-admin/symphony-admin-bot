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
import java.util.concurrent.ExecutionException;
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
    } catch (ExecutionException e) {
      LOG.warn("Could not get user " + adminName + ":", e);
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
    return adminSessionCache.getIfPresent(new SessionCacheKey(sessionToken));
  }

  class SessionCacheKey {
    private String sessionToken;

    SessionCacheKey () {
      this.sessionToken = new BigInteger(130, random).toString(32);;
    }
    SessionCacheKey (String sessionToken) {
      this.sessionToken = sessionToken;
    }

    @Override
    public boolean equals(Object obj){
      if(obj instanceof SessionCacheKey) {
        return ((SessionCacheKey) obj).getSessionToken().equals(sessionToken);
      }
      return false;
    }

    public String getSessionToken() {
      return sessionToken;
    }
  }
}
