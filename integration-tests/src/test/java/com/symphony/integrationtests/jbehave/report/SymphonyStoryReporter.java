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
