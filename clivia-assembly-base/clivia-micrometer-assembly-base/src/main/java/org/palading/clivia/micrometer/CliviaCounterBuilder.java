package org.palading.clivia.micrometer;

import java.util.function.Consumer;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * @author palading_cr
 * @title CliviaCounterBuilder
 * @project clivia
 */
public class CliviaCounterBuilder implements CliviaMetricsBuilder<Counter> {

    private final MeterRegistry meterRegistry;
    private Counter.Builder builder;
    private Consumer<Counter.Builder> consumer;

    public CliviaCounterBuilder(MeterRegistry meterRegistry, String name, Consumer<Counter.Builder> consumer) {

        this.builder = Counter.builder(name);
        this.meterRegistry = meterRegistry;
        this.consumer = consumer;
    }

    @Override
    public Counter build() {
        this.consumer.accept(builder);
        return builder.register(meterRegistry);
    }
}
