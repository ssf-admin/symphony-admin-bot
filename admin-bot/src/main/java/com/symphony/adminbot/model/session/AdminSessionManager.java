package com.symphony.adminbot.model.session;

import com.symphony.adminbot.commons.BotConstants;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by nick.tarsillo on 7/6/17.
 */
public class AdminSessionManager {
  private final Logger LOG = LoggerFactory.getLogger(AdminSessionManager.class);
  private Map<Long, Set<SessionCacheKey>> validationMap;
  private Cache<SessionCacheKey, AdminSession> adminSessionMap;

  public AdminSessionManager(){
    adminSessionMap = CacheBuilder.newBuilder()
        .concurrencyLevel(4)
        .maximumSize(10000)
        .expireAfterAccess(BotConstants.MANAGER_EXPIRE_MINUTES, TimeUnit.MINUTES)
        .build();

    validationMap = new HashMap<>();
  }

  public void putAdminSession(String sessionToken, String keyManagerToken, AdminSession adminSession){
    SessionCacheKey sessionCacheKey = new SessionCacheKey(sessionToken, keyManagerToken);
    adminSessionMap.put(sessionCacheKey, adminSession);
    replaceInvalidSession(sessionCacheKey, adminSession);
  }

  public AdminSession getAdminSession(String sessionToken, String keyManagerToken){
    SessionCacheKey sessionCacheKey = new SessionCacheKey(sessionToken, keyManagerToken);
    return adminSessionMap.getIfPresent(sessionCacheKey);
  }

  private void replaceInvalidSession(SessionCacheKey sessionCacheKey, AdminSession adminSession){
    Long userKey = adminSession.getSymphonyUser().getId();
    Set<SessionCacheKey> keys;
    if(validationMap.containsKey(userKey)) {
      keys = validationMap.get(userKey);

      for(SessionCacheKey cacheKey: keys) {
        adminSessionMap.put(cacheKey, adminSession);
      }

      keys.add(sessionCacheKey);
    } else {
      keys = new HashSet<>();
      keys.add(sessionCacheKey);
      validationMap.put(userKey, keys);
    }
  }

  /**
   * Container class for Agent authentication tokens.
   */
  static class SessionCacheKey {
    private final String sessionToken;
    private final String keyManagerToken;

    SessionCacheKey(final String sessionToken, final String keyManagerToken) {
      this.sessionToken = sessionToken;
      this.keyManagerToken = keyManagerToken;
    }

    @Override
    public boolean equals(Object other) {
      return (other instanceof SessionCacheKey
          && Objects.equals(sessionToken, ((SessionCacheKey) other).sessionToken)
          && Objects.equals(keyManagerToken, ((SessionCacheKey) other).keyManagerToken));
    }

    @Override
    public int hashCode() {
      return Objects.hash(sessionToken, keyManagerToken);
    }
  }
}
