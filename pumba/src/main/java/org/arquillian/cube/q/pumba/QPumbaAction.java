package org.arquillian.cube.q.pumba;

import org.arquillian.cube.CubeController;
import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.docker.impl.model.DockerCube;
import org.arquillian.cube.q.api.ContainerChaos;
import org.arquillian.cube.q.pumba.util.PumbaCommandLineCreator;
import org.arquillian.cube.q.spi.StandaloneContainer;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.CubeRegistry;

public class QPumbaAction implements ContainerChaos.Action {

    private final CubeController cubeController;
    private final CubeRegistry cubeRegistry;
    private final CubeDockerConfiguration cubeDockerConfiguration;

    public QPumbaAction(CubeController cubeController, CubeRegistry cubeRegistry, CubeDockerConfiguration cubeDockerConfiguration) {
        this.cubeController = cubeController;
        this.cubeRegistry = cubeRegistry;
        this.cubeDockerConfiguration = cubeDockerConfiguration;
    }

    @Override
    public ContainerChaos.Action stop(ContainerChaos.ContainersType containersType, ContainerChaos.IntervalType intervalType) {
        configurePumbaCube(PumbaChaosCommandBuilder.create()
                .containers(containersType)
                .interval(intervalType)
                .chaosOperation(ChaosOperation.STOP)
                , false);
        return this;
    }

    @Override
    public ContainerChaos.Action stopRandomly(ContainerChaos.ContainersType containersType, ContainerChaos.IntervalType intervalType) {
        configurePumbaCube(PumbaChaosCommandBuilder.create()
                        .containers(containersType)
                        .interval(intervalType)
                        .chaosOperation(ChaosOperation.STOP)
                , true);
        return this;
    }

    @Override
    public ContainerChaos.Action remove(ContainerChaos.ContainersType containersType, ContainerChaos.IntervalType intervalType) {
        configurePumbaCube(PumbaChaosCommandBuilder.create()
                        .containers(containersType)
                        .interval(intervalType)
                        .chaosOperation(ChaosOperation.RM)
                , false);
        return this;
    }

    @Override
    public ContainerChaos.Action removeRandomly(ContainerChaos.ContainersType containersType, ContainerChaos.IntervalType intervalType) {
        configurePumbaCube(PumbaChaosCommandBuilder.create()
                        .containers(containersType)
                        .interval(intervalType)
                        .chaosOperation(ChaosOperation.RM)
                , true);
        return this;
    }

    @Override
    public ContainerChaos.Action kill(ContainerChaos.ContainersType containersType, ContainerChaos.IntervalType intervalType, ContainerChaos.KillSignal killSignal) {
        configurePumbaCube(PumbaChaosCommandBuilder.create()
                        .containers(containersType)
                        .interval(intervalType)
                        .chaosOperation(ChaosOperation.KILL)
                        .killSignal(killSignal)
                , false);
        return this;
    }

    @Override
    public ContainerChaos.Action killRandomly(ContainerChaos.ContainersType containersType, ContainerChaos.IntervalType intervalType, ContainerChaos.KillSignal killSignal) {
        configurePumbaCube(PumbaChaosCommandBuilder.create()
                        .containers(containersType)
                        .interval(intervalType)
                        .chaosOperation(ChaosOperation.KILL)
                        .killSignal(killSignal)
                , true);
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

    private void configurePumbaCube(PumbaChaosCommandBuilder chaosCommand, boolean random) {
        final Cube<?> cube = cubeRegistry.getCube(StandaloneContainer.Builder.DEFAULT_NAME);

        DockerCube dockerCube = (DockerCube) cube;
        dockerCube.configuration().setCmd(PumbaCommandLineCreator.run(chaosCommand.build(), random, cubeDockerConfiguration));
    }

    enum ChaosOperation {
        STOP, RM, KILL
    }

    public static class PumbaChaosCommandBuilder {

        private static final String SEPARATOR = "|";
        public static final String SECONDS = "s";

        private ContainerChaos.ContainersType containersType;
        private ContainerChaos.IntervalType intervalType;
        private ChaosOperation chaosOperation;
        private ContainerChaos.KillSignal killSignal;

        private PumbaChaosCommandBuilder() {
        }

        public static PumbaChaosCommandBuilder create() {
            return new PumbaChaosCommandBuilder();
        }

        public PumbaChaosCommandBuilder containers(ContainerChaos.ContainersType containersType) {
            this.containersType = containersType;
            return this;
        }

        public PumbaChaosCommandBuilder interval(ContainerChaos.IntervalType intervalType) {
            this.intervalType = intervalType;
            return this;
        }

        public PumbaChaosCommandBuilder chaosOperation(ChaosOperation chaosOperation) {
            this.chaosOperation = chaosOperation;
            return this;
        }

        public PumbaChaosCommandBuilder killSignal(ContainerChaos.KillSignal killSignal) {
            this.killSignal = killSignal;
            return this;
        }

        public String build() {
            StringBuilder command = new StringBuilder();
            //containersType.getValue() + "|" + intervalType.getValue() + "s|KILL:" + killSignal.name()
            command.append(containersType.getValue()).append(SEPARATOR);
            command.append(intervalType.getValue()).append(SECONDS).append(SEPARATOR);
            command.append(chaosOperation.name());

            if(this.chaosOperation == ChaosOperation.KILL) {
                command.append(":").append(killSignal.name());
            }
            return command.toString();
        }

    }

}