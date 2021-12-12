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


import org.palading.clivia.support.common.domain.common.*;
import org.palading.clivia.support.common.util.JsonUtil;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/** @ClassName ApiInfo @Description TODO @Author palading_cr @Version 1.0 */
public class ApiDetail implements Serializable {

    private boolean enabled;
    private String group;
    private String apiId;
    private String rpcType;
    private String apiServiceType;
    private String appKey;
    private String version;

    private String url;

    private String methodType;

    private ApiNonHttpRoute apiNonHttpRoute;

    //
    private ApiHystrix apiHystrix;

    private ApiRequestLimit apiRequestLimit;

    private ApiAuth apiAuth;

    private ApiDefaultRoute apiDefaultRoute;

    private Date apiDate;

    private boolean blackListEnabled;

    private String desCode;

    // private ApiCheck apiCheck;

    private ApiReqSizeLimit apiReqSizeLimit;

    private ApiHeader apiHeader;

    private ApiRewrite apiRewrite;

    private ApiParamModify apiParamModify;

    private ApiMock apiMock;

    public static void main(String[] args) {
        List<ApiDetail> list = new ArrayList<>();
        for (int i = 0; i < 250000; i++) {
            ApiDetail apiDetail = new ApiDetail();
            apiDetail.setGroup("1");
            apiDetail.setApiId("1");
            apiDetail.setEnabled(true);
            apiDetail.setRpcType("http");
            ApiAuth apiAuth = new ApiAuth();
            apiAuth.setEnabled(true);
            apiAuth.setSecureKey(UUID.randomUUID().toString());
            apiAuth.setInvalid("20000");
            apiDetail.setApiAuth(apiAuth);
            ApiRequestLimit apiRequestLimit = new ApiRequestLimit();
            apiRequestLimit.setEnabled(true);
            apiRequestLimit.setReplenishRate(2);
            apiRequestLimit.setBurstCapacity(100);
            apiDetail.setApiRequestLimit(apiRequestLimit);
            ApiDefaultRoute apiDefaultRoute = new ApiDefaultRoute();
            apiDefaultRoute.setClientIp("1.0.1.1");
            apiDefaultRoute.setLoadbalanceType("round");
            apiDefaultRoute.setRetryTimes(3);
            apiDefaultRoute.setServiceId(null);
            List<ApiDefaultLoadbalanceRouter> loadbalanceRouters = new ArrayList<>();
            ApiDefaultLoadbalanceRouter apiDefaultLoadbalanceRouter = new ApiDefaultLoadbalanceRouter();
            apiDefaultLoadbalanceRouter.setEnabled(true);
            apiDefaultLoadbalanceRouter.setUpstreamUrl("http://172.17.26.25");
            apiDefaultLoadbalanceRouter.setUpstreamWeight(10);
            ApiDefaultLoadbalanceRouter apiDefaultLoadbalanceRouter2 = new ApiDefaultLoadbalanceRouter();
            apiDefaultLoadbalanceRouter.setEnabled(true);
            apiDefaultLoadbalanceRouter.setUpstreamUrl("http://172.17.26.24");
            apiDefaultLoadbalanceRouter.setUpstreamWeight(15);
            loadbalanceRouters.add(apiDefaultLoadbalanceRouter);
            loadbalanceRouters.add(apiDefaultLoadbalanceRouter2);
            apiDefaultRoute.setLoadbalanceRouters(loadbalanceRouters);
            apiDetail.setApiDefaultRoute(apiDefaultRoute);

            ApiHystrix apiHystrix = new ApiHystrix(10, 10, 10, 10);
            apiDetail.setApiHystrix(apiHystrix);
            apiDetail.setApiDate(new Date());
            list.add(apiDetail);
        }

        System.out.println(JsonUtil.toJson(list));

    }

    public ApiMock getApiMock() {
        return apiMock;
    }

    public void setApiMock(ApiMock apiMock) {
        this.apiMock = apiMock;
    }

    public ApiParamModify getApiParamModify() {
        return apiParamModify;
    }

    public void setApiParamModify(ApiParamModify apiParamModify) {
        this.apiParamModify = apiParamModify;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getApiServiceType() {
        return apiServiceType;
    }

    public void setApiServiceType(String apiServiceType) {
        this.apiServiceType = apiServiceType;
    }

    public ApiRewrite getApiRewrite() {
        return apiRewrite;
    }

    public void setApiRewrite(ApiRewrite apiRewrite) {
        this.apiRewrite = apiRewrite;
    }

    public ApiHeader getApiHeader() {
        return apiHeader;
    }

    public void setApiHeader(ApiHeader apiHeader) {
        this.apiHeader = apiHeader;
    }

    public ApiReqSizeLimit getApiReqSizeLimit() {
        return apiReqSizeLimit;
    }

    public void setApiReqSizeLimit(ApiReqSizeLimit apiReqSizeLimit) {
        this.apiReqSizeLimit = apiReqSizeLimit;
    }

    public String getDesCode() {
        return desCode;
    }

    public void setDesCode(String desCode) {
        this.desCode = desCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean getBlackListEnabled() {
        return blackListEnabled;
    }

    public void setBlackListEnabled(boolean blackListEnabled) {
        this.blackListEnabled = blackListEnabled;
    }

    public ApiDefaultRoute getApiDefaultRoute() {
        return apiDefaultRoute;
    }

    public void setApiDefaultRoute(ApiDefaultRoute apiDefaultRoute) {
        this.apiDefaultRoute = apiDefaultRoute;
    }

    public String getRpcType() {
        return rpcType;
    }

    public void setRpcType(String rpcType) {
        this.rpcType = rpcType;
    }

    public ApiRequestLimit getApiRequestLimit() {
        return apiRequestLimit;
    }

    public void setApiRequestLimit(ApiRequestLimit apiRequestLimit) {
        this.apiRequestLimit = apiRequestLimit;
    }

    public ApiHystrix getApiHystrix() {
        return apiHystrix;
    }

    public void setApiHystrix(ApiHystrix apiHystrix) {
        this.apiHystrix = apiHystrix;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public Date getApiDate() {
        return apiDate;
    }

    public void setApiDate(Date apiDate) {
        this.apiDate = apiDate;
    }

    public ApiAuth getApiAuth() {
        return apiAuth;
    }

    public void setApiAuth(ApiAuth apiAuth) {
        this.apiAuth = apiAuth;
    }

    public ApiNonHttpRoute getApiNonHttpRoute() {
        return apiNonHttpRoute;
    }

    public void setApiNonHttpRoute(ApiNonHttpRoute apiNonHttpRoute) {
        this.apiNonHttpRoute = apiNonHttpRoute;
    }
}
