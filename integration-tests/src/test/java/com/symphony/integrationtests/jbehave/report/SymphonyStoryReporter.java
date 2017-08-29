package com.symphony.integrationtests.jbehave.report;

import org.jbehave.core.reporters.NullStoryReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymphonyStoryReporter extends NullStoryReporter{
  private static final Logger LOG = LoggerFactory.getLogger(SymphonyStoryReporter.class);

  @Override
  public void successful(String step) {
    LOG.info(">>Successful Step: " + step);
  }

  @Override
  public void pending(String step) {
    LOG.info(">>>Pending Step: " + step);
  }

  @Override
  public void failed(String step, Throwable cause) {
    LOG.info(">>Error: "+ step + ", Reason:" + cause);
  }


}
