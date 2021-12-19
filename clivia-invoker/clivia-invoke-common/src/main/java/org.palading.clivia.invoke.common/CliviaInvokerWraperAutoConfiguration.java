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
package org.palading.clivia.invoke.common;

import io.micrometer.core.instrument.MeterRegistry;
import org.palading.clivia.micrometer.CliviaCountMetricsBuilderFactory;
import org.palading.clivia.micrometer.CliviaTimerMetricsBuilderFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
/**
 * @author palading_cr
 * @title CliviaInvokerWraperAutoConfiguration
 * @project clivia
 */
@Configuration
public class CliviaInvokerWraperAutoConfiguration {


    @Conditional(CliviaCommonCondition.class)
    @Bean
    public CliviaDefaultInvokerWraperFactory cliviaDefaultInvokerWraperFactory(){
        return new CliviaDefaultInvokerWraperFactory();
    }

    @Conditional(CliviaMetricsCondition.class)
    @Bean
    public CliviaMetricsInvokerWraperFactory cliviaMetricsInvokerWraperFactory(ObjectProvider<CliviaCountMetricsBuilderFactory> cliviaCountMetricsBuilderFactory, ObjectProvider<CliviaTimerMetricsBuilderFactory> cliviaTimerMetricsBuilderFactory, ObjectProvider<MeterRegistry> meterRegistries){
        return new CliviaMetricsInvokerWraperFactory(cliviaCountMetricsBuilderFactory.getIfAvailable(),cliviaTimerMetricsBuilderFactory.getIfAvailable(),meterRegistries.getIfAvailable());
    }


}
