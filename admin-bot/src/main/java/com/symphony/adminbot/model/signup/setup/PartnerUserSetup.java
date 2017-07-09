package com.symphony.adminbot.model.signup.setup;

import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerSignUpForm;
import com.symphony.clients.UsersClient;
import com.symphony.adminbot.model.signup.PartnerState;

import org.apache.commons.lang.WordUtils;
import org.symphonyoss.symphony.pod.invoker.ApiException;
import org.symphonyoss.symphony.pod.model.Feature;
import org.symphonyoss.symphony.pod.model.FeatureList;
import org.symphonyoss.symphony.pod.model.Password;
import org.symphonyoss.symphony.pod.model.UserAttributes;
import org.symphonyoss.symphony.pod.model.UserCreate;
import org.symphonyoss.symphony.pod.model.UserDetail;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/2/17.
 */
public class PartnerUserSetup {
  enum FeaturesEnum{
    WRITE("imWriteEnabled"),
    POST_READ("postReadEnabled"),
    POST_WRITE("postWriteEnabled"),
    ROOM_READ("roomReadEnabled"),
    ROOM_WRITE("roomWriteEnabled"),
    EXTERNAL("isExternalIMEnabled"),
    SHARE_FILES_EXTERNAL("canShareFilesExternally"),
    CREATE_PUBLIC_ROOM("canCreatePublicRoom"),
    READ_IM("imReadEnabled"),
    SEND_FILES("sendFilesEnabled");

    private String entitlement;

    FeaturesEnum(String entitlement){
      this.entitlement = entitlement;
    }

    public Feature enabled(){
      Feature feature = new Feature();
      feature.setEnabled(true);
      feature.setEntitlment(entitlement);
      return feature;
    }

    public Feature disabled(){
      Feature feature = new Feature();
      feature.setEnabled(false);
      feature.setEntitlment(entitlement);
      return feature;
    }
  }

  private UsersClient usersClient;

  public PartnerUserSetup(UsersClient usersClient){
    this.usersClient = usersClient;
  }

  /**
   * Creates initial states for partner within sign up form. (Team members and creator)
   * @param signUpForm the sign up form
   * @return the initial partner states
   */
  public Set<PartnerState> getPartnerSetupStates(PartnerSignUpForm signUpForm){
    Set<Partner> partnerSet = new HashSet<>();
    partnerSet.add(signUpForm.getCreator());
    partnerSet.addAll(signUpForm.getTeam());

    Set<PartnerState> partnerStates = new HashSet<>();
    for(Partner partner: partnerSet) {
      UserCreate userCreate = new UserCreate();
      UserAttributes userAttributes = new UserAttributes();
      userAttributes.setAccountType(UserAttributes.AccountTypeEnum.NORMAL);
      userAttributes.setEmailAddress(partner.getEmail());
      userAttributes.setFirstName(partner.getFirstName());
      userAttributes.setLastName(partner.getLastName());
      userAttributes.setUserName(partner.getFirstName().toLowerCase()
          + WordUtils.capitalize(partner.getLastName()));
      userAttributes.displayName(partner.getFirstName() + " " + partner.getLastName());
      userAttributes.setDepartment(signUpForm.getAppCompanyName());
      userCreate.setUserAttributes(userAttributes);

      SecureRandom random = new SecureRandom();
      String randomPassword = new BigInteger(130, random).toString();
      Password pass = new Password();
      pass.setHPassword(randomPassword);
      pass.setHSalt(randomPassword);
      pass.setKhPassword(randomPassword);
      pass.setKhSalt(randomPassword);
      userCreate.setPassword(pass);

      List<String> roles = new ArrayList<>();
      roles.add("INDIVIDUAL");
      userCreate.setRoles(roles);

      PartnerState partnerState = new PartnerState();
      partnerState.setPartner(partner);
      partnerState.setPartnerSignUpForm(signUpForm);
      partnerState.setUserCreate(userCreate);
      partnerState.setPassword(randomPassword);

      Set<Partner> teamMembers = new HashSet<>(partnerSet);
      teamMembers.remove(partner);
      partnerState.setTeamMembers(teamMembers);

      partnerStates.add(partnerState);
    }

    return partnerStates;
  }

