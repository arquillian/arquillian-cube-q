package org.arquillian.cube.q.simianarmy;

import org.arquillian.cube.q.api.OperativeSystemChaos;
import org.arquillian.cube.spi.CubeRegistry;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;

public class QOperativeSystemChaosSimianArmyCreator {

    @Inject
    @ApplicationScoped
    private InstanceProducer<OperativeSystemChaos> operativeSystemChaosInstanceProducer;

    @Inject
    private Instance<CubeRegistry> cubeRegistryInstance;

    public void createSimianArmyScripts(@Observes BeforeSuite event) {
        operativeSystemChaosInstanceProducer.set(new QOperativeSystemChaosSimianArmy(cubeRegistryInstance.get()));
    }

}
