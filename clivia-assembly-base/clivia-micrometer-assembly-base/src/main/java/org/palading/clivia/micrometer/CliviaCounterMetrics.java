package org.palading.clivia.micrometer;

import java.util.function.Consumer;

import io.micrometer.core.instrument.Counter;

/**
 * @author palading_cr
 * @title CliviaCounterMetrics
 * @project clivia
 */
public interface CliviaCounterMetrics<T> {

    public Counter create(Consumer<T> consumer);

    public void increment(Counter counter);
}
