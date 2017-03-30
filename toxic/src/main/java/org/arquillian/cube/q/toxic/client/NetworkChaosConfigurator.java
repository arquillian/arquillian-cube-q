package org.arquillian.cube.q.toxic.client;

import org.arquillian.cube.q.spi.NetworkChaosConfiguration;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

import java.util.Map;

public class NetworkChaosConfigurator {

    private static final String EXTENSION_NAME = "networkChaos";

    @Inject
    @ApplicationScoped
    private InstanceProducer<NetworkChaosConfiguration> networkChaosConfigurationInstanceProducer;

    //Add precedence -10 because we need that ContainerRegistry is available in the Arquillian scope.
    public void configure(@Observes ArquillianDescriptor arquillianDescriptor) {
        Map<String, String> config = arquillianDescriptor.extension(EXTENSION_NAME).getExtensionProperties();
        NetworkChaosConfiguration cubeConfiguration = NetworkChaosConfiguration.fromMap(config);
        networkChaosConfigurationInstanceProducer.set(cubeConfiguration);
    }
}
