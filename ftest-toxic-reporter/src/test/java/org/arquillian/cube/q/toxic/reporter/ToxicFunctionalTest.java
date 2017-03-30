package org.arquillian.cube.q.toxic.reporter;

import org.apache.commons.io.FileUtils;
import org.arquillian.cube.HostIp;
import org.arquillian.cube.impl.util.IOUtil;
import org.arquillian.cube.q.api.NetworkChaos;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.arquillian.cube.q.api.NetworkChaos.DistributedLatencyType.logNormalLatencyInMillis;
import static org.arquillian.cube.q.api.NetworkChaos.RateType.rate;
import static org.arquillian.cube.q.api.NetworkChaos.TimeoutType.timeoutInMillis;
import static org.arquillian.cube.q.api.Q.IterationRunCondition.times;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class ToxicFunctionalTest {

    @ArquillianResource
    private NetworkChaos networkChaos;

    @HostIp
    private String ip;

    @Rule
    public TestName name = new TestName();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static List<String> executedMethods = new ArrayList<>();

    private static final String LOG_DIR = (System.getProperty("user.dir") + "/target/reports/chaos/");

    @BeforeClass
    public static void clean_json_files_dir_from_target_reports() throws IOException {
        File chaosDir = new File(LOG_DIR);
        if (chaosDir.exists()) {
            FileUtils.cleanDirectory(new File(LOG_DIR));
        }
    }

    @Test
    public void should_add_timeout() throws Exception {
        thrown.expect(SocketException.class);
        thrown.expectMessage("Unexpected end of file from server");
        networkChaos.on("hw", 8080).timeout(timeoutInMillis(1000)).exec(() -> {
            getResponse();
        });
    }

    @Test
    public void should_add_bandwidth_with_iterations() throws Exception {
        networkChaos.on("hw", 8080).bandwidth(rate(1000)).exec(times(3), () -> {
            getResponse();
        });
    }

    @Test
    public void should_add_log_normal_latency_and_bandwidth_with_iterations() throws Exception {
        NetworkChaos.Action chaosAction = networkChaos.on("hw", 8080);

        chaosAction.bandwidth(NetworkChaos.DistributedRateType.logNormalLatencyInMillis(3000, 0.2));
        chaosAction.latency(logNormalLatencyInMillis(2000, 0.3));

        chaosAction.exec(times(3), () -> {
            getResponse();
        });
    }

    @After
    public void add_method_name() {
        executedMethods.add(name.getMethodName() + ".json");
    }

    @AfterClass
    public static void verify_json_files_in_reports_dir() {
        File chaosDir = new File(LOG_DIR);
        final int size = executedMethods.size();
        final String[] fileNames = executedMethods.toArray(new String[executedMethods.size()]);

        assertThat(chaosDir.exists()).isTrue();
        assertThat(chaosDir.list()).hasSize(size).containsExactlyInAnyOrder(fileNames);
    }

    private void getResponse() throws IOException {
        URL url = new URL("http://" + ip + ":" + 8081 + "/hw/HelloWorld");
        final long l = System.currentTimeMillis();
        String response = IOUtil.asString(url.openStream());
        System.out.println(response);
        System.out.println("Time:" + (System.currentTimeMillis() - l));
    }
}
