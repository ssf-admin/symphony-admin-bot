package com.symphony.adminbot.model.signup;

import com.symphony.adminbot.config.BotConfig;
import com.symphony.adminbot.commons.BotConstants;
import com.symphony.adminbot.model.signup.setup.PartnerCompanyCertSetup;
import com.symphony.adminbot.model.signup.setup.PartnerMessageSetup;
import com.symphony.adminbot.model.signup.setup.PartnerUserSetup;
import com.symphony.adminbot.util.file.ExpiringFileLoaderCache;
import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerBootstrapInfo;
import com.symphony.api.adminbot.model.PartnerSignUpForm;
import com.symphony.clients.SymphonyClient;
import com.symphony.adminbot.bots.AdminBot;

import com.symphony.adminbot.model.signup.setup.PartnerConfirmationSetup;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.pod.invoker.ApiException;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.BadRequestException;

/**
 * Created by nick.tarsillo on 7/5/17.
 */
public class PartnerSignUpService {
  private static final Logger LOG = LoggerFactory.getLogger(AdminBot.class);

  private ExpiringFileLoaderCache<Partner, PartnerState> partnerStateCache;

  private PartnerUserSetup partnerUserSetup;
  private PartnerMessageSetup partnerMessageSetup;
  private PartnerCompanyCertSetup partnerCompanyCertSetup;
  private PartnerConfirmationSetup partnerConfirmationSetup;

  public PartnerSignUpService(SymphonyClient symClient){
    partnerStateCache = new ExpiringFileLoaderCache<>(
        System.getProperty(BotConfig.FILES_JSON),
        (partner) -> partner.getEmail(),
        BotConstants.EXPIRE_TIME_DAYS,
        TimeUnit.DAYS,
        PartnerState.class);

    partnerConfirmationSetup = new PartnerConfirmationSetup();
    partnerUserSetup = new PartnerUserSetup(symClient.getUsersClient());
    partnerMessageSetup = new PartnerMessageSetup(symClient.getMessagesClient(),
        symClient.getStreamsClient());
    partnerCompanyCertSetup = new PartnerCompanyCertSetup(symClient.getSecurityClient(),
        symClient.getAttachmentsClient());

  }

  /**
   * Generate bot cert, register with pod, create bot user.
   * Generate app cert, register with pod, create app.
   * Update all team member states so they know certs were created.
   * @param partner the partner to base the bootstrap on
   */
  public void bootstrapPartner(Partner partner) throws ApiException, ExecutionException {
    PartnerState partnerState = partnerStateCache.get(partner);
    PartnerSignUpForm signUpForm = partnerState.getPartnerSignUpForm();

    if(partnerState.getCertAttachmentInfo() == null) {
      String password = partnerState.getPassword();

      String botUsername = partnerCompanyCertSetup.getDefaultBotUsername();
      botUsername = partnerCompanyCertSetup.generateAndRegisterCert(botUsername, password);

      String appUsername = null;
      if(StringUtils.isNotBlank(signUpForm.getAppName())) {
        appUsername = partnerCompanyCertSetup.generateAndRegisterCert(signUpForm.getAppName(), password);
      }

      partnerUserSetup.createBot(signUpForm);

      PartnerBootstrapInfo partnerBootstrapInfo = new PartnerBootstrapInfo();
      partnerBootstrapInfo.setBotUsername(botUsername);
      partnerBootstrapInfo.setAppId(appUsername);
      partnerBootstrapInfo.setAppName(signUpForm.getAppName());
      partnerBootstrapInfo.setBotEmail(signUpForm.getBotEmail());
      for(Partner teamMember: partnerState.getTeamMembers()){
        PartnerState teamMemberState = partnerStateCache.get(teamMember);
        teamMemberState.setCertAttachmentInfo(partnerState.getCertAttachmentInfo());
        teamMemberState.setBootstrapInfo(partnerBootstrapInfo);
      }
    }
  }

  /**
   * Send symphony message containing the bootstrap package.
   * Package contains bot, app certs and info.
   * @param partner the partner to send bootstrap package
   * @return the bootstrap info
   */
  public PartnerBootstrapInfo sendBootstrapPackage(Partner partner) throws ExecutionException {
    PartnerState partnerState = partnerStateCache.get(partner);
    partnerCompanyCertSetup.uploadCerts(partnerState);
    partnerMessageSetup.sendBootstrapMessage(partnerState);

    return partnerState.getBootstrapInfo();
  }

  /**
   * Creates partner symphony user with random temp password.
   * Sends welcome email and message in symphony to partner.
   * @param signUpForm the partner sign up form
   */
  public void welcomePartner(PartnerSignUpForm signUpForm) throws ApiException {
    Set<PartnerState> welcomeStates = partnerUserSetup.getPartnerSetupStates(signUpForm);
    for(PartnerState partnerState : welcomeStates){
      partnerStateCache.put(partnerState.getPartner(), partnerState);

      partnerConfirmationSetup.sendWelcomeEmail(partnerState);
      partnerUserSetup.createPartnerUser(partnerState);
      partnerMessageSetup.sendDirectionalMessage(partnerState);
    }
  }

  /**
   * Validates that all the fields in the sign up form are valid
   * @param signUpForm the sign up form
   */
  public void validateSignUpForm(PartnerSignUpForm signUpForm) throws ApiException {
    if(!partnerUserSetup.allPartnersDoNotExist(signUpForm)){
      throw new BadRequestException(BotConstants.USER_EXISTS);
    }

    if(partnerUserSetup.botAndAppDoNotExist(signUpForm)){
      throw new BadRequestException(BotConstants.BOT_EXISTS);
    }
  }

}
