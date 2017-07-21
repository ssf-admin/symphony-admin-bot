package com.symphony.adminbot.model.session;

import com.symphony.adminbot.model.user.AdminBotUser;

/**
 * Created by nick.tarsillo on 7/5/17.
 *
 * This class isn't used yet, but could be used to store info about admin current session.
 */
public class AdminBotUserSession {
  public AdminBotUser adminUser;

  public AdminBotUserSession(AdminBotUser adminBotUser) {
    this.adminUser = adminBotUser;
  }

  public AdminBotUser getAdminUser() {
    return adminUser;
  }
}
