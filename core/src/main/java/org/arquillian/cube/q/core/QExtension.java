package org.arquillian.cube.q.core;

import org.jboss.arquillian.core.spi.LoadableExtension;

public class QExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(InstallProxy.class)
               .observer(RegisterProxy.class)
               .observer(InstallStandaloneContainer.class);
    }

}
