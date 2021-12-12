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
package org.palading.clivia.common.api;

/**
 * @author palading_cr
 * @title CliviaServerProperties
 * @project clivia
 */
public class CliviaServerProperties {
    private String cliviaAdminUrl;

    private String cliviaClientUrl;

    private String cliviaSecureKey;

    private String cliviaApiBakFilePath;

    private int cliviaApiBakFileLimit;

    private String dynamicFilterPath;

    private String dynamicInvokerPath;

    private String pollingIntervalHour;

    private String delaypollingIntervalHour;
    /************* cache flush period *****************/
    private long registerPeriod;
    private long serverPeriod;
    private long cacheFlushPeriod;

    private String dynamicFileType;

    private String secureFile;

    private int webClientmaxConnections;

    private int webClientConnectTimeoutMillis;

    private int webClientReadTimeout;

    private String customFilterScanPackages;

    private String customInvokerScanPackages;

    private long requestMaxSize;

    private String serverName;

    private String redisLimitScriptHash;
    /************ client check ************/
    private String checkType;

    /************ client security ************/
    private String cliviaClientName;
    private String cliviaClientPwd;

    /************** redis ****************/
    private String redisType;
    private String redisMasterName;
    private String redisUrl;
    private int redisTimeout;
    private String redisPassword;
    private int redisDatabase;
    private int redisMaxActive;
    private int redisMaxIdle;
    private int redisMinIdle;

    /************* fileRecord ******************/
    private String recordFileMaxSize;
    private String recordFilePath;
    private int recordFileMaxHistory;
    private String recordFileTotalSizeCap;

    public CliviaServerProperties(String cliviaClientUrl, String serverName) {
        this.cliviaClientUrl = cliviaClientUrl;
        this.serverName = serverName;
    }

    public CliviaServerProperties() {}

    public String getRedisMasterName() {
        return redisMasterName;
    }

    public void setRedisMasterName(String redisMasterName) {
        this.redisMasterName = redisMasterName;
    }

    public String getRedisUrl() {
        return redisUrl;
    }

    public void setRedisUrl(String redisUrl) {
        this.redisUrl = redisUrl;
    }

    public int getRedisTimeout() {
        return redisTimeout;
    }

    public void setRedisTimeout(int redisTimeout) {
        this.redisTimeout = redisTimeout;
    }

