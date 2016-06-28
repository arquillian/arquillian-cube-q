package org.arquillian.cube.q.toxic;

import org.arquillian.cube.q.spi.ProxyManager;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class QNetworkChaosToxicExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(QNetworkChaosToxicCreator.class)
               .service(ProxyManager.class, ToxicProxyHandler.class)
               .service(ResourceProvider.class, QNetworkChaosResourceProvider.class);
    }

}
