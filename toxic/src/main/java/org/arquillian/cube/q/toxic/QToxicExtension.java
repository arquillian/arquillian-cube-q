package org.arquillian.cube.q.toxic;

import org.arquillian.cube.q.spi.ProxyManager;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class QToxicExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(QToxicCreator.class)
               .service(ProxyManager.class, ToxicProxyHandler.class);
    }

}
