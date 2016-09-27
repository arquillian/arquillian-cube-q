package org.arquillian.cube.q.toxic.client;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.Toxic;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import eu.rekawek.toxiproxy.model.ToxicList;
import eu.rekawek.toxiproxy.model.toxic.Bandwidth;
import eu.rekawek.toxiproxy.model.toxic.Latency;
import eu.rekawek.toxiproxy.model.toxic.Slicer;
import eu.rekawek.toxiproxy.model.toxic.Timeout;
import org.arquillian.cube.q.api.NetworkChaos;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ToxiProxyClient {

    Map<String, Proxy> getProxies();

    Proxy createProxy(String name, String listen, String upstream);

    void createToxic(Proxy proxy, BaseToxic toxic);

    void updateToxic(Proxy proxy, BaseToxic toxic);

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

        public boolean hasAnyDistributedField() {
            return false;
        }

        public abstract void create(Proxy proxy) throws IOException;
        public abstract void update() throws IOException;

    }

    public static class Latency extends BaseToxic {
        private NetworkChaos.LatencyType latency;
        private NetworkChaos.JitterType jitter;

        // To avoid calls in update, pointer to remote toxic is saved
        private eu.rekawek.toxiproxy.model.toxic.Latency toxicLatency;

        public Latency(String name, String stream, float toxicity, NetworkChaos.LatencyType latency, NetworkChaos.JitterType jitter) {
            super(name, stream, toxicity);
            this.latency = latency;
            this.jitter = jitter;
        }

        @Override
        public boolean hasAnyDistributedField() {
            return latency.isDistributed() || jitter.isDistributed();
        }

        @Override
        public void create(Proxy proxy) throws IOException {
            toxicLatency = proxy.toxics().latency(this.getName(),
                    ToxicDirection.valueOf(this.getStream()),
                    this.getLatency());
            toxicLatency
                    .setJitter(this.getJitter())
                    .setToxicity(this.getToxcicity());
        }

        @Override
        public void update() throws IOException {
            if (toxicLatency != null) {
                toxicLatency.setLatency(this.getLatency());
                toxicLatency.setJitter(this.getJitter());
                toxicLatency.setToxicity(this.getToxcicity());
            } else {
                throw new IllegalStateException("This Toxic object has not been created in server side so it cannot be updated.");
            }
        }

        public long getLatency() {
            return latency.getValue();
        }

        public long getJitter() {
            return jitter.getValue();
        }
    }

    public static class Bandwidth extends BaseToxic {
        private NetworkChaos.RateType rate;

        // To avoid calls in update, pointer to remote toxic is saved
        eu.rekawek.toxiproxy.model.toxic.Bandwidth toxicBandwidth;

        public Bandwidth(String name, String stream, float toxicity, NetworkChaos.RateType rate) {
            super(name, stream, toxicity);
            this.rate = rate;
        }

        public long getRate() {
            return rate.getValue();
        }

        @Override
        public boolean hasAnyDistributedField() {
            return rate.isDistributed();
        }

        @Override
        public void create(Proxy proxy) throws IOException {
            toxicBandwidth = proxy.toxics().bandwidth(this.getName(),
                    ToxicDirection.valueOf(this.getStream()),
                    this.getRate());
            toxicBandwidth
                    .setToxicity(this.getToxcicity());
        }

        @Override
        public void update() throws IOException {
            if (toxicBandwidth != null) {
                toxicBandwidth.setRate(this.getRate());
                toxicBandwidth.setToxicity(this.getToxcicity());
            } else {
                throw new IllegalStateException("This Toxic object has not been created in server side so it cannot be updated.");
            }
        }
    }

    public static class Down extends BaseToxic {
        public Down(String name, String stream, float toxicity) {
            super(name, stream, toxicity);
        }

        @Override
        public void create(Proxy proxy) throws IOException {
            // For ToxicProxy down is in the category of toxicity although is not a toxicity per se
            proxy.disable();
        }

        @Override
        public void update() throws IOException {
        }
    }

    public static class SlowClose extends BaseToxic {

        private NetworkChaos.DelayType delay;

        // To avoid calls in update, pointer to remote toxic is saved
        private eu.rekawek.toxiproxy.model.toxic.SlowClose toxicSlowClose;

        public SlowClose(String name, String stream, float toxicity, NetworkChaos.DelayType delay) {
            super(name, stream, toxicity);
            this.delay = delay;
        }

        public long getDelay() {
            return delay.getValue();
        }

        @Override
        public void create(Proxy proxy) throws IOException {
            toxicSlowClose = proxy.toxics().slowClose(this.getName(),
                    ToxicDirection.valueOf(this.getStream()),
                    this.getDelay());
            toxicSlowClose
                    .setToxicity(this.getToxcicity());
        }

        @Override
        public boolean hasAnyDistributedField() {
            return delay.isDistributed();
        }

        @Override
        public void update() throws IOException {
            if (toxicSlowClose != null) {
                toxicSlowClose.setDelay(this.getDelay());
                toxicSlowClose.setToxicity(this.getToxcicity());
            } else {
                throw new IllegalStateException("This Toxic object has not been created in server side so it cannot be updated.");
            }
        }
    }

    public static class Timeout extends BaseToxic {

        private NetworkChaos.TimeoutType timeout;

        // To avoid calls in update, pointer to remote toxic is saved
        private eu.rekawek.toxiproxy.model.toxic.Timeout toxicTimeout;

        public Timeout(String name, String stream, float toxicity, NetworkChaos.TimeoutType timeout) {
            super(name, stream, toxicity);
            this.timeout = timeout;
        }

        public long getTimeout() {
            return timeout.getValue();
        }

        @Override
        public boolean hasAnyDistributedField() {
            return timeout.isDistributed();
        }

        @Override
        public void create(Proxy proxy) throws IOException {
            toxicTimeout = proxy.toxics().timeout(this.getName(),
                    ToxicDirection.valueOf(this.getStream()),
                    this.getTimeout());
            toxicTimeout
                    .setToxicity(this.getToxcicity());
        }

        @Override
        public void update() throws IOException {
            if (toxicTimeout != null) {
                toxicTimeout.setTimeout(this.getTimeout());
                toxicTimeout.setToxicity(this.getToxcicity());
            } else {
                throw new IllegalStateException("This Toxic object has not been created in server side so it cannot be updated.");
            }
        }
    }

    public static class Slice extends BaseToxic {
        private long averageSize;
        private long sizeVariation;
        private NetworkChaos.DelayType delay;

        // To avoid calls in update, pointer to remote toxic is saved
        private Slicer toxicSlicer;

        public Slice(String name, String stream, float toxicity, long averageSize, NetworkChaos.DelayType delay, long variableSize) {
            super(name, stream, toxicity);
            this.averageSize = averageSize;
            this.sizeVariation = variableSize;
            this.delay = delay;
        }

        public long getAverageSize() {
            return averageSize;
        }

        public long getDelay() {
            return delay.getValue();
        }

        public long getSizeVariation() {
            return sizeVariation;
        }

        @Override
        public boolean hasAnyDistributedField() {
            return delay.isDistributed();
        }

        @Override
        public void create(Proxy proxy) throws IOException {
            toxicSlicer = proxy.toxics().slicer(this.getName(),
                    ToxicDirection.valueOf(this.getStream()),
                    this.getAverageSize(),
                    this.getDelay());
            toxicSlicer
                    .setSizeVariation(this.getSizeVariation())
                    .setToxicity(this.getToxcicity());
        }

        @Override
        public void update() throws IOException {
            if (toxicSlicer != null) {
                toxicSlicer.setDelay(this.getDelay());
                toxicSlicer.setAverageSize(this.getAverageSize());
                toxicSlicer.setSizeVariation(this.getSizeVariation());
                toxicSlicer.setToxicity(this.getToxcicity());
            } else {
                throw new IllegalStateException("This Toxic object has not been created in server side so it cannot be updated.");
            }
        }
    }

    public static class Builder {

        public static ToxiProxyClient create(final String ip, final int port) {
            return new ToxiProxyClient() {

                ToxiproxyClient toxiproxyClient = new ToxiproxyClient(ip, port);

                // Storing toxics locally avoid extra calls in case of updates.
                Map<String, Toxic> toxics = new HashMap<>();

                @Override
                public Map<String, Proxy> getProxies() {
                    try {
                        final List<Proxy> proxies = toxiproxyClient.getProxies();
                        final Map<String, Proxy> proxyMap = new HashMap<>();

                        for (Proxy proxy : proxies) {
                            proxyMap.put(proxy.getName(), proxy);
                        }

                        return proxyMap;
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
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
                        toxic.create(proxy);
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }

                @Override
                public void updateToxic(Proxy proxy, BaseToxic toxic) {
                    try {
                        toxic.update();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }

                @Override
                public void reset() {
                    try {
                        toxiproxyClient.reset();
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            };
        }
    }

}
