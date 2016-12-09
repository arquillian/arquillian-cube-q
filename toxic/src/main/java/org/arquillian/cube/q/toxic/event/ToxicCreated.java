package org.arquillian.cube.q.toxic.event;

import org.arquillian.cube.q.api.Q;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient;
import org.jboss.arquillian.core.spi.event.Event;


public class ToxicCreated implements Event {

    private ToxiProxyClient.BaseToxic toxic;

    private Q.RunCondition runCondition;

    public ToxicCreated(ToxiProxyClient.BaseToxic toxic) {
        this.toxic = toxic;
    }

    public ToxicCreated(ToxiProxyClient.BaseToxic toxic, Q.RunCondition runCondition) {
        this.toxic = toxic;
        this.runCondition = runCondition;
    }

    public ToxiProxyClient.BaseToxic getToxic() {
        return toxic;
    }

    public Q.RunCondition getRunCondition() {
        return runCondition;
    }
}
