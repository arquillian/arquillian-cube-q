package org.arquillian.cube.q.recorder;

import org.arquillian.reporter.api.model.StringKey;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class CubeQReporterExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        if (Validate.classExists("org.arquillian.core.reporter.ArquillianCoreReporterExtension")) {
            builder.observer(TakeNetworkChaosInformation.class);
            builder.service(StringKey.class, NetworkChaosInformationReportKey.class);
        }
    }
}
