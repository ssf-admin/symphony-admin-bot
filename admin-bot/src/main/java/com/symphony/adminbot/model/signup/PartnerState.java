package com.symphony.adminbot.model.signup;


import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerBootstrapInfo;
import com.symphony.api.adminbot.model.PartnerSignUpForm;

import org.symphonyoss.symphony.agent.model.AttachmentInfo;
import org.symphonyoss.symphony.pod.model.Stream;
import org.symphonyoss.symphony.pod.model.UserCreate;
import org.symphonyoss.symphony.pod.model.UserDetail;

import java.util.List;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/2/17.
 *
 * Represents the current state of partner in the sign up process.
 */
public class PartnerState {
  private Partner partner;
  private PartnerSignUpForm partnerSignUpForm;
  private Set<Partner> teamMembers;
  private UserCreate userCreate;
  private UserDetail userDetail;
  private String password;
  private Stream partnerIM;
  private List<AttachmentInfo> certAttachmentInfo;
  private PartnerBootstrapInfo bootstrapInfo;

  public UserCreate getUserCreate() {
    return userCreate;
  }

  public void setUserCreate(UserCreate userCreate) {
    this.userCreate = userCreate;
  }

  public Partner getPartner() {
    return partner;
  }

  public void setPartner(Partner partner) {
    this.partner = partner;
  }

  public Set<Partner> getTeamMembers() {
    return teamMembers;
  }

  public void setTeamMembers(Set<Partner> teamMembers) {
    this.teamMembers = teamMembers;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public PartnerSignUpForm getPartnerSignUpForm() {
    return partnerSignUpForm;
  }

  public void setPartnerSignUpForm(PartnerSignUpForm partnerSignUpForm) {
    this.partnerSignUpForm = partnerSignUpForm;
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

  public Stream getPartnerIM() {
    return partnerIM;
  }

  public void setPartnerIM(Stream partnerIM) {
    this.partnerIM = partnerIM;
  }

  public PartnerBootstrapInfo getBootstrapInfo() {
    return bootstrapInfo;
  }

  public void setBootstrapInfo(PartnerBootstrapInfo bootstrapInfo) {
    this.bootstrapInfo = bootstrapInfo;
  }
}
