package org.arquillian.cube.q.toxic;

import org.arquillian.cube.q.api.Q;
import org.arquillian.cube.q.toxic.client.ToxiProxy;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient.ToxicType;
import org.arquillian.cube.q.toxic.client.ToxiProxyScenario;

public class QToxic implements Q {

    private ToxiProxyScenario scenario;
    
    public QToxic(ToxiProxyScenario scenario) {
        this.scenario = scenario;
    }

    @Override
    public Action on(String machine, int port) {
        return new ToxicAction(machine + ":" + port);
    }

    public class ToxicAction implements Action {

        public String name;
        
        public ToxicAction(String name) {
            this.name = name;
        }
        
        @Override
        public void timeout(int millis, final Perform perform) throws Exception {
            run(name, new ToxiProxyClient.Timeout(millis), perform);
        }
        
        @Override
        public void latency(int millis, int jitter, final Perform perform) throws Exception {
            run(name, new ToxiProxyClient.Latency(millis, jitter), perform);
        }

        @Override
        public void down(Perform perform) throws Exception {
            
        }

        @Override
        public void bandwidth(int kbs, Perform perform) throws Exception {
            run(name, new ToxiProxyClient.Bandwidth(kbs), perform);            
        }

        @Override
        public void slowclose(int delay, Perform perform) throws Exception {
            run(name, new ToxiProxyClient.SlowClose(delay), perform);
        }

        @Override
        public void slice(int average_size, int size_variation, int delay, Perform perform) throws Exception {
            run(name, new ToxiProxyClient.Slicer(average_size, size_variation, delay), perform);
        }
        
        private void run(String name, ToxicType type, final Perform perform) throws Exception {
            scenario.given(name)
                .downstream(type)
                .then(new ToxiProxy.Callable() {
                        
                        @Override
                        public void call() throws Exception {
                            perform.execute();
                        }
                    });
        }
    } 
}
