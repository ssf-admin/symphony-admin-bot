package com.symphony.adminbot.model.user;

/**
 * Created by nick.tarsillo on 7/20/17.
 *
 * This class is not used yet, but it could be used to save info about admin bot user.
 */
public class AdminBotUser {
  private String adminName;

  public AdminBotUser(String adminName) {
    this.adminName = adminName;
  }

  public String getAdminName() {
    return adminName;
  }
}
