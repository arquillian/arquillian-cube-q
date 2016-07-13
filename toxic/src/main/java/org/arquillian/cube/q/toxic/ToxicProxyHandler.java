package org.arquillian.cube.q.toxic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.arquillian.cube.docker.impl.client.config.CubeContainer;
import org.arquillian.cube.docker.impl.client.config.DockerCompositions;
import org.arquillian.cube.docker.impl.client.config.ExposedPort;
import org.arquillian.cube.docker.impl.client.config.Link;
import org.arquillian.cube.docker.impl.client.config.PortBinding;
import org.arquillian.cube.q.spi.NetworkChaosConfiguration;
import org.arquillian.cube.q.spi.Proxy;
import org.arquillian.cube.q.spi.Proxy.Relation;
import org.arquillian.cube.q.spi.ProxyManager;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient;
import org.arquillian.cube.q.toxic.client.ToxiProxyScenario;
import org.arquillian.cube.spi.Cube;
import org.arquillian.cube.spi.metadata.HasPortBindings;
import org.arquillian.cube.spi.metadata.HasPortBindings.PortAddress;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;

public class ToxicProxyHandler implements ProxyManager {

    @Inject
    private Instance<Proxy> proxyInst;

    @Inject
    Instance<NetworkChaosConfiguration> networkChaosConfigurationInstance;

    @Inject
    @ApplicationScoped
    private InstanceProducer<ToxiProxyScenario> scenarioInst;


    @Override
    public Proxy install(DockerCompositions cubes) {
        Proxy.Builder builder = Proxy.create();

        final Map<String, CubeContainer> containers = cubes.getContainers();
        for (Map.Entry<String, CubeContainer> cube : containers.entrySet()) {
            String cubeName = cube.getKey();

            CubeContainer data = cube.getValue();

            NetworkChaosConfiguration networkChaosConfiguration = networkChaosConfigurationInstance.get();
            if (!networkChaosConfiguration.isToxifyPortBinding()) {
                // Now we need to expose the same ports of the linked service
                final Collection<Link> links = data.getLinks();

                if (links != null) {
                    for (Link link : links) {
                        if (containers.containsKey(link.getName())) {
                            final CubeContainer linkedContainer = containers.get(link.getName());

                            final Collection<PortBinding> portBindings = linkedContainer.getPortBindings();
                            if (portBindings != null) {
                                for (PortBinding portBinding : portBindings) {
                                    final ExposedPort exposedPort = portBinding.getExposedPort();
                                    if (exposedPort != null) {
                                        builder.containerExpose(link.getName(), exposedPort.getExposed(), exposedPort.getType());
                                    }
                                }
                            }

                            final Collection<ExposedPort> exposedPorts = linkedContainer.getExposedPorts();
                            if (exposedPorts != null) {
                                for (ExposedPort exposedPort : exposedPorts) {
                                    builder.containerExpose(link.getName(), exposedPort.getExposed(), exposedPort.getType());
                                }
                            }
                        }
                    }
                }
                // redirect links to proxy and adds links to proxy to the old links
                data.setLinks(updateLinks(cubeName, builder, data.getLinks()));

            } else {

                // finally we need to detect all cubes that binds a port to the host computer

                // all services that binds a port to host computer should not binds anymore (since they will be bound by toxiproxy)
                // so we need to remove all of them

                List<PortBinding> removedBoundPorts = new ArrayList<PortBinding>();
                if (data.getPortBindings() != null) {
                    Collection<PortBinding> ports = data.getPortBindings();
                    for (PortBinding binding : ports) {
                        builder.containerBinds(cubeName, binding.getBound(), binding.getExposedPort().getExposed(), binding.getExposedPort().getType());
                        removedBoundPorts.add(binding);
                    }
                    data.setPortBindings(null);
                }

                // if it has exposed ports, these ports should be added as well
                if (data.getExposedPorts() != null) {
                    Collection<ExposedPort> ports = data.getExposedPorts();
                    for (ExposedPort port : ports) {
                        builder.containerExpose(cubeName, port.getExposed(), port.getType());
                    }
                }

                // if
                if (removedBoundPorts.size() > 0) {
                    Collection<ExposedPort> ports = new ArrayList<ExposedPort>();
                    if (data.getExposedPorts() != null) {
                        ports = data.getExposedPorts();
                    }
                    for (PortBinding binding : removedBoundPorts) {
                        ports.add(binding.getExposedPort());
                    }
                    data.setExposedPorts(ports);
                }
            }
        }

        return builder.build();
    }


    private Collection<Link> updateLinks(String cubeName, Proxy.Builder proxy, Collection<Link> links) {
        Collection<Link> updatedLinks = new ArrayList<Link>();
        if (links != null) {
            updatedLinks = new ArrayList<Link>();
            for (Link link : links) {
                proxy.containerLinks(link.getName(), link.getName() + "_toxiproxy");
                updatedLinks.add(new Link(proxy.getName(), link.getAlias()));
            }
            updatedLinks.add(new Link(proxy.getName(), proxy.getName()));
        }
        return updatedLinks;
    }


    @Override
    public void proxyStarted(Cube<?> cube) {
        Proxy proxy = proxyInst.get();

        if (!cube.hasMetadata(HasPortBindings.class)) {
            throw new IllegalStateException("Proxy Cube " + proxy.getName() + " has no PortBinding data.");
        }
        HasPortBindings bindings = cube.getMetadata(HasPortBindings.class);

        PortAddress communicationPort = bindings.getMappedAddress(proxy.getCommunicationPort().getExposed());

        scenarioInst.set(new ToxiProxyScenario(ToxiProxyClient.Builder.create(communicationPort.getIP(), communicationPort.getPort())));
    }


    @Override
    public void populateProxies() {
        Proxy proxy = proxyInst.get();
        ToxiProxyScenario scenario = scenarioInst.get();
        if (scenario == null) {
            throw new IllegalStateException("Scenario is not Initiated. Proxy not started before others.");
        }

        for (Relation rel : proxy.getRelations()) {
            String relName = rel.getFrom() + ":" + rel.getPort().getExposed();
            String localExp = "0.0.0.0:" + rel.getPort().getExposed();
            String remoteExp = rel.getTo() + ":" + rel.getPort().getExposed();

            System.out.println("Registered proxy " + relName + " " + localExp + " -> " + remoteExp);
            scenario.register(
                    relName,
                    localExp,
                    remoteExp);
        }
    }

    @Override
    public void cubeStopped(Cube<?> cube) {
    }
}
