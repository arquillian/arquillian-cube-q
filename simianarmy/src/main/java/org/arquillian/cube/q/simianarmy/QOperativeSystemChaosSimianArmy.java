package org.arquillian.cube.q.simianarmy;

import org.arquillian.cube.q.api.OperativeSystemChaos;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.CubeRegistry;

public class QOperativeSystemChaosSimianArmy implements OperativeSystemChaos {

    private CubeRegistry cubeRegistry;

    public QOperativeSystemChaosSimianArmy(CubeRegistry cubeRegistry) {
        this.cubeRegistry = cubeRegistry;
    }

    @Override
    public Action on(String containerId) {
        final Cube<?> cube = cubeRegistry.getCube(containerId);

        if (cube == null) {
            throw new IllegalArgumentException(String.format("Container %s is not registered in Arquillian Cube.", containerId));
        }
        return new QSimianArmyAction(cube);
    }
}
