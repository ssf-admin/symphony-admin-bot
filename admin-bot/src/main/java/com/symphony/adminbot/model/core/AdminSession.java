package com.symphony.adminbot.model.core;

import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.model.bootstrap.DeveloperBootstrapService;
import com.symphony.api.clients.SessionClient;
import com.symphony.api.clients.SymphonyClient;
import com.symphony.api.clients.UsersClient;
import com.symphony.api.clients.model.SymphonyUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.SessionInfo;
import org.symphonyoss.symphony.pod.model.StringList;
import org.symphonyoss.symphony.pod.model.UserDetail;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.ForbiddenException;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public class AdminSession {
  private static ObjectMapper MAPPER = new ObjectMapper();

  private SymphonyUser symphonyUser;
  private SymphonyClient symClient;
  private DeveloperBootstrapService signUpService;

  public AdminSession(SymphonyClient symClient) throws ApiException, IOException {
    this.symClient = symClient;

    SessionClient sessionClient = symClient.getSessionClient();
    UsersClient usersClient = symClient.getUsersClient();

    //Get user info
    symphonyUser = sessionClient.getSessionInfo();

    //Check entitlements
    UserDetail userDetail = usersClient.getUserDetail(symphonyUser.getId());
    StringList stringList = userDetail.getRoles();
    StringList required = MAPPER.readValue(new File(System.getProperty(BotConfig.ROLES_FILE)), StringList.class);

    if(!stringList.containsAll(required)){
      throw new ForbiddenException("User provided is not a admin.");
    }

    //Init session services
    signUpService = new DeveloperBootstrapService(symClient);
  }

  public DeveloperBootstrapService getSignUpService() {
    return signUpService;
  }

  public SymphonyUser getSymphonyUser() {
    return symphonyUser;
  }
}
