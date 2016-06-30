package org.arquillian.cube.q.pumba.util;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;

@RunWith(MockitoJUnitRunner.class)
public class CommandLineUtilTest {

    @Mock
    CubeDockerConfiguration cubeDockerConfiguration;

    @Test
    public void shouldCreatePumbaCli() {
        Mockito.when(cubeDockerConfiguration.getDockerServerUri()).thenReturn("https://192.168.0.1");

        final Collection<String> cli = CommandLineUtil.pumbaCli("re2:^hp|10s|KILL:SIGTERM", false, cubeDockerConfiguration);
        Assertions.assertThat(cli).containsExactly("pumba", "--host", "https://192.168.0.1", "--tlsverify",  "run", "--chaos", "re2:^hp|10s|KILL:SIGTERM");

    }

    @Test
    public void shouldCreatePumbaCliWithRandom() {
        Mockito.when(cubeDockerConfiguration.getDockerServerUri()).thenReturn("https://192.168.0.1");

        final Collection<String> cli = CommandLineUtil.pumbaCli("re2:^hp|10s|KILL:SIGTERM", true, cubeDockerConfiguration);
        Assertions.assertThat(cli).containsExactly("pumba", "--host", "https://192.168.0.1", "--tlsverify",  "run", "--random", "--chaos", "re2:^hp|10s|KILL:SIGTERM");

    }

}
