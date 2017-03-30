package org.arquillian.cube.q.pumba;

import org.arquillian.cube.q.spi.StandaloneManager;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class QContainerChaosPumbaExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder
            .service(StandaloneManager.class, PumbaStandaloneContainerHandler.class)
            .service(ResourceProvider.class, QContainerChaosPumbaResourceProvider.class)
            .observer(QContainerChaosPumbaCreator.class)
            .observer(QContainerChaosPumbaStopper.class);
    }
}
