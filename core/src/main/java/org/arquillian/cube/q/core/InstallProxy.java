package org.arquillian.cube.q.core;

import org.arquillian.cube.docker.impl.client.CubeDockerConfiguration;
import org.arquillian.cube.docker.impl.client.config.CubeContainer;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.docker.impl.util.ConfigUtil;
import org.arquillian.cube.q.spi.Proxy;
import org.arquillian.cube.q.spi.ProxyManager;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;

public class InstallProxy {

    @Inject
    private Instance<ServiceLoader> serviceLoaderInst;

    @Inject @ApplicationScoped
    private InstanceProducer<Proxy> proxyInst;

    public void install(@Observes(precedence = 100) CubeDockerConfiguration configuration) {

        DockerCompositions cubes = configuration.getDockerContainersContent();
        ProxyManager installer = serviceLoaderInst.get().onlyOne(ProxyManager.class);

        if (installer != null) {
            Proxy proxy = installer.install(cubes);
            proxyInst.set(proxy);

            final CubeContainer cube = proxy.getCube();
            cubes.add(proxy.getName(), cube);

            System.out.println("PROXY INSTALLED");
            System.out.println(ConfigUtil.dump(cubes));
        }
    }
}
