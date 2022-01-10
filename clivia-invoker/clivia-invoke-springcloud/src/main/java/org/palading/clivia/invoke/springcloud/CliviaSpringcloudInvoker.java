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
package org.palading.clivia.invoke.springcloud;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.invoker.api.CliviaInvoker;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaResponseEnum;
import org.palading.clivia.support.common.domain.ApiDefaultRoute;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;


/**
 * excute spring cloud service
 * 
 * @author palading_cr
 * @title CliviaSpringcloudInvoker
 * @project clivia
 */
public class CliviaSpringcloudInvoker implements CliviaInvoker {

    private static final String default_springcloud_invoke_error = JsonUtil.toJson(CliviaResponse.invoke_springcloud_fail());

    private WebClient webClient;

    public CliviaSpringcloudInvoker(WebClient webClient) {
        this.webClient = webClient;
    }

    public static void main(String[] args) {}

    /**
     * excute spring cloud service. Dynamically obtain the service registry, and obtain the service list according to
     * the serviceid. The registry can be zookeeper or Eureka but I didn't find out how to call spring cloud in non SC
     * environment, which is similar to Dubbo generalization call. So, if you can solve this problem, pr it。
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> invoke(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        logger.info("CliviaHttpInvoker[invoke] current request call:[" + cliviaRequestContext.getCacheKey() + "]");
        ApiDetail apiDetail = cliviaRequestContext.getAppInfo();
        ApiDefaultRoute apiDefaultRoute = apiDetail.getApiDefaultRoute();
        checkBasicProperty(apiDefaultRoute, exchange);
        LoadBalancerClient loadBalancerClient = getLoadBalanceClient(exchange);
        if (Objects.isNull(loadBalancerClient)) {
            return writeResponse(exchange, JsonUtil.toJson(CliviaResponse.fail(
                CliviaResponseEnum.service_gateway_config_error.getCode(),
                CliviaResponseEnum.service_gateway_config_error.getMsg(), null)));
        }
        ServiceInstance serviceInstance = loadBalancerClient.choose(apiDefaultRoute.getServiceId());
        if (Objects.isNull(serviceInstance)) {
            return writeResponse(exchange, JsonUtil.toJson(CliviaResponse.fail(
                CliviaResponseEnum.service_springcloud_serviceid_error.getCode(),
                CliviaResponseEnum.service_springcloud_serviceid_error.getMsg(), null)));
        }
        String query = exchange.getRequest().getURI().getQuery();
        String upstreamUrl = serviceInstance.getUri().toASCIIString();
        if (StringUtils.isNotEmpty(query)) {
            upstreamUrl = upstreamUrl.concat("?").concat(query);
        }
        return httpInvoke(exchange, webClient, default_springcloud_invoke_error, upstreamUrl,
            Optional.ofNullable(apiDefaultRoute.getRetryTimes()).orElse(0));
    }

    /**
     * get LoadBalancerClient
     *
     * @author palading_cr
     *
     */
    private LoadBalancerClient getLoadBalanceClient(ServerWebExchange exchange) {
        return exchange.getApplicationContext().getBean("loadBalancerClient", LoadBalancerClient.class);
    }

    /**
     * checkBasicProperty if ApiLoadbalance or serviceid not exists ,return fail
     *
     * @author palading_cr
     *
     */
    private Mono<Void> checkBasicProperty(ApiDefaultRoute apiDefaultRoute, ServerWebExchange exchange) {
        if (Objects.isNull(apiDefaultRoute)) {
            return writeResponse(exchange, JsonUtil.toJson(CliviaResponse.fail(
                CliviaResponseEnum.service_config_httpOrspringcloud_error.getCode(),
                CliviaResponseEnum.service_config_httpOrspringcloud_error.getMsg(), null)));
        }
        if (StringUtils.isEmpty(apiDefaultRoute.getServiceId())) {
            return writeResponse(exchange, JsonUtil.toJson(CliviaResponse.fail(
                CliviaResponseEnum.service_springcloud_serviceId_error.getCode(),
                CliviaResponseEnum.service_springcloud_serviceId_error.getMsg(), null)));
        }
        return Mono.empty();
    }

    @Override
    public String getRpcType() {
        return CliviaConstants.rpc_type_springcloud;
    }
}
