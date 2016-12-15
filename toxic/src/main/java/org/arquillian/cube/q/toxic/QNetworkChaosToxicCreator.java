package org.arquillian.cube.q.toxic;

import org.arquillian.cube.q.api.NetworkChaos;
import org.arquillian.cube.q.toxic.client.ToxiProxyScenario;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

public class QNetworkChaosToxicCreator {

    @Inject
    @ApplicationScoped
    private InstanceProducer<NetworkChaos> qInst;

    @Inject
    private Instance<Injector> injectorInstance;
    
    public void createProxy(@Observes ToxiProxyScenario proxy) {
        QNetworkChaosToxic qNetworkChaosToxic = new QNetworkChaosToxic(proxy);
        injectorInstance.get().inject(qNetworkChaosToxic);
        qInst.set(qNetworkChaosToxic);
    } 
}
