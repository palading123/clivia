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
package org.palading.clivia.request.switcher;

import java.util.Optional;

import org.palading.clivia.cache.CliviaStandandCacheFactory;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;


/**
 * request can only continue when the switch is on
 * 
 * @author palading_cr
 * @title AtehnaRequestSwitchFilter
 * @project clivia
 */
public class CliviaRequestSwitchFilter implements CliviaFilter {

    /**
     * defalut error message
     *
     * @author palading_cr
     *
     */
    private static final String default_client_switch_error = JsonUtil.toJson(CliviaResponse.error_client_switch());

    /**
     * if switcher is off,return default error message.otherwise,the request can excute next filter
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        return Optional.ofNullable(CliviaStandandCacheFactory.getCliviaStandandCacheFactory())
            .map(switcher -> switcher.getString(CliviaConstants.gateway_node_switch))
            .filter(currentClientSwitcher -> CliviaConstants.gateway_client_switch_off.equals(currentClientSwitcher))
            .map(a -> writeResponse(exchange, default_client_switch_error))
            .orElseGet(() -> cliviaFilterChain.filter(exchange, cliviaFilterChain));
    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        return true;
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_switch_order;
    }
}
