package com.symphony.adminbot.enums;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public enum SymphonyReplacementEnums {
  URL("{URL}"),
  BOT_NAME("{BOT_NAME}"),
  BOT_ID("{BOT_ID}"),
  BOT_EMAIL("{BOT_EMAIL}"),
  APP_NAME("{APP_NAME}"),
  APP_ID("{APP_ID}"),
  COMPANY_NAME("{COMPANY_NAME}"),
  FIRST_NAME("{FIRST_NAME}"),
  EMAIL("{EMAIL}"),
  LAST_NAME("{LAST_NAME}"),
  PASSWORD("{PASSWORD}");

  private String replacement;

  SymphonyReplacementEnums(String replacement){
    this.replacement = replacement;
  }

  public String getReplacement() {
    return replacement;
  }
}
