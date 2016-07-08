package org.arquillian.cube.q.api;

import java.util.concurrent.TimeUnit;

/**
 * Interface for executing chaos at operative system level.
 */
public interface OperativeSystemChaos {

    /**
     * Method to set in which container you want to run the process.
     * @param containerId of container.
     * @return Action element to set the chaos.
     */
    Action on(String containerId);

    /**
     * Interface that abstracts on how operative system chaos is implemented
     */
    public interface Action extends Q {

        /**
         * Burn CPU setting CPU to 100%
         * @param numberCpu to run this process.
         * @return this element.
         */
        Action burnCpu(NumberCpuType numberCpu);

        /**
         * Burn IO channels
         * @return this element.
         */
        Action burnIo();

        /**
         * Provoke a fail in DNS server (blocking port 53)
         * @return this element.
         */
        Action failDns();

        /**
         * Fill the disk with trash
         * @param size of the trash
         * @return this element.
         */
        Action fillDisk(SizeType size);

        /**
         * Kills process in specified interval
         * @param processName to kill
         * @return this element.
         */
        Action killProcess(String processName);

        /**
         * Set null route
         * @return this element.
         */
        Action nullRoute();

        /**
         * Blocks a port
         * @param port port(s) to block
         * @return this element.
         */
        Action blockPort(PortSizeType port);

    }

    public static final class NumberCpuType extends Q.IntegerType {

        protected NumberCpuType(int value) {
            super(value);
        }

        public static final NumberCpuType cpus(int cpu) {
            return new NumberCpuType(cpu);
        }

        public static final NumberCpuType singleCpu() {
            return new NumberCpuType(1);
        }
    }

    public static final class SizeType extends Q.LongType {

        protected SizeType(long value) {
            super(value);
        }

        /**
         * Default size of 65GB
         * @return Size element.
         */
        public static final SizeType defaultSize() {
            return new SizeType(65536);
        }

        public static final SizeType sizeInMegas(long size) {
            return new SizeType(size);
        }
    }

    public static final class PortSizeType extends Q.ArrayType<Integer> {

        protected PortSizeType(Integer[] value) {
            super(value);
        }

        public static final PortSizeType port(Integer port) {
            return new PortSizeType(new Integer[]{port});
        }

        public static final PortSizeType ports(Integer...ports) {
            return new PortSizeType(ports);
        }

        public static final PortSizeType portRange(int start, int stop) {
            int totalNumber = (stop - start) + 1;
            Integer[] ports = new Integer[totalNumber];

            int currentPort = start;
            for(int i=0; i < ports.length; i++) {
                ports[i] = currentPort;
                currentPort++;
            }

            return new PortSizeType(ports);
        }
    }
}
