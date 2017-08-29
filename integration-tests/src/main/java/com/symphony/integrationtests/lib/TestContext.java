package com.symphony.integrationtests.lib;

import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.adminbot.model.DeveloperWelcomeResponse;
import com.symphony.integrationtests.lib.config.IntegrationTestConfig;

/**
 * Created by nick.tarsillo on 8/27/17.
 */
public class TestContext {
  public static final ThreadLocal<String> STORY_IN_PROGRESS = new ThreadLocal<>();

  private static TestContext instance = new TestContext();

  private String adminSessionToken;
  private DeveloperSignUpForm developerSignUpForm;
  private DeveloperWelcomeResponse developerWelcomeResponse;

  static {
    IntegrationTestConfig.init();
  }

  public static TestContext getInstance() {
    return instance;
  }

  public String getAdminSessionToken() {
    return adminSessionToken;
  }

  public void setAdminSessionToken(String adminSessionToken) {
    this.adminSessionToken = adminSessionToken;
  }

  public DeveloperSignUpForm getDeveloperSignUpForm() {
    return developerSignUpForm;
  }

  public void setDeveloperSignUpForm(
      DeveloperSignUpForm developerSignUpForm) {
    this.developerSignUpForm = developerSignUpForm;
  }

  public DeveloperWelcomeResponse getDeveloperWelcomeResponse() {
    return developerWelcomeResponse;
  }

  public void setDeveloperWelcomeResponse(
      DeveloperWelcomeResponse developerWelcomeResponse) {
    this.developerWelcomeResponse = developerWelcomeResponse;
  }
}
