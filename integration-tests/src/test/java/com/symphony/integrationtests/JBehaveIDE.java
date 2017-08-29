package com.symphony.integrationtests;

import com.symphony.integrationtests.jbehave.report.SymphonyStoryReporter;
import com.symphony.integrationtests.jbehave.report.SymphonyStoryReporterBuilder;

import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;
import org.apache.commons.io.FileUtils;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.context.Context;
import org.jbehave.core.context.ContextView;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.MetaFilter;
import org.jbehave.core.embedder.StoryMapper;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.model.Story;
import org.jbehave.core.model.StoryMap;
import org.jbehave.core.reporters.ContextOutput;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.ContextStepMonitor;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.ScanningStepsFactory;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An example JBehave test suite to be used by IDE JUnit runner.
 * Note that Failsafe plugin doesn't pick up test results.
 */
@RunWith(JUnitReportingRunner.class)
public class JBehaveIDE extends JUnitStories {

  private static final String STEPS_PACKAGE = "com.symphony.integrationtests.jbehave.steps";

  /**
   * Meta Filters to filter out stories, e.g "-crypto *", "-xpod *", "-messages *", "-social *"
   */
  private static final String META_FILTERS = "metaFilters";
  private static final String STORY_PATH = "storyPath";
  private static final String STORY_TIMEOUT = "storyTimeout";
  private static final String MULTI_THREAD = "numberThreads";

  private Configuration configuration;
  private String storyPath;
  private String storyTimeout;
  private String multiThread;


  public JBehaveIDE() {
    FileUtils.deleteQuietly(new File("integration-tests/target/jbehave").getAbsoluteFile());

    storyPath = System.getProperty(STORY_PATH, "**" + File.separator + "*.story");
    storyTimeout = System.getProperty(STORY_TIMEOUT, "7m");
    multiThread = System.getProperty(MULTI_THREAD, "1");

    configuration = this.getConfiguration();
  }

  @Override
  public Embedder configuredEmbedder() {
    final Embedder embedder = super.configuredEmbedder();

    embedder.embedderControls().useStoryTimeouts(this.storyTimeout);
    embedder.embedderControls().doFailOnStoryTimeout(Boolean.TRUE);
    embedder.embedderControls().useThreads(Integer.parseInt(multiThread));
    embedder.embedderControls().doVerboseFailures(Boolean.TRUE);
    embedder.embedderControls().doVerboseFiltering(Boolean.FALSE);

    String metafilterProperty = System.getProperty(META_FILTERS);
    String[] metafilterProperties = null;

    if (metafilterProperty == null || metafilterProperty.isEmpty()) {
      metafilterProperties = new String[] {"-skip"};
    }
    else {
      metafilterProperty += ",-skip";
      metafilterProperties = metafilterProperty.split(",");

      for (int i = 0; i < metafilterProperties.length; i++) {
        if (!metafilterProperties[i].startsWith("+") && !metafilterProperties[i].startsWith("-")) {
          metafilterProperties[i] = "+" + metafilterProperties[i];
        }
      }
    }

    embedder.useMetaFilters(Arrays.asList(metafilterProperties));

    return embedder;
  }

  @Override
  public InjectableStepsFactory stepsFactory() {
    return new ScanningStepsFactory(configuration, STEPS_PACKAGE);
  }

  @Override
  public Configuration configuration() {
    return configuration;
  }

  private Configuration getConfiguration() {
    final CrossReference xref = new CrossReference();
    final Context context = new Context();
    final Format contextFormat = new ContextOutput(context);
    configuration = new MostUsefulConfiguration()
        .useStoryReporterBuilder(new SymphonyStoryReporterBuilder()
        .withReporters(new SymphonyStoryReporter()).withFormats(contextFormat)
        .withCrossReference(xref))
        .useFailureStrategy(new FailingUponPendingStep())
        .useStepMonitor(new ContextStepMonitor(context, new SymphonyJBehaveContextView(),
            xref.getStepMonitor()));
    return configuration;
  }

  private StoryReporterBuilder getStoryReporterBuilder() {
    StoryReporterBuilder storyReporterBuilder = new SymphonyStoryReporterBuilder();
    storyReporterBuilder.withReporters(this.getStoryReporter());
    storyReporterBuilder.withCrossReference(this.getXref());
    storyReporterBuilder.withFormats(this.getContextFormats());
    storyReporterBuilder.withFailureTraceCompression(Boolean.FALSE);
    storyReporterBuilder.withFailureTrace(Boolean.TRUE);

    return storyReporterBuilder;
  }

  private StoryReporter getStoryReporter() {
    return new SymphonyStoryReporter();
  }

  private ContextStepMonitor getContextStepMonitor() {
    return new ContextStepMonitor(this.getContext(), this.getContextView(), this.getXref().getStepMonitor());
  }

  private ContextView getContextView() {
    return new SymphonyJBehaveContextView();
  }

  private CrossReference getXref() {
    return new CrossReference();
  }

  private Context getContext() {
    return new Context();
  }

  private Format getContextFormats() {
    return new ContextOutput(this.getContext());
  }

  @Override
  protected List<String> storyPaths() {
    String codeLocation = CodeLocations.codeLocationFromClass(JBehaveIDE.class).getFile();
    List<String> storyPaths = new StoryFinder().findPaths(codeLocation, storyPath, null);
    if (hasIncludeFilters()) {
      storyPaths = filterPaths(storyPaths);
    }
    return storyPaths;
  }

  private List<String> filterPaths(List<String> pathsToFilter) {
    StoryMapper storyMapper = new StoryMapper();
    for (String path : pathsToFilter) {
      Story story =  configuredEmbedder().storyManager().storyOfPath(path);
      for (String filter : configuredEmbedder().metaFilters()) {
        storyMapper.map(story, new MetaFilter(filter));
      }
    }
    List<String> storyPaths = new ArrayList<>();
    for (StoryMap storyMap : storyMapper.getStoryMaps().getMaps()) {
      if (storyMap.getMetaFilter().startsWith("+")) {
        storyPaths.addAll(storyMap.getStoryPaths());
      }
    }
    return storyPaths;
  }

  private boolean hasIncludeFilters() {
    for (String filter : configuredEmbedder().metaFilters()) {
      if (filter.startsWith("+")) {
        return true;
      }
    }
    return false;
  }

}
