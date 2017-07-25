package com.symphony.adminbot.util.template;

import java.util.LinkedHashSet;
import java.util.Set;

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
      doReplacement = doReplacement.replace("{" + replace + "}", replacement);
      doReplacement = doReplacement.replace("{#" + replace + "}", "");
      doReplacement = doReplacement.replace("{/" + replace + "}", "");
    } else {
      String[] logicalSplit = doReplacement.split("\\{#" + replace + "\\}");
      Set<String> parseLogic = new LinkedHashSet<>();
      for(String line : logicalSplit) {
        if(!line.contains("{/" + replace + "}")) {
          parseLogic.add(line);
        } else {
          String[] splitGarbage = line.split("\\{/" + replace + "\\}");
          if(splitGarbage.length > 1) {
            parseLogic.add(splitGarbage[1]);
          }
        }
      }

      doReplacement = String.join("", (String[]) parseLogic.toArray(new String[parseLogic.size()]));
    }

    return doReplacement;
  }
}
