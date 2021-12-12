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

import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.util.RequestIdGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author palading_cr
 * @title CliviaWebSocketContextBuilder
 * @project clivia
 */
public class CliviaWebSocketContextBuilder implements ContextBuilder<CliviaRequestContext> {

    @Override
    public CliviaRequestContext contextBuild(ServerWebExchange serverWebExchange, CliviaRequestContext cliviaRequestContext,
        CliviaServerProperties cliviaServerProperties) {
        return cliviaRequestContext.requestTime(LocalDateTime.now()).nonce(getNonce(serverWebExchange))
            .path(buildRealPath(getPath(serverWebExchange), cliviaServerProperties)).group(getGroup(serverWebExchange))
            .version(getVersion(serverWebExchange)).sign(getSign(serverWebExchange)).requestId(RequestIdGenerator.generate())
            .client(getRemoteAddr(serverWebExchange)).appKey(getAppKey(serverWebExchange));
    }

    @Override
    public String getNonce(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getQueryParams().getFirst("nonce")).orElse("120Lkjg#");
    }

    @Override
    public boolean type(ServerWebExchange serverWebExchange) {
        String protocol = serverWebExchange.getRequest().getHeaders().getFirst("Upgrade");
        return StringUtils.isNotEmpty(protocol) && protocol.equals(WEB_SOCKET);
    }

    @Override
    public String getVersion(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getQueryParams().getFirst("version")).orElse("v1.0");

    }

    @Override
    public String getSign(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getQueryParams().getFirst("signature")).orElse(null);

    }

    @Override
    public String getAppKey(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getQueryParams().getFirst("appKey")).orElse(null);

    }

    @Override
    public String getGroup(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getQueryParams().getFirst("group")).orElse("default");

    }

}
