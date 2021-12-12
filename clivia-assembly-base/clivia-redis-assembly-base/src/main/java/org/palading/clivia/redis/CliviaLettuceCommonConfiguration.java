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
package org.palading.clivia.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.palading.clivia.common.api.CliviaServerProperties;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import java.time.Duration;

/**
 * @author palading_cr
 * @title CliviaLettuceConnectionFactory
 * @project clivia
 */
public class CliviaLettuceCommonConfiguration {

    /**
     * LettuceClientConfiguration build
     *
     * @author palading_cr
     *
     */
    public LettuceClientConfiguration build(CliviaServerProperties cliviaServerProperties) {
        LettuceClientConfiguration clientConfig =
            LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(defaultPropertyValue(cliviaServerProperties.getRedisTimeout(), 1000)))
                .poolConfig(genericObjectPoolConfig(cliviaServerProperties)).build();
        return clientConfig;
    }

    /**
     * GenericObjectPoolConfig
     *
     * @author palading_cr
     *
     */
    public GenericObjectPoolConfig genericObjectPoolConfig(CliviaServerProperties cliviaServerProperties) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(defaultPropertyValue(cliviaServerProperties.getRedisMaxIdle(), 5));
        config.setMaxIdle(defaultPropertyValue(cliviaServerProperties.getRedisMinIdle(), 100));
        config.setMaxTotal(defaultPropertyValue(cliviaServerProperties.getRedisMaxActive(), 10));
        return config;
    }

    /**
     * defaultPropertyValue
     *
     * @author palading_cr
     *
     */
    private int defaultPropertyValue(int propertyValue, int defaultPropertyValue) {
        return 0 == propertyValue ? defaultPropertyValue : propertyValue;
    }
}