  /**
   * Creates a symphony user for the partner.
   * @param state the partner's current state in the sign up process
   */
  public void createPartnerUser(PartnerState state) throws ApiException {
    UserDetail userDetail = usersClient.createUser(state.getUserCreate());
    state.setUserDetail(userDetail);

    FeatureList features = new FeatureList();

    features.add(FeaturesEnum.WRITE.enabled());
    features.add(FeaturesEnum.CREATE_PUBLIC_ROOM.enabled());
    features.add(FeaturesEnum.EXTERNAL.enabled());
    features.add(FeaturesEnum.SHARE_FILES_EXTERNAL.enabled());
    features.add(FeaturesEnum.ROOM_READ.enabled());
    features.add(FeaturesEnum.POST_WRITE.enabled());
    features.add(FeaturesEnum.POST_READ.enabled());
    features.add(FeaturesEnum.ROOM_WRITE.enabled());
    features.add(FeaturesEnum.READ_IM.enabled());
    features.add(FeaturesEnum.SEND_FILES.enabled());

    usersClient.updateEntitlements(userDetail.getUserSystemInfo().getId(), features);
  }

  /**
   * Creates a symphony service bot
   * @param signUpForm the sign up form to base the bot on
   */
  public void createBot(PartnerSignUpForm signUpForm) throws ApiException {
    String userName = signUpForm.getBotEmail().split("@")[0];

    UserCreate userCreate = new UserCreate();
    UserAttributes userAttributes = new UserAttributes();
    userAttributes.setAccountType(UserAttributes.AccountTypeEnum.SYSTEM);
    userAttributes.setUserName(userName);
    userAttributes.displayName(signUpForm.getBotName());
    userAttributes.setEmailAddress(signUpForm.getBotEmail());
    userAttributes.setDepartment(signUpForm.getAppCompanyName());
    userCreate.setUserAttributes(userAttributes);

    UserDetail userV2 = usersClient.createUser(userCreate);

    FeatureList features = new FeatureList();

    features.add(FeaturesEnum.WRITE.enabled());
    features.add(FeaturesEnum.CREATE_PUBLIC_ROOM.enabled());
    features.add(FeaturesEnum.EXTERNAL.enabled());
    features.add(FeaturesEnum.SHARE_FILES_EXTERNAL.enabled());
    features.add(FeaturesEnum.ROOM_READ.enabled());
    features.add(FeaturesEnum.POST_WRITE.enabled());
    features.add(FeaturesEnum.POST_READ.enabled());
    features.add(FeaturesEnum.ROOM_WRITE.enabled());
    features.add(FeaturesEnum.READ_IM.enabled());
    features.add(FeaturesEnum.SEND_FILES.enabled());

    usersClient.updateEntitlements(userV2.getUserSystemInfo().getId(), features);
  }

  /**
   * Checks if partners already exist as symphony users.
   * @param signUpForm the sign up form containing the partners
   * @return if all partners do not exist as symphony users
   */
  public boolean allPartnersDoNotExist(PartnerSignUpForm signUpForm) throws ApiException {
    Set<String> allEmails = new HashSet<>();
    String creatorEmail = signUpForm.getCreator().getEmail();
    for(Partner partner: signUpForm.getTeam()) {
      allEmails.add(partner.getEmail());
    }
    allEmails.add(creatorEmail);

    for (String email : allEmails) {
      if (usersClient.userExistsByEmail(email)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if bot or app already exists
   * @param partnerSignUpForm the sign up form to base the bot and app info on
   * @return if the bot and app does not exist
   */
  public boolean botAndAppDoNotExist(PartnerSignUpForm partnerSignUpForm) throws ApiException {
    return usersClient.userExistsByEmail(partnerSignUpForm.getBotEmail());
  }

}
