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
package org.palading.clivia.fileCore.domain;

import org.palading.clivia.support.common.domain.ApiDefaultLoadbalanceRouter;
import org.palading.clivia.support.common.domain.ApiDefaultRoute;
import org.palading.clivia.support.common.domain.common.ApiHeader;
import org.palading.clivia.support.common.domain.common.ApiReqSizeLimit;
import org.palading.clivia.support.common.domain.common.ApiRequestLimit;
import org.palading.clivia.support.common.domain.common.ApiRewrite;
import org.palading.clivia.support.common.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CliviaFileApiInfo {
    private String apiId;
    private String apiName;
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

    public static void main(String[] args) {
        List<CliviaFileApiInfo> cliviaFileApiInfos = new ArrayList<>();
        CliviaFileApiInfo tblApiInfo = new CliviaFileApiInfo();
        tblApiInfo.setApiId(UUID.randomUUID().toString().replaceAll("-", ""));
        tblApiInfo.setGroupId("1");
        // tblApiInfo.setApiId(UUID.randomUUID().toString().replaceAll("-", ""));
        tblApiInfo.setApiName("for test api1");
        tblApiInfo.setApiAuth(null);
        tblApiInfo.setMethodType("post");
        tblApiInfo.setRpcType("http");
        tblApiInfo.setBlacklistEnabled("0");
        tblApiInfo.setApiEnabled("0");
        tblApiInfo.setUrl("/org.palading.clivia.config.api/test");
        tblApiInfo.setVersion("V0.0.1");
        // tblApiInfo.setGroupId("131066a0-c413-11eb-b019-00ffa820f179");
        ApiRequestLimit apiRequestLimit = new ApiRequestLimit();
        apiRequestLimit.setEnabled(true);
        apiRequestLimit.setBurstCapacity(20);
        apiRequestLimit.setReplenishRate(10);
        tblApiInfo.setApiRequestLimit(JsonUtil.toJson(apiRequestLimit));
        ApiDefaultRoute apiDefaultRoute = new ApiDefaultRoute();
        // apiHttpRoute.setHeaders("TEST-HEADER=CLIVIA-TEST");
        apiDefaultRoute.setRetryTimes(1);
        apiDefaultRoute.setLoadbalanceType("roundRobin");
        List<ApiDefaultLoadbalanceRouter> loadbalanceRouters = new ArrayList<>();
        ApiDefaultLoadbalanceRouter loadbalanceRouter = new ApiDefaultLoadbalanceRouter();
        loadbalanceRouter.setEnabled(true);
        loadbalanceRouter.setUpstreamUrl("http://localhost:1023");
        loadbalanceRouter.setTimestamp(System.currentTimeMillis());
        loadbalanceRouter.setWarmup(1);
        loadbalanceRouter.setUpstreamWeight(1);
        ApiDefaultLoadbalanceRouter loadbalanceRouter2 = new ApiDefaultLoadbalanceRouter();
        loadbalanceRouter2.setEnabled(true);
        loadbalanceRouter2.setUpstreamUrl("http://localhost:1023");
        loadbalanceRouter2.setTimestamp(System.currentTimeMillis());
        loadbalanceRouter2.setWarmup(2);
        loadbalanceRouter2.setUpstreamWeight(3);
        loadbalanceRouters.add(loadbalanceRouter);
        loadbalanceRouters.add(loadbalanceRouter2);
        apiDefaultRoute.setLoadbalanceRouters(loadbalanceRouters);
        tblApiInfo.setApiHttpRoute(JsonUtil.toJson(apiDefaultRoute));
        ApiHeader apiHeader = new ApiHeader();
        apiHeader.setEnabled(true);
        apiHeader.setAddHeader("TEST-HEADER=CLIVIA-TEST,TEST-HEADER2=CLIVIA-TEST2,TEST-HEADER3=CLIVIA-TEST3");
        apiHeader.setRemoveHeader("TEST-HEADER2,TEST-HEADER3");
        tblApiInfo.setApiHeader(JsonUtil.toJson(apiHeader));
        ApiRewrite apiRewrite = new ApiRewrite();
        apiRewrite.setEnabled(true);
        apiRewrite.setRewritePath("/admin/server/test");
        tblApiInfo.setApiRewrite(JsonUtil.toJson(apiRewrite));
        ApiReqSizeLimit apiReqSizeLimit = new ApiReqSizeLimit(true, 500);
        tblApiInfo.setApiReqSize(JsonUtil.toJson(apiReqSizeLimit));
        tblApiInfo.setApiType("1");
        tblApiInfo.setAppKey("appKey1");
        cliviaFileApiInfos.add(tblApiInfo);

        CliviaFileApiInfo tblApiInfo2 = new CliviaFileApiInfo();
        tblApiInfo2.setApiId(UUID.randomUUID().toString().replaceAll("-", ""));
        tblApiInfo2.setGroupId("1");
        // tblApiInfo.setApiId(UUID.randomUUID().toString().replaceAll("-", ""));
        tblApiInfo2.setApiName("for test api1");
        tblApiInfo2.setApiAuth(null);
        tblApiInfo2.setMethodType("post");
        tblApiInfo2.setRpcType("http");
        tblApiInfo2.setBlacklistEnabled("0");
        tblApiInfo2.setApiEnabled("0");
        tblApiInfo2.setUrl("/org.palading.clivia.config.api/test");
        tblApiInfo2.setVersion("V0.0.2");
        // tblApiInfo.setGroupId("131066a0-c413-11eb-b019-00ffa820f179");
        ApiRequestLimit apiRequestLimit2 = new ApiRequestLimit();
        apiRequestLimit2.setEnabled(true);
        apiRequestLimit2.setBurstCapacity(20);
        apiRequestLimit2.setReplenishRate(10);
        tblApiInfo.setApiRequestLimit(JsonUtil.toJson(apiRequestLimit2));
        ApiDefaultRoute apiDefaultRoute2 = new ApiDefaultRoute();
        // apiHttpRoute.setHeaders("TEST-HEADER=CLIVIA-TEST");
        apiDefaultRoute2.setRetryTimes(1);
        apiDefaultRoute2.setLoadbalanceType("roundRobin");
        List<ApiDefaultLoadbalanceRouter> loadbalanceRouters2 = new ArrayList<>();
        ApiDefaultLoadbalanceRouter loadbalanceRouterL1 = new ApiDefaultLoadbalanceRouter();
        loadbalanceRouterL1.setEnabled(true);
        loadbalanceRouterL1.setUpstreamUrl("http://localhost:1024");
        loadbalanceRouterL1.setTimestamp(System.currentTimeMillis());
        loadbalanceRouterL1.setWarmup(1);
        loadbalanceRouterL1.setUpstreamWeight(1);
        ApiDefaultLoadbalanceRouter loadbalanceRouterL2 = new ApiDefaultLoadbalanceRouter();
        loadbalanceRouterL2.setEnabled(true);
        loadbalanceRouterL2.setUpstreamUrl("http://localhost:1025");
        loadbalanceRouterL2.setTimestamp(System.currentTimeMillis());
        loadbalanceRouterL2.setWarmup(2);
        loadbalanceRouterL2.setUpstreamWeight(3);
        loadbalanceRouters2.add(loadbalanceRouter);
        loadbalanceRouters2.add(loadbalanceRouter2);
        apiDefaultRoute2.setLoadbalanceRouters(loadbalanceRouters);
        tblApiInfo2.setApiHttpRoute(JsonUtil.toJson(apiDefaultRoute2));
        ApiHeader apiHeader2 = new ApiHeader();
        apiHeader2.setEnabled(true);
        apiHeader2.setAddHeader("TEST-HEADER=CLIVIA-TEST,TEST-HEADER2=CLIVIA-TEST2,TEST-HEADER3=CLIVIA-TEST3");
        apiHeader2.setRemoveHeader("TEST-HEADER2,TEST-HEADER3");
        tblApiInfo2.setApiHeader(JsonUtil.toJson(apiHeader));
        ApiRewrite apiRewrite2 = new ApiRewrite();
        apiRewrite2.setEnabled(true);
        apiRewrite2.setRewritePath("/admin/server/test2");
        tblApiInfo2.setApiRewrite(JsonUtil.toJson(apiRewrite2));
        ApiReqSizeLimit apiReqSizeLimit2 = new ApiReqSizeLimit(true, 500);
        tblApiInfo2.setApiReqSize(JsonUtil.toJson(apiReqSizeLimit2));
        tblApiInfo2.setApiType("1");
        tblApiInfo2.setAppKey("appKey1");
        cliviaFileApiInfos.add(tblApiInfo2);
        System.out.println(JsonUtil.toJson(cliviaFileApiInfos));
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

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
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

    public String getApiNonHttproute() {
        return apiNonHttproute;
    }

    public void setApiNonHttproute(String apiNonHttproute) {
        this.apiNonHttproute = apiNonHttproute;
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

    public String getApiParamModify() {
        return apiParamModify;
    }

    public void setApiParamModify(String apiParamModify) {
        this.apiParamModify = apiParamModify;
    }
}