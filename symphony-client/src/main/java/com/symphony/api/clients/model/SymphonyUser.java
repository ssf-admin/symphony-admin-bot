package com.symphony.api.clients.model;

import org.symphonyoss.symphony.pod.model.UserV2;

/**
 * Created by nick.tarsillo on 7/6/17.
 */
public class SymphonyUser extends UserV2 {
  public SymphonyUser(){}

  public SymphonyUser(UserV2 user){
    setAvatars(user.getAvatars());
    setCompany(user.getCompany());
    setDepartment(user.getDepartment());
    setDisplayName(user.getDisplayName());
    setDivision(user.getDivision());
    setEmailAddress(user.getEmailAddress());
    setFirstName(user.getFirstName());
    setId(user.getId());
    setJobFunction(user.getJobFunction());
    setLastName(user.getLastName());
    setLocation(user.getLocation());
    setMobilePhoneNumber(user.getMobilePhoneNumber());
    setTitle(user.getTitle());
    setUsername(user.getUsername());
    setWorkPhoneNumber(user.getWorkPhoneNumber());
  }
}
