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

import org.palading.clivia.support.common.util.JsonUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author palading_cr
 * @title ApiLoadbalance
 * @project clivia
 */
public class ApiDefaultRoute implements Serializable {

    List<ApiDefaultLoadbalanceRouter> loadbalanceRouters;

    private String loadbalanceType;

    private String clientIp;

    private int retryTimes;

    private long timeOutMillis;

    // the serviceId is not null if excute spring cloud service
    private String serviceId;

    public static void main(String[] args) {
        ApiDefaultRoute apiDefaultRoute = new ApiDefaultRoute();
        List<ApiDefaultLoadbalanceRouter> loadbalanceRouters = new ArrayList<>();
        ApiDefaultLoadbalanceRouter router = new ApiDefaultLoadbalanceRouter();
        router.setEnabled(true);
        router.setUpstreamUrl("http://xxx/xxxx");
        router.setUpstreamWeight(10);
        router.setWarmup(1);
        router.setTimestamp(1902929303);
        ApiDefaultLoadbalanceRouter router2 = new ApiDefaultLoadbalanceRouter();
        router.setEnabled(true);
        router.setUpstreamUrl("http://xxx/xxxx2");
        router.setUpstreamWeight(20);
        router.setWarmup(1);
        router.setTimestamp(1902929304);
        loadbalanceRouters.add(router2);
        loadbalanceRouters.add(router);

        apiDefaultRoute.setLoadbalanceType("roundrobin");
        apiDefaultRoute.setRetryTimes(1);
        // apiHttpRoute.setHeaders("headerKey=headerValue,headerKey2=headerValue2");
        apiDefaultRoute.setTimeOutMillis(1000);
        apiDefaultRoute.setLoadbalanceRouters(loadbalanceRouters);
        System.out.println(JsonUtil.toJson(apiDefaultRoute));
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public long getTimeOutMillis() {
        return timeOutMillis;
    }

    public void setTimeOutMillis(long timeOutMillis) {
        this.timeOutMillis = timeOutMillis;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getLoadbalanceType() {
        return loadbalanceType;
    }

    public void setLoadbalanceType(String loadbalanceType) {
        this.loadbalanceType = loadbalanceType;
    }

    public List<ApiDefaultLoadbalanceRouter> getLoadbalanceRouters() {
        return loadbalanceRouters;
    }

    public void setLoadbalanceRouters(List<ApiDefaultLoadbalanceRouter> loadbalanceRouters) {
        this.loadbalanceRouters = loadbalanceRouters;
    }

}
