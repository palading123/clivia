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
