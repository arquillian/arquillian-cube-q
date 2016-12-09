package org.arquillian.cube.q.toxic.client;

import eu.rekawek.toxiproxy.Proxy;
import org.arquillian.cube.q.api.Q;
import org.arquillian.cube.q.toxic.event.ToxicCreated;
import org.arquillian.cube.q.toxic.event.ToxicUpdated;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.annotation.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToxiProxyScenario implements ToxiProxy {

    private static final Logger logger = Logger.getLogger(ToxiProxyScenario.class.getName());

    private ToxiProxyClient client;
    
    private Map<String, Proxy> proxies;

    private Injector injector;

    public ToxiProxyScenario(ToxiProxyClient client, Injector injector) {
        this.client = client;
        this.proxies = new HashMap<>();
        this.injector = injector;
    }

    public ToxiProxyScenario(ToxiProxyClient client) {
        this.client = client;
        this.proxies = new HashMap<>();
    }

    public void register(String name, String listen, String upstream) {
        proxies.put(name, client.createProxy(name, listen, upstream));
    }

    public void reset() {
        client.reset();
        proxies = client.getProxies();
    }


    public Scenario given(String name) {
        if(!proxies.containsKey(name)) {
            throw new IllegalArgumentException("No known proxy with name " + name);
        }

        ToxicScenario toxicScenario = new ToxicScenario(proxies.get(name));

        return injector.inject(toxicScenario);
    }

    public class ToxicScenario implements Scenario {
        
        private Proxy proxy;
        private List<ToxiProxyClient.BaseToxic> toxics;

        @Inject
        private Event<ToxicCreated> toxicCreated;

        @Inject
        private Event<ToxicUpdated> toxicUpdated;

        public ToxicScenario(Proxy proxy) {
            this.proxy = proxy;
        }

        @Override
        public Scenario given(String name) {
            return ToxiProxyScenario.this.given(name);
        }

        @Override
        public Scenario using(final List<ToxiProxyClient.BaseToxic> toxics) {
            this.toxics = toxics;
            return this;
        }

        @Override
        public void then(Callable callable) throws Exception {
            try {
                execute();
                callable.call();
            } finally {
               reset();
            }
        }

        @Override
        public void then(Q.RunCondition runCondition, Callable callable) throws Exception {
            try {
                execute(runCondition);
                callable.call();
            } finally {
                reset();
            }
        }

        @Override
        public void execute() throws Exception {
            for (ToxiProxyClient.BaseToxic toxic : toxics) {
                logger.log(Level.FINER, String.format("Next toxic is created %s.", toxic.toString()));
                client.createToxic(proxy, toxic);
                toxicCreated.fire(new ToxicCreated(toxic));
            }
        }

        @Override
        public void execute(Q.RunCondition runCondition) throws Exception {
            for (ToxiProxyClient.BaseToxic toxic : toxics) {
                logger.log(Level.FINER, String.format("Next toxic is created %s.", toxic.toString()));
                client.createToxic(proxy, toxic);
                toxicCreated.fire(new ToxicCreated(toxic, runCondition));
            }
        }

        @Override
        public void update() throws Exception {
            for (ToxiProxyClient.BaseToxic toxic : toxics) {
                logger.log(Level.FINER, String.format("Next toxic is updated %s.", toxic.toString()));
                client.updateToxic(proxy, toxic);
                toxicUpdated.fire(new ToxicUpdated(toxic));
            }
        }
    }
}
