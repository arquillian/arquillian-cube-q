package org.arquillian.cube.q.pumba;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.q.spi.StandaloneContainer;
import org.arquillian.cube.q.spi.StandaloneManager;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;

import java.util.Arrays;

public class PumbaStandaloneContainerHandler implements StandaloneManager {

    @Inject
    Instance<CubeDockerConfiguration> cubeDockerConfigurationInstance;

    @Override
    public StandaloneContainer install() {
        StandaloneContainer.Builder builder = StandaloneContainer.create();

        if(isNativeDocker()) {
            builder.volumes(Arrays.asList("/var/run/docker.sock:/var/run/docker.sock"));
        } else {
            final CubeDockerConfiguration cubeDockerConfiguration = cubeDockerConfigurationInstance.get();
            builder.volumes(Arrays.asList(cubeDockerConfiguration.getCertPath() + ":/etc/ssl/docker"));
        }

        return builder.build();
    }

    private boolean isNativeDocker() {
        final CubeDockerConfiguration cubeDockerConfiguration = cubeDockerConfigurationInstance.get();
        return cubeDockerConfiguration.getDockerServerUri().startsWith("unix");
    }

}
