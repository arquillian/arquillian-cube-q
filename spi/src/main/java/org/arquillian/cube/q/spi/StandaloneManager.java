package org.arquillian.cube.q.spi;

import org.arquillian.cube.docker.impl.client.config.DockerCompositions;

public interface StandaloneManager {

    StandaloneContainer install(DockerCompositions dockerCompositions);

}
