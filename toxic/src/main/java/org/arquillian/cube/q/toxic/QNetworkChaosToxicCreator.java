package org.arquillian.cube.q.toxic;

import org.arquillian.cube.q.api.NetworkChaos;
import org.arquillian.cube.q.api.Q;
import org.arquillian.cube.q.toxic.client.ToxiProxyScenario;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

public class QNetworkChaosToxicCreator {

    @Inject @ApplicationScoped
    private InstanceProducer<NetworkChaos> qInst;
    
    public void createProxy(@Observes ToxiProxyScenario proxy) {
        qInst.set(new QNetworkChaosToxic(proxy));
    } 
}
