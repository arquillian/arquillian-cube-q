package org.arquillian.cube.q.toxic;

import eu.rekawek.toxiproxy.ToxiproxyClient;
import eu.rekawek.toxiproxy.model.Proxy;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.impl.util.IOUtil;
import org.arquillian.cube.q.api.Q;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.arquillian.cube.q.api.Q.IterationRunCondition.times;
import static org.arquillian.cube.q.api.Q.LatencyType.latency;

@RunWith(Arquillian.class) //@Ignore
public class ToxicFuntionalTestCase {

    @ArquillianResource
    private Q Q;

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
        Q.on("pingpong", 8080).latency(latency(4000)).exec(() -> {

            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));

        });
    }

    @Test
    public void shouldAddLatencyWithExec() throws Exception {
        Q.on("pingpong", 8080).latency(latency(4000)).exec();

        URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
        final long l = System.currentTimeMillis();
        String response = IOUtil.asString(url.openStream());
        System.out.println(response);
        System.out.println("Time:" + (System.currentTimeMillis() - l));

        ToxiproxyClient client = new ToxiproxyClient(ip, 8474);
        final Proxy proxy = client.getProxy("pingpong:8080");
        Assert.assertThat(proxy.toxics().getAll().size(), CoreMatchers.is(1));
    }

    @Test
    public void shouldAddLatencyWithIterations() throws Exception {
        Q.on("pingpong", 8080).latency(latency(4000)).exec(times(2), () -> {

            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));

        });
    }
}
