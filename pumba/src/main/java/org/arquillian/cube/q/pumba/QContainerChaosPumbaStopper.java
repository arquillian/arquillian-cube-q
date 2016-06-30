package org.arquillian.cube.q.pumba;

import org.arquillian.cube.CubeController;
import org.arquillian.cube.q.spi.StandaloneContainer;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.CubeRegistry;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;

public class QContainerChaosPumbaStopper {

    @Inject
    private Instance<CubeRegistry> cubeRegistryInstance;

    @Inject
    private Instance<CubeController> cubeControllerInstance;

    public void stopPumbaContainer(@Observes AfterSuite afterSuite) {
        final Cube<?> cube = cubeRegistryInstance.get().getCube(StandaloneContainer.Builder.DEFAULT_NAME);
        if (cube != null) {
            if (cube.state() != Cube.State.STOPPED && cube.state() != Cube.State.DESTROYED) {
                cubeControllerInstance.get().stop(StandaloneContainer.Builder.DEFAULT_NAME);
                cubeControllerInstance.get().destroy(StandaloneContainer.Builder.DEFAULT_NAME);
            }
        }
    }

}
