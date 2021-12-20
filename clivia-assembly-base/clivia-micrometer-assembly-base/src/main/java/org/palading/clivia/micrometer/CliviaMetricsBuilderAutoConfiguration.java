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
package org.palading.clivia.micrometer;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * @author palading_cr
 * @title CliviaMetricsBuilderAutoConfiguration
 * @project clivia
 */
@Configuration
public class CliviaMetricsBuilderAutoConfiguration {

    private static final String default_metrics_name = "clivia-metrics";

    @Bean
    @ConditionalOnMissingBean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(Environment environment) {
        return registry -> {
            registry.config().commonTags("clivia-monitor",
                environment.getProperty("spring.application.name", default_metrics_name));
        };
    }

    @Bean
    public CliviaCountMetricsBuilderFactory countMetricsBuilderFactory(ObjectProvider<MeterRegistry> meterRegistries) {
        return new CliviaCountMetricsBuilderFactory(meterRegistries.getIfAvailable(), "clivia.request.count");
    }

    @Bean
    public CliviaTimerMetricsBuilderFactory timerMetricsBuilderFactory(ObjectProvider<MeterRegistry> meterRegistries) {
        return new CliviaTimerMetricsBuilderFactory(meterRegistries.getIfAvailable(), "clivia.request.timer");
    }
}
