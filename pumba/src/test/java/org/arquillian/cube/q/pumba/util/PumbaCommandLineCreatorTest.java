package org.arquillian.cube.q.pumba.util;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PumbaCommandLineCreatorTest {

    @Mock
    CubeDockerConfiguration cubeDockerConfiguration;

    @Test
    public void shouldCreatePumbaCli() {
        when(cubeDockerConfiguration.getDockerServerUri()).thenReturn("https://192.168.0.1");

        final Collection<String> cli = PumbaCommandLineCreator.run("re2:^hp|10s|KILL:SIGTERM", false, cubeDockerConfiguration);
        assertThat(cli).containsExactly("pumba", "--host", "https://192.168.0.1", "--tlsverify",  "run", "--chaos", "re2:^hp|10s|KILL:SIGTERM");

    }

    @Test
    public void shouldCreatePumbaCliWithRandom() {
        when(cubeDockerConfiguration.getDockerServerUri()).thenReturn("https://192.168.0.1");

        final Collection<String> cli = PumbaCommandLineCreator.run("re2:^hp|10s|KILL:SIGTERM", true, cubeDockerConfiguration);
        assertThat(cli).containsExactly("pumba", "--host", "https://192.168.0.1", "--tlsverify",  "run", "--random", "--chaos", "re2:^hp|10s|KILL:SIGTERM");

    }

}
