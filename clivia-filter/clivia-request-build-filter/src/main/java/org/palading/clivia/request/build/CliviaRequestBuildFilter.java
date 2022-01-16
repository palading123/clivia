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
package org.palading.clivia.request.build;

import org.palading.clivia.cache.DefaultCliviaCacheManager;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * build context filter
 * 
 * @author palading_cr
 * @title CliviaRequestBuildFilter
 * @project clivia
 */
public class CliviaRequestBuildFilter implements CliviaFilter {

    private static final String default_service_not_found_msg = JsonUtil.toJson(CliviaResponse.error_service_not_found());

    private static Logger logger = LoggerFactory.getLogger(CliviaRequestBuildFilter.class);

    private CliviaServerProperties cliviaServerProperties;

    private List<ContextBuilder> cliviaContextBuilderList;

    public CliviaRequestBuildFilter(CliviaServerProperties cliviaServerProperties, List<ContextBuilder> cliviaContextBuilderList) {
        this.cliviaServerProperties = cliviaServerProperties;
        this.cliviaContextBuilderList = cliviaContextBuilderList;
    }

    /**
     * request context build
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String methodType = serverHttpRequest.getMethodValue();
        if (!HttpMethod.POST.name().equals(methodType) && !HttpMethod.GET.name().equals(methodType)) {
            return writeResponse(exchange, JsonUtil.toJson(CliviaResponse.error_method_type()));
        }
        CliviaRequestContext cliviaRequestContext = buildContext(new CliviaRequestContext(), exchange, cliviaServerProperties);
        ApiDetail apiDetail = getApiDetail(cliviaRequestContext);
        if(null == apiDetail){
            return writeResponse(exchange, default_service_not_found_msg);
        }
        exchange.getAttributes().put(CliviaConstants.request_context,
                cliviaRequestContext.appInfo(apiDetail));
        return Optional.of(cliviaRequestContext).filter(u -> null == u.getAppInfo() || !u.getAppInfo().getEnabled())
            .map(a -> writeResponse(exchange, default_service_not_found_msg))
            .orElseGet(() -> cliviaFilterChain.filter(exchange, cliviaFilterChain));
    }

    /**
     * build CliviaRequestContext
     *
     * @author palading_cr
     *
     */

    private CliviaRequestContext buildContext(CliviaRequestContext context, ServerWebExchange serverWebExchange,
        CliviaServerProperties cliviaServerProperties) {
        for (ContextBuilder contextBuilder : cliviaContextBuilderList) {
            if (contextBuilder.type(serverWebExchange)) {
                return (CliviaRequestContext)contextBuilder.contextBuild(serverWebExchange, context, cliviaServerProperties);
            }
        }
        return null;
    }

    private boolean buildLocalAttribute(ServerWebExchange serverWebExchange, CliviaRequestContext cliviaRequestContext) {
        ApiDetail apiDetail = getApiDetail(cliviaRequestContext);
        if(null != apiDetail){
            serverWebExchange.getAttributes().put(CliviaConstants.request_context,
                    cliviaRequestContext.appInfo(apiDetail));
            return true;
        }
        return false;
    }

    /**
     * get ApiDetail from cache
     *
     * @author palading_cr
     *
     */
    private ApiDetail getApiDetail(CliviaRequestContext context) {
        return DefaultCliviaCacheManager.getCliviaServerCache().getApiDetailByCacheKey(context.getPath(), context.getVersion(),
            context.getGroup());
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_build_order;
    }

    /**
     * always filter
     *
     * @author palading_cr
     *
     */
    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        return true;
    }
}
