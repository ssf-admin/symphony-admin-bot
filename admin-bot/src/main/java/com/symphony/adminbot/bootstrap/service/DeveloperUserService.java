package com.symphony.adminbot.bootstrap.service;

import com.symphony.adminbot.bootstrap.model.DeveloperBootstrapState;
import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.clients.UsersClient;
import com.symphony.api.pod.client.ApiException;
import com.symphony.api.pod.model.Feature;
import com.symphony.api.pod.model.FeatureList;
import com.symphony.api.pod.model.Password;
import com.symphony.api.pod.model.UserAttributes;
import com.symphony.api.pod.model.UserCreate;
import com.symphony.api.pod.model.UserDetail;
import com.symphony.security.hash.ClientHash;
import com.symphony.security.hash.IClientHash;
import com.symphony.security.utils.CryptoGenerator;

import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by nick.tarsillo on 7/2/17.
 */
public class DeveloperUserService {
  enum FeaturesEnum{
    EXTERNAL("isExternalIMEnabled"),
    SHARE_FILES_EXTERNAL("canShareFilesExternally"),
    CREATE_PUBLIC_ROOM("canCreatePublicRoom"),
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

  public DeveloperUserService(UsersClient usersClient){
    this.usersClient = usersClient;
  }

  /**
   * Creates initial states for partner within sign up form. (Team members and creator)
   * @param signUpForm the sign up form
   * @return the initial partner states
   */
  public Set<DeveloperBootstrapState> getDeveloperSetupStates(DeveloperSignUpForm signUpForm){
    Set<Developer> developerSet = new HashSet<>();
    developerSet.add(signUpForm.getCreator());
    developerSet.addAll(signUpForm.getTeam());

    Set<DeveloperBootstrapState> developerStates = new HashSet<>();
    for(Developer developer : developerSet) {
      UserCreate userCreate = new UserCreate();
      UserAttributes userAttributes = new UserAttributes();
      userAttributes.setAccountType(UserAttributes.AccountTypeEnum.NORMAL);
      userAttributes.setEmailAddress(developer.getEmail());
      userAttributes.setFirstName(developer.getFirstName());
      userAttributes.setLastName(developer.getLastName());
      userAttributes.setUserName(developer.getFirstName().toLowerCase()
          + WordUtils.capitalize(developer.getLastName()));
      userAttributes.displayName(developer.getFirstName() + " " + developer.getLastName());
      userAttributes.setDepartment(signUpForm.getAppCompanyName());
      userCreate.setUserAttributes(userAttributes);

      String randomPassword = UUID.randomUUID().toString().replace("-", "");
      int randomBegin = (int)(Math.random() * (randomPassword.length() - 3));
      int randomEnd = ThreadLocalRandom.current().nextInt(randomBegin, randomPassword.length());
      randomPassword = randomPassword.replace(randomPassword.substring(randomBegin, randomEnd),
          randomPassword.substring(randomBegin, randomEnd).toUpperCase());

      IClientHash clientHash = new ClientHash();
      String salt = CryptoGenerator.generateBase64Salt();
      String clientHashedPassword = clientHash.getClientHashedPassword(randomPassword, salt);

      Password pass = new Password();
      pass.setHPassword(clientHashedPassword);
      pass.setHSalt(salt);
      pass.setKhPassword(clientHashedPassword);
      pass.setKhSalt(salt);
      userCreate.setPassword(pass);

      List<String> roles = new ArrayList<>();
      roles.add("INDIVIDUAL");
      userCreate.setRoles(roles);

      DeveloperBootstrapState developerState = new DeveloperBootstrapState();
      developerState.setDeveloper(developer);
      developerState.setDeveloperSignUpForm(signUpForm);
      developerState.setUserCreate(userCreate);
      developerState.setPassword(randomPassword);

      Set<Developer> teamMembers = new HashSet<>(developerSet);
      teamMembers.remove(developer);
      developerState.setTeamMembers(teamMembers);

      developerStates.add(developerState);
    }

    return developerStates;
  }

  /**
   * Creates a symphony user for the partner.
   * @param state the partner's current state in the sign up process
   */
  public void createPartnerUser(DeveloperBootstrapState state) throws ApiException {
    UserDetail userDetail = usersClient.createUser(state.getUserCreate());
    state.setUserDetail(userDetail);

    FeatureList features = new FeatureList();

    features.add(FeaturesEnum.CREATE_PUBLIC_ROOM.enabled());
    features.add(FeaturesEnum.EXTERNAL.enabled());
    features.add(FeaturesEnum.SHARE_FILES_EXTERNAL.enabled());
    features.add(FeaturesEnum.SEND_FILES.enabled());

    usersClient.updateEntitlements(userDetail.getUserSystemInfo().getId(), features);
  }

  /**
   * Creates a symphony service bot
   * @param signUpForm the sign up form to base the bot on
   */
  public void createBot(DeveloperSignUpForm signUpForm) throws ApiException {
    String userName = signUpForm.getBotEmail().split("@")[0];

    UserCreate userCreate = new UserCreate();
    UserAttributes userAttributes = new UserAttributes();
    userAttributes.setAccountType(UserAttributes.AccountTypeEnum.SYSTEM);
    userAttributes.setUserName(userName);
    userAttributes.displayName(signUpForm.getBotName());
    userAttributes.setEmailAddress(signUpForm.getBotEmail());
    userAttributes.setDepartment(signUpForm.getAppCompanyName());
    userCreate.setUserAttributes(userAttributes);

    List<String> roles = new ArrayList<>();
    roles.add("INDIVIDUAL");
    userCreate.setRoles(roles);

    UserDetail userV2 = usersClient.createUser(userCreate);

    FeatureList features = new FeatureList();

    features.add(FeaturesEnum.CREATE_PUBLIC_ROOM.enabled());
    features.add(FeaturesEnum.EXTERNAL.enabled());
    features.add(FeaturesEnum.SHARE_FILES_EXTERNAL.enabled());
    features.add(FeaturesEnum.SEND_FILES.enabled());

    usersClient.updateEntitlements(userV2.getUserSystemInfo().getId(), features);
  }

  /**
   * Checks if partners already exist as symphony users.
   * @param signUpForm the sign up form containing the partners
   * @return if all partners do not exist as symphony users
   */
  public boolean allPartnersDoNotExist(DeveloperSignUpForm signUpForm) throws ApiException {
    Set<String> allEmails = new HashSet<>();
    String creatorEmail = signUpForm.getCreator().getEmail();
    for(Developer developer : signUpForm.getTeam()) {
      allEmails.add(developer.getEmail());
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
   * @param developerSignUpForm the sign up form to base the bot and app info on
   * @return if the bot and app does not exist
   */
  public boolean botAndAppDoNotExist(DeveloperSignUpForm developerSignUpForm) throws ApiException {
    return usersClient.userExistsByEmail(developerSignUpForm.getBotEmail());
  }

}
