package org.arquillian.cube.q.toxic;

import org.arquillian.cube.HostIp;
import org.arquillian.cube.impl.util.IOUtil;
import org.arquillian.cube.q.api.NetworkChaos;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URL;

import static org.arquillian.cube.q.api.NetworkChaos.LatencyType.latencyInMillis;

@RunWith(Arquillian.class)
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
        networkChaos.on("hw", 8080).latency(latencyInMillis(4000)).exec(() -> {

            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));

        });
    }
}
