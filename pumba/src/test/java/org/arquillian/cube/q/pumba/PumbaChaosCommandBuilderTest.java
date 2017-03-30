package org.arquillian.cube.q.pumba;

import org.arquillian.cube.q.api.ContainerChaos;
import org.arquillian.cube.q.pumba.QPumbaAction.ChaosOperation;
import org.junit.Test;

import static org.arquillian.cube.q.api.ContainerChaos.ContainersType.containers;
import static org.arquillian.cube.q.api.ContainerChaos.ContainersType.regularExpression;
import static org.arquillian.cube.q.api.ContainerChaos.IntervalType.intervalInSeconds;
import static org.assertj.core.api.Assertions.assertThat;

public class PumbaChaosCommandBuilderTest {

    @Test
    public void shouldCreateStopCommand() {
        final String stopCommand = QPumbaAction.PumbaChaosCommandBuilder.create()
            .containers(containers("a", "b"))
            .interval(intervalInSeconds(4))
            .chaosOperation(ChaosOperation.STOP)
            .build();

        assertThat(stopCommand).isEqualTo("a,b|4s|STOP");
    }

    @Test
    public void shouldCreateRemoveCommand() {
        final String removeCommand = QPumbaAction.PumbaChaosCommandBuilder.create()
            .containers(regularExpression("^pingpong"))
            .interval(intervalInSeconds(4))
            .chaosOperation(ChaosOperation.RM)
            .build();

        assertThat(removeCommand).isEqualTo("re2:^pingpong|4s|RM");
    }

    @Test
    public void shouldCreateKillCommand() {
        final String killCommand = QPumbaAction.PumbaChaosCommandBuilder.create()
            .containers(containers("a"))
            .interval(intervalInSeconds(4))
            .chaosOperation(ChaosOperation.KILL)
            .killSignal(ContainerChaos.KillSignal.SIGTERM)
            .build();

        assertThat(killCommand).isEqualTo("a|4s|KILL:SIGTERM");
    }
}
