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
package org.palading.clivia.invoke.config;

import org.palading.clivia.invoke.apachedubbo.CliviaApacheDubboInvoker;
import org.palading.clivia.invoke.common.CliviaCommonInvoker;
import org.palading.clivia.invoke.http.CliviaHttpInvoker;
import org.palading.clivia.invoke.springcloud.CliviaSpringcloudInvoker;
import org.palading.clivia.invoke.websocket.CliviaWebsocketInvoker;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.WebSocketService;


/**
 * @author palading_cr
 * @title CliviaFilterConfig
 * @project clivia
 */
@Configuration
public class CliviaInvokeAutoConfiguration {

    @Bean
    public CliviaHttpInvoker cliviaHttpInvoker(WebClient webClient) {
        return new CliviaHttpInvoker(webClient);
    }

    @Bean
    public CliviaApacheDubboInvoker cliviaApacheDubboInvoker() {
        return new CliviaApacheDubboInvoker();
    }

    @Bean
    public CliviaSpringcloudInvoker cliviaSpringcloudInvoker(WebClient webClient) {
        return new CliviaSpringcloudInvoker(webClient);
    }

    @Bean
    public CliviaCommonInvoker cliviaCommonInvoker() {
        return new CliviaCommonInvoker();
    }

    @Bean
    public CliviaWebsocketInvoker cliviaWebsocketInvoker(ObjectProvider<WebSocketClient> webSocketClient,
                                                         ObjectProvider<WebSocketService> webSocketService) {
        return new CliviaWebsocketInvoker(webSocketService.getIfAvailable(), webSocketClient.getIfAvailable());
    }
}
