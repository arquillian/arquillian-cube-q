package org.arquillian.cube.q.pumba;

import org.arquillian.cube.q.api.ContainerChaos;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

import java.lang.annotation.Annotation;

public class QContainerChaosPumbaResourceProvider implements ResourceProvider {

    @Inject
    private Instance<ContainerChaos> containerChaosInst;

    @Override
    public boolean canProvide(Class<?> type) {
        return type.isAssignableFrom(ContainerChaos.class);
    }

    @Override
    public Object lookup(ArquillianResource arquillianResource, Annotation... annotations) {
        return containerChaosInst.get();
    }
}
