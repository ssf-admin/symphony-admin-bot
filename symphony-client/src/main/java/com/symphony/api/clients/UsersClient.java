package com.symphony.api.clients;

import com.symphony.api.clients.model.SymphonyUser;
import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.multipart.MultiPartUserClient;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.symphonyoss.symphony.pod.api.UserApi;
import org.symphonyoss.symphony.pod.api.UsersApi;
import org.symphonyoss.symphony.pod.invoker.ApiClient;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.invoker.Configuration;
import org.symphonyoss.symphony.pod.model.FeatureList;
import org.symphonyoss.symphony.pod.model.SuccessResponse;
import org.symphonyoss.symphony.pod.model.UserCreate;
import org.symphonyoss.symphony.pod.model.UserDetail;
import org.symphonyoss.symphony.pod.model.UserV2;

import javax.ws.rs.client.ClientBuilder;
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

    ClientConfig clientConfig = new ClientConfig();
    clientConfig.register(MultiPartFeature.class);
    clientConfig.register(apiClient.getJSON());
    clientConfig.register(JacksonFeature.class);

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

    clientConfig.register(new JacksonJsonProvider(objectMapper));

    apiClient.setHttpClient(ClientBuilder.newClient(clientConfig));
  }

  public UserDetail createUser(UserCreate user) throws ApiException {
    UserApi userApi = new UserApi(apiClient);


    UserDetail userDetail;
    try {
      userDetail = userApi.v1AdminUserCreatePost(symAuth.getSessionToken().getToken(), user);
    } catch (ApiException e) {
      throw new ApiException("Could not create user: " + e);
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
      throw new ApiException("User search failed: " + e);
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
        throw new ApiException("User search failed: " + e);
      }
    }

    return userV2 != null;
  }

  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
