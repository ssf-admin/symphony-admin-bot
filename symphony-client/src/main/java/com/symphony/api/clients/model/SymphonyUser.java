package com.symphony.api.clients.model;

import org.symphonyoss.symphony.pod.model.AvatarList;
import org.symphonyoss.symphony.pod.model.UserDetail;
import org.symphonyoss.symphony.pod.model.UserV2;

/**
 * Created by nick.tarsillo on 7/6/17.
 */
public class SymphonyUser extends UserV2 {
  public SymphonyUser(){}

  public SymphonyUser(UserV2 user){
    setAvatars(user.getAvatars());
    setCompany(user.getCompany());
    setDisplayName(user.getDisplayName());
    setEmailAddress(user.getEmailAddress());
    setFirstName(user.getFirstName());
    setId(user.getId());
    setLastName(user.getLastName());
    setLocation(user.getLocation());
    setTitle(user.getTitle());
    setUsername(user.getUsername());

//    setDivision(user.getDivision());
//    setJobFunction(user.getJobFunction());
//    setMobilePhoneNumber(user.getMobilePhoneNumber());
//    setWorkPhoneNumber(user.getWorkPhoneNumber());
//    setDepartment(user.getDepartment());
  }

  public SymphonyUser(UserDetail user) {
    AvatarList avatars = new AvatarList();
    avatars.add(user.getAvatar());
    setAvatars(avatars);
    setDisplayName(user.getUserAttributes().getDisplayName());
    setEmailAddress(user.getUserAttributes().getEmailAddress());
    setFirstName(user.getUserAttributes().getFirstName());
    setId(user.getUserSystemInfo().getId());
    setLastName(user.getUserAttributes().getLastName());
    setLocation(user.getUserAttributes().getLocation());
    setTitle(user.getUserAttributes().getTitle());
    setUsername(user.getUserAttributes().getUserName());
  }
}
