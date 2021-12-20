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

import java.util.function.Consumer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.palading.clivia.cache.CliviaStandandCacheFactory;

/**
 * @author palading_cr
 * @title CliviaTimerMetricsBuilderFactory
 * @project clivia
 */
public class CliviaTimerMetricsBuilderFactory implements CliviaTimerMetrics<Timer.Builder> {

    private final MeterRegistry meterRegistry;
    private Timer timer;
    private String name;

    public CliviaTimerMetricsBuilderFactory(MeterRegistry meterRegistry, String name) {
        this.meterRegistry = meterRegistry;
        this.name = name;
    }

    @Override
    public Timer create(Consumer<Timer.Builder> consumer) {
        CliviaStandandCacheFactory cliviaStandandCacheFactory = CliviaStandandCacheFactory.getCliviaStandandCacheFactory();
        if (null != cliviaStandandCacheFactory.get(name)) {
            return (Timer)cliviaStandandCacheFactory.get(name);
        }
        timer = new CliviaTimerBuilder(meterRegistry, name, consumer).build();
        cliviaStandandCacheFactory.putIfAbsent(name, timer);
        return timer;
    }

    @Override
    public void stop(Timer timer, Timer.Sample sample) {
        sample.stop(timer);
    }

    @Override
    public Timer.Sample start(Timer timer) {
        Timer.Sample sample = Timer.start(meterRegistry);
        return sample;
    }
}
