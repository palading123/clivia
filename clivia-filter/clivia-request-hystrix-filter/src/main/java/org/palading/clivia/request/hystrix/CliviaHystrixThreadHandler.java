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
package org.palading.clivia.request.hystrix;

import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;

import com.netflix.hystrix.HystrixCommand;

/**
 * @author palading_cr
 * @title CliviaHystrixThread
 * @project clivia
 */
public class CliviaHystrixThreadHandler extends HystrixCommand<Mono<Void>> {

    private static Logger logger = LoggerFactory.getLogger(CliviaHystrixThreadHandler.class);

    private final ServerWebExchange exchange;

    private final CliviaFilterChain chain;

    public CliviaHystrixThreadHandler(final Setter setter, ServerWebExchange exchange, CliviaFilterChain chain) {
        super(setter);
        this.exchange = exchange;
        this.chain = chain;
    }

    @Override
    protected Mono<Void> run() throws Exception {
        RxReactiveStreams.toObservable(chain.filter(exchange, chain)).toBlocking().subscribe();
        return Mono.empty();
    }

    @Override
    protected Mono<Void> getFallback() {
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(
            Mono.just(exchange.getResponse().bufferFactory().wrap(JsonUtil.toJson(CliviaResponse.rejected()).getBytes())));
    }

    public Observable<Void> excute() {
        return RxReactiveStreams.toObservable(execute());
    }

}
