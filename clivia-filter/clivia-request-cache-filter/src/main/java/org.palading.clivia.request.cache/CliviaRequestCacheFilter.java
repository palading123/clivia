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
package org.palading.clivia.request.cache;

import java.io.UnsupportedEncodingException;

import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * @author palading_cr
 * @title CliviaRequestCacheFilter
 * @project clivia
 */
public class CliviaRequestCacheFilter implements CliviaFilter {

    private static Logger logger = LoggerFactory.getLogger(CliviaRequestCacheFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        if (null != cliviaRequestContext.getRequestParam()) {
            return cliviaFilterChain.filter(exchange, cliviaFilterChain);
        }
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethodValue();
        if (CliviaConstants.clivia_support_request_method_get.equals(method)) {
            cliviaRequestContext.requestParam(request.getURI().getQuery());
            return cliviaFilterChain.filter(exchange.mutate().request(request).build(), cliviaFilterChain);
        }
        if (CliviaConstants.clivia_support_request_method_post.equals(method)) {
            // if (null == exchange.getRequest().getBody()) {
            // return cliviaFilterChain.filter(exchange.mutate().request(request).build(), cliviaFilterChain);
            // }
            return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                try {
                    cliviaRequestContext.requestParam(new String(bytes, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                DataBufferUtils.release(dataBuffer);
                Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    return Mono.just(buffer);
                });
                ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                    @Override
                    public Flux<DataBuffer> getBody() {
                        return cachedFlux;
                    }
                };
                return cliviaFilterChain.filter(exchange.mutate().request(mutatedRequest).build(), cliviaFilterChain);
            }).switchIfEmpty(cliviaFilterChain.filter(exchange, cliviaFilterChain));
        }
        return cliviaFilterChain.filter(exchange, cliviaFilterChain);
    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        return true;
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_cache_order;
    }
}
