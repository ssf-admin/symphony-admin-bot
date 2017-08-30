/*
 * Copyright 2017 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

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
