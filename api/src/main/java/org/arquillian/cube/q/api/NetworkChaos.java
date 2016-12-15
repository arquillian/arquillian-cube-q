package org.arquillian.cube.q.api;

import java.util.concurrent.TimeUnit;

public interface NetworkChaos {

    Action on(String machine, int port);

    interface Action extends Q {
        Action down();
        Action timeout(TimeoutType timeType);
        Action timeout(TimeoutType timeType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream);
        Action latency(LatencyType latencyType);
        Action latency(LatencyType latencyType, JitterType jitterType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream);
        Action bandwidth(RateType rateType);
        Action bandwidth(RateType rateType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream);
        Action slowClose(DelayType delayType);
        Action slowClose(DelayType delayType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream);
        Action slice(SliceAverageSizeType sliceAverageSizeType, DelayType delayType);
        Action slice(SliceAverageSizeType sliceAverageSizeType, DelayType delayType, SliceSizeVariationType sliceSizeVariationType, ToxicityType toxicityType, ToxicDirectionStream toxicDirectionStream);
    }

    enum ToxicDirectionStream {
        DOWNSTREAM, UPSTREAM
    }

    /**
     * Toxicity type to set the level of toxicity to apply.
     * A toxicity represents a percentage of when apply a toxicity.
     * 1f is always applies the toxic, 0f is never.
     *
     * For example applying a toxicity of 0.5 to delay toxic, means that only half of the connections
     * done will be affected by delay toxic.
     */
    final class ToxicityType extends Q.FloatType {

        protected ToxicityType(float value) {
            super(value);
        }

        public static ToxicityType fullToxic() {
            return new ToxicityType(1f);
        }

        public static ToxicityType noToxic() {
            return new ToxicityType(0f);
        }

        /**
         * Toxicity value. It is a percentage between 0 to 1.
         * @param toxicity
         * @return
         */
        public static ToxicityType toxicity(float toxicity) {
            return new ToxicityType(toxicity);
        }

    }

    /**
     * Slice Size Variation type for slicing toxic.
     */
    final class SliceSizeVariationType extends Q.LongType {

        protected SliceSizeVariationType(long value) {
            super(value);
        }

        public static SliceSizeVariationType sliceSizeVariation(long sizeVariation) {
            return new SliceSizeVariationType(sizeVariation);
        }
    }

    /**
     * Slice Average Size type for slicing toxic.
     */
    final class SliceAverageSizeType extends Q.LongType {

        protected SliceAverageSizeType(long value) {
            super(value);
        }

        public static SliceAverageSizeType sliceAverageSize(long average) {
            return new SliceAverageSizeType(average);
        }
    }

    /**
     * Delay type to set any delay on toxics that requires a delay configuration.
     */
    class DelayType extends Q.LongType {

        protected DelayType(long value) {
            super(value);
        }

        public static DelayType delayInMillis(long delay) {
            return new DelayType(delay);
        }

        public static DelayType delay(long delay, TimeUnit timeUnit) {
            return new DelayType(timeUnit.toMillis(delay));
        }
    }

    /**
     * Type for configuring random delays.
     */
    class DistributedDelayType extends DelayType {

        private Distribution delayDistribution;

        protected DistributedDelayType(Distribution distribution, long delay) {
            super(delay);
            this.delayDistribution = distribution;
            setDistributed();
        }

        public static DistributedDelayType logNormalLatencyInMillis(long median, double sigma) {
            return new DistributedDelayType(new LogNormalDistribution(median, sigma), median);
        }

        public static DistributedDelayType logNormalLatency(long median, TimeUnit medianTimeUnit, double sigma) {
            return new DistributedDelayType(
                    new LogNormalDistribution(medianTimeUnit.toMillis(median),
                            sigma), medianTimeUnit.toMillis(median));
        }

        public static DistributedDelayType uniformLatencyInMillis(long upper, long lower) {
            return new DistributedDelayType(new UniformDistribution(upper, lower), lower);
        }

        public static DistributedDelayType uniformLatency(int upper, TimeUnit upperTimeUnit, int lower, TimeUnit lowerTimeUnit) {
            return new DistributedDelayType(
                    new UniformDistribution(upperTimeUnit.toMillis(upper),
                            lowerTimeUnit.toMillis(lower)), lowerTimeUnit.toMillis(lower));
        }

        @Override
        public void calculateValue() {
            value = this.delayDistribution.calculate();
        }

    }

    /**
     * Rate type to set in bandwidth toxic.
     */
    class RateType extends Q.LongType {

        protected RateType(long value) {
            super(value);
        }

        public static RateType rate(long rate) {
            return new RateType(rate);
        }
    }

    /**
     * Type for configuring random rates.
     */
    class DistributedRateType extends RateType {
        private Distribution delayDistribution;

        protected DistributedRateType(Distribution distribution, long rate) {
            super(rate);
            this.delayDistribution = distribution;
            setDistributed();
        }

        public static DistributedRateType logNormalLatencyInMillis(long median, double sigma) {
            return new DistributedRateType(new LogNormalDistribution(median, sigma), median);
        }

        public static DistributedRateType logNormalLatency(long median, TimeUnit medianTimeUnit, double sigma) {
            return new DistributedRateType(
                    new LogNormalDistribution(medianTimeUnit.toMillis(median),
                            sigma), medianTimeUnit.toMillis(median));
        }

        public static DistributedRateType uniformLatencyInMillis(long upper, long lower) {
            return new DistributedRateType(new UniformDistribution(upper, lower), lower);
        }

