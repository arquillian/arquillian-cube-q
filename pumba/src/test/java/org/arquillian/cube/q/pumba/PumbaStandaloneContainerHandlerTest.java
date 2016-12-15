package org.arquillian.cube.q.pumba;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.q.spi.StandaloneContainer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PumbaStandaloneContainerHandlerTest {

    @Mock
    CubeDockerConfiguration cubeDockerConfiguration;

    @Test
    public void shouldAddUnixVolume() {

        when(cubeDockerConfiguration.getDockerServerUri()).thenReturn("unix:///var");
        PumbaStandaloneContainerHandler pumbaStandaloneContainerHandler = new PumbaStandaloneContainerHandler();
        pumbaStandaloneContainerHandler.cubeDockerConfigurationInstance = () -> cubeDockerConfiguration;

        final StandaloneContainer install = pumbaStandaloneContainerHandler.install();
        assertThat(install.getCube().getBinds()).contains("/var/run/docker.sock:/var/run/docker.sock");

    }

    @Test
    public void shouldAddCertsVolume() {

        when(cubeDockerConfiguration.getDockerServerUri()).thenReturn("https://192.168.0.1");
        when(cubeDockerConfiguration.getCertPath()).thenReturn("/home/user/.machine/ssl");
        PumbaStandaloneContainerHandler pumbaStandaloneContainerHandler = new PumbaStandaloneContainerHandler();
        pumbaStandaloneContainerHandler.cubeDockerConfigurationInstance = () -> cubeDockerConfiguration;

        final StandaloneContainer install = pumbaStandaloneContainerHandler.install();
        assertThat(install.getCube().getBinds()).contains("/home/user/.machine/ssl:/etc/ssl/docker");

    }
}
