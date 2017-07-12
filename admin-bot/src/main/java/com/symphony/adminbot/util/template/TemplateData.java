package com.symphony.adminbot.util.template;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nick.tarsillo on 7/5/17.
 *
 * Data to generate template from
 */
public class TemplateData {
  private Map<String, String> replacementHash = new HashMap<>();

  public void addData(String replace, String replacement){
    replacementHash.put(replace, replacement);
  }

  public Map<String, String> getReplacementHash() {
    return replacementHash;
  }
}
