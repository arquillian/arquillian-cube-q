package org.arquillian.cube.q.toxic.reporter;

import org.arquillian.cube.HostIp;
import org.arquillian.cube.impl.util.IOUtil;
import org.arquillian.cube.q.api.NetworkChaos;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.io.IOException;
import java.net.SocketException;
import java.net.URL;

import static org.arquillian.cube.q.api.NetworkChaos.DistributedLatencyType.logNormalLatencyInMillis;
import static org.arquillian.cube.q.api.NetworkChaos.RateType.rate;
import static org.arquillian.cube.q.api.NetworkChaos.TimeoutType.timeoutInMillis;
import static org.arquillian.cube.q.api.Q.IterationRunCondition.times;

@RunWith(Arquillian.class)
public class ToxicFunctionalTest {

    @ArquillianResource
    private NetworkChaos networkChaos;

    @HostIp
    private String ip;

    @Test(expected = SocketException.class)
    public void should_add_timeout() throws Exception {

        networkChaos.on("hw", 8080).timeout(timeoutInMillis(1000)).exec(() -> {

            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));

        });

    }

    @Test
    public void should_add_bandwidth_with_iterations() throws Exception {
        networkChaos.on("hw", 8080).bandwidth(rate(1000)).exec(times(3), () -> {

            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));

        });
    }

    @Test
    public void should_add_log_normal_latency_and_bandwidth_with_iterations() throws Exception {
        NetworkChaos.Action chaosAction = networkChaos.on("hw", 8080);

        chaosAction.bandwidth(NetworkChaos.DistributedRateType.logNormalLatencyInMillis(3000, 0.2));
        chaosAction.latency(logNormalLatencyInMillis(2000, 0.3));

        chaosAction.exec(times(3), () -> {

            URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
            final long l = System.currentTimeMillis();
            String response = IOUtil.asString(url.openStream());
            System.out.println(response);
            System.out.println("Time:" + (System.currentTimeMillis() - l));

        });
    }

}
