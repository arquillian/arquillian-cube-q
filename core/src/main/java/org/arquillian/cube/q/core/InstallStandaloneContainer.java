package org.arquillian.cube.q.core;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.docker.impl.client.config.CubeContainer;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.docker.impl.util.ConfigUtil;
import org.arquillian.cube.q.spi.StandaloneContainer;
import org.arquillian.cube.q.spi.StandaloneManager;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;

public class InstallStandaloneContainer {

    @Inject
    private Instance<ServiceLoader> serviceLoaderInst;

    @Inject
    @ApplicationScoped
    private InstanceProducer<StandaloneContainer> standaloneContainerInst;

    public void install(@Observes(precedence = 100) CubeDockerConfiguration configuration) {

        StandaloneManager installer = serviceLoaderInst.get().onlyOne(StandaloneManager.class);

        if (installer != null) {
            DockerCompositions cubes = configuration.getDockerContainersContent();

            final StandaloneContainer install = installer.install();
            final CubeContainer cube = install.getCube();
            cubes.add(install.getName(), cube);


            standaloneContainerInst.set(install);

            System.out.println("STANDALONE CONTAINER INSTALLED");
            System.out.println(ConfigUtil.dump(cubes));
        }
    }


}
