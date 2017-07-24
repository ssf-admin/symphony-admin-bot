package com.symphony.api.clients;

import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.clients.model.SymphonyUser;
import com.symphony.api.pod.api.AppEntitlementApi;
import com.symphony.api.pod.api.UserApi;
import com.symphony.api.pod.api.UsersApi;
import com.symphony.api.pod.client.ApiClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.client.Configuration;
import com.symphony.api.pod.model.FeatureList;
import com.symphony.api.pod.model.PodAppEntitlementList;
import com.symphony.api.pod.model.SuccessResponse;
import com.symphony.api.pod.model.UserAppEntitlementList;
import com.symphony.api.pod.model.UserCreate;
import com.symphony.api.pod.model.UserDetail;
import com.symphony.api.pod.model.UserV2;

import javax.ws.rs.core.Response;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class UsersClient {
  private final ApiClient apiClient;
  private SymphonyAuth symAuth;

  public UsersClient(SymphonyAuth symAuth, String serviceUrl) {
    this.symAuth = symAuth;

    //Get Service clients to query for userID.
    apiClient = Configuration.getDefaultApiClient();
    apiClient.setBasePath(serviceUrl);
  }

  public UserDetail createUser(UserCreate user) throws ApiException {
    UserApi userApi = new UserApi(apiClient);

    UserDetail userDetail;
    try {
      userDetail = userApi.v1AdminUserCreatePost(symAuth.getSessionToken().getToken(), user);
    } catch (ApiException e) {
      throw new ApiException("Could not create user: " + e.getMessage());
    }

    return userDetail;
  }

  public SuccessResponse updateEntitlements(Long userId, FeatureList entitlements)
      throws ApiException {
    UserApi userApi = new UserApi(apiClient);

    SuccessResponse successResponse;
    try {
      successResponse = userApi.v1AdminUserUidFeaturesUpdatePost(symAuth.getSessionToken().getToken(),
              userId, entitlements);
    } catch (ApiException e) {
      throw new ApiException("Could not update entitlements: " + e);
    }

    return successResponse;
  }

  public UserDetail getUserDetail(Long userId) throws ApiException {
    UserApi userApi = new UserApi(apiClient);

    UserDetail userDetail;
    try {
      userDetail = userApi.v1AdminUserUidGet(symAuth.getSessionToken().getToken(), userId);
    } catch (ApiException e) {
      throw new ApiException("Could not update entitlements: " + e);
    }

    return userDetail;
  }

  public SymphonyUser userSearchByEmail(String email) throws ApiException {
    UsersApi userApi = new UsersApi(apiClient);

    SymphonyUser symphonyUser;
    try {
      symphonyUser = new SymphonyUser(userApi.v2UserGet(
          symAuth.getSessionToken().getToken(), null, email, null, false));
    } catch (ApiException e) {
      throw new ApiException("User get failed: " + e);
    }

    return symphonyUser;
  }

  public boolean userExistsByEmail(String email) throws ApiException {
    UsersApi userApi = new UsersApi(apiClient);

    UserV2 userV2;
    try {
      userV2 = userApi.v2UserGet(symAuth.getSessionToken().getToken(), null, email, null, false);
    } catch (ApiException e) {
      if (e.getCode() == Response.Status.NO_CONTENT.getStatusCode()) {
        return false;
      } else {
        throw new ApiException("User get failed: " + e);
      }
    }

    return userV2 != null;
  }

  public boolean userExistsByUsername(String username) throws ApiException {
    UsersApi userApi = new UsersApi(apiClient);

    UserV2 userV2;
    try {
      userV2 = userApi.v2UserGet(symAuth.getSessionToken().getToken(), null, null, username, true);
    } catch (ApiException e) {
      if (e.getCode() == Response.Status.NO_CONTENT.getStatusCode()) {
        return false;
      } else {
        throw new ApiException("User get failed: " + e);
      }
    }

    return userV2 != null;
  }

  public UserAppEntitlementList updateUserApps(Long userId, UserAppEntitlementList entitlements)
      throws ApiException {
    AppEntitlementApi appEntitlementApi = new AppEntitlementApi(apiClient);

    UserAppEntitlementList appEntitlementList;
    try {
      appEntitlementList = appEntitlementApi.v1AdminUserUidAppEntitlementListPost(
          symAuth.getSessionToken().getToken(), userId, entitlements);
    } catch (ApiException e) {
      throw new ApiException("Could not update entitlements: " + e);
    }

    return appEntitlementList;
  }

  public PodAppEntitlementList listPodApps() throws ApiException {
    AppEntitlementApi appEntitlementApi = new AppEntitlementApi(apiClient);

    PodAppEntitlementList appEntitlementList;
    try {
      appEntitlementList = appEntitlementApi.v1AdminAppEntitlementListGet(symAuth.getSessionToken().getToken());
    } catch (ApiException e) {
      throw new ApiException("Could not update entitlements: " + e);
    }

    return appEntitlementList;
  }

  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
