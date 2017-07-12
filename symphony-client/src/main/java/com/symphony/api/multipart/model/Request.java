package com.symphony.api.multipart.model;

import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Created by nick.tarsillo on 7/11/17.
 *
 * FOR TESTING PURPOSES
 */
public class Request<T> {
  private String path;
  private Entity<T> payload;
  private MediaType mediaType;
  private Class returnObjectType;
  private Map<String, String> headers;
  private MultivaluedMap<String, String> params;
  private Map<String, String> cookies;

  public MediaType getMediaType() {
    return mediaType;
  }

  public void setMediaType(MediaType mediaType) {
    this.mediaType = mediaType;
  }

  public Class getReturnObjectType() {
    return returnObjectType;
  }

  public void setReturnObjectType(Class returnObjectType) {
    this.returnObjectType = returnObjectType;
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setHeaders(Map<String, String> headers) {
    this.headers = headers;
  }

  public MultivaluedMap<String, String> getParams() {
    return params;
  }

  public void setParams(MultivaluedMap<String, String> params) {
    this.params = params;
  }

  public Map<String, String> getCookies() {
    return cookies;
  }

  public void setCookies(Map<String, String> cookies) {
    this.cookies = cookies;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Entity<T> getPayload() {
    return payload;
  }

  public void setPayload(Entity<T> payload) {
    this.payload = payload;
  }
}

