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

package com.symphony.adminbot.bootstrap.model;


import com.symphony.api.adminbot.model.Developer;
import com.symphony.api.adminbot.model.DeveloperBootstrapInfo;
import com.symphony.api.adminbot.model.DeveloperSignUpForm;
import com.symphony.api.agent.model.AttachmentInfo;
import com.symphony.api.pod.model.ApplicationDetail;
import com.symphony.api.pod.model.CompanyCert;
import com.symphony.api.pod.model.Stream;
import com.symphony.api.pod.model.UserCreate;
import com.symphony.api.pod.model.UserDetail;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by nick.tarsillo on 7/2/17.
 *
 * Represents the current state of developer in the bootstrap process.
 */
public class DeveloperBootstrapState {
  private Developer developer;
  private DeveloperSignUpForm developerSignUpForm;
  private Set<Developer> teamMembers;
  private UserDetail userDetail;
  private UserDetail botDetail;
  private ApplicationDetail applicationDetail;
  private Stream developerIM;
  private Stream developerRoom;
  private Map<String, CompanyCert> companyCertMap;
  private List<AttachmentInfo> certAttachmentInfo;
  private DeveloperBootstrapInfo bootstrapInfo;

  public Developer getDeveloper() {
    return developer;
  }

  public void setDeveloper(Developer developer) {
    this.developer = developer;
  }

  public Set<Developer> getTeamMembers() {
    return teamMembers;
  }

  public void setTeamMembers(Set<Developer> teamMembers) {
    this.teamMembers = teamMembers;
  }

  public DeveloperSignUpForm getDeveloperSignUpForm() {
    return developerSignUpForm;
  }

  public void setDeveloperSignUpForm(DeveloperSignUpForm developerSignUpForm) {
    this.developerSignUpForm = developerSignUpForm;
  }

  public UserDetail getUserDetail() {
    return userDetail;
  }

  public void setUserDetail(UserDetail userDetail) {
    this.userDetail = userDetail;
  }

  public List<AttachmentInfo> getCertAttachmentInfo() {
    return certAttachmentInfo;
  }

  public void setCertAttachmentInfo(
      List<AttachmentInfo> certAttachmentInfo) {
    this.certAttachmentInfo = certAttachmentInfo;
  }

  public Stream getDeveloperIM() {
    return developerIM;
  }

  public void setDeveloperIM(Stream developerIM) {
    this.developerIM = developerIM;
  }

  public DeveloperBootstrapInfo getBootstrapInfo() {
    return bootstrapInfo;
  }

  public void setBootstrapInfo(DeveloperBootstrapInfo bootstrapInfo) {
    this.bootstrapInfo = bootstrapInfo;
  }

  public Map<String, CompanyCert> getCompanyCertMap() {
    return companyCertMap;
  }

  public void setCompanyCertMap(
      Map<String, CompanyCert> companyCertMap) {
    this.companyCertMap = companyCertMap;
  }

  public ApplicationDetail getApplicationDetail() {
    return applicationDetail;
  }

  public void setApplicationDetail(ApplicationDetail applicationDetail) {
    this.applicationDetail = applicationDetail;
  }

  public UserDetail getBotDetail() {
    return botDetail;
  }

  public void setBotDetail(UserDetail botDetail) {
    this.botDetail = botDetail;
  }

  public Stream getDeveloperRoom() {
    return developerRoom;
  }

  public void setDeveloperRoom(Stream developerRoom) {
    this.developerRoom = developerRoom;
  }
}
