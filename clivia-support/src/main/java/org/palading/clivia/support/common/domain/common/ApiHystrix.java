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
package org.palading.clivia.support.common.domain.common;

import java.io.Serializable;

/**
 * @author palading_cr
 * @title ApiHystrix
 * @project clivia
 */
public class ApiHystrix implements Serializable {

    private boolean enabled;

    // There are at least 20 requests before the fuse calculates the error rate
    private int circuitBreakerRequestVolumeThreshold = 20;

    // After 5 seconds of the fuse interrupt request, it will enter the half open state, put part of the flow in the
    // past and try again
    private int circuitBreakerSleepWindowInMilliseconds = 5000;

    // Turn on the fuse protection when the error rate reaches 50%
    private int CircuitBreakerErrorThresholdPercentage = 60;

    private int executionTimeoutInMilliseconds = 3000;

    public ApiHystrix(int circuitBreakerRequestVolumeThreshold, int circuitBreakerSleepWindowInMilliseconds,
        int circuitBreakerErrorThresholdPercentage, int executionTimeoutInMilliseconds) {
        this.circuitBreakerRequestVolumeThreshold = circuitBreakerRequestVolumeThreshold;
        this.circuitBreakerSleepWindowInMilliseconds = circuitBreakerSleepWindowInMilliseconds;
        CircuitBreakerErrorThresholdPercentage = circuitBreakerErrorThresholdPercentage;
        this.executionTimeoutInMilliseconds = executionTimeoutInMilliseconds;
    }

    public int getCircuitBreakerRequestVolumeThreshold() {
        return circuitBreakerRequestVolumeThreshold;
    }

    public void setCircuitBreakerRequestVolumeThreshold(int circuitBreakerRequestVolumeThreshold) {
        this.circuitBreakerRequestVolumeThreshold = circuitBreakerRequestVolumeThreshold;
    }

    public int getCircuitBreakerSleepWindowInMilliseconds() {
        return circuitBreakerSleepWindowInMilliseconds;
    }

    public void setCircuitBreakerSleepWindowInMilliseconds(int circuitBreakerSleepWindowInMilliseconds) {
        this.circuitBreakerSleepWindowInMilliseconds = circuitBreakerSleepWindowInMilliseconds;
    }

    public int getCircuitBreakerErrorThresholdPercentage() {
        return CircuitBreakerErrorThresholdPercentage;
    }

    public void setCircuitBreakerErrorThresholdPercentage(int circuitBreakerErrorThresholdPercentage) {
        CircuitBreakerErrorThresholdPercentage = circuitBreakerErrorThresholdPercentage;
    }

    public int getExecutionTimeoutInMilliseconds() {
        return executionTimeoutInMilliseconds;
    }

    public void setExecutionTimeoutInMilliseconds(int executionTimeoutInMilliseconds) {
        this.executionTimeoutInMilliseconds = executionTimeoutInMilliseconds;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
