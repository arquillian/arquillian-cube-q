package org.arquillian.cube.q.toxic;

import java.lang.annotation.Annotation;

import org.arquillian.cube.q.api.NetworkChaos;
import org.arquillian.cube.q.api.Q;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class QNetworkChaosResourceProvider implements ResourceProvider {

    @Inject
    private Instance<NetworkChaos> networkChaosInst;
    
    public boolean canProvide(Class<?> type) {
        return type.isAssignableFrom(NetworkChaos.class);
    }

    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        return networkChaosInst.get();
    }

}
