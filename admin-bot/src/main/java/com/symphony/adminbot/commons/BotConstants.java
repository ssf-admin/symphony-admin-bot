package com.symphony.adminbot.commons;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class BotConstants {
  //For error messages
  public static final String USER_EXISTS = "One or more partners already exists on pod.";
  public static final String NOT_ENTITLED = "User is not entitled to use these endpoints.";
  public static final String INTERNAL_ERROR = "Internal server error.";
  public static final String BOT_EXISTS = "Bot email already exists.";
  public static final String FORBIDDEN = "Forbidden";
  public static final String NO_CERT = "Please provide a cert with request.";

  //For cert generation
  public static final String BOT_USERNAME = "BotUser";
  public static final String CERT_NAME =  "Intermediate Root Cert";

  //Cache
  public static final int VALID_DURATION = 7200;
  public static final long EXPIRE_TIME_DAYS = 1;
  public static final long MANAGER_EXPIRE_MINUTES = 30;

  //For Email Confirmation
  public static final String ADMIN_BOT_NAME = "Admin Bot";
  public static final String CONFIRMATION_PATH = "/v1/emailConfirmation/{PASSWORD}"
      + "?firstName={FIRST_NAME}&lastName={LAST_NAME}&email={EMAIL}";
}
