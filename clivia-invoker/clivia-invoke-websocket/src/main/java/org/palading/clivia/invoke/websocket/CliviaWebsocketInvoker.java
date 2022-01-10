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
package org.palading.clivia.invoke.websocket;

import static org.springframework.util.StringUtils.commaDelimitedListToStringArray;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.invoker.api.CliviaInvoker;
import org.palading.clivia.loadbalance.Loadbalance;
import org.palading.clivia.spi.CliviaExtendClassLoader;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.domain.ApiDefaultLoadbalanceRouter;
import org.palading.clivia.support.common.domain.ApiDefaultRoute;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

/**
 * websocket invoker
 * 
 * @author palading_cr
 * @title CliviaWebsocketInvoker
 * @project clivia
 */
public class CliviaWebsocketInvoker implements CliviaInvoker {

    public static final String SEC_WEBSOCKET_PROTOCOL = "Sec-WebSocket-Protocol";

    private WebSocketService webSocketService;

    private WebSocketClient webSocketClient;

    public CliviaWebsocketInvoker(WebSocketService webSocketService, WebSocketClient webSocketClient) {
        this.webSocketService = webSocketService;
        this.webSocketClient = webSocketClient;
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

    private String getWebSocketUrl(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiDefaultRoute apiDefaultRoute = cliviaRequestContext.getAppInfo().getApiDefaultRoute();
        return getApiLoadbalanceRouter(apiDefaultRoute).getUpstreamUrl();

    }

    @Override
    public Mono<Void> invoke(ServerWebExchange exchange) {
        String upstreamUrl = getWebSocketUrl(exchange);
        if (StringUtils.isEmpty(upstreamUrl) || upstreamUrl.startsWith("ws")) {
            return Mono.error(new ServerWebInputException("Bad websocket load balancing forwarding address" + upstreamUrl));
        }
        HttpHeaders headers = exchange.getRequest().getHeaders();
        HttpHeaders filtered = new HttpHeaders();
        headers.entrySet().stream().filter(entry -> !entry.getKey().toLowerCase().startsWith("sec-websocket"))
            .forEach(header -> filtered.addAll(header.getKey(), header.getValue()));
        List<String> protocols = headers.get(SEC_WEBSOCKET_PROTOCOL);
        if (protocols != null) {
            protocols =
                headers.get(SEC_WEBSOCKET_PROTOCOL).stream()
                    .flatMap(header -> Arrays.stream(commaDelimitedListToStringArray(header))).map(String::trim)
                    .collect(Collectors.toList());
        }
        return this.webSocketService.handleRequest(exchange,
            new ProxyWebSocketHandler(UriComponentsBuilder.fromUri(URI.create(upstreamUrl)).build().toUri(),
                this.webSocketClient, filtered, protocols));
    }

    @Override
    public String getRpcType() {
        return CliviaConstants.rpc_type_websocket;
    }

    private static class ProxyWebSocketHandler implements WebSocketHandler {

        private final WebSocketClient client;

        private final URI url;

        private final HttpHeaders headers;

        private final List<String> subProtocols;

        ProxyWebSocketHandler(URI url, WebSocketClient client, HttpHeaders headers, List<String> protocols) {
            this.client = client;
            this.url = url;
            this.headers = headers;
            if (protocols != null) {
                this.subProtocols = protocols;
            } else {
                this.subProtocols = Collections.emptyList();
            }
        }

        @Override
        public List<String> getSubProtocols() {
            return this.subProtocols;
        }

        @Override
        public Mono<Void> handle(WebSocketSession session) {
            // pass headers along so custom headers can be sent through
            return client.execute(url, this.headers, new WebSocketHandler() {
                @Override
                public Mono<Void> handle(WebSocketSession proxySession) {
                    // Use retain() for Reactor Netty
                    Mono<Void> proxySessionSend = proxySession.send(session.receive().doOnNext(WebSocketMessage::retain));
                    // .log("proxySessionSend", Level.FINE);
                    Mono<Void> serverSessionSend = session.send(proxySession.receive().doOnNext(WebSocketMessage::retain));
                    // .log("sessionSend", Level.FINE);
                    return Mono.zip(proxySessionSend, serverSessionSend).then();
                }

                /**
                 * Copy subProtocols so they are available downstream.
                 * 
                 * @return
                 */
                @Override
                public List<String> getSubProtocols() {
                    return ProxyWebSocketHandler.this.subProtocols;
                }
            });
        }

    }
}
