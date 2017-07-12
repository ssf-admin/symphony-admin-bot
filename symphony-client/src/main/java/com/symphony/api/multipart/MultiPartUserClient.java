package com.symphony.api.multipart;

import com.symphony.api.clients.SymphonyClient;
import com.symphony.api.clients.model.SymphonyAuth;
import com.symphony.api.multipart.model.Request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.UserCreate;
import org.symphonyoss.symphony.pod.model.UserDetail;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

/**
 * Created by nick.tarsillo on 7/12/17.
 *
 * FOR TESTING PURPOSES
 */
public class MultiPartUserClient {
  private static ObjectMapper MAPPER = new ObjectMapper();
  private final Logger LOG = LoggerFactory.getLogger(MultiPartUserClient.class);

  private MultiPartClient<UserDetail> client;
  private SymphonyAuth symAuth;

  private static final String SESSION_TOKEN_NAME = "sessionToken";
  private static final String PATH = "v1/admin/user/create";

  public MultiPartUserClient(SymphonyAuth symAuth, String baseUrl) {
    client = new MultiPartClient<>(baseUrl);
    this.symAuth = symAuth;
  }

  /**
   * This method is being used to get around a bug with setting passwords using OSS
   */
  public UserDetail createUserV1(UserCreate userCreate) throws ApiException {
    Map<String, String> headers = new HashMap<>();
    headers.put(SESSION_TOKEN_NAME, symAuth.getSessionToken().getToken());
    Request request = new Request();
    request.setHeaders(headers);
    request.setReturnObjectType(UserDetail.class);
    request.setPath(PATH);

    String toJson = "";
    try {
      toJson = MAPPER.writeValueAsString(userCreate);
      toJson = removeField("hpassword", toJson);
      toJson = removeField("hsalt", toJson);
    } catch (JsonProcessingException e) {
      LOG.error("Parsing failed: ", e);
    }

    request.setPayload(Entity.entity(toJson, MediaType.APPLICATION_JSON_TYPE));

    UserDetail userDetail;
    try {
      userDetail = client.doPost(request);
    } catch (ApiException e) {
      LOG.error("Create user v1 failed: ", e);
      throw new ApiException("Create user v1 failed.");
    }

    return userDetail;
  }

  private String removeField(String fieldName, String json){
    String toJson = json;
    int beginIndex = toJson.indexOf("\"" + fieldName);
    int endIndex = 0;
    int currentComma = 0;
    for(int index = beginIndex; index < toJson.length(); index ++){
      if(toJson.charAt(index) == '"'){
        currentComma ++;
        if(currentComma == 4){
          endIndex = index + 2;
        }
      }
    }
    return toJson.replace(toJson.substring(beginIndex, endIndex), "");
  }

  public void setSymphonyAuth(SymphonyAuth symAuth){
    this.symAuth = symAuth;
  }
}
