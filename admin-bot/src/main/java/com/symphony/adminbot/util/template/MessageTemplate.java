package com.symphony.adminbot.util.template;

/**
 * Created by nick.tarsillo on 7/4/17.
 *
 * Template for replacing symphony attributes using symphony data
 */
public class MessageTemplate {

  private String template;

  public MessageTemplate(String template){
    this.template = template;
  }

  public String buildFromData(TemplateData templateData) {
    String message = template;
    for(String replace: templateData.getReplacementHash().keySet()){
      message = replace(message, replace, templateData.getReplacementHash().get(replace));
    }

    return message;
  }

  private String replace(String template, String replace, String replacement){
    String doReplacement = template;
    if(replacement != null){
      doReplacement = doReplacement.replace(replace, replacement);
    }

    return doReplacement;
  }
}
