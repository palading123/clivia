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
package org.palading.clivia.request.rewrite;

import java.util.Optional;

import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;


/**
 * @author palading_cr
 * @title CliviaRequestPathRewriteFilter
 * @project clivia
 */
public class CliviaRequestPathRewriteFilter implements CliviaFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        cliviaRequestContext.setRewritePath(cliviaRequestContext.getAppInfo().getApiRewrite().getRewritePath());
        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiDetail apiDetail = cliviaRequestContext.getAppInfo();
        return Optional.ofNullable(apiDetail.getApiRewrite()).map(apiRewrite -> apiRewrite.getEnabled()).orElse(false);
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_path_rewrite_order;
    }
}
