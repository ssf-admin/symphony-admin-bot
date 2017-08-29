package com.symphony.integrationtests.jbehave.report;

import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;
import static org.jbehave.core.reporters.Format.XML;

import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.StoryReporterBuilder;

public class SymphonyStoryReporterBuilder extends StoryReporterBuilder {

  private CrossReference xref;

  public SymphonyStoryReporterBuilder() {
    withDefaultFormats();
    withCodeLocation(CodeLocations.codeLocationFromClass(this.getClass()));
    withFormats(CONSOLE, HTML, XML);
    withFailureTrace(true);
    withFailureTraceCompression(true);
    this.xref = new CrossReference();
    withCrossReference(xref);
  }

  public CrossReference getXref() {
    return xref;
  }
}
