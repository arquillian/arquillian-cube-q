package org.arquillian.cube.q.api;

import java.util.concurrent.TimeUnit;

public interface NetworkChaos {

    Action on(String machine, int port);

    public interface Action extends Q {
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

    public static enum ToxicDirectionStream {
        DOWNSTREAM, UPSTREAM
    }

    public static final class ToxicityType extends Q.FloatType {

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
         * Toxicity value. It is a percentage between 0 and 1.
         * @param toxicity
         * @return
         */
        public static ToxicityType toxicity(float toxicity) {
            return new ToxicityType(toxicity);
        }
    }

    public static final class SliceSizeVariationType extends Q.LongType {

        protected SliceSizeVariationType(long value) {
            super(value);
        }

        public static SliceSizeVariationType sliceSizeVariation(long sizeVariation) {
            return new SliceSizeVariationType(sizeVariation);
        }
    }

    public static final class SliceAverageSizeType extends Q.LongType {

        protected SliceAverageSizeType(long value) {
            super(value);
        }

        public static SliceAverageSizeType sliceAverageSize(long average) {
            return new SliceAverageSizeType(average);
        }
    }

    public static final class DelayType extends Q.LongType {

        protected DelayType(long value) {
            super(value);
        }

        public static DelayType delay(long delay) {
            return new DelayType(delay);
        }

        public static DelayType delay(long delay, TimeUnit timeUnit) {
            return new DelayType(timeUnit.toMillis(delay));
        }
    }

    public static final class RateType extends Q.LongType {

        protected RateType(long value) {
            super(value);
        }

        public static RateType rate(long rate) {
            return new RateType(rate);
        }
    }

    public static final class JitterType extends Q.IntegerType {

        protected JitterType(int value) {
            super(value);
        }

        public static JitterType jitter(int jitter) {
            return new JitterType(jitter);
        }
    }

    public static final class LatencyType extends Q.LongType {
        protected LatencyType(long value) {
            super(value);
        }

        public static LatencyType latency(long time) {
            return new LatencyType(time);
        }

        public static LatencyType latency(long time, TimeUnit timeUnit) {
            return new LatencyType(timeUnit.toMillis(time));
        }
    }

    public static final class TimeoutType extends Q.LongType {
        protected TimeoutType(long value) {
            super(value);
        }

        public static TimeoutType timeout(long time) {
            return new TimeoutType(time);
        }

        public static TimeoutType timeout(long time, TimeUnit timeUnit) {
            return new TimeoutType(timeUnit.toMillis(time));
        }
    }

}
