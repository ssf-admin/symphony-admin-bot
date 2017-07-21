package com.symphony.adminbot.model.session;

import com.symphony.adminbot.bootstrap.service.DeveloperBootstrapService;
import com.symphony.api.clients.SymphonyClient;

/**
 * Created by nick.tarsillo on 7/20/17.
 */
public class AdminBotSession {
  private DeveloperBootstrapService bootstrapService;

  public AdminBotSession (SymphonyClient symphonyClient) {
    bootstrapService = new DeveloperBootstrapService(symphonyClient);
  }

  public DeveloperBootstrapService getBootstrapService() {
    return bootstrapService;
  }
}
