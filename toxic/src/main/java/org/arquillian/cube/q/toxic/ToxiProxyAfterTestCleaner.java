package org.arquillian.cube.q.toxic;

import org.arquillian.cube.q.toxic.client.ToxiProxyScenario;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.After;

public class ToxiProxyAfterTestCleaner {

    public void resetToxiProxiesAfterTest(@Observes After event, ToxiProxyScenario scenario) {
        scenario.reset();
    }
}
