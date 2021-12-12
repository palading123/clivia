/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.palading.clivia.support.common.domain;

import java.io.Serializable;

/**
 * @author palading_cr
 * @title ApiLoadbalanceRouter
 * @project clivia
 */
public class ApiDefaultLoadbalanceRouter implements Serializable {
    private String upstreamUrl;
    private int upstreamWeight;
    private boolean enabled;
    private long timestamp;
    private int warmup;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getWarmup() {
        return warmup;
    }

    public void setWarmup(int warmup) {
        this.warmup = warmup;
    }

    public String getUpstreamUrl() {
        return upstreamUrl;
    }

    public void setUpstreamUrl(String upstreamUrl) {
        this.upstreamUrl = upstreamUrl;
    }

    public int getUpstreamWeight() {
        return upstreamWeight;
    }

    public void setUpstreamWeight(int upstreamWeight) {
        this.upstreamWeight = upstreamWeight;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
