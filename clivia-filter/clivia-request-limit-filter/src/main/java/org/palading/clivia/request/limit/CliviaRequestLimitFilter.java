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
package org.palading.clivia.request.limit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.palading.clivia.filter.api.CliviaFilter;
import org.palading.clivia.filter.api.CliviaFilterChain;
import org.palading.clivia.support.common.constant.CliviaConstants;
import org.palading.clivia.support.common.constant.CliviaFilterOrder;
import org.palading.clivia.support.common.domain.CliviaRequestContext;
import org.palading.clivia.support.common.domain.common.ApiRequestLimit;
import org.palading.clivia.support.common.response.CliviaResponse;
import org.palading.clivia.support.common.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Using redis to Limit the request speed
 * 
 * @author palading_cr
 * @title CliviaRequestLimitFilter
 * @project clivia
 */
public class CliviaRequestLimitFilter implements CliviaFilter {

    private static final String default_error_request_limited_msg = JsonUtil.toJson(CliviaResponse.error_request_limited());

    private static Logger log = LoggerFactory.getLogger(CliviaRequestLimitFilter.class);

    private ReactiveRedisTemplate reactiveRedisTemplate;

    public CliviaRequestLimitFilter(ReactiveRedisTemplate reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    /**
     * Limit the request speed if the request is not allowed,return
     * 
     * @author palading_cr
     *
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, CliviaFilterChain cliviaFilterChain) {
        return isAllowed(exchange).flatMap(res -> {
            if (!res.getAllowed()) {
                return writeResponse(exchange, default_error_request_limited_msg);
            } else {
                return cliviaFilterChain.filter(exchange, cliviaFilterChain);
            }
        });
    }

    /**
     * enable filter if ApiRequestLimit exists and enabled property is true. Filter the requests that need to be
     * filtered
     * 
     * @author palading_cr
     *
     */
    @Override
    public boolean shouldFilter(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        return Optional.of(cliviaRequestContext).map(cliviaRequest -> cliviaRequest.getAppInfo())
            .map(appInfo -> (null != appInfo.getApiRequestLimit() && appInfo.getApiRequestLimit().getEnabled())).orElse(false);
    }

    @Override
    public int getOrder() {
        return CliviaFilterOrder.filter_redis_limit_order;
    }

    /**
     * get RedisScript
     *
     * @author palading_cr
     *
     */
    private RedisScript<List<Long>> getRedisScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("/script/clivia-request-limit.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    /**
     * from
     * https://github.com/spring-cloud/spring-cloud-gateway/blob/e61028a8b79f66a3a907b8f199454f49a10fea80/spring-cloud
     * -gateway-core/src/main/java/org/springframework/cloud/gateway/filter/ratelimit/RedisRateLimiter.java
     *
     * @author palading_cr
     *
     */
    public Mono<CliviaRedisLimitResponse> isAllowed(ServerWebExchange exchange) {
        CliviaRequestContext cliviaRequestContext =
            (CliviaRequestContext)exchange.getAttributes().get(CliviaConstants.request_context);
        ApiRequestLimit apiRequestLimit = cliviaRequestContext.getAppInfo().getApiRequestLimit();
        // It's how many requests you want to allow users to execute per second without dropping any. This is the rate
        // of token bucket filling
        int replenishRate = apiRequestLimit.getReplenishRate();
        // The maximum number of requests allowed to complete in one second. Setting this value to zero will block all
        // requests
        int burstCapacity = apiRequestLimit.getBurstCapacity();
        try {
            List<String> keys =
                getKeys(cliviaRequestContext.getGroup().concat(cliviaRequestContext.getVersion())
                    .concat(cliviaRequestContext.getAppInfo().getUrl()));
            // The arguments to the LUA script. time() returns unixtime in seconds.
            List<String> scriptArgs =
                Arrays.asList(replenishRate + "", burstCapacity + "", Instant.now().getEpochSecond() + "", "1");
            // allowed, tokens_left = redis.eval(SCRIPT, keys, args)
            // ByteBuffer byteBuffer = ByteBuffer.wrap(JsonUtil.toJson(keys).getBytes());
            // byteBuffer.put(JsonUtil.toJson(scriptArgs).getBytes());
            // Flux<List<Long>> flux =
            // reactiveRedisTemplate.execute((ReactiveRedisConnection rs) -> rs.scriptingCommands().evalSha(
            // cliviaServerProperties.getRedisLimitScriptHash(), ReturnType.fromJavaType(List.class), 2, byteBuffer));
            Flux<List<Long>> flux = reactiveRedisTemplate.execute(getRedisScript(), keys, scriptArgs);
            return flux.onErrorResume(throwable -> Flux.just(Arrays.asList(1L, -1L)))
                .reduce(new ArrayList<Long>(), (longs, l) -> {
                    longs.addAll(l);
                    return longs;
                }).map(results -> {
                    boolean allowed = results.get(0) == 1L;
                    Long tokensLeft = results.get(1);
                    CliviaRedisLimitResponse response = new CliviaRedisLimitResponse(allowed, tokensLeft);
                    if (log.isDebugEnabled()) {
                        log.debug("response: " + response);
                    }
                    return response;
                }).doOnError(ex -> log.error("---------", ex));
        } catch (Exception e) {
            /*
             * We don't want a hard dependency on Redis to allow traffic. Make sure to set
             * an alert so you know if this is happening too much. Stripe's observed
             * failure rate is 0.01%.
             */
            log.error("Error determining if user allowed from redis", e);
        }
        return Mono.just(new CliviaRedisLimitResponse(true, 0));
    }

    /**
     * @author palading_cr
     *
     */
    private List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    class CliviaRedisLimitResponse {

        private final boolean allowed;

        private final long tokensRemaining;

        public CliviaRedisLimitResponse(boolean allowed, long tokensRemaining) {
            this.allowed = allowed;
            this.tokensRemaining = tokensRemaining;
        }

        public boolean getAllowed() {
            return allowed;
        }

        public long getTokensRemaining() {
            return tokensRemaining;
        }

    }
}
