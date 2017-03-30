package org.arquillian.cube.q.api;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public interface Q {

    void exec() throws Exception;

    void exec(Perform perform) throws Exception;

    void exec(RunCondition runCondition, Perform perform) throws Exception;

    interface Perform {
        void execute() throws Exception;
    }

    interface RunCondition {
        boolean isExecutable();
    }

    class DurationRunCondition implements Q.RunCondition {

        private long finishTime;

        protected DurationRunCondition(long duration, TimeUnit unit) {
            final long durationInMillis = unit.toMillis(duration);
            this.finishTime = System.currentTimeMillis() + durationInMillis;
        }

        public long getFinishTime() {
            return finishTime;
        }

        public static DurationRunCondition during(long duration, TimeUnit timeUnit) {
            return new DurationRunCondition(duration, timeUnit);
        }

        @Override
        public boolean isExecutable() {
            return System.currentTimeMillis() < finishTime;
        }
    }

    class IterationRunCondition implements Q.RunCondition {
        private final long iterations;
        protected long currentIteration = 0;

        protected IterationRunCondition(long iterations) {
            this.iterations = iterations;
        }

        public long getIterations() {
            return iterations;
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

    abstract class BaseType<T> {
        private boolean distributed = false;
        protected T value;

        protected BaseType(boolean distributed, T value) {
            this.distributed = distributed;
            this.value = value;
        }

        protected void setDistributed() {
            this.distributed = true;
        }

        public boolean isDistributed() {
            return this.distributed;
        }

        public void calculateValue() {
        }

        public T getValue() {
            return value;
        }
    }

    abstract class FloatType extends BaseType<Float> {
        public FloatType(float value) {
            super(false, value);
            this.value = value;
        }

        @Override
        public String toString() {
            return Float.toString(value);
        }
    }

    abstract class LongType extends BaseType<Long> {
        protected LongType(long value) {
            super(false, value);
            this.value = value;
        }

        @Override
        public String toString() {
            return Long.toString(value);
        }
    }

    abstract class IntegerType extends BaseType<Integer> {
        protected IntegerType(int value) {
            super(false, value);
            this.value = value;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }
    }

    abstract class StringType extends BaseType<String> {
        protected StringType(String value) {
            super(false, value);
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    abstract class ArrayType<T> extends BaseType<T[]> {

        protected ArrayType(T[] value) {
            super(false, value);
            this.value = value;
        }

        @Override
        public String toString() {
            return Arrays.toString(value);
        }
    }
}
