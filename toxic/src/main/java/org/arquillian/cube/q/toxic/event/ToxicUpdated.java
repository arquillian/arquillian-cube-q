package org.arquillian.cube.q.toxic.event;

import org.arquillian.cube.q.toxic.client.ToxiProxyClient;
import org.jboss.arquillian.core.spi.event.Event;

public class ToxicUpdated implements Event {

    private ToxiProxyClient.BaseToxic toxic;

    public ToxiProxyClient.BaseToxic getToxic() {
        return toxic;
    }

    public ToxicUpdated(ToxiProxyClient.BaseToxic toxic) {
        this.toxic = toxic;
    }
}
