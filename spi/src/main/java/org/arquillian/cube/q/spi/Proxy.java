package org.arquillian.cube.q.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.arquillian.cube.docker.impl.client.config.Await;
import org.arquillian.cube.docker.impl.client.config.CubeContainer;
import org.arquillian.cube.docker.impl.client.config.ExposedPort;
import org.arquillian.cube.docker.impl.client.config.Image;
import org.arquillian.cube.docker.impl.client.config.Link;
import org.arquillian.cube.docker.impl.client.config.PortBinding;

public class Proxy {

    private String name;
    private ExposedPort communicationPort;
    private CubeContainer cube;
    private Collection<Relation> relations;

    private Proxy(String name, ExposedPort communicationPort, CubeContainer cube, Collection<Relation> relations) {
        this.name = name;
        this.communicationPort = communicationPort;
        this.cube = cube;
        this.relations = relations;
    }

    public String getName() {
        return name;
    }
    
    public ExposedPort getCommunicationPort() {
        return communicationPort;
    }

    public CubeContainer getCube() {
        return cube;
    }

    public Collection<Relation> getRelations() {
        return relations;
    }
    
    public Collection<Relation> getRelations(String to) {
        List<Relation> relations = new ArrayList<Relation>();
        for(Relation rel : getRelations()) {
            if(rel.getTo().equals(to)) {
                relations.add(rel);
            }
        }
        return relations;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Relation {
        private String from;
        private String to;
        private ExposedPort port;

        public Relation(String from, String to, ExposedPort port) {
            this.from = from;
            this.to = to;
            this.port = port;
        }

        public String getFrom() {
            return from;
        }

        public String getTo() {
            return to;
        }

        public ExposedPort getPort() {
            return port;
        }

        @Override
        public String toString() {
            return "Relation [from=" + from + ", to=" + to + ", port=" + port + "]";
        }
    }

    public static class Builder {
        private final String DEFAULT_NAME = "toxiproxy";
        private final int DEFAULT_PORT = 8474;

        private String image = "shopify/toxiproxy";
        private Set<String> expose = new HashSet<>();
        private Set<String> bind = new HashSet<>();

        private Map<String, List<String>> containerExpose = new HashMap<String, List<String>>(); 
        private Map<String, List<String>> containerLinks = new HashMap<String, List<String>>();
        
        public Builder() {
        }
        
        public Builder containerExpose(String containerName, int exposed, String protocol) {
            exposePort(exposed, protocol);
            getValue(containerName, containerExpose).add(exposed + "/" + protocol);
            return this;
        }
        
        public Builder containerBinds(String containerName, int bound, int exposed, String protocol) {
            bindPort(bound, exposed, protocol);
            getValue(containerName, containerExpose).add(exposed + "/" + protocol);
            containerLinks(containerName, containerName + "_toxiproxy");
            return this;
        }
        
        public Builder containerLinks(String containerFrom, String containerTo) {
            getValue(containerFrom, containerLinks).add(containerTo);
            return this;
        }
        
        private Builder exposePort(int expose, String protocol) {
            this.expose.add(expose + "/" + protocol);
            return this;
        }
        
        private Builder bindPort(int bound, int exposed, String protocol) {
            bind.add(bound + "->" + exposed + "/" + protocol);
            return this;
        }
        
        public Proxy build() {
            bindPort(DEFAULT_PORT, DEFAULT_PORT, "tcp");

            CubeContainer cube = new CubeContainer();
            cube.setImage(Image.valueOf(image));
            cube.setExposedPorts(ExposedPort.valuesOf(expose));
            cube.setPortBindings(PortBinding.valuesOf(bind));
            Await await = new Await();
            await.setStrategy("polling");
            await.setPorts(Arrays.asList(DEFAULT_PORT));
            cube.setAwait(await);
            cube.setRemoveVolumes(true);
            Collection<Relation> relations = buildRelations(); 
            Collection<Link> uniqeLinks = buildUniqueLinks();
            cube.setLinks(uniqeLinks);

            return new Proxy(getName(), ExposedPort.valueOf(DEFAULT_PORT + "/tcp"), cube, relations);
        }
        
        private Collection<Link> buildUniqueLinks() {
            List<Link> unique = new ArrayList<Link>();
            for (Map.Entry<String, List<String>> links : containerLinks.entrySet()) {
                final List<String> linksValue = links.getValue();
                for (String linkValue : linksValue) {
                    unique.add(new Link(links.getKey(), linkValue));
                }
            }
            return unique;
        }

        private Collection<Relation> buildRelations() {
            List<Relation> relations = new ArrayList<Relation>();
            for(Map.Entry<String, List<String>> links : containerLinks.entrySet()) {
                for(String linkedTo : links.getValue()) {
                    List<String> exposed = containerExpose.get(links.getKey());
                    if(exposed == null) {
                        continue;
                    }
                    for(String port : exposed) {
                        relations.add(new Relation(links.getKey(), linkedTo, ExposedPort.valueOf(port)));
                    }
                }
            }
            return relations;
        }

        private List<String> getValue(String key, Map<String, List<String>> map) {
            if(!map.containsKey(key)) {
                map.put(key, new ArrayList<String>());
            }
            return map.get(key);
        }

        public String getName() {
            return DEFAULT_NAME;
        }
    }
}
