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
package org.palading.clivia.dbCore.domain;

import java.io.Serializable;

/**
 * @author palading_cr
 * @title CliviaApiDetail
 * @project clivia
 */
public class CliviaApiDetail implements Serializable {
    private String apiId;

    private String groupId;

    private String rpcType;

    private String version;

    private String apiType;

    private String apiEnabled;

    private String apiHeader;

    private String apiRewrite;

    private String url;

    private String methodType;

    private String apiReqSize;

    private String blacklistEnabled;

    private String apiHystrix;

    private String apiRequestLimit;

    private String apiHttpRoute;

    private String apiNonHttproute;

    private String apiAuth;

    private String appKey;

    private String mock;

    private String apiParamModify;

    public CliviaApiDetail(String apiId, String groupId, String rpcType, String version, String apiType, String apiEnabled,
        String apiHeader, String apiRewrite, String url, String methodType, String apiReqSize, String blacklistEnabled,
        String apiHystrix, String apiRequestLimit, String apiHttpRoute, String apiNonHttproute, String apiAuth, String appKey,
        String mock, String apiParamModify) {
        this.apiId = apiId;
        this.groupId = groupId;
        this.rpcType = rpcType;
        this.version = version;
        this.apiType = apiType;
        this.apiEnabled = apiEnabled;
        this.apiHeader = apiHeader;
        this.apiRewrite = apiRewrite;
        this.url = url;
        this.methodType = methodType;
        this.apiReqSize = apiReqSize;
        this.blacklistEnabled = blacklistEnabled;
        this.apiHystrix = apiHystrix;
        this.apiRequestLimit = apiRequestLimit;
        this.apiHttpRoute = apiHttpRoute;
        this.apiNonHttproute = apiNonHttproute;
        this.apiAuth = apiAuth;
        this.appKey = appKey;
        this.mock = mock;
        this.apiParamModify = apiParamModify;
    }

    public String getApiParamModify() {
        return apiParamModify;
    }

    public void setApiParamModify(String apiParamModify) {
        this.apiParamModify = apiParamModify;
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getRpcType() {
        return rpcType;
    }

    public void setRpcType(String rpcType) {
        this.rpcType = rpcType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiType() {
        return apiType;
    }

    public void setApiType(String apiType) {
        this.apiType = apiType;
    }

    public String getApiNonHttproute() {
        return apiNonHttproute;
    }

    public void setApiNonHttproute(String apiNonHttproute) {
        this.apiNonHttproute = apiNonHttproute;
    }

    public String getApiEnabled() {
        return apiEnabled;
    }

    public void setApiEnabled(String apiEnabled) {
        this.apiEnabled = apiEnabled;
    }

    public String getApiHeader() {
        return apiHeader;
    }

    public void setApiHeader(String apiHeader) {
        this.apiHeader = apiHeader;
    }

    public String getApiRewrite() {
        return apiRewrite;
    }

    public void setApiRewrite(String apiRewrite) {
        this.apiRewrite = apiRewrite;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getApiReqSize() {
        return apiReqSize;
    }

    public void setApiReqSize(String apiReqSize) {
        this.apiReqSize = apiReqSize;
    }

    public String getBlacklistEnabled() {
        return blacklistEnabled;
    }

    public void setBlacklistEnabled(String blacklistEnabled) {
        this.blacklistEnabled = blacklistEnabled;
    }

    public String getApiHystrix() {
        return apiHystrix;
    }

    public void setApiHystrix(String apiHystrix) {
        this.apiHystrix = apiHystrix;
    }

    public String getApiRequestLimit() {
        return apiRequestLimit;
    }

    public void setApiRequestLimit(String apiRequestLimit) {
        this.apiRequestLimit = apiRequestLimit;
    }

    public String getApiHttpRoute() {
        return apiHttpRoute;
    }

    public void setApiHttpRoute(String apiHttpRoute) {
        this.apiHttpRoute = apiHttpRoute;
    }

    public String getApiAuth() {
        return apiAuth;
    }

    public void setApiAuth(String apiAuth) {
        this.apiAuth = apiAuth;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
}
