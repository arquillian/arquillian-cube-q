package org.arquillian.cube.q.simianarmy;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class QOperativeSystemChaosSimianArmyExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder extensionBuilder) {
        extensionBuilder
                .service(ResourceProvider.class, QOperativeSystemChaosSimianArmyResourceProvider.class)
                .observer(QOperativeSystemChaosSimianArmyCreator.class);
    }
}
