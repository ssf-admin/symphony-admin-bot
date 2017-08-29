package com.symphony.adminbot.health;

/**
 * Created by seung-woo.choi on 8/21/16.
 */
public class HealthCheckFailedException extends Exception {
  public HealthCheckFailedException(String s) {
    super(s);
  }
}
