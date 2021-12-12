package org.palading.clivia.micrometer;

import java.util.function.Consumer;


import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.palading.clivia.cache.CliviaStandandCacheFactory;

/**
 * @author palading_cr
 * @title CliviaCountMetricsBuilderFactory
 * @project clivia
 */
public class CliviaCountMetricsBuilderFactory implements CliviaCounterMetrics<Counter.Builder> {

    private final MeterRegistry meterRegistry;
    private Counter counter;
    private String name;

    public CliviaCountMetricsBuilderFactory(MeterRegistry meterRegistry, String name) {
        this.meterRegistry = meterRegistry;
        this.name = name;
    }

    @Override
    public Counter create(Consumer<Counter.Builder> consumer) {
        CliviaStandandCacheFactory cliviaStandandCacheFactory = CliviaStandandCacheFactory.getCliviaStandandCacheFactory();
        if (null != cliviaStandandCacheFactory.get(name)) {
            return (Counter)cliviaStandandCacheFactory.get(name);
        }
        counter = new CliviaCounterBuilder(meterRegistry, name, consumer).build();
        cliviaStandandCacheFactory.putIfAbsent(name, counter);
        return counter;
    }

    @Override
    public void increment(Counter counter) {
        counter.increment();
    }
}