        public static DistributedRateType uniformLatency(int upper, TimeUnit upperTimeUnit, int lower, TimeUnit lowerTimeUnit) {
            return new DistributedRateType(
                    new UniformDistribution(upperTimeUnit.toMillis(upper),
                            lowerTimeUnit.toMillis(lower)), lowerTimeUnit.toMillis(lower));
        }

        @Override
        public void calculateValue() {
            value = delayDistribution.calculate();
        }
    }

    /**
     * Jitter type to set in latency toxic.
     *
     * Jitter is the variation in latency as measured in the variability over time of the packet latency across a network.
     * For example a network with constant latency has no variation (or jitter).
     */
    class JitterType extends Q.LongType {

        protected JitterType(long value) {
            super(value);
        }

        public static JitterType jitter(long jitter) {
            return new JitterType(jitter);
        }

        public static JitterType noJitter() {
            return new JitterType(0);
        }
    }

    /**
     * Type for configuring random jitter values.
     */
    class DistributedJitterType extends JitterType {
        private Distribution delayDistribution;

        protected DistributedJitterType(Distribution distribution, int jitter) {
            super(jitter);
            this.delayDistribution = distribution;
            setDistributed();
        }

        public static DistributedRateType logNormalLatencyInMillis(long median, double sigma) {
            return new DistributedRateType(new LogNormalDistribution(median, sigma), median);
        }

        public static DistributedRateType logNormalLatency(long median, TimeUnit medianTimeUnit, double sigma) {
            return new DistributedRateType(
                    new LogNormalDistribution(medianTimeUnit.toMillis(median),
                            sigma), medianTimeUnit.toMillis(median));
        }

        public static DistributedRateType uniformLatencyInMillis(long upper, long lower) {
            return new DistributedRateType(new UniformDistribution(upper, lower), lower);
        }

        public static DistributedRateType uniformLatency(int upper, TimeUnit upperTimeUnit, int lower, TimeUnit lowerTimeUnit) {
            return new DistributedRateType(
                    new UniformDistribution(upperTimeUnit.toMillis(upper),
                            lowerTimeUnit.toMillis(lower)), lowerTimeUnit.toMillis(lower));
        }

        @Override
        public void calculateValue() {
            value = delayDistribution.calculate();
        }
    }

    /**
     * Latency type to set in latency toxic.
     */
    class LatencyType extends Q.LongType {
        protected LatencyType(long value) {
            super(value);
        }

        public static LatencyType latencyInMillis(long time) {
            return new LatencyType(time);
        }

        public static LatencyType latency(long time, TimeUnit timeUnit) {
            return new LatencyType(timeUnit.toMillis(time));
        }
    }

    /**
     * Type for configuring random latency times.
     */
    class DistributedLatencyType extends LatencyType {

        private Distribution distribution;

        protected DistributedLatencyType(Distribution distribution, long value) {
            super(value);
            this.distribution = distribution;
            setDistributed();
        }

        public static DistributedLatencyType logNormalLatencyInMillis(long median, double sigma) {
            return new DistributedLatencyType(new LogNormalDistribution(median, sigma), median);
        }

        public static DistributedLatencyType logNormalLatency(long median, TimeUnit medianTimeUnit, double sigma) {
            return new DistributedLatencyType(
                    new LogNormalDistribution(medianTimeUnit.toMillis(median),
                            sigma), medianTimeUnit.toMillis(median));
        }

        public static DistributedLatencyType uniformLatencyInMillis(long upper, long lower) {
            return new DistributedLatencyType(new UniformDistribution(upper, lower), lower);
        }

        public static DistributedLatencyType uniformLatency(int upper, TimeUnit upperTimeUnit, int lower, TimeUnit lowerTimeUnit) {
            return new DistributedLatencyType(
                    new UniformDistribution(upperTimeUnit.toMillis(upper),
                            lowerTimeUnit.toMillis(lower)), lowerTimeUnit.toMillis(lower));
        }

        @Override
        public void calculateValue() {
            value = distribution.calculate();
        }
    }

    /**
     * Timeout type to set in timeout toxic.
     */
    class TimeoutType extends Q.LongType {
        protected TimeoutType(long value) {
            super(value);
        }

        public static TimeoutType timeoutInMillis(long time) {
            return new TimeoutType(time);
        }

        public static TimeoutType timeout(long time, TimeUnit timeUnit) {
            return new TimeoutType(timeUnit.toMillis(time));
        }
    }

    /**
     * Type for configuring random timeouts.
     */
    class DistributedTimeoutType extends TimeoutType {
        private Distribution distribution;

        protected DistributedTimeoutType(Distribution distribution, long value) {
            super(value);
            this.distribution = distribution;
            setDistributed();
        }

        public static DistributedTimeoutType logNormalLatencyInMillis(long median, double sigma) {
            return new DistributedTimeoutType(new LogNormalDistribution(median, sigma), median);
        }

        public static DistributedTimeoutType logNormalLatency(long median, TimeUnit medianTimeUnit, double sigma) {
            return new DistributedTimeoutType(
                    new LogNormalDistribution(medianTimeUnit.toMillis(median),
                            sigma), medianTimeUnit.toMillis(median));
        }

        public static DistributedTimeoutType uniformLatencyInMillis(long upper, long lower) {
            return new DistributedTimeoutType(new UniformDistribution(upper, lower), lower);
        }

        public static DistributedTimeoutType uniformLatency(int upper, TimeUnit upperTimeUnit, int lower, TimeUnit lowerTimeUnit) {
            return new DistributedTimeoutType(
                    new UniformDistribution(upperTimeUnit.toMillis(upper),
                            lowerTimeUnit.toMillis(lower)), lowerTimeUnit.toMillis(lower));
        }

        @Override
        public void calculateValue() {
            value = distribution.calculate();
        }
    }

}
