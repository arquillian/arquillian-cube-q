package org.arquillian.cube.q.spi;

import org.arquillian.cube.docker.impl.client.config.Await;
import org.arquillian.cube.docker.impl.client.config.CubeContainer;
import org.arquillian.cube.docker.impl.client.config.Image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StandaloneContainer {

    private String name;
    private CubeContainer cube;

    public StandaloneContainer(String name, CubeContainer cubeContainer) {
        this.name = name;
        this.cube = cubeContainer;
    }

    public String getName() {
        return name;
    }

    public CubeContainer getCube() {
        return cube;
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {

        public static final String DEFAULT_NAME = "pumba";
        private String image = "gaiaadm/pumba";

        private List<String> volumes = new ArrayList<>();

        public Builder() {
        }

        public Builder volumes(List<String> volumes) {
            this.volumes.addAll(volumes);
            return this;
        }

        public StandaloneContainer build() {
            CubeContainer cube = new CubeContainer();
            cube.setImage(Image.valueOf(image));

            Await await = new Await();
            await.setStrategy("sleeping");
            await.setSleepTime("1 s");
            cube.setAwait(await);

            cube.setManual(true);
            cube.setRemoveVolumes(false);

            cube.setBinds(volumes);

            return new StandaloneContainer(DEFAULT_NAME, cube);

        }

    }

}
