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
package org.palading.clivia.event;

import org.apache.commons.lang3.StringUtils;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.palading.clivia.event.api.listener.CliviaListenerCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * configure system properties and initialize the listener collection
 * 
 * @author palading_cr
 * @title CliviaEventListerConfig
 * @project clivia
 *
 */
@Configuration
public class CliviaEventAutoConfiguration {

    private static Logger logger = LoggerFactory.getLogger(CliviaEventAutoConfiguration.class);

    /**
     * instance CliviaServerProperties
     *
     * @author palading_cr
     *
     */
    @Bean(name = "cliviaServerProperties")
    @ConditionalOnClass(CliviaServerProperties.class)
    @ConfigurationProperties(prefix = "clivia.server.config")
    public CliviaServerProperties cliviaServerProperties(ApplicationContext applicationContext) {
        StringBuilder stringBuilder = new StringBuilder();
        String cliviaClientUrl = applicationContext.getEnvironment().getProperty("clivia.server.config.cliviaClientUrl");
        String cliviaClientContext = applicationContext.getEnvironment().getProperty("server.context-path");
        String clientPort = applicationContext.getEnvironment().getProperty("server.port");
        String serverName = applicationContext.getEnvironment().getProperty("server.name");
        String cliviaClientPort = StringUtils.isEmpty(clientPort) ? "8080" : clientPort;
        try {
            if (StringUtils.isEmpty(cliviaClientUrl)) {
                stringBuilder.append("http://".concat(InetAddress.getLocalHost().getHostAddress())).append(cliviaClientPort);
                if (StringUtils.isNotEmpty(cliviaClientContext)) {
                    stringBuilder.append("/").append(cliviaClientContext);
                }
                return new CliviaServerProperties(cliviaClientUrl, serverName);
            }
        } catch (Exception e) {
            logger.error("CliviaEventConfig instance CliviaServerProperties error", e);
        }
        return new CliviaServerProperties();
    }

    /**
     * instance CliviaCompleteCacheEventListener
     *
     * @author palading_cr
     *
     */
    @Bean
    public CliviaCompleteCacheEventListener cliviaFixCacheEventListener() {
        return new CliviaCompleteCacheEventListener();
    }

    /**
     * instance CliviaGatewayServerOperationEventListener
     *
     * @author palading_cr
     *
     */
    @Bean
    public CliviaGatewayClientSwitchrEventListener getCliviaGatewayServerOperationEventListener() {
        return new CliviaGatewayClientSwitchrEventListener();
    }

    /**
     * instance CliviaGatewayRegisterEventListener
     *
     * @author palading_cr
     *
     */
    @Bean
    public CliviaGatewayRegisterEventListener getCliviaGatewayRegisterEventListener() {
        return new CliviaGatewayRegisterEventListener();
    }

    @Bean
    public CliviaGatewayClientStartedListener cliviaGatewayClientBannerListener() {
        return new CliviaGatewayClientStartedListener();
    }

    /**
     * instance CliviaEventLoadlistener
     *
     * @author palading_cr
     *
     */
    @Bean
    public CliviaEventLoadlistener getCliviaEventLoadlistener(
        final ObjectProvider<List<CliviaListenerCallable>> cliviaListenerCallableList,
        CliviaServerProperties cliviaServerProperties) {
        final List<CliviaListenerCallable> cliviaListenerCallables =
            cliviaListenerCallableList.getIfAvailable().stream().sorted(Comparator.comparing(e -> e.getOrder()))
                .collect(Collectors.toList());
        return new CliviaEventLoadlistener(cliviaListenerCallables, cliviaServerProperties);
    }
}