    public String getRedisPassword() {
        return redisPassword;
    }

    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }

    public int getRedisDatabase() {
        return redisDatabase;
    }

    public void setRedisDatabase(int redisDatabase) {
        this.redisDatabase = redisDatabase;
    }

    public int getRedisMaxActive() {
        return redisMaxActive;
    }

    public void setRedisMaxActive(int redisMaxActive) {
        this.redisMaxActive = redisMaxActive;
    }

    public int getRedisMaxIdle() {
        return redisMaxIdle;
    }

    public void setRedisMaxIdle(int redisMaxIdle) {
        this.redisMaxIdle = redisMaxIdle;
    }

    public int getRedisMinIdle() {
        return redisMinIdle;
    }

    public void setRedisMinIdle(int redisMinIdle) {
        this.redisMinIdle = redisMinIdle;
    }

    public String getRedisType() {
        return redisType;
    }

    public void setRedisType(String redisType) {
        this.redisType = redisType;
    }

    public String getRedisLimitScriptHash() {
        return redisLimitScriptHash;
    }

    public void setRedisLimitScriptHash(String redisLimitScriptHash) {
        this.redisLimitScriptHash = redisLimitScriptHash;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public long getRequestMaxSize() {
        return requestMaxSize;
    }

    public void setRequestMaxSize(long requestMaxSize) {
        this.requestMaxSize = requestMaxSize;
    }

    public String getCliviaClientName() {
        return cliviaClientName;
    }

    public void setCliviaClientName(String cliviaClientName) {
        this.cliviaClientName = cliviaClientName;
    }

    public String getCliviaClientPwd() {
        return cliviaClientPwd;
    }

    public void setCliviaClientPwd(String cliviaClientPwd) {
        this.cliviaClientPwd = cliviaClientPwd;
    }

    public String getCustomFilterScanPackages() {
        return customFilterScanPackages;
    }

    public void setCustomFilterScanPackages(String customFilterScanPackages) {
        this.customFilterScanPackages = customFilterScanPackages;
    }

    public String getCustomInvokerScanPackages() {
        return customInvokerScanPackages;
    }

    public void setCustomInvokerScanPackages(String customInvokerScanPackages) {
        this.customInvokerScanPackages = customInvokerScanPackages;
    }

    public int getWebClientmaxConnections() {
        return webClientmaxConnections;
    }

    public void setWebClientmaxConnections(int webClientmaxConnections) {
        this.webClientmaxConnections = webClientmaxConnections;
    }

    public int getWebClientConnectTimeoutMillis() {
        return webClientConnectTimeoutMillis;
    }

    public void setWebClientConnectTimeoutMillis(int webClientConnectTimeoutMillis) {
        this.webClientConnectTimeoutMillis = webClientConnectTimeoutMillis;
    }

    public int getWebClientReadTimeout() {
        return webClientReadTimeout;
    }

    public void setWebClientReadTimeout(int webClientReadTimeout) {
        this.webClientReadTimeout = webClientReadTimeout;
    }

    public long getCacheFlushPeriod() {
        return cacheFlushPeriod;
    }

    public void setCacheFlushPeriod(long cacheFlushPeriod) {
        this.cacheFlushPeriod = cacheFlushPeriod;
    }

    public long getRegisterPeriod() {
        return registerPeriod;
    }

    public void setRegisterPeriod(long registerPeriod) {
        this.registerPeriod = registerPeriod;
    }

    public String getSecureFile() {
        return secureFile;
    }

    public void setSecureFile(String secureFile) {
        this.secureFile = secureFile;
    }

    public String getDynamicFilterPath() {
        return dynamicFilterPath;
    }

    public void setDynamicFilterPath(String dynamicFilterPath) {
        this.dynamicFilterPath = dynamicFilterPath;
    }

    public String getDynamicFileType() {
        return dynamicFileType;
    }

    public void setDynamicFileType(String dynamicFileType) {
        this.dynamicFileType = dynamicFileType;
    }

    public String getPollingIntervalHour() {
        return pollingIntervalHour;
    }

    public void setPollingIntervalHour(String pollingIntervalHour) {
        this.pollingIntervalHour = pollingIntervalHour;
    }

    public String getDelaypollingIntervalHour() {
        return delaypollingIntervalHour;
    }

    public void setDelaypollingIntervalHour(String delaypollingIntervalHour) {
        this.delaypollingIntervalHour = delaypollingIntervalHour;
    }

    public String getCliviaApiBakFilePath() {
        return cliviaApiBakFilePath;
    }

    public void setCliviaApiBakFilePath(String cliviaApiBakFilePath) {
        this.cliviaApiBakFilePath = cliviaApiBakFilePath;
    }

    public int getCliviaApiBakFileLimit() {
        return cliviaApiBakFileLimit;
    }

    public void setCliviaApiBakFileLimit(int cliviaApiBakFileLimit) {
        this.cliviaApiBakFileLimit = cliviaApiBakFileLimit;
    }

    public String getCliviaSecureKey() {
        return cliviaSecureKey;
    }

    public void setCliviaSecureKey(String cliviaSecureKey) {
        this.cliviaSecureKey = cliviaSecureKey;
    }

    public String getCliviaAdminUrl() {
        return cliviaAdminUrl;
    }

    public void setCliviaAdminUrl(String cliviaAdminUrl) {
        this.cliviaAdminUrl = cliviaAdminUrl;
    }

    public String getCliviaClientUrl() {
        return cliviaClientUrl;
    }

    public void setCliviaClientUrl(String cliviaClientUrl) {
        this.cliviaClientUrl = cliviaClientUrl;
    }

    public long getServerPeriod() {
        return serverPeriod;
    }

    public void setServerPeriod(long serverPeriod) {
        this.serverPeriod = serverPeriod;
    }

    public String getDynamicInvokerPath() {
        return dynamicInvokerPath;
    }

    public void setDynamicInvokerPath(String dynamicInvokerPath) {
        this.dynamicInvokerPath = dynamicInvokerPath;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public String getRecordFileMaxSize() {
        return recordFileMaxSize;
    }

    public void setRecordFileMaxSize(String recordFileMaxSize) {
        this.recordFileMaxSize = recordFileMaxSize;
    }

    public String getRecordFilePath() {
        return recordFilePath;
    }

    public void setRecordFilePath(String recordFilePath) {
        this.recordFilePath = recordFilePath;
    }

    public int getRecordFileMaxHistory() {
        return recordFileMaxHistory;
    }

    public void setRecordFileMaxHistory(int recordFileMaxHistory) {
        this.recordFileMaxHistory = recordFileMaxHistory;
    }

    public String getRecordFileTotalSizeCap() {
        return recordFileTotalSizeCap;
    }

    public void setRecordFileTotalSizeCap(String recordFileTotalSizeCap) {
        this.recordFileTotalSizeCap = recordFileTotalSizeCap;
    }
}
