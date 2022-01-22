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
package org.palading.clivia.request.blacklist;


import org.palading.clivia.cache.DefaultCliviaCacheManager;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.domain.common.ApiBlacklist;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * blacklist filter
 * 
 * @author palading_cr
 * @title CliviaBlackListFilter
 * @project clivia /15
 */
public class CliviaRequestBlackListFilter implements CliviaFilter {

    private static final String default_ip_rejected_error_msg = JsonUtil.toJson(CliviaResponse.ip_rejected());

    private static Logger logger = LoggerFactory.getLogger(CliviaRequestBlackListFilter.class);

    /**
     * blacklist filter
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        if (clientRequestLimit(cliviaRequestContext.getGroup(), cliviaRequestContext.getClient(),
            cliviaRequestContext.getRequestId())) {
            return writeResponse(exchange, default_ip_rejected_error_msg);
        }
        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }

    /**
     * If the blackListEnabled attribute of appinfo is true, it determines whether filtering is needed
     *
     * @author palading_cr
     *
     */
    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        return Optional.ofNullable(cliviaRequestContext.getAppInfo()).map(appInfo -> appInfo.getBlackListEnabled()).orElse(false);
    }

    /**
     * @author palading_cr
     *
     */
    private boolean clientRequestLimit(String group, String clientIp, String requestId) {
        return Optional
            .ofNullable(getBlackListCacheByGroup(group))
            .map(
                b -> (b.getBlackList().contains(clientIp.substring(clientIp.lastIndexOf(".")).concat("*")))
                    || b.getBlackList().contains(clientIp)).orElse(false);
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_blacklist_order;
    }

    private ApiBlacklist getBlackListCacheByGroup(String group){
        return DefaultCliviaCacheManager.getCliviaServerCache().getBlackListCacheByGroup(group);
    }


}
