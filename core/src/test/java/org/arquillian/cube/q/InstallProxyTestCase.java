package org.arquillian.cube.q;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.q.core.InstallProxy;
import org.arquillian.cube.q.spi.Proxy;
import org.arquillian.cube.q.spi.ProxyManager;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.core.test.AbstractManagerTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class InstallProxyTestCase extends AbstractManagerTestBase {

    private static final String CONTENT =
        "a:\n" +
            "  image: a/a\n" +
            "  portBindings: [8089/tcp]\n" +
            "  links:\n" +
            "    - b:b\n" +
            "b:\n" +
            "  image: b/b\n" +
            "  exposedPorts: [2112/tcp]\n";

    @Mock
    private ArquillianDescriptor descriptor;

    @Mock
    private ServiceLoader serviceLoader;

    @Mock
    private ProxyManager proxyManager;

    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(InstallProxy.class);
    }

    @Before
    public void setup() {

        Proxy p = new Proxy.Builder().build();

        bind(ApplicationScoped.class, ArquillianDescriptor.class, descriptor);
        bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);

        Mockito.when(serviceLoader.onlyOne(ProxyManager.class)).thenReturn(proxyManager);
        Mockito.when(proxyManager.install(Mockito.any(DockerCompositions.class))).thenReturn(p);
    }

    @Test
    public void shouldInstallProxy() throws Exception {
        CubeDockerConfiguration config = createConfig(CONTENT);
        fire(config);

        DockerCompositions cubes = config.getDockerContainersContent();
        Assert.assertEquals(3, cubes.getContainerIds().size());

        System.out.println(config.toString());
    }

    private CubeDockerConfiguration createConfig(String content) {
        Map<String, String> parameters = new HashMap<>();

        parameters.put("serverVersion", "1.13");
        parameters.put("serverUri", "http://localhost:25123");
        parameters.put("dockerContainers", content);

        return CubeDockerConfiguration.fromMap(parameters, null);
    }
}
