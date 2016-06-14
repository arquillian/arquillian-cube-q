package org.arquillian.cube.q.toxic.client;

import java.util.Map;

import feign.gson.GsonDecoder;
import feign.gson.GsonEncoder;

public interface ToxiProxyClient {

    @feign.RequestLine("GET /proxies")
    Map<String, Proxy> getProxies();
    
    @feign.RequestLine("POST /proxies")
    Proxy createProxy(Proxy proxy);

    @feign.RequestLine("GET /proxies/{proxy}")
    Proxy getProxy(@feign.Param("proxy") String name);

    @feign.RequestLine("POST /proxies/{proxy}")
    Proxy updateProxy(@feign.Param("proxy") String name, Proxy proxy);

    @feign.RequestLine("DELETE /proxies/{proxy}")
    Proxy removeProxy(@feign.Param("proxy") String name);

    @feign.RequestLine("GET /proxies/{proxy}/upstream/toxics")
    Toxic getUpstreamToxic(@feign.Param("proxy") String name);

    @feign.RequestLine("POST /proxies/{proxy}/upstream/toxics/{toxic}")
    void updateUpstreamToxic(@feign.Param("proxy") String name, @feign.Param("toxic") String toxic, ToxicType data);

    @feign.RequestLine("GET /proxies/{proxy}/downstream/toxics")
    Toxic getDownstreamToxic(@feign.Param("proxy") String name);

    @feign.RequestLine("POST /proxies/{proxy}/downstream/toxics/{toxic}")
    void updateDownstreamToxic(@feign.Param("proxy") String name, @feign.Param("toxic") String toxic, ToxicType data);

    @feign.RequestLine("GET /reset")
    void reset();
    
    public class Proxy {
        private String name;
        private String listen;
        private String upstream;
        private boolean enabled;
    
        private Toxic upstream_toxics;
        private Toxic downstream_toxics;
        
        protected Proxy() {}
        
        public Proxy(String name, String listen, String upstream) {
            this.name = name;
            this.listen = listen;
            this.upstream = upstream;
            this.enabled = true;
        }
        
        public Proxy addUpstreamToxic(ToxicType type) {
            if(upstream_toxics == null) {
                upstream_toxics = new Toxic();
            }
            if(type instanceof Latency) {
                upstream_toxics.latency = (Latency) type;
            }
            else if(type instanceof Bandwidth) {
                upstream_toxics.bandwidth = (Bandwidth)type;
            }
            else if(type instanceof SlowClose) {
                upstream_toxics.slow_close = (SlowClose) type;
            }
            else if(type instanceof Timeout) {
                upstream_toxics.timeout = (Timeout) type;
            }
            else if(type instanceof Slicer) {
                upstream_toxics.slicer = (Slicer)type;
            }
            else {
                throw new IllegalArgumentException("Unknown ToxicType " + type.getClass());
            }
            
            return this;
        }

        public Proxy addDownstreamToxic(ToxicType type) {
            if(downstream_toxics == null) {
                downstream_toxics = new Toxic();
            }
            if(type instanceof Latency) {
                downstream_toxics.latency = (Latency) type;
            }
            else if(type instanceof Bandwidth) {
                downstream_toxics.bandwidth = (Bandwidth)type;
            }
            else if(type instanceof SlowClose) {
                downstream_toxics.slow_close = (SlowClose) type;
            }
            else if(type instanceof Timeout) {
                downstream_toxics.timeout = (Timeout) type;
            }
            else if(type instanceof Slicer) {
                downstream_toxics.slicer = (Slicer)type;
            }
            else {
                throw new IllegalArgumentException("Unknown ToxicType " + type.getClass());
            }
            
            return this;
        }

        @Override
        public String toString() {
            return "Proxy [name=" + name + ", listen=" + listen + ", upstream=" + upstream + ", enabled=" + enabled
                    + ", upstream_toxics=" + upstream_toxics + ", downstream_toxics=" + downstream_toxics + "]";
        }

    }
    
    public static class Toxic {
        private Latency latency;
        private Bandwidth bandwidth;
        private SlowClose slow_close;
        private Timeout timeout;
        private Slicer slicer;
        @Override
        public String toString() {
            return "Toxic [latency=" + latency + ", bandwidth=" + bandwidth + ", slow_close=" + slow_close
                    + ", timeout=" + timeout + ", slicer=" + slicer + "]";
        }
    }
    
    public static class ToxicType {
        private boolean enabled;
        
        public ToxicType(boolean enabled) {
            this.enabled = enabled;
        }
        
        public void disable() {
            this.enabled = false;
        }
    }
    
    public static class Latency extends ToxicType {
        private int latency;
        private int jitter;
        
        public Latency(int latency, int jitter) {
            super(true);
            this.latency = latency;
            this.jitter = jitter;
        }

        @Override
        public String toString() {
            return "Latency [latency=" + latency + ", jitter=" + jitter + "]";
        }

    }
    
    public static class Bandwidth extends ToxicType {
        private int rate;
        
        public Bandwidth(int rate) {
            super(true);
            this.rate = rate;
        }

        @Override
        public String toString() {
            return "Bandwidth [rate=" + rate + "]";
        }
    }

    public static class SlowClose extends ToxicType {
        private int delay;

        public SlowClose(int delay) {
            super(true);
            this.delay = delay;
        }

        @Override
        public String toString() {
            return "SlowClose [delay=" + delay + "]";
        }
    }
    
    public static class Timeout extends ToxicType {
        private int timeout;
        
        public Timeout(int timeout) {
            super(true);
            this.timeout = timeout;
        }

        @Override
        public String toString() {
            return "Timeout [timeout=" + timeout + "]";
        }
    }
    
    public static class Slicer extends ToxicType {
        private int average_size;
        private int size_variation;
        private int delay;
        
        public Slicer(int average_size, int size_variation, int delay) {
            super(true);
            this.average_size = average_size;
            this.size_variation = size_variation;
            this.delay = delay;
        }

        @Override
        public String toString() {
            return "Slicer [average_size=" + average_size + ", size_variation=" + size_variation + ", delay=" + delay
                    + "]";
        }
    }

    public static class Builder {

        public static ToxiProxyClient create(String url) {
            return feign.Feign.builder()
                        .logger(new feign.Logger.JavaLogger())
                        .logLevel(feign.Logger.Level.FULL)
                        .decoder(new GsonDecoder())
                        .encoder(new GsonEncoder())
                        .target(ToxiProxyClient.class, url);
        }
    }
    
    public static class ProxyExpander implements feign.Param.Expander {

        public String expand(Object value) {
            if(!Proxy.class.isInstance(value)) {
                throw new IllegalArgumentException("Object of type " + value.getClass() + " is not of type " + Proxy.class);
            }
            return ((Proxy)value).name;
        }
        
    }
}
