package org.arquillian.cube.q.toxic.client;

import eu.rekawek.toxiproxy.model.Proxy;

import java.util.HashMap;
import java.util.Map;

public class ToxiProxyScenario implements ToxiProxy{

    private ToxiProxyClient client;
    
    private Map<String, Proxy> proxies;
    
    public ToxiProxyScenario(ToxiProxyClient client) {
        this.client = client;
        this.proxies = new HashMap<String, Proxy>();
    }
    
    public void register(String name, String listen, String upstream) {
        proxies.put(name, client.createProxy(name, listen, upstream));
    }

    // TODO
    /**public void reset() {
        client.reset();
        proxies = client.getProxies();
    }**/


    public Scenario given(String name) {
        if(!proxies.containsKey(name)) {
            throw new IllegalArgumentException("No known proxy with name " + name);
        }
        return new ToxicScenario(proxies.get(name));
    }

    public class ToxicScenario implements Scenario {
        
        private Proxy proxy;
        private ToxiProxyClient.BaseToxic toxic;

        public ToxicScenario(Proxy proxy) {
            this.proxy = proxy;
        }
        
        public Scenario given(String name) {
            return ToxiProxyScenario.this.given(name);
        }
        public Scenario using(ToxiProxyClient.BaseToxic toxic) {
            this.toxic = toxic;
            return this;
        }

        public void then(Callable callable) throws Exception {
            try {
                client.createToxic(proxy, toxic);
                callable.call();
            } finally {
                //reset();
            }
        }
    }
}
