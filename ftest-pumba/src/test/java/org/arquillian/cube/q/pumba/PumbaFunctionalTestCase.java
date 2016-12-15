package org.arquillian.cube.q.pumba;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import org.arquillian.cube.q.api.ContainerChaos;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(Arquillian.class)
public class PumbaFunctionalTestCase {

    @ArquillianResource
    ContainerChaos containerChaos;

    @ArquillianResource
    DockerClient dockerClient;

    @Test
    public void shouldKillContainers() throws Exception {
        containerChaos
                .onCubeDockerHost()
                    .killRandomly(
                            ContainerChaos.ContainersType.regularExpression("^pingpong"),
                            ContainerChaos.IntervalType.intervalInSeconds(4),
                            ContainerChaos.KillSignal.SIGTERM
                    )
                .exec();


        TimeUnit.SECONDS.sleep(12);

        final List<Container> containers = dockerClient.listContainersCmd().exec();
        //Pumba container is not killed by itself
        assertThat(containers).hasSize(1);

    }

}
