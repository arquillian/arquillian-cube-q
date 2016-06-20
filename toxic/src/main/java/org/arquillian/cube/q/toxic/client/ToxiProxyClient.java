package org.arquillian.cube.q.toxic.client;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.Proxy;
import eu.rekawek.toxiproxy.model.ToxicDirection;

import java.io.IOException;
import java.util.Map;

public interface ToxiProxyClient {

    Map<String, Proxy> getProxies();

    Proxy createProxy(String name, String listen, String upstream);

    void createToxic(Proxy proxy, BaseToxic toxic);

    void reset();

    public static abstract class BaseToxic {
        private String name;
        private String stream;
        private float toxcicity;

        public BaseToxic(String name, String stream, float toxicity) {
            this.name = name;
            this.stream = stream;
            this.toxcicity = toxicity;
        }

        public String getName() {
            return name;
        }

        public String getStream() {
            return stream;
        }

        public float getToxcicity() {
            return toxcicity;
        }
    }

    public static class Latency extends BaseToxic {
        private long latency;
        private long jitter;

        public Latency(String name, String stream, float toxicity, long latency, long jitter) {
            super(name, stream, toxicity);
            this.latency = latency;
            this.jitter = jitter;
        }

        public long getLatency() {
            return latency;
        }

        public long getJitter() {
            return jitter;
        }
    }

    public static class Bandwidth extends BaseToxic {
        private long rate;

        public Bandwidth(String name, String stream, float toxicity, long rate) {
            super(name, stream, toxicity);
            this.rate = rate;
        }

        public long getRate() {
            return rate;
        }
    }

    public static class Down extends BaseToxic {
        public Down(String name, String stream, float toxicity) {
            super(name, stream, toxicity);
        }
    }

    public static class SlowClose extends BaseToxic {

        private long delay;

        public SlowClose(String name, String stream, float toxicity, long delay) {
            super(name, stream, toxicity);
            this.delay = delay;
        }

        public long getDelay() {
            return delay;
        }
    }

    public static class Timeout extends BaseToxic {

        private long timeout;

        public Timeout(String name, String stream, float toxicity, long timeout) {
            super(name, stream, toxicity);
            this.timeout = timeout;
        }

        public long getTimeout() {
            return timeout;
        }
    }

    public static class Slice extends BaseToxic {
        private long averageSize;
        private long sizeVariation;
        private long delay;


        public Slice(String name, String stream, float toxicity, long averageSize, long delay, long variableSize) {
            super(name, stream, toxicity);
            this.averageSize = averageSize;
            this.sizeVariation = variableSize;
            this.delay = delay;
        }

        public long getAverageSize() {
            return averageSize;
        }

        public long getDelay() {
            return delay;
        }

        public long getSizeVariation() {
            return sizeVariation;
        }
    }

    public static class Builder {

        public static ToxiProxyClient create(final String ip, final int port) {
            return new ToxiProxyClient() {

                ToxiproxyClient toxiproxyClient = new ToxiproxyClient(ip, port);

                @Override
                public Map<String, Proxy> getProxies() {
                    return null;
                }

                @Override
                public Proxy createProxy(String name, String listen, String upstream) {
                    try {
                        return toxiproxyClient.createProxy(name, listen, upstream);
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }

                @Override
                public void createToxic(Proxy proxy, BaseToxic toxic) {
                    try {
                        if (toxic instanceof Latency) {
                            Latency latency = (Latency) toxic;
                            proxy.toxics().latency(latency.getName(),
                                    ToxicDirection.valueOf(toxic.getStream()),
                                    latency.getLatency())
                                    .setJitter(latency.getJitter())
                                    .setToxicity(latency.getToxcicity());
                        } else {
                            if (toxic instanceof Bandwidth) {
                                Bandwidth bandwidth = (Bandwidth) toxic;
                                proxy.toxics().bandwidth(bandwidth.getName(),
                                        ToxicDirection.valueOf(toxic.getStream()),
                                        bandwidth.getRate())
                                        .setToxicity(bandwidth.getToxcicity());
                            } else {
                                if (toxic instanceof Down) {
                                    // For ToxicProxy down is in the category of toxicity although is nt a toxicity per se
                                    proxy.disable();
                                } else {
                                    if (toxic instanceof SlowClose) {
                                        SlowClose slowClose = (SlowClose) toxic;
                                        proxy.toxics().slowClose(slowClose.getName(),
                                                ToxicDirection.valueOf(toxic.getStream()),
                                                slowClose.getDelay())
                                                .setToxicity(slowClose.getToxcicity());
                                    } else {
                                        if (toxic instanceof Timeout) {
                                            Timeout timeout = (Timeout) toxic;
                                            proxy.toxics().timeout(timeout.getName(),
                                                    ToxicDirection.valueOf(toxic.getStream()),
                                                    timeout.getTimeout())
                                                    .setToxicity(timeout.getToxcicity());
                                        } else {
                                            if (toxic instanceof Slice) {
                                                Slice slice = (Slice) toxic;
                                                proxy.toxics().slicer(slice.getName(),
                                                        ToxicDirection.valueOf(toxic.getStream()),
                                                        slice.getAverageSize(),
                                                        slice.getDelay())
                                                        .setSizeVariation(slice.getSizeVariation())
                                                        .setToxicity(slice.getToxcicity());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }

                @Override
                public void reset() {

                }
            };
        }
    }

}
