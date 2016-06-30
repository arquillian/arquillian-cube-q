package org.arquillian.cube.q.pumba;

import org.arquillian.cube.CubeController;
import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.docker.impl.model.DockerCube;
import org.arquillian.cube.q.api.ContainerChaos;
import org.arquillian.cube.q.pumba.util.CommandLineUtil;
import org.arquillian.cube.q.spi.StandaloneContainer;
import org.arquillian.cube.spi.Cube;
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
        return new Action() {

            private void configurePumbaCube(String chaosCommand, boolean random) {
                final Cube<?> cube = cubeRegistry.getCube(StandaloneContainer.Builder.DEFAULT_NAME);

                DockerCube dockerCube = (DockerCube) cube;
                dockerCube.configuration().setCmd(CommandLineUtil.pumbaCli(chaosCommand, random, cubeDockerConfiguration));
            }

            @Override
            public Action stop(ContainersType containersType, IntervalType intervalType) {
                configurePumbaCube(containersType.getValue() + "|" + intervalType.getValue() + "s|STOP", false);
                return this;
            }

            @Override
            public Action stopRandomly(ContainersType containersType, IntervalType intervalType) {
                configurePumbaCube(containersType.getValue() + "|" + intervalType.getValue() + "s|STOP", true);
                return this;
            }

            @Override
            public Action remove(ContainersType containersType, IntervalType intervalType) {
                configurePumbaCube(containersType.getValue() + "|" + intervalType.getValue() + "s|RM", false);
                return this;
            }

            @Override
            public Action removeRandomly(ContainersType containersType, IntervalType intervalType) {
                configurePumbaCube(containersType.getValue() + "|" + intervalType.getValue() + "s|RM", true);
                return this;
            }

            @Override
            public Action kill(ContainersType containersType, IntervalType intervalType, KillSignal killSignal) {
                configurePumbaCube(containersType.getValue() + "|" + intervalType.getValue() + "s|KILL:" + killSignal.name(), false);
                return this;
            }

            @Override
            public Action killRandomly(ContainersType containersType, IntervalType intervalType, KillSignal killSignal) {
                configurePumbaCube(containersType.getValue() + "|" + intervalType.getValue() + "s|KILL:" + killSignal.name(), true);
                return this;
            }

            @Override
            public void exec() throws Exception {
                startPumba();
            }

            @Override
            public void exec(Perform perform) throws Exception {
                try {
                    startPumba();
                    perform.execute();
                } finally {
                    stopPumba();
                }

            }

            @Override
            public void exec(RunCondition runCondition, Perform perform) throws Exception {
                try {
                    startPumba();
                    while (runCondition.isExecutable()) {
                        perform.execute();
                    }
                } finally {
                    stopPumba();
                }
            }

            private void startPumba() {
                cubeController.create(StandaloneContainer.Builder.DEFAULT_NAME);
                cubeController.start(StandaloneContainer.Builder.DEFAULT_NAME);
            }


            private void stopPumba() {
                cubeController.stop(StandaloneContainer.Builder.DEFAULT_NAME);
                cubeController.destroy(StandaloneContainer.Builder.DEFAULT_NAME);
            }
        };
    }
}
