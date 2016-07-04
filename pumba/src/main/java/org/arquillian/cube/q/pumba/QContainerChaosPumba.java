package org.arquillian.cube.q.pumba;

import org.arquillian.cube.CubeController;
import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.q.api.ContainerChaos;
import org.arquillian.cube.spi.CubeRegistry;


public class QContainerChaosPumba implements ContainerChaos {

    private CubeRegistry cubeRegistry;
    private CubeController cubeController;
    private CubeDockerConfiguration cubeDockerConfiguration;

    public QContainerChaosPumba(CubeRegistry cubeRegistry, CubeController cubeController, CubeDockerConfiguration cubeDockerConfiguration) {
        this.cubeRegistry = cubeRegistry;
        this.cubeController = cubeController;
        this.cubeDockerConfiguration = cubeDockerConfiguration;
    }

    @Override
    public Action onCubeDockerHost() {
        return new QPumbaAction(this.cubeController, this.cubeRegistry, this.cubeDockerConfiguration);
    }

}
