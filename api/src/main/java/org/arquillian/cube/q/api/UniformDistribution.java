package org.arquillian.cube.q.api;

import java.util.concurrent.ThreadLocalRandom;

public class UniformDistribution implements DelayDistribution {

    private long lower;
    private long upper;

    public UniformDistribution(long upper, long lower) {
        this.lower = lower;
        this.upper = upper;
    }

    @Override
    public long calculate() {
        return ThreadLocalRandom.current().nextLong(lower, upper + 1);
    }
}
