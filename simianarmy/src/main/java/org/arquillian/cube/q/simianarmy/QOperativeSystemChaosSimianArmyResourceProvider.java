package org.arquillian.cube.q.simianarmy;

import org.arquillian.cube.q.api.OperativeSystemChaos;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

import java.lang.annotation.Annotation;

public class QOperativeSystemChaosSimianArmyResourceProvider implements ResourceProvider {

    @Inject
    private Instance<OperativeSystemChaos> operativeSystemChaosInstance;


    @Override
    public boolean canProvide(Class<?> type) {
        return type.isAssignableFrom(OperativeSystemChaos.class);
    }

    @Override
    public Object lookup(ArquillianResource arquillianResource, Annotation... annotations) {
        return operativeSystemChaosInstance.get();
    }
}
