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
package org.palading.clivia.filter.config;


import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.invoke.common.CliviaCommonInvoker;
import org.palading.clivia.invoke.common.CliviaInvokerWraper;
import org.palading.clivia.invoke.common.CliviaInvokerWraperFactory;
import org.palading.clivia.request.blacklist.CliviaRequestBlackListFilter;
import org.palading.clivia.request.build.CliviaDefaultContextBuilder;
import org.palading.clivia.request.build.CliviaRequestBuildFilter;
import org.palading.clivia.request.build.CliviaWebSocketContextBuilder;
import org.palading.clivia.request.build.ContextBuilder;
import org.palading.clivia.request.cache.CliviaRequestCacheFilter;
import org.palading.clivia.request.header.CliviaRequestAddHeaderFilter;
import org.palading.clivia.request.header.CliviaRequestRemoveHeaderFilter;
import org.palading.clivia.request.hystrix.CliviaRequestHystrixFilter;
import org.palading.clivia.request.invoke.CliviaRequestInvokeFilter;
import org.palading.clivia.request.limit.CliviaRequestLimitFilter;
import org.palading.clivia.request.mock.CliviaRequestMockFilter;
import org.palading.clivia.request.parameter.CliviaRequestAddParameterFilter;
import org.palading.clivia.request.parameter.CliviaRequestRemoveParameterFilter;
import org.palading.clivia.request.parameter.CliviaRequestUpdateParameterFilter;
import org.palading.clivia.request.rewrite.CliviaRequestPathRewriteFilter;
import org.palading.clivia.request.sign.CliviaRequestSignCheckFilter;
import org.palading.clivia.request.sizeLimit.CliviaRequestSizeLimitFilter;
import org.palading.clivia.request.switcher.CliviaRequestSwitchFilter;
import org.palading.clivia.request.websocket.CliviaRequestWebsocketRoutingFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;

import java.util.List;

/**
 * @author palading_cr
 * @title CliviaFilterConfig
 * @project clivia /15
 */

@Configuration
public class CliviaFilterAutoConfiguration {

    /**
     * fix header filter
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestAddHeaderFilter cliviaRequestHeaderFilter() {
        return new CliviaRequestAddHeaderFilter();
    }

    /**
     * redis limit
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestLimitFilter cliviaRequestLimitFilter(ReactiveRedisTemplate reactiveRedisTemplate) {
        return new CliviaRequestLimitFilter(reactiveRedisTemplate);
    }

    /**
     * request auth
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestSignCheckFilter cliviaRequestAuthFilter() {
        return new CliviaRequestSignCheckFilter();
    }

    /**
     * hystrix filter
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestHystrixFilter cliviaRequestHystrixFilter() {
        return new CliviaRequestHystrixFilter();
    }

    /**
     * default conetxt builder
     *
     * @author palading_cr
     *
     */
    @Bean
    CliviaDefaultContextBuilder cliviaDefaultContextBuilder() {
        return new CliviaDefaultContextBuilder();

    }

    /**
     * websocket context builder
     *
     * @author palading_cr
     *
     */
    @Bean
    CliviaWebSocketContextBuilder cliviaWebSocketContextBuilder() {
        return new CliviaWebSocketContextBuilder();

    }

    /**
     * build request
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestBuildFilter cliviaRequestBuildFilter(ObjectProvider<CliviaServerProperties> cliviaServerProperties,
                                                             ObjectProvider<List<ContextBuilder>> cliviaContextBuilderList) {
        return new CliviaRequestBuildFilter(cliviaServerProperties.getIfAvailable(), cliviaContextBuilderList.getIfAvailable());
    }

    /**
     * invoke request
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestInvokeFilter cliviaRequestInvokeFilter(ObjectProvider<CliviaInvokerWraperFactory> cliviaInvokerWraperFactories) {
        return new CliviaRequestInvokeFilter(cliviaInvokerWraperFactories.getIfAvailable());
    }

    /**
     * blacklist filter
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestBlackListFilter cliviaRequestBlackListFilter() {
        return new CliviaRequestBlackListFilter();
    }

    /**
     * limit request size
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestSizeLimitFilter
        cliviaRequestSizeLimitFilter(ObjectProvider<CliviaServerProperties> cliviaServerProperties) {
        return new CliviaRequestSizeLimitFilter(cliviaServerProperties.getIfAvailable());
    }

    /**
     * header remove
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestRemoveHeaderFilter cliviaRequestRemoveHeaderFilter() {
        return new CliviaRequestRemoveHeaderFilter();
    }

    /**
     * path rewrite
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestPathRewriteFilter cliviaRequestPathRewriteFilter() {
        return new CliviaRequestPathRewriteFilter();
    }

    /**
     * client switcher
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestSwitchFilter cliviaRequestSwitchFilter() {
        return new CliviaRequestSwitchFilter();
    }

    /**
     * cache request body
     *
     * @author palading_cr
     */
    @Bean
    public CliviaRequestCacheFilter cliviaRequestCacheFilter() {
        return new CliviaRequestCacheFilter();
    }

    /**
     * mock filter
     *
     * @author palading_cr
     *
     */
    @Bean
    public CliviaRequestMockFilter cliviaRequestMockFilter() {
        return new CliviaRequestMockFilter();
    }

    /**
     * Parameter add filter
     *
     * @author palading_cr
     *
     */
    @Bean
    public CliviaRequestAddParameterFilter cliviaRequestAddParameterFilter() {
        return new CliviaRequestAddParameterFilter();
    }

    /**
     * webSocketClient
     * 
     * @author palading_cr
     *
     */
    @Bean
    public WebSocketClient webSocketClient() {
        return new ReactorNettyWebSocketClient();
    }

    /**
     * webSocketService
     * 
     * @author palading_cr
     *
     */
    @Bean
    public WebSocketService webSocketService() {
        return new HandshakeWebSocketService();
    }

    /**
     * websocket filter
     * 
     * @author palading_cr
     *
     */
    @Bean
    public CliviaRequestWebsocketRoutingFilter cliviaRequestWebsocketFilter() {
        return new CliviaRequestWebsocketRoutingFilter();
    }

    /**
     * Parameter remove filter
     *
     * @author palading_cr
     *
     */
    @Bean
    public CliviaRequestRemoveParameterFilter cliviaRequestRemoveParameterFilter() {
        return new CliviaRequestRemoveParameterFilter();
    }

    /**
     * Parameter update filter
     *
     * @author palading_cr
     *
     */
    @Bean
    public CliviaRequestUpdateParameterFilter cliviaRequestUpdateParameterFilter() {
        return new CliviaRequestUpdateParameterFilter();
    }
}
