package com.symphony.api.clients;

import com.symphony.api.auth.client.ApiException;
import com.symphony.api.clients.model.SymphonyAuth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class SymphonyClient {
  private final Logger LOG = LoggerFactory.getLogger(SymphonyClient.class);
  private static final long SYMAUTH_REFRESH_TIME = 7200000;

  /**
   * Clients
   */
  private AttachmentsClient attachmentsClient;
  private MessagesClient messagesClient;
  private SecurityClient securityClient;
  private StreamsClient streamsClient;
  private UsersClient usersClient;
  private SessionClient sessionClient;
  private ApplicationClient applicationClient;

  private SymphonyAuth symAuth;

  /**
   * Initialize clients with required parameters.
   *
   * @param symAuth    Contains valid key and session tokens generated from AuthorizationClient.
   * @param agentUrl   The Agent URL
   * @param serviceUrl The Service URL (pod url)
   */
  public void init(SymphonyAuth symAuth, String agentUrl, String serviceUrl) {
    this.symAuth = symAuth;

    attachmentsClient = new AttachmentsClient(symAuth, agentUrl);
    messagesClient = new MessagesClient(symAuth, agentUrl);
    securityClient = new SecurityClient(symAuth, serviceUrl);
    streamsClient = new StreamsClient(symAuth, serviceUrl);
    usersClient = new UsersClient(symAuth, serviceUrl);
    sessionClient = new SessionClient(symAuth, serviceUrl);
    applicationClient = new ApplicationClient(symAuth, serviceUrl);

    startAuthRefresh();
  }

  /**
   * Sets timer to refresh auth every so often.
   */
  private void startAuthRefresh(){
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
          applicationClient.setSymphonyAuth(symAuth);
        } catch (ApiException e) {
          LOG.error("Auth refresh failed: " + e.getStackTrace());
        }
      }
    }, SYMAUTH_REFRESH_TIME, SYMAUTH_REFRESH_TIME);
  }

  public void setSymAuth(SymphonyAuth symAuth) {
    this.symAuth = symAuth;
  }

  public AttachmentsClient getAttachmentsClient() {
    return attachmentsClient;
  }

  public MessagesClient getMessagesClient() {
    return messagesClient;
  }

  public SecurityClient getSecurityClient() {
    return securityClient;
  }

  public StreamsClient getStreamsClient() {
    return streamsClient;
  }

  public UsersClient getUsersClient() {
    return usersClient;
  }

  public SessionClient getSessionClient() {
    return sessionClient;
  }
}
