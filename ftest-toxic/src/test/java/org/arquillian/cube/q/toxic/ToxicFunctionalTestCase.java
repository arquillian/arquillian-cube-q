package org.arquillian.cube.q.toxic;

import eu.rekawek.toxiproxy.Proxy;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.docker.impl.requirement.RequiresDockerMachine;
import org.arquillian.cube.impl.util.IOUtil;
import org.arquillian.cube.q.api.NetworkChaos;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.URL;

import static org.arquillian.cube.q.api.NetworkChaos.DistributedLatencyType.logNormalLatencyInMillis;
import static org.arquillian.cube.q.api.NetworkChaos.LatencyType.latencyInMillis;
import static org.arquillian.cube.q.api.Q.IterationRunCondition.times;
import static org.hamcrest.CoreMatchers.is;

@RequiresDockerMachine(name = "dev")
@RunWith(ArquillianConditionalRunner.class)
public class ToxicFunctionalTestCase {

    @ArquillianResource
    private NetworkChaos networkChaos;

    @HostIp
    private String ip;

    @Test
    public void should() throws Exception {

        URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
        final long l = System.currentTimeMillis();
        String response = IOUtil.asString(url.openStream());
        System.out.println("Time:" + (System.currentTimeMillis() - l));
        Assert.assertNotNull(response);
    }

    @Test
    public void shouldAddLatency() throws Exception {
        networkChaos.on("pingpong", 8080).latency(latencyInMillis(4000)).exec(() -> {

            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));

        });
    }

    @Test
    public void shouldAddLatencyWithExec() throws Exception {
        networkChaos.on("pingpong", 8080).latency(latencyInMillis(4000)).exec();

        URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
        final long l = System.currentTimeMillis();
        String response = IOUtil.asString(url.openStream());
        System.out.println(response);
        System.out.println("Time:" + (System.currentTimeMillis() - l));

        ToxiproxyClient client = new ToxiproxyClient(ip, 8474);
        final Proxy proxy = client.getProxy("pingpong:8080");
        Assert.assertThat(proxy.toxics().getAll().size(), is(1));
    }

    @Test
    public void shouldAddLogNormalLatencyWithIterations() throws Exception {
        networkChaos.on("pingpong", 8080).latency(logNormalLatencyInMillis(2000, 0.3)).exec(times(2), () -> {

            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));

        });
    }

    @Test
    public void shouldAddLatencyWithIterations() throws Exception {
        networkChaos.on("pingpong", 8080).latency(latencyInMillis(4000)).exec(times(2), () -> {

            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));

        });
    }

    @Test(expected = IOException.class)
    public void shouldAddDownToxic() throws Exception {
        networkChaos.on("pingpong", 8080).down().exec(() -> {
            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));
        });
    }
}
