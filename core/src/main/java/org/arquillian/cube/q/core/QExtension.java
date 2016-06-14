package org.arquillian.cube.q.core;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class QExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(InstallProxy.class)
               .observer(RegisterProxy.class)
               .service(ResourceProvider.class, QResourceProvider.class);
    }

}
