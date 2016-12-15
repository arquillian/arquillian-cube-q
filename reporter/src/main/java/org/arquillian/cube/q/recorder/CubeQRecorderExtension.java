package org.arquillian.cube.q.recorder;

import org.jboss.arquillian.core.spi.LoadableExtension;


public class CubeQRecorderExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        final boolean reportedInClasspath = Validate.classExists("org.arquillian.recorder.reporter.ReporterExtension");
        if (reportedInClasspath) {
            builder.observer(TakeNetworkChaosInformation.class);
        }
    }
}
