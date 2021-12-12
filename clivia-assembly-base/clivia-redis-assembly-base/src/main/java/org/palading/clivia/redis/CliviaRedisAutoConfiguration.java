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

import org.palading.clivia.common.api.CliviaServerProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

/**
 * Redis configuration class supports stand-alone, sentinel mode and cluster mode
 *
 * @author palading_cr
 * @title CliviaRedisTemplateConfiguration
 * @project clivia
 *
 */
@Configuration
@ConditionalOnBean(CliviaServerProperties.class)
public class CliviaRedisAutoConfiguration {

    private static final String clivia_redis_type_standalone = "standalone";

    private static final String clivia_redis_type_sentinel = "sentinel";

    private static final String clivia_redis_type_cluster = "cluster";

    /**
     * Instantiate different redis modes by redisType property
     *
     * @author palading_cr
     *
     */
    @Bean
   AbstractRedisInitialzerFactory
        getAbstractRedisInitialzerFactory(ObjectProvider<CliviaServerProperties> cliviaServerProperties) {
        AbstractRedisInitialzerFactory abstractRedisInitialzerFactory =
            buildRedisInitialzerFactory(cliviaRedisType(cliviaServerProperties.getIfAvailable().getRedisType()));
        return abstractRedisInitialzerFactory;
    }

    private AbstractRedisInitialzerFactory buildRedisInitialzerFactory(String redisType) {
        AbstractRedisInitialzerFactory abstractRedisInitialzerFactory = null;
        switch (redisType) {
            case clivia_redis_type_sentinel:
                abstractRedisInitialzerFactory = new SentinalRedisConnectionFactory();
                break;
            case clivia_redis_type_cluster:
                abstractRedisInitialzerFactory = new ClusterRedisConnectionFactory();
                break;
            default:
                abstractRedisInitialzerFactory = new StandaloneRedisConnectionFactory();
        }
        return abstractRedisInitialzerFactory;
    }

    private String cliviaRedisType(String redisType) {
        return StringUtils.isEmpty(redisType) ? clivia_redis_type_standalone : redisType;
    }

    /**
     * Instantiate the connection factory according to different redis modes
     *
     * @author palading_cr
     *
     */
    @Bean
    LettuceConnectionFactory getJedisConnectionFactory(ObjectProvider<CliviaServerProperties> cliviaServerProperties,
        AbstractRedisInitialzerFactory abstractRedisInitialzerFactory) {
        LettuceConnectionFactory lettuceConnectionFactory =
            abstractRedisInitialzerFactory.buildLettuceConnectionFactory(cliviaServerProperties.getIfAvailable());
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }

    /**
     * Instantiate redis template operation class according to the connection factory
     *
     * @author palading_cr
     *
     */
    @Bean
    ReactiveRedisTemplate<String, String> reactiveRedisTemplate(LettuceConnectionFactory lettuceConnectionFactory) {
        RedisSerializer<String> serializer = new StringRedisSerializer();
        RedisSerializationContext<String, String> serializationContext =
            RedisSerializationContext.<String, String>newSerializationContext().key(serializer).value(serializer)
                .hashKey(serializer).hashValue(serializer).build();
        return new ReactiveRedisTemplate<>(lettuceConnectionFactory, serializationContext);
    }

}
