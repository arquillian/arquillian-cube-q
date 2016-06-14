package org.arquillian.cube.q.toxic.client;

public class ToxiProxyContainer implements ToxiProxy {

    private String hostIp;
    private int hostPort;
    
    private ToxiProxyClient client;
    private ToxiProxy toxiProxy;
    
    public ToxiProxyClient getClient() {
        if(this.client == null) {
            this.client = ToxiProxyClient.Builder.create("http://" + hostIp + ":" + hostPort);
        }
        return this.client;
    }
    
    private ToxiProxy getProxy() {
        if(this.toxiProxy == null) {
            this.toxiProxy = new ToxiProxyScenario(getClient());
        }
        return this.toxiProxy;
    }

    public void register(String name, String listen, String upstream) {
        getProxy().register(name, listen, upstream);
    }

    public Scenario given(String name) {
        return getProxy().given(name);
    }
}
