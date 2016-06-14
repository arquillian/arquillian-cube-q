package org.arquillian.cube.q.core;

import java.lang.annotation.Annotation;

import org.arquillian.cube.q.api.Q;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class QResourceProvider implements ResourceProvider {

    @Inject
    private Instance<Q> qInst;
    
    public boolean canProvide(Class<?> type) {
        return type.isAssignableFrom(Q.class);
    }

    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        return qInst.get();
    }

}
