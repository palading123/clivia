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

import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.request.hystrix.CliviaHystrixThreadHandler;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.domain.common.ApiHystrix;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import rx.Observer;

import com.netflix.hystrix.*;

/**
 * @author palading_cr
 * @title CliviaRequestHystrixFilter
 * @project clivia
 */
public class CliviaRequestHystrixFilter implements CliviaFilter {

    /**
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        CliviaHystrixThreadHandler hystrixHandler =
            new CliviaHystrixThreadHandler(buildForHystrixThreadCommand(cliviaRequestContext), exchange, cliviaFilterChain);
        return Mono.create(a -> {
            hystrixHandler.excute().subscribe(new Observer<Void>() {
                @Override
                public void onCompleted() {}

                @Override
                public void onError(Throwable throwable) {
                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    exchange.getResponse().writeWith(
                        Mono.just(exchange.getResponse().bufferFactory()
                            .wrap(JsonUtil.toJson(CliviaResponse.rejected()).getBytes())));
                }

                @Override
                public void onNext(Void aVoid) {
                    cliviaFilterChain.filter(exchange, cliviaFilterChain);
                }
            });
        }).then();

    }

    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        return false;
        // CliviaRequestContext cliviaRequestContext =
        // (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        // ApiDetail apiDetail = cliviaRequestContext.getAppInfo();
        // try {
        // return Optional.ofNullable(apiDetail).map(u -> u.getEnabled() && u.getApiHystrix().getEnabled())
        // .orElseThrow(() -> new Exception("Hystrix Property is null"));
        // } catch (Exception e) {
        // return false;
        // }
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_hystrix_order;
    }

    /**
     * @author palading_cr
     *
     */
    public HystrixCommand.Setter buildForHystrixThreadCommand(CliviaRequestContext cliviaRequestContext) {
        ApiHystrix apiHystrix = cliviaRequestContext.getAppInfo().getApiHystrix();
        HystrixCommandGroupKey groupKey = HystrixCommandGroupKey.Factory.asKey(cliviaRequestContext.getGroup());
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey(cliviaRequestContext.getCacheKey());
        HystrixCommandProperties.Setter propertiesSetter =
            HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(apiHystrix.getExecutionTimeoutInMilliseconds())
                .withCircuitBreakerEnabled(true)
                .withCircuitBreakerErrorThresholdPercentage(apiHystrix.getCircuitBreakerErrorThresholdPercentage())
                .withCircuitBreakerRequestVolumeThreshold(apiHystrix.getCircuitBreakerRequestVolumeThreshold())
                .withCircuitBreakerSleepWindowInMilliseconds(apiHystrix.getCircuitBreakerSleepWindowInMilliseconds());
        HystrixThreadPoolProperties.Setter threadPoolPropertiesSetter =
            HystrixThreadPoolProperties.Setter().withCoreSize(15).withMaximumSize(20).withMaxQueueSize(800)
                .withQueueSizeRejectionThreshold(600).withKeepAliveTimeMinutes(1);
        return HystrixCommand.Setter.withGroupKey(groupKey).andCommandKey(commandKey)
            .andCommandPropertiesDefaults(propertiesSetter).andThreadPoolPropertiesDefaults(threadPoolPropertiesSetter);
    }
}
