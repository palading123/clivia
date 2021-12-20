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

/**
 * @author palading_cr
 * @title CliviaTimerBuilder
 * @project clivia
 */
public class CliviaTimerBuilder implements CliviaMetricsBuilder<Timer> {

    private final MeterRegistry meterRegistry;

    private Timer.Builder builder;

    private Consumer<Timer.Builder> consumer;

    public CliviaTimerBuilder(MeterRegistry meterRegistry, String name, Consumer<Timer.Builder> consumer) {
        this.builder = Timer.builder(name);
        this.meterRegistry = meterRegistry;
        this.consumer = consumer;
    }

    @Override
    public Timer build() {
        this.consumer.accept(builder);
        return builder.register(meterRegistry);
    }

}
