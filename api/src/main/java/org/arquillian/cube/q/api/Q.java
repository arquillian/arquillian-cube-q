package org.arquillian.cube.q.api;

import java.util.concurrent.TimeUnit;

public interface Q {

    void exec() throws Exception;
    void exec(Perform perform) throws Exception;
    void exec(RunCondition runCondition, Perform perform) throws Exception;

    public interface Perform {
        void execute() throws Exception;
    }

    public interface RunCondition {
        boolean isExecutable();
    }

    public static class DurationRunCondition implements Q.RunCondition {

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

    public static class IterationRunCondition implements Q.RunCondition {
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

    public static abstract class BaseType {
        private boolean distributed = false;

        protected BaseType(boolean distributed) {
            this.distributed = distributed;
        }

        protected void setDistributed() {
            this.distributed = true;
        }

        public boolean isDistributed() {
            return this.distributed;
        }
    }

    public static abstract class FloatType extends BaseType {
        private float value;

        public FloatType(float value) {
            super(false);
            this.value = value;
        }

        public float getValue() {
            return value;
        }
    }

    public static abstract class LongType extends BaseType {
        private long value;

        protected LongType(long value) {
            super(false);
            this.value = value;
        }

        public long getValue() {
            return value;
        }
    }

    public static abstract class IntegerType extends BaseType {
        private int value;

        protected IntegerType(int value) {
            super(false);
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public static abstract class StringType extends BaseType {
        private String value;

        protected StringType(String value) {
            super(false);
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static abstract class ArrayType<T> extends BaseType {

        private T[] value;

        protected ArrayType(T[] value) {
            super(false);
            this.value = value;
        }

        public T[] getValue() {
            return value;
        }
    }
}
