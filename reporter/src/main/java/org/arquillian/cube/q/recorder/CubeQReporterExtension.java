package org.arquillian.cube.q.recorder;

import org.jboss.arquillian.core.spi.LoadableExtension;

public class CubeQReporterExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        if (Validate.classExists("org.arquillian.core.reporter.ArquillianCoreReporterExtension")) {
            builder.observer(TakeNetworkChaosInformation.class);
        }
    }
}
