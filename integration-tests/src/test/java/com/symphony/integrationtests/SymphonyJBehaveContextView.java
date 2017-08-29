package com.symphony.integrationtests;

import com.symphony.integrationtests.lib.TestContext;

import org.jbehave.core.context.ContextView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymphonyJBehaveContextView implements ContextView {
  private static final Logger LOG = LoggerFactory.getLogger(SymphonyJBehaveContextView.class);

  public SymphonyJBehaveContextView() {}

  @Override
  public void show(String story, String scenario, String step) {
    story = normalizeStoryName(story);

    if (story != null) {
      TestContext.STORY_IN_PROGRESS.set(story);
    }
  }

  private String normalizeStoryName(String story) {
    if (story == null) {
      LOG.debug("called with null story name -- cannot namespace story variables!");
      return null;
    }

    // story value is like this: jbehave/stories/core_services/user_search.story
    String originalStoryString = story;
    int indexOfLastSlash = story.lastIndexOf('/');
    if (indexOfLastSlash == -1) {
      indexOfLastSlash = story.lastIndexOf('\\');
    }
    if (indexOfLastSlash != -1 && indexOfLastSlash+1 < story.length()) {
      story = story.substring(indexOfLastSlash+1);
    }
    story = story.trim();
    if (story.toLowerCase().endsWith(".story")) {
      story = story.substring(0, story.length()-".story".length());
    }
    LOG.debug("namespacing variables with " + story + " for story " + originalStoryString);
    return story;
  }

  @Override
  public void close() {}
}
