package org.arquillian.cube.q.api;

import java.util.concurrent.TimeUnit;

public interface Q {

    Action on(String machine, int port);
    
    public interface Action {

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

        void exec() throws Exception;
        void exec(Perform perform) throws Exception;
        void exec(RunCondition runCondition, Perform perform) throws Exception;

    }
    
    public interface Perform {
        
        void execute() throws Exception;
    }

    public interface RunCondition {
        boolean isExecutable();
    }

    public static enum ToxicDirectionStream {
        DOWNSTREAM, UPSTREAM
    }

    public static class DurationRunCondition implements RunCondition {

        protected long finishTime;

        protected DurationRunCondition(long duration, TimeUnit unit) {
            final long durationInMillis = unit.toMillis(duration);
            this.finishTime = System.currentTimeMillis() + durationInMillis;
        }

        public static DurationRunCondition during(long duration, TimeUnit timeUnit) {
            return new DurationRunCondition(duration, timeUnit);
        }

        @Override
        public boolean isExecutable() {
            return System.currentTimeMillis() < finishTime;
        }
    }

    public static class IterationRunCondition implements RunCondition {
        protected final long iterations;
        protected long currentIteration = 0;

        protected IterationRunCondition(long iterations) {
            this.iterations = iterations;
        }

        public static IterationRunCondition times(long numberOfIterations) {
            return new IterationRunCondition(numberOfIterations);
        }

        @Override
        public boolean isExecutable() {

            if (currentIteration < iterations) {
                currentIteration++;
                return true;
            } else {
                return false;
            }
        }
    }

    public static final class ToxicityType extends FloatType {

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

    public static final class SliceSizeVariationType extends LongType {

        protected SliceSizeVariationType(long value) {
            super(value);
        }

        public static SliceSizeVariationType sliceSizeVariation(long sizeVariation) {
            return new SliceSizeVariationType(sizeVariation);
        }
    }

    public static final class SliceAverageSizeType extends LongType {

        protected SliceAverageSizeType(long value) {
            super(value);
        }

        public static SliceAverageSizeType sliceAverageSize(long average) {
            return new SliceAverageSizeType(average);
        }
    }

    public static final class DelayType extends LongType {

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

    public static final class RateType extends LongType {

        protected RateType(long value) {
            super(value);
        }

        public static RateType rate(long rate) {
            return new RateType(rate);
        }
    }

    public static final class JitterType extends IntegerType {

        protected JitterType(int value) {
            super(value);
        }

        public static JitterType jitter(int jitter) {
            return new JitterType(jitter);
        }
    }

    public static final class LatencyType extends LongType {
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

    public static final class TimeoutType extends LongType {
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

    public static abstract class FloatType {
        private float value;

        public FloatType(float value) {
            this.value = value;
        }

        public float getValue() {
            return value;
        }
    }

    public static abstract class LongType {
        private long value;

        protected LongType(long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }
    }

    public static abstract class IntegerType {
        private int value;

        protected IntegerType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
