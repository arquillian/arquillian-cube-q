package org.arquillian.cube.q.pumba;

import org.arquillian.cube.CubeController;
import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.q.api.ContainerChaos;
import org.arquillian.cube.spi.CubeRegistry;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

public class QContainerChaosPumbaCreator {

    @Inject
    @ApplicationScoped
    private InstanceProducer<ContainerChaos> containerChaosInst;

    @Inject
    private Instance<CubeRegistry> cubeRegistryInstance;

    @Inject
    private Instance<CubeController> cubeControllerInstance;

    @Inject
    private Instance<CubeDockerConfiguration> cubeDockerConfigurationInstance;

    public void createPumba(@Observes BeforeSuite event) {
        containerChaosInst.set(
                new QContainerChaosPumba(cubeRegistryInstance.get(), cubeControllerInstance.get(), cubeDockerConfigurationInstance.get())
        );
    }

}
