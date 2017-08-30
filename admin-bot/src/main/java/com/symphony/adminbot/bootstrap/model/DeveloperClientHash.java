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

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Created by nick.tarsillo on 8/30/17.
 */
public class DeveloperClientHash extends PBEParametersGenerator {
  private HMac hMac = new HMac(new SHA256Digest());

  private void F(byte[] P, byte[] S, int c, byte[] iBuf, byte[] out, int outOff) {
    byte[] state = new byte[this.hMac.getMacSize()];
    CipherParameters param = new KeyParameter(P);
    this.hMac.init(param);
    if(S != null) {
      this.hMac.update(S, 0, S.length);
    }

    this.hMac.update(iBuf, 0, iBuf.length);
    this.hMac.doFinal(state, 0);
    System.arraycopy(state, 0, out, outOff, state.length);
    if(c == 0) {
      throw new IllegalArgumentException("iteration count must be at least 1.");
    } else {
      for(int count = 1; count < c; ++count) {
        this.hMac.init(param);
        this.hMac.update(state, 0, state.length);
        this.hMac.doFinal(state, 0);

        for(int j = 0; j != state.length; ++j) {
          out[outOff + j] ^= state[j];
        }
      }

    }
  }

  private void intToOctet(byte[] buf, int i) {
    buf[0] = (byte)(i >>> 24);
    buf[1] = (byte)(i >>> 16);
    buf[2] = (byte)(i >>> 8);
    buf[3] = (byte)i;
  }

  private byte[] generateDerivedKey(int dkLen) {
    int hLen = this.hMac.getMacSize();
    int l = (dkLen + hLen - 1) / hLen;
    byte[] iBuf = new byte[4];
    byte[] out = new byte[l * hLen];

    for(int i = 1; i <= l; ++i) {
      this.intToOctet(iBuf, i);
      this.F(this.password, this.salt, this.iterationCount, iBuf, out, (i - 1) * hLen);
    }

    return out;
  }

  public CipherParameters generateDerivedParameters(int keySize) {
    keySize /= 8;
    byte[] dKey = this.generateDerivedKey(keySize);
    return new KeyParameter(dKey, 0, keySize);
  }

  public CipherParameters generateDerivedParameters(int keySize, int ivSize) {
    keySize /= 8;
    ivSize /= 8;
    byte[] dKey = this.generateDerivedKey(keySize + ivSize);
    return new ParametersWithIV(new KeyParameter(dKey, 0, keySize), dKey, keySize, ivSize);
  }

  public CipherParameters generateDerivedMacParameters(int keySize) {
    return this.generateDerivedParameters(keySize);
  }

  public String getClientHashedPassword(String password, String salt) {
    this.init(password.getBytes(Charsets.UTF_8), Base64.decodeBase64(salt), 10000);
    byte[] dk = ((KeyParameter)this.generateDerivedParameters(256)).getKey();
    return Base64.encodeBase64String(dk);
  }
}
