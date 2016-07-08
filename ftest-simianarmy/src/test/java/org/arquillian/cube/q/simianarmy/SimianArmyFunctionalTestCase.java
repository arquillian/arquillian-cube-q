package org.arquillian.cube.q.simianarmy;

import com.github.dockerjava.api.DockerClient;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.HostPort;
import org.arquillian.cube.q.api.OperativeSystemChaos;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.arquillian.cube.q.api.OperativeSystemChaos.NumberCpuType.singleCpu;

@RunWith(Arquillian.class)
public class SimianArmyFunctionalTestCase {

    @ArquillianResource
    OperativeSystemChaos operativeSystemChaos;

    @ArquillianResource
    DockerClient dockerClient;

    @HostIp
    String dockerHost;

    @HostPort(containerName = "pingpong ", value = 8080)
    int port;

    @Test(expected = Exception.class) @Ignore //Running this test in same machine makes everything screwed
    public void shouldExecuteBurnCpuChaos() throws Exception {
        operativeSystemChaos.on("pingpong").burnCpu(singleCpu()).exec();

        URL obj = new URL("http://"+ dockerHost +":" + port);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Http URL");

        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

    }

}
