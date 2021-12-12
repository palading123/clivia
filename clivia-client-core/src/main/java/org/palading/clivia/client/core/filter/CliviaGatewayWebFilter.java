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
package org.palading.clivia.client.core.filter;

import org.palading.clivia.cache.DefaultCliviaCacheManager;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author palading_cr
 * @title CliviaGatewayWebHandler
 * @project clivia
 */
public class CliviaGatewayWebFilter implements WebFilter {

    private static final String default_error_msg = JsonUtil.toJson(CliviaResponse.error());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return filter(exchange, DefaultCliviaCacheManager.getCliviaServerCache().getCacheApiFilter());
    }

    public Mono<Void> filter(ServerWebExchange exchange, List<CliviaFilter> cliviaFilters) {
        CliviaFilterChain cliviaFilterChain = new CliviaFilterChain(cliviaFilters, default_error_msg);
        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }
}
