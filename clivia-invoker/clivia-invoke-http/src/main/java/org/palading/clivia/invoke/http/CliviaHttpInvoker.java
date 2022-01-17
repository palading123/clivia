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
package org.palading.clivia.invoke.http;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.invoker.api.CliviaInvoker;

import org.palading.clivia.loadbalance.Loadbalance;
import org.palading.clivia.spi.CliviaExtendClassLoader;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.domain.ApiDefaultLoadbalanceRouter;
import org.palading.clivia.support.common.domain.ApiDefaultRoute;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;



/**
 * http invoker
 * 
 * @author palading_cr
 * @title CliviaHttpInvoker
 * @project clivia
 */
public class CliviaHttpInvoker implements CliviaInvoker {

    private static final String default_http_invoke_error = JsonUtil.toJson(CliviaResponse.invoke_http_fail());
    private static final String invoke_http_router_not_exists = JsonUtil.toJson(CliviaResponse.invoke_http_router_not_exists());
    private static final String invoke_http_upstream_not_exists = JsonUtil.toJson(CliviaResponse.invoke_http_upstream_not_exists());

    private WebClient webClient;

    public CliviaHttpInvoker(WebClient webClient) {
        this.webClient = webClient;
    }

    private Loadbalance getLoadbalance(ApiDefaultRoute apiDefaultRoute) {
        return CliviaExtendClassLoader.getCliviaExtendClassLoaderInstance().getExtendClassInstance(
            Loadbalance.class,
            StringUtils.isNotEmpty(apiDefaultRoute.getLoadbalanceType()) ? apiDefaultRoute.getLoadbalanceType()
                : default_loadbalance_type);
    }

    private ApiDefaultLoadbalanceRouter getApiLoadbalanceRouter(ApiDefaultRoute apiDefaultRoute) {
        Loadbalance loadbalance = getLoadbalance(apiDefaultRoute);
        return loadbalance.choose(apiDefaultRoute);
    }

    @Override
    public Mono<Void> invoke(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiDefaultRoute apiDefaultRoute = cliviaRequestContext.getAppInfo().getApiDefaultRoute();
        ApiDefaultLoadbalanceRouter apiDefaultLoadbalanceRouter = getApiLoadbalanceRouter(apiDefaultRoute);
        if(null == apiDefaultLoadbalanceRouter){
            return writeResponse(exchange,invoke_http_router_not_exists);
        }
        String upstreamUrl = apiDefaultLoadbalanceRouter.getUpstreamUrl();
        if(StringUtils.isEmpty(upstreamUrl)){
            return writeResponse(exchange,invoke_http_upstream_not_exists);
        }
        String query = exchange.getRequest().getURI().getQuery();
        return httpInvoke(exchange, webClient, default_http_invoke_error,
            buildUpstreamUrl(cliviaRequestContext, upstreamUrl, query), Optional.ofNullable(apiDefaultRoute.getRetryTimes())
                .orElse(0));
    }

    private String buildUpstreamUrl(CliviaRequestContext cliviaRequestContext, String upstreamUrl, String query) {
        String rewritePath = cliviaRequestContext.getRewritePath();
        if (StringUtils.isNotEmpty(rewritePath)) {
            upstreamUrl = upstreamUrl.concat(rewritePath);
        }else{
            upstreamUrl = upstreamUrl.concat(cliviaRequestContext.getPath());
        }
        if (StringUtils.isNotEmpty(query)) {
            upstreamUrl = upstreamUrl.concat("?").concat(query);
        }
        return upstreamUrl;
    }

    @Override
    public String getRpcType() {
        return CliviaConstants.rpc_type_http;
    }
}
