package com.symphony.adminbot.model.core;

import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.model.signup.PartnerSignUpService;
import com.symphony.clients.SessionClient;
import com.symphony.clients.SymphonyClient;
import com.symphony.clients.UsersClient;
import com.symphony.clients.model.SymphonyUser;

import com.fasterxml.jackson.databind.ObjectMapper;
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
  private PartnerSignUpService signUpService;

  public AdminSession(SymphonyClient symClient)
      throws org.symphonyoss.symphony.pod.invoker.ApiException, IOException {
    this.symClient = symClient;

    //Get user info
    SessionClient sessionClient = symClient.getSessionClient();
    symphonyUser = sessionClient.getSessionInfo();

    //Check entitlements
    UsersClient usersClient = symClient.getUsersClient();
    UserDetail userDetail = usersClient.getUserDetail(symphonyUser.getId());
    StringList stringList = userDetail.getRoles();
    StringList required = MAPPER.readValue(new File(System.getProperty(BotConfig.ROLES_FILE)), StringList.class);

    if(!stringList.containsAll(required)){
      throw new ForbiddenException("User provided is not a admin.");
    }

    //Init session services
    signUpService = new PartnerSignUpService(symClient);
  }

  public PartnerSignUpService getSignUpService() {
    return signUpService;
  }

  public SymphonyUser getSymphonyUser() {
    return symphonyUser;
  }
}
