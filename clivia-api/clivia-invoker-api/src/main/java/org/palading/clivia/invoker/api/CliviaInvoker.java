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
package org.palading.clivia.invoker.api;

import io.netty.channel.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author palading_cr
 * @title CliviaInvoker
 * @project clivia
 */
public interface CliviaInvoker {

    public static final String default_loadbalance_type = "random";

    Logger logger = LoggerFactory.getLogger(CliviaInvoker.class);

    Mono<Void> invoke(ServerWebExchange exchange);

    /**
     * writeResponse
     *
     * @author palading_cr
     *
     */
    default Mono<Void> writeResponse(final ServerWebExchange exchange, final String result) {
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(result.getBytes())));
    }

    /**
     * get MediaType
     *
     * @author palading_cr
     *
     */
    default MediaType getMediaType(ServerHttpRequest serverHttpRequest) {
        return serverHttpRequest.getHeaders().getContentType();
    }

    /**
     * @author palading_cr
     *
     */
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

    /**
     * http invoke by webClient
     *
     * @author palading_cr
     *
     */
    default Mono<Void> httpInvoke(final ServerWebExchange exchange, WebClient webClient, String defaultError, String upstreamUrl,
        int retryTimes) {
        ServerHttpResponse response = exchange.getResponse();
        HttpMethod method = HttpMethod.valueOf(exchange.getRequest().getMethodValue());
        WebClient.RequestBodySpec requestBodySpec = webClient.method(method).uri(upstreamUrl);
        return requestBodySpec.headers(httpHeaders -> {
            httpHeaders.addAll(exchange.getRequest().getHeaders());
        }).contentType(exchange.getRequest().getHeaders().getContentType())
            .body(BodyInserters.fromDataBuffers(exchange.getRequest().getBody())).exchange().flatMap(res -> {
                response.getHeaders().putAll(res.headers().asHttpHeaders());
                response.setStatusCode(res.statusCode());
                response.getCookies().putAll(res.cookies());
                if (res.statusCode().is2xxSuccessful()) {
                    return response.writeWith(res.bodyToFlux(DataBuffer.class));
                } else {
                    return writeResponse(exchange, defaultError);
                }
            }).retryWhen(Retry.backoff(retryTimes, Duration.ofSeconds(3)).filter(ex -> ex instanceof ConnectTimeoutException))
            .timeout(Duration.ofSeconds(10)).doOnError(err -> logger.error("httpInvoke error", err));
    }

    /**
     * define rpcType
     *
     * @author palading_cr
     *
     */
    String getRpcType();
}
