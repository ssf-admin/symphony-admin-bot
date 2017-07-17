package com.symphony.adminbot.bootstrap.model;


import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperBootstrapInfo;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.agent.model.AttachmentInfo;
import com.symphony.api.pod.model.CompanyCert;
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.UserCreate;
import com.symphony.api.pod.model.UserDetail;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/2/17.
 *
 * Represents the current state of developer in the bootstrap process.
 */
public class DeveloperBootstrapState {
  private Developer developer;
  private DeveloperSignUpForm developerSignUpForm;
  private Set<Developer> teamMembers;
  private UserCreate userCreate;
  private UserDetail userDetail;
  private String password;
  private Stream developerIM;
  private Map<String, CompanyCert> companyCertMap;
  private List<AttachmentInfo> certAttachmentInfo;
  private DeveloperBootstrapInfo bootstrapInfo;

  public UserCreate getUserCreate() {
    return userCreate;
  }

  public void setUserCreate(UserCreate userCreate) {
    this.userCreate = userCreate;
  }

  public Developer getDeveloper() {
    return developer;
  }

  public void setDeveloper(Developer developer) {
    this.developer = developer;
  }

  public Set<Developer> getTeamMembers() {
    return teamMembers;
  }

  public void setTeamMembers(Set<Developer> teamMembers) {
    this.teamMembers = teamMembers;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public DeveloperSignUpForm getDeveloperSignUpForm() {
    return developerSignUpForm;
  }

  public void setDeveloperSignUpForm(DeveloperSignUpForm developerSignUpForm) {
    this.developerSignUpForm = developerSignUpForm;
  }

  public UserDetail getUserDetail() {
    return userDetail;
  }

  public void setUserDetail(UserDetail userDetail) {
    this.userDetail = userDetail;
  }

  public List<AttachmentInfo> getCertAttachmentInfo() {
    return certAttachmentInfo;
  }

  public void setCertAttachmentInfo(
      List<AttachmentInfo> certAttachmentInfo) {
    this.certAttachmentInfo = certAttachmentInfo;
  }

  public Stream getDeveloperIM() {
    return developerIM;
  }

  public void setDeveloperIM(Stream developerIM) {
    this.developerIM = developerIM;
  }

  public DeveloperBootstrapInfo getBootstrapInfo() {
    return bootstrapInfo;
  }

  public void setBootstrapInfo(DeveloperBootstrapInfo bootstrapInfo) {
    this.bootstrapInfo = bootstrapInfo;
  }

  public Map<String, CompanyCert> getCompanyCertMap() {
    return companyCertMap;
  }

  public void setCompanyCertMap(
      Map<String, CompanyCert> companyCertMap) {
    this.companyCertMap = companyCertMap;
  }
}
