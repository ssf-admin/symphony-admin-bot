package com.symphony.clients.model;

import org.symphonyoss.symphony.authenticator.model.Token;

import javax.ws.rs.client.Client;

/**
 * Created by nick.tarsillo on 7/1/17.
 */
public class SymphonyAuth {
  private Token sessionToken;
  private Token keyToken;
  private String email;
  private String sessionUrl;
  private String keyUrl;
  private Client httpClient;

  public Token getSessionToken() {
    return sessionToken;
  }

  public void setSessionToken(Token sessionToken) {
    this.sessionToken = sessionToken;
  }

  public Token getKeyToken() {
    return keyToken;
  }

  public void setKeyToken(Token keyToken) {
    this.keyToken = keyToken;
  }


  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getSessionUrl() {
    return sessionUrl;
  }

  public void setSessionUrl(String sessionUrl) {
    this.sessionUrl = sessionUrl;
  }

  public String getKeyUrl() {
    return keyUrl;
  }

  public void setKeyUrl(String keyUrl) {
    this.keyUrl = keyUrl;
  }

  public Client getClient() {
    return httpClient;
  }

  public void setClient(Client httpClient) {
    this.httpClient = httpClient;
  }
}