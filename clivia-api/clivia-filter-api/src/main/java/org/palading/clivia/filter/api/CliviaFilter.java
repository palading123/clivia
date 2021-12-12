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
package org.palading.clivia.filter.api;

import io.netty.buffer.ByteBufAllocator;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author palading_cr
 * @title CliviaFilter
 * @project clivia
 */
public interface CliviaFilter {

    static final String IP_UNKNOWN = "unknown";
    static final String IP_LOCAL = "127.0.0.1";
    static final String IPV6_LOCAL = "0:0:0:0:0:0:0:1";
    static final int IP_LEN = 15;
    static final String WEB_SOCKET = "websocket";

    Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) throws Exception;

    boolean shouldFilter(ServerWebExchange exchange);

    default Mono<Void> writeResponse(final ServerWebExchange exchange, final String result) {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));
    }

    default String getBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }

    default DataBuffer getDataBufferByBodyStr(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer dataBuffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        dataBuffer.write(bytes);
        return dataBuffer;
    }

    default ServerHttpRequest getBodyFluxRequest(String param, ServerHttpRequest serverHttpRequest) {
        URI uri = serverHttpRequest.getURI();
        ServerHttpRequest request = serverHttpRequest.mutate().uri(uri).build();
        DataBuffer bodyDataBuffer = getDataBufferByBodyStr(param);
        Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
        return request = new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                return bodyFlux;
            }
        };
    }

    default MediaType getMediaType(ServerWebExchange exchange) {
        return MediaType.valueOf(Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE))
            .orElse(MediaType.APPLICATION_JSON_VALUE));
    }

    default URI rebuildUri(ServerHttpRequest serverHttpRequest, String urlParam) {
        return UriComponentsBuilder.fromUri(serverHttpRequest.getURI()).replaceQuery(urlParam).build(true).toUri();
    }

    public int getOrder();

}
