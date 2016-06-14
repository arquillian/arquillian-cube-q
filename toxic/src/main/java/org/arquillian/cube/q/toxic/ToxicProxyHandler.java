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
    
    @Inject @ApplicationScoped
    private InstanceProducer<ToxiProxyScenario> scenarioInst;
    
  
    @Override
    public Proxy install(DockerCompositions cubes) {
        Proxy.Builder builder = Proxy.create();

        for(Map.Entry<String, CubeContainer> cube : cubes.getContainers().entrySet()) {
            String cubeName = cube.getKey();
            
            CubeContainer data = cube.getValue();
            data.setLinks(updateLinks(cubeName, builder, data.getLinks()));
            
            List<PortBinding> removedBoundPorts = new ArrayList<PortBinding>();
            if(data.getPortBindings() != null) {
                Collection<PortBinding> ports = data.getPortBindings();
                for(PortBinding binding : ports) {
                    builder.containerBinds(cubeName, binding.getBound(), binding.getExposedPort().getExposed(), binding.getExposedPort().getType());
                    removedBoundPorts.add(binding);
                }
                data.setPortBindings(null);
            }
            if(data.getExposedPorts() != null) {
                Collection<ExposedPort> ports = data.getExposedPorts();
                for(ExposedPort port : ports) {
                    builder.containerExpose(cubeName, port.getExposed(), port.getType());
                }
            }
            if(removedBoundPorts.size() > 0) {
                Collection<ExposedPort> ports = new ArrayList<ExposedPort>();
                if(data.getExposedPorts() != null) {
                    ports = data.getExposedPorts();
                }
                for(PortBinding binding : removedBoundPorts) {
                    ports.add(binding.getExposedPort());
                }
                data.setExposedPorts(ports);
            }
        }

        return builder.build();        
    }


    private Collection<Link> updateLinks(String cubeName, Proxy.Builder proxy, Collection<Link> links) {
        Collection<Link> updatedLinks = new ArrayList<Link>();
        if(links != null) {
            updatedLinks = new ArrayList<Link>();
            for(Link link : links) {
                proxy.containerLinks(cubeName, link.getName());
                updatedLinks.add(new Link(proxy.getName(), link.getName()));
            }
        }
        updatedLinks.add(new Link(proxy.getName(), proxy.getName()));
        return updatedLinks;
    }


    @Override
    public void proxyStarted(Cube<?> cube) {
        Proxy proxy = proxyInst.get();

        if(!cube.hasMetadata(HasPortBindings.class)) {
            throw new IllegalStateException("Proxy Cube " + proxy.getName() + " has no PortBinding data.");
        }
        HasPortBindings bindings = cube.getMetadata(HasPortBindings.class);
        
        PortAddress communicationPort = bindings.getMappedAddress(proxy.getCommunicationPort().getExposed());
        String proxyURL = "http://" + communicationPort.getIP() + ":" + communicationPort.getPort();
        
        scenarioInst.set(new ToxiProxyScenario(ToxiProxyClient.Builder.create(proxyURL)));
    }


    @Override
    public void cubeStarted(Cube<?> cube) {
        Proxy proxy = proxyInst.get();
        ToxiProxyScenario scenario = scenarioInst.get();
        if(scenario == null) {
            throw new IllegalStateException("Scenario is not Initiated. Proxy not started before others.");
        }

        HasPortBindings bindings = cube.getMetadata(HasPortBindings.class);

        for(Relation rel : proxy.getRelations(cube.getId())) {
            String relName = rel.getTo() + ":" + rel.getPort().getExposed();
            String localExp = "0.0.0.0:" + rel.getPort().getExposed();
            String remoteExp = bindings.getInternalIP() + ":" + rel.getPort().getExposed(); 
            
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
