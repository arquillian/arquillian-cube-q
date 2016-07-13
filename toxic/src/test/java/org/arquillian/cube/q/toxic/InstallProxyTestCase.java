package org.arquillian.cube.q.toxic;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.docker.impl.client.config.*;
import org.arquillian.cube.q.core.InstallProxy;
import org.arquillian.cube.q.core.RegisterProxy;
import org.arquillian.cube.q.spi.NetworkChaosConfiguration;
import org.arquillian.cube.q.spi.ProxyManager;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.core.test.AbstractManagerTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


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
    private ServiceLoader loader;

    @Mock
    NetworkChaosConfiguration networkChaosConfiguration;

    private ToxicProxyHandler t;

    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(InstallProxy.class);
    }

    @Before
    public void setup() {
        t = new ToxicProxyHandler();
        t.networkChaosConfigurationInstance = new Instance<NetworkChaosConfiguration>() {
            @Override
            public NetworkChaosConfiguration get() {
                Mockito.when(networkChaosConfiguration.isToxifyPortBinding()).thenReturn(false);
                return networkChaosConfiguration;
            }
        };
        Mockito.when(loader.onlyOne(ProxyManager.class)).thenReturn(t);
        bind(ApplicationScoped.class, ServiceLoader.class, loader);
    }
    
    @Test
    public void shouldInstallProxy() throws Exception {
        CubeDockerConfiguration config = createConfig(CONTENT);
        fire(config);
        
        DockerCompositions cubes = config.getDockerContainersContent();
        assertThat(cubes.getContainerIds()).hasSize(3);
        System.out.println(config.toString());
    }

    @Test
    public void shouldRedirectLinksToToxicProxy() {
        CubeDockerConfiguration config = createConfig(CONTENT);
        fire(config);

        DockerCompositions cubes = config.getDockerContainersContent();
        CubeContainer a = cubes.get("a");
        assertThat(a.getLinks()).containsExactlyInAnyOrder(new Link("toxiproxy", "b"), new Link("toxiproxy", "toxiproxy"));

        CubeContainer b = cubes.get("toxiproxy");
        assertThat(b.getLinks()).containsExactlyInAnyOrder(new Link("b", "b_toxiproxy"));
    }

    @Test
    public void shouldRedirtPortBindingToToxicProxy() {

        t = new ToxicProxyHandler();
        t.networkChaosConfigurationInstance = new Instance<NetworkChaosConfiguration>() {
            @Override
            public NetworkChaosConfiguration get() {
                Mockito.when(networkChaosConfiguration.isToxifyPortBinding()).thenReturn(true);
                return networkChaosConfiguration;
            }
        };
        Mockito.when(loader.onlyOne(ProxyManager.class)).thenReturn(t);


        CubeDockerConfiguration config = createConfig(CONTENT);
        fire(config);

        DockerCompositions cubes = config.getDockerContainersContent();
        CubeContainer a = cubes.get("a");
        assertThat(a.getPortBindings()).isNullOrEmpty();
        assertThat(a.getExposedPorts()).containsExactlyInAnyOrder(ExposedPort.valueOf("8089/tcp"));

        CubeContainer b = cubes.get("toxiproxy");
        assertThat(b.getLinks()).containsExactlyInAnyOrder(new Link("a", "a_toxiproxy"));
        assertThat(b.getPortBindings()).containsExactlyInAnyOrder(PortBinding.valueOf("8474/tcp"), PortBinding.valueOf("8089/tcp"));

    }

    private CubeDockerConfiguration createConfig(String content) {
        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("serverVersion", "1.13");
        parameters.put("serverUri", "http://localhost:25123");
        parameters.put("dockerContainers", content);

        return CubeDockerConfiguration.fromMap(parameters, null);
    }
}
