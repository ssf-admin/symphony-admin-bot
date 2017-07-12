package com.symphony.api.multipart;

import com.symphony.api.clients.SymphonyClient;
import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.multipart.model.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.UserCreate;
import org.symphonyoss.symphony.pod.model.UserDetail;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;

/**
 * Created by nick.tarsillo on 7/12/17.
 *
 * FOR TESTING PURPOSES
 */
public class MultiPartUserClient {
  private final Logger LOG = LoggerFactory.getLogger(MultiPartUserClient.class);

  private MultiPartClient<UserDetail> client;
  private SymphonyAuth symAuth;

  private static final String SESSION_TOKEN_NAME = "sessionToken";
  private static final String PATH = "v1/admin/user/create";

  public MultiPartUserClient(SymphonyAuth symAuth, String baseUrl) {
    client = new MultiPartClient<>(baseUrl);
    this.symAuth = symAuth;
  }

  public UserDetail createUserV1(UserCreate userCreate) throws ApiException {
    Map<String, String> headers = new HashMap<>();
    headers.put(SESSION_TOKEN_NAME, symAuth.getSessionToken().getToken());
    Request request = new Request();
    request.setHeaders(headers);
    request.setReturnObjectType(UserDetail.class);
    request.setPath(PATH);
    request.setPayload(Entity.json(userCreate));

    UserDetail userDetail;
    try {
      userDetail = client.doPost(request);
    } catch (ApiException e) {
      LOG.error("Create user v1 failed: ", e);
      throw new ApiException("Create user v1 failed.");
    }

    return userDetail;
  }

  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
