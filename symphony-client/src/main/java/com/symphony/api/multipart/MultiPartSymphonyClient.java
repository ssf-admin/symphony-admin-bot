package com.symphony.api.multipart;

import com.symphony.api.clients.AuthorizationClient;
import com.symphony.api.clients.SymphonyClient;
import com.symphony.api.clients.model.SymphonyAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.authenticator.invoker.ApiException;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nick.tarsillo on 7/12/17.
 */
public class MultiPartSymphonyClient extends SymphonyClient {
  private final Logger LOG = LoggerFactory.getLogger(MultiPartSymphonyClient.class);

  private MultiPartUserClient multiPartUserClient;

  public void init(SymphonyAuth symAuth, String agentUrl, String serviceUrl){
    super.init(symAuth, agentUrl, serviceUrl);
    multiPartUserClient = new MultiPartUserClient(symAuth, serviceUrl);
  }

  @Override
  protected void startAuthRefresh(){
    Timer timer = new Timer(true);
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        AuthorizationClient authorizationClient = new AuthorizationClient(symAuth);
        try {
          setSymAuth(authorizationClient.authenticate());
          attachmentsClient.setSymphonyAuth(symAuth);
          messagesClient.setSymphonyAuth(symAuth);
          securityClient.setSymphonyAuth(symAuth);
          streamsClient.setSymphonyAuth(symAuth);
          usersClient.setSymphonyAuth(symAuth);
          sessionClient.setSymphonyAuth(symAuth);
          multiPartUserClient.setSymphonyAuth(symAuth);
        } catch (ApiException e) {
          LOG.error("Auth refresh failed: " + e.getStackTrace());
        }
      }
    }, SYMAUTH_REFRESH_TIME, SYMAUTH_REFRESH_TIME);
  }

  public MultiPartUserClient getMultiPartUserClient() {
    return multiPartUserClient;
  }

  public void setMultiPartUserClient(MultiPartUserClient multiPartUserClient) {
    this.multiPartUserClient = multiPartUserClient;
  }
}
