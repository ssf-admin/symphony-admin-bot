package com.symphony.adminbot.model.signup.template;

import com.symphony.adminbot.enums.SymphonyReplacementEnums;
import com.symphony.adminbot.model.signup.PartnerState;
import com.symphony.adminbot.util.template.TemplateData;
import com.symphony.api.adminbot.model.Partner;
import com.symphony.api.adminbot.model.PartnerSignUpForm;

import org.apache.commons.lang.WordUtils;

/**
 * Created by nick.tarsillo on 7/4/17.
 */
public class PartnerTemplateData extends TemplateData {
  public PartnerTemplateData(PartnerState partnerState, String url){
    Partner partner = partnerState.getPartner();
    PartnerSignUpForm partnerSignUpForm = partnerState.getPartnerSignUpForm();

    if(partner != null) {
      addData(SymphonyReplacementEnums.FIRST_NAME.getReplacement(), partner.getFirstName());
      addData(SymphonyReplacementEnums.LAST_NAME.getReplacement(), partner.getLastName());
      addData(SymphonyReplacementEnums.EMAIL.getReplacement(), partner.getEmail());
    }
    if(partnerSignUpForm != null) {
      addData(SymphonyReplacementEnums.APP_NAME.getReplacement(), partnerSignUpForm.getAppName());
      addData(SymphonyReplacementEnums.APP_ID.getReplacement(),
          getCommonName(partnerSignUpForm.getAppName()));
      addData(SymphonyReplacementEnums.COMPANY_NAME.getReplacement(),
          partnerSignUpForm.getAppCompanyName());
      addData(SymphonyReplacementEnums.BOT_NAME.getReplacement(), partnerSignUpForm.getBotName());
    }
    if(partnerSignUpForm != null) {
      addData(SymphonyReplacementEnums.BOT_ID.getReplacement(),
          partnerState.getBootstrapInfo().getBotUsername());
      addData(SymphonyReplacementEnums.BOT_EMAIL.getReplacement(), partnerSignUpForm.getBotEmail());
    }
    if(partnerState != null) {
      addData(SymphonyReplacementEnums.PASSWORD.getReplacement(), partnerState.getPassword());
    }
    addData(SymphonyReplacementEnums.URL.getReplacement(), url);
  }

  public PartnerTemplateData(Partner partner, PartnerSignUpForm partnerSignUpForm, String password, String url){
    if(partnerSignUpForm != null){
      addData(SymphonyReplacementEnums.APP_NAME.getReplacement(), partnerSignUpForm.getAppName());
      addData(SymphonyReplacementEnums.APP_ID.getReplacement(), getCommonName(partnerSignUpForm.getAppName()));
      addData(SymphonyReplacementEnums.COMPANY_NAME.getReplacement(), partnerSignUpForm.getAppCompanyName());
      addData(SymphonyReplacementEnums.BOT_NAME.getReplacement(), partnerSignUpForm.getBotName());
      addData(SymphonyReplacementEnums.BOT_EMAIL.getReplacement(), partnerSignUpForm.getBotEmail());
    }
    if(partner != null) {
      addData(SymphonyReplacementEnums.FIRST_NAME.getReplacement(), partner.getFirstName());
      addData(SymphonyReplacementEnums.LAST_NAME.getReplacement(), partner.getLastName());
      addData(SymphonyReplacementEnums.EMAIL.getReplacement(), partner.getEmail());
    }
    addData(SymphonyReplacementEnums.PASSWORD.getReplacement(), password);
    addData(SymphonyReplacementEnums.URL.getReplacement(), url);
  }

  private String getCommonName(String name) {
    String commonName = WordUtils.capitalize(name);
    char firstIndex = commonName.charAt(0);
    commonName = commonName.replaceFirst("" + firstIndex, "" + Character.toLowerCase(firstIndex));
    commonName = commonName.replaceAll(" ", "");

    return commonName;
  }

}
