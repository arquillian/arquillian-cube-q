package org.arquillian.cube.q.toxic.client;

import java.util.HashMap;
import java.util.Map;

import org.arquillian.cube.q.toxic.client.ToxiProxyClient.Proxy;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient.ToxicType;

public class ToxiProxyScenario implements ToxiProxy{

    private ToxiProxyClient client;
    
    private Map<String, Proxy> proxies;
    
    public ToxiProxyScenario(ToxiProxyClient client) {
        this.client = client;
        this.proxies = new HashMap<String, Proxy>();
    }
    
    public void register(String name, String listen, String upstream) {
        proxies.put(name, client.createProxy(new Proxy(name, listen, upstream)));
    }
    
    public void reset() {
        client.reset();
        proxies = client.getProxies();
    }


    public Scenario given(String name) {
        if(!proxies.containsKey(name)) {
            throw new IllegalArgumentException("No known proxy with name " + name);
        }
        return new ToxicScenario(proxies.get(name));
    }

    public class ToxicScenario implements Scenario {
        
        private Proxy proxy;

        public ToxicScenario(Proxy proxy) {
            this.proxy = proxy;
        }
        
        public Scenario given(String name) {
            return ToxiProxyScenario.this.given(name);
        }
        
        public Scenario downstream(ToxicType type) {
            proxy.addDownstreamToxic(type);
            return this;
        }
        
        public Scenario upstream(ToxicType type) {
            proxy.addUpstreamToxic(type);
            return this;
        }

        public void then(Callable callable) throws Exception {
            try {
                for(Map.Entry<String, Proxy> entry : proxies.entrySet()) {
                    client.updateProxy(entry.getKey(), entry.getValue());
                }
                callable.call();
            } finally {
                //reset();
            }
        }
    }
}
