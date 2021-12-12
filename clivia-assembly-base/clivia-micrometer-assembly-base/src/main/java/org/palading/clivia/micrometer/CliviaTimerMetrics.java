package org.palading.clivia.micrometer;

import java.util.function.Consumer;

import io.micrometer.core.instrument.Timer;

/**
 * @author palading_cr
 * @title CliviaTimerMetrics
 * @project clivia
 */
public interface CliviaTimerMetrics<T> {
    public Timer create(Consumer<T> consumer);

    public Timer.Sample start(Timer timer);

    public void stop(Timer timer, Timer.Sample sample);
}
