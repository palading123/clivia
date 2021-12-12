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
import java.security.cert.X509Certificate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.ProxyProvider;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import static org.palading.clivia.invoke.config.CliviaHttpClientProperties.Pool.PoolType.DISABLED;
import static org.palading.clivia.invoke.config.CliviaHttpClientProperties.Pool.PoolType.FIXED;

/**
 * @author palading_cr
 * @title CliviaWebClientConfig
 * @project clivia
 */
@Configuration
public class CliviaWebClientAutoConfiguration {

    private static final String connectionProvider_name = "clivia_connectionProvider";

    /**
     * webClient configuration
     *
     * @author palading_cr
     *
     */

    @Bean
    public CliviaHttpClientProperties httpClientProperties() {
        return new CliviaHttpClientProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpClient gatewayHttpClient(CliviaHttpClientProperties properties) {
        // configure pool resources
        CliviaHttpClientProperties.Pool pool = properties.getPool();
        ConnectionProvider connectionProvider;
        if (pool.getType() == DISABLED) {
            connectionProvider = ConnectionProvider.newConnection();
        } else if (pool.getType() == FIXED) {
            connectionProvider =
                ConnectionProvider.fixed(null == pool.getName() ? connectionProvider_name : pool.getName(),
                    pool.getMaxConnections(), pool.getAcquireTimeout());
        } else {
            connectionProvider = ConnectionProvider.elastic(null == pool.getName() ? connectionProvider_name : pool.getName());
        }
        HttpClient httpClient = HttpClient.create(connectionProvider).httpResponseDecoder(spec -> {
            if (properties.getMaxHeaderSize() != null) {
                // cast to int is ok, since @Max is Integer.MAX_VALUE
            spec.maxHeaderSize((int)properties.getMaxHeaderSize().toBytes());
        }
        return spec;
    }   ).tcpConfiguration(tcpClient -> {
            if (properties.getConnectTimeout() != null) {
                tcpClient = tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout());
            }
            // configure proxy if proxy host is set.
            CliviaHttpClientProperties.Proxy proxy = properties.getProxy();
            if (StringUtils.hasText(proxy.getHost())) {
                tcpClient = tcpClient.proxy(proxySpec -> {
                    ProxyProvider.Builder builder = proxySpec.type(ProxyProvider.Proxy.HTTP).host(proxy.getHost());
                    PropertyMapper map = PropertyMapper.get();
                    map.from(proxy::getPort).whenNonNull().to(builder::port);
                    map.from(proxy::getUsername).whenHasText().to(builder::username);
                    map.from(proxy::getPassword).whenHasText().to(password -> builder.password(s -> password));
                    map.from(proxy::getNonProxyHostsPattern).whenHasText().to(builder::nonProxyHosts);
                });
            }
            return tcpClient;
        });
        CliviaHttpClientProperties.Ssl ssl = properties.getSsl();
        if (ssl.getTrustedX509CertificatesForTrustManager().length > 0 || ssl.isUseInsecureTrustManager()) {
            httpClient =
                httpClient.secure(sslContextSpec -> {
                    // configure ssl
                    SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
                    X509Certificate[] trustedX509Certificates = ssl.getTrustedX509CertificatesForTrustManager();
                    if (trustedX509Certificates.length > 0) {
                        sslContextBuilder.trustManager(trustedX509Certificates);
                    } else if (ssl.isUseInsecureTrustManager()) {
                        sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
                    }
                    sslContextSpec.sslContext(sslContextBuilder).defaultConfiguration(ssl.getDefaultConfigurationType())
                        .handshakeTimeout(ssl.getHandshakeTimeout()).closeNotifyFlushTimeout(ssl.getCloseNotifyFlushTimeout())
                        .closeNotifyReadTimeout(ssl.getCloseNotifyReadTimeout());
                });
        }
        if (properties.isWiretap()) {
            httpClient = httpClient.wiretap(true);
        }
        return httpClient;
    }

    /**
     * @author palading_cr
     *
     */
    @Bean
    public WebClient webClient(CliviaHttpClientProperties cliviaHttpClientProperties) {
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(gatewayHttpClient(cliviaHttpClientProperties)))
            .build();
    }

}
