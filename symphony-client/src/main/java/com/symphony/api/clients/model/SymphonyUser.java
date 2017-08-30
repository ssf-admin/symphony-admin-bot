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

package com.symphony.api.clients.model;

import com.symphony.api.pod.model.AvatarList;
import com.symphony.api.pod.model.UserDetail;
import com.symphony.api.pod.model.UserV2;

/**
 * Created by nick.tarsillo on 7/6/17.
 */
public class SymphonyUser extends UserV2 {
  public SymphonyUser(){}

  public SymphonyUser(UserV2 user){
    setAvatars(user.getAvatars());
    setCompany(user.getCompany());
    setDisplayName(user.getDisplayName());
    setEmailAddress(user.getEmailAddress());
    setFirstName(user.getFirstName());
    setId(user.getId());
    setLastName(user.getLastName());
    setLocation(user.getLocation());
    setTitle(user.getTitle());
    setUsername(user.getUsername());
    setDivision(user.getDivision());
    setJobFunction(user.getJobFunction());
    setMobilePhoneNumber(user.getMobilePhoneNumber());
    setWorkPhoneNumber(user.getWorkPhoneNumber());
    setDepartment(user.getDepartment());
  }

  public SymphonyUser(UserDetail user) {
    AvatarList avatars = new AvatarList();
    avatars.add(user.getAvatar());
    setAvatars(avatars);
    setDisplayName(user.getUserAttributes().getDisplayName());
    setEmailAddress(user.getUserAttributes().getEmailAddress());
    setFirstName(user.getUserAttributes().getFirstName());
    setId(user.getUserSystemInfo().getId());
    setLastName(user.getUserAttributes().getLastName());
    setLocation(user.getUserAttributes().getLocation());
    setTitle(user.getUserAttributes().getTitle());
    setUsername(user.getUserAttributes().getUserName());
  }
}
