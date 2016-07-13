package org.arquillian.cube.q.spi;

import java.util.Map;

public class NetworkChaosConfiguration {

    private static final String TOXIFY_PORT_BINDING = "toxifyPortBinding";

    private boolean toxifyPortBinding = false;

    public boolean isToxifyPortBinding() {
        return toxifyPortBinding;
    }

    public static NetworkChaosConfiguration fromMap(Map<String, String> configuration) {

        NetworkChaosConfiguration networkChaosConfiguration = new NetworkChaosConfiguration();

        if (configuration.containsKey(TOXIFY_PORT_BINDING)) {
            networkChaosConfiguration.toxifyPortBinding = Boolean.parseBoolean(configuration.get(TOXIFY_PORT_BINDING));
        }

        return networkChaosConfiguration;

    }
}
