package org.arquillian.cube.q.api;

import java.util.concurrent.ThreadLocalRandom;

public class LogNormalDistribution implements Distribution {

    private double sigma;
    private long median;

    public LogNormalDistribution(long median, double sigma) {
        this.sigma = sigma;
        this.median = median;
    }

    @Override
    public long calculate() {
        return Math.round(Math.exp(ThreadLocalRandom.current().nextGaussian() * sigma) * median);
    }
}
