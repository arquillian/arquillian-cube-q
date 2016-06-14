package org.arquillian.cube.q.spi;

import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.spi.Cube;

public interface ProxyManager {

    Proxy install(DockerCompositions containers);
    
    void proxyStarted(Cube<?> cube);
    void cubeStarted(Cube<?> cube);
    void cubeStopped(Cube<?> cube);
}
