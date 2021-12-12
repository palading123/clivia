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
