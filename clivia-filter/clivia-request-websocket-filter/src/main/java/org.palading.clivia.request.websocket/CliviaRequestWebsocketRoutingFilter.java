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
package org.palading.clivia.request.websocket;

import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import reactor.core.publisher.Mono;


/**
 * @author palading_cr
 * @title CliviaRequestWebsocketFilter
 * @project clivia
 */
public class CliviaRequestWebsocketRoutingFilter implements CliviaFilter {

    private static final String SEC_WEBSOCKET_KEY = "Sec-WebSocket-Key";

    private static final Log logger = LogFactory.getLog(CliviaRequestWebsocketRoutingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) throws Exception {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String methodType = serverHttpRequest.getMethodValue();
        if (!HttpMethod.GET.name().equals(methodType)) {
            return writeResponse(exchange, JsonUtil.toJson(CliviaResponse.error_method_type()));
        }
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (!"WebSocket".equalsIgnoreCase(headers.getUpgrade())) {
            return handleBadRequest(exchange, "Invalid 'Upgrade' header: " + headers);
        }
        List<String> connectionValue = headers.getConnection();
        if (!connectionValue.contains("Upgrade") && !connectionValue.contains("upgrade")) {
            return handleBadRequest(exchange, "Invalid 'Connection' header: " + headers);
        }
        String key = headers.getFirst(SEC_WEBSOCKET_KEY);
        if (key == null) {
            return handleBadRequest(exchange, "Missing \"Sec-WebSocket-Key\" header");
        }

        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }

    private Mono<Void> handleBadRequest(ServerWebExchange exchange, String reason) {
        if (logger.isDebugEnabled()) {
            logger.debug(exchange.getLogPrefix() + reason);
        }
        return Mono.error(new ServerWebInputException(reason));
    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        return Optional.of(cliviaRequestContext).map(cliviaRequest -> cliviaRequest.getAppInfo())
            .map(appInfo -> (null != appInfo.getRpcType() && appInfo.getRpcType().equals(CliviaConstants.rpc_type_websocket)))
            .orElse(false);
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_websocket_order;
    }
}
