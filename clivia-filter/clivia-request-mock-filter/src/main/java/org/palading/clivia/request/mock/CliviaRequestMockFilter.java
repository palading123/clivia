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
package org.palading.clivia.request.mock;

import java.util.Objects;
import java.util.Optional;

import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.ApiDetail;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.domain.common.ApiMock;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;


/**
 * @author palading_cr
 * @title CliviaRequestMockFilter
 * @project clivia
 */
public class CliviaRequestMockFilter implements CliviaFilter {

    /**
     * request mock filter
     *
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiDetail apiDetail = cliviaRequestContext.getAppInfo();
        ApiMock apiMock = apiDetail.getApiMock();
        return Optional
            .ofNullable(apiMock)
            .map(apiMockDetail -> apiMockDetail.getMock())
            .filter(mockValue -> Objects.nonNull(mockValue))
            .map(
                mockValue -> exchange.getResponse().writeWith(
                    Mono.just(exchange.getResponse().bufferFactory().wrap(mockValue.getBytes()))))
            .orElseGet(() -> cliviaFilterChain.filter(exchange, cliviaFilterChain));
    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        return Optional.of(cliviaRequestContext).map(cliviaRequest -> cliviaRequest.getAppInfo())
            .map(appInfo -> appInfo.getApiMock()).map(apiMock -> apiMock.getEnabled()).orElse(false);
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_mock_order;
    }
}
