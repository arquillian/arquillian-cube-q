package org.arquillian.cube.q.api;

import java.util.concurrent.TimeUnit;

public interface ContainerChaos {

    Action onCubeDockerHost();

    public interface Action extends Q {
        Action stop(ContainersType containersType, IntervalType intervalType);
        Action stopRandomly(ContainersType containersType, IntervalType intervalType);

        Action remove(ContainersType containersType, IntervalType intervalType);
        Action removeRandomly(ContainersType containersType, IntervalType intervalType);

        Action kill(ContainersType containersType, IntervalType intervalType, KillSignal killSignal);
        Action killRandomly(ContainersType containersType, IntervalType intervalType, KillSignal killSignal);
    }

    public enum KillSignal {
        SIGHUP, SIGINT, SIGKILL, SIGTERM, SIGSTOP;
    }

    public static final class ContainersType extends Q.StringType {

        protected ContainersType(String value) {
            super(value);
        }

        public static ContainersType regularExpression(String expression) {
            return new ContainersType("re2:" + expression);
        }

        public static ContainersType containers(String...containers) {
            return new ContainersType(joiner(containers));
        }


        private static String joiner(String...containers) {
            StringBuilder csv = new StringBuilder();
            for(String container : containers) {
                csv.append(container).append(",");
            }

            return csv.substring(0, csv.length() - 1);
        }
    }

    public static final class IntervalType extends Q.LongType {

        protected IntervalType(long value) {
            super(value);
        }

        public static IntervalType intervalWithSeconds(long interval) {
            return new IntervalType(interval);
        }

        public static IntervalType interval(long interval, TimeUnit timeUnit) {
            return new IntervalType(timeUnit.toSeconds(interval));
        }
    }

}
