package org.arquillian.cube.q.spi;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertThat;

import org.arquillian.cube.docker.impl.client.config.CubeContainer;
import org.arquillian.cube.docker.impl.client.config.ExposedPort;
import org.arquillian.cube.docker.impl.client.config.PortBinding;
import org.junit.Assert;
import org.junit.Test;

public class ProxyBuilderTestCase {

    @Test
    public void shouldAddDefaultPort() {
        Proxy proxy = Proxy.create()
            .build();

        CubeContainer cube = proxy.getCube();
        assertThat(cube.getPortBindings(), hasItem(PortBinding.valueOf("8474/tcp")));
    }

    @Test
    public void shouldExposeAdditionalPorts() {
        Proxy proxy = Proxy.create()
            .containerExpose("A", 8080, "tcp")
            .build();

        CubeContainer cube = proxy.getCube();
        assertThat(cube.getExposedPorts(), hasItem(ExposedPort.valueOf("8080/tcp")));
    }

    @Test
    public void shouldBindAdditionalPorts() {
        Proxy proxy = Proxy.create()
            .containerBinds("A", 1000, 2000, "tcp")
            .build();

        CubeContainer cube = proxy.getCube();
        Assert.assertThat(cube.getPortBindings(),
            hasItems(PortBinding.valueOf("1000->2000/tcp"), PortBinding.valueOf("8474/tcp")));

        System.out.println(proxy.getRelations());
    }
}
