package com.symphony.adminbot.commons;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class BotConstants {
  //For error messages
  public static final String USER_EXISTS = "One or more partners already exists on pod.";
  public static final String NOT_ENTITLED = "User is not entitled to use these endpoints.";
  public static final String INTERNAL_ERROR = "Internal server error.";
  public static final String NO_CERT = "Please provide a cert with request.";

  //For cert generation
  public static final String BOT_USERNAME = "BotUser";

  //Cache
  public static final int VALID_DURATION = 7200;
  public static final long EXPIRE_TIME_DAYS = 1;
  public static final long MANAGER_EXPIRE_MINUTES = 30;

  //For Email Confirmation
  public static final String ADMIN_BOT_NAME = "Admin Bot";

  //Bootstrap Error Messages
  public static final String APP_ID_REQUIRED = "App Id is required.";
  public static final String BOT_APP_EXISTS = "Bot or app already exists.";
  public static final String DEVELOPER_REQUIRED = "All developer fields are required.";
  public static final String APP_NAME_REQUIRED = "App name is required.";
  public static final String APP_DESCRIPTION_REQUIRED = "App description is required.";
  public static final String APP_DOMAIN_REQUIRED = "App domain is required.";
  public static final String APP_COMPANY_REQUIRED = "App company name is required.";
  public static final String APP_URL_REQUIRED = "App url is required.";
  public static final String BOT_EMAIL_REQUIRED = "Bot email is required.";
  public static final String BOT_NAME_REQUIRED = "Bot name is required.";
  public static final String ICON_START_WITH_HTTPS = "Icon url must start with \"https://\".";
  public static final String APP_START_WITH_HTTPS = "App url must start with \"https://\".";
  public static final String BAD_APP_URL = "Please provide a valid app url.";
  public static final String DOMAIN_MUST_MATCH = "Domain must match app url domain.";
  public static final String DEVELOPER_NOT_FOUND = "Developer not found.";
}
