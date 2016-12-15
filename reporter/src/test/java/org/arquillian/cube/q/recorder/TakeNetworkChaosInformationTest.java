package org.arquillian.cube.q.recorder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.arquillian.cube.q.toxic.QNetworkChaosToxic;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient;
import org.arquillian.cube.q.toxic.event.ToxicCreated;
import org.arquillian.cube.q.toxic.event.ToxicUpdated;
import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.event.PropertyReportEvent;
import org.arquillian.recorder.reporter.model.entry.FileEntry;
import org.arquillian.recorder.reporter.model.entry.GroupEntry;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.arquillian.cube.q.api.NetworkChaos.RateType.rate;
import static org.arquillian.cube.q.api.NetworkChaos.TimeoutType.timeoutInMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TakeNetworkChaosInformationTest {

    @Mock
    private QNetworkChaosToxic.ToxicAction toxicAction;

    @Mock
    private Event<PropertyReportEvent> propertyReportEvent;

    @Captor
    ArgumentCaptor<PropertyReportEvent> propertyReportEventArgumentCaptor;

    @Before
    public void configureToxicAction() {
        when(toxicAction.getName()).thenReturn("helloworld");
    }

    @Test
    public void should_capture_toxic_details_after_create() throws NoSuchFieldException, IllegalAccessException {

        //given
        ToxiProxyClient.Timeout timeout = new ToxiProxyClient.Timeout("timeout_downstream", "DOWNSTREAM", 1.0f, timeoutInMillis(1000));
        ToxicCreated toxicCreated = new ToxicCreated(timeout);

        TakeNetworkChaosInformation takeNetworkChaosInformation = new TakeNetworkChaosInformation();
        ArrayList<Map<String, Object>> toxics = getToxicList(takeNetworkChaosInformation);

        //when
        takeNetworkChaosInformation.captureToxicDetailsAfterCreate(toxicCreated, toxicAction);

        //then
        assertThat(toxics).hasSize(1);
        assertThat(toxics.get(0)).containsExactly(entry("actionon", "helloworld"),
                entry("type", "Timeout"),
                entry("phase", "create"),
                entry("toxic", timeout)
        );
    }

    @Test
    public void should_capture_toxic_details_after_update() throws NoSuchFieldException, IllegalAccessException {
        //given
        ToxiProxyClient.Bandwidth bandwidth = new ToxiProxyClient.Bandwidth("bandwidth_downstream", "DOWNSTREAM", 1.0f, rate(1000));
        ToxicUpdated toxicUpdated = new ToxicUpdated(bandwidth);

        TakeNetworkChaosInformation takeNetworkChaosInformation = new TakeNetworkChaosInformation();
        ArrayList<Map<String, Object>> toxics = getToxicList(takeNetworkChaosInformation);

        //when
        takeNetworkChaosInformation.captureToxicDetailsAfterUpdate(toxicUpdated, toxicAction);

        //then
        assertThat(toxics).hasSize(1);
        assertThat(toxics.get(0)).containsExactly(
                entry("actionon", "helloworld"),
                entry("type", "Bandwidth"),
                entry("phase", "update"),
                entry("toxic", bandwidth)
        );
    }

    @Test
    public void should_create_json_for_toxic_creation_and_write_it_to_file() throws IOException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, JSONException {
        //given
        ToxiProxyClient.Timeout timeout = new ToxiProxyClient.Timeout("timeout_downstream", "DOWNSTREAM", 1.0f, timeoutInMillis(1000));
        ToxicCreated toxicCreated = new ToxicCreated(timeout);
        ReporterConfiguration reporterConfiguration = new ReporterConfiguration();
        Method method = getMethod("should_create_json_for_toxic_updation_and_write_to_file");
        String path = getFilePath(reporterConfiguration, method.getName());

        TakeNetworkChaosInformation takeNetworkChaosInformation = new TakeNetworkChaosInformation();
        takeNetworkChaosInformation.propertyReportEvent = propertyReportEvent;
        ArrayList<Map<String, Object>> toxics = getToxicList(takeNetworkChaosInformation);

        //when
        takeNetworkChaosInformation.captureToxicDetailsAfterCreate(toxicCreated, toxicAction);
        takeNetworkChaosInformation.reportToxicConfiguration(new After(TakeNetworkChaosInformationTest.class, method),
                reporterConfiguration);

        //then
        String actual = readJSONFileAsString(new File(path));
        String expected = "{\"services\":[{\"actionon\":\"helloworld\",\"type\":\"Timeout\",\"phase\":\"create\",\"toxic\":{\"name\":\"timeout_downstream\",\"stream\":\"DOWNSTREAM\",\"toxcicity\":1.0,\"timeout\":1000}}]}";
        JSONAssert.assertEquals(expected, actual, false);
    }

    @Test
    public void should_create_json_for_toxic_updation_and_write_to_file() throws IOException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, JSONException {
        //given
        ToxiProxyClient.Timeout timeout = new ToxiProxyClient.Timeout("timeout_downstream", "DOWNSTREAM", 1.0f, timeoutInMillis(1000));
        ToxicUpdated toxicUpdated = new ToxicUpdated(timeout);
        ReporterConfiguration reporterConfiguration = new ReporterConfiguration();
        Method method = getMethod("should_create_json_for_toxic_updation_and_write_to_file");
        String path = getFilePath(reporterConfiguration, method.getName());

        TakeNetworkChaosInformation takeNetworkChaosInformation = new TakeNetworkChaosInformation();
        takeNetworkChaosInformation.propertyReportEvent = propertyReportEvent;
        ArrayList<Map<String, Object>> toxics = getToxicList(takeNetworkChaosInformation);

        //when
        takeNetworkChaosInformation.captureToxicDetailsAfterUpdate(toxicUpdated, toxicAction);
        takeNetworkChaosInformation.reportToxicConfiguration(new After(TakeNetworkChaosInformationTest.class,
                        method),
                reporterConfiguration);

        //then
        String actual = readJSONFileAsString(new File(path));
        String expected = "{\"services\":[{\"actionon\":\"helloworld\",\"type\":\"Timeout\",\"phase\":\"update\",\"toxic\":{\"name\":\"timeout_downstream\",\"stream\":\"DOWNSTREAM\",\"toxcicity\":1.0,\"timeout\":1000}}]}";
        JSONAssert.assertEquals(expected, actual, false);
    }


    @Test
    public void should_report_json_file_with_toxicity_params_after_test() throws NoSuchMethodException, IOException {

        TakeNetworkChaosInformation takeNetworkChaosInformation = new TakeNetworkChaosInformation();
        takeNetworkChaosInformation.propertyReportEvent = propertyReportEvent;

        takeNetworkChaosInformation.reportToxicConfiguration(new After(TakeNetworkChaosInformationTest.class,
                getMethod("should_report_json_file_with_toxicity_params_after_test")), new ReporterConfiguration());

        verify(propertyReportEvent).fire(propertyReportEventArgumentCaptor.capture());

        final PropertyReportEvent propertyReportEvent = propertyReportEventArgumentCaptor.getValue();
        final PropertyEntry propertyEntry = propertyReportEvent.getPropertyEntry();
        assertThat(propertyEntry).isInstanceOf(GroupEntry.class);

        List<PropertyEntry> childEntry = propertyEntry.getPropertyEntries();
        assertThat(childEntry).hasSize(1).extracting("class.simpleName").containsExactly("FileEntry");

        FileEntry fileEntry = (FileEntry) childEntry.get(0);
        assertThat(fileEntry.getPath()).isEqualTo("reports/chaos/should_report_json_file_with_toxicity_params_after_test.json");
    }


    private Method getMethod(String name) throws NoSuchMethodException {
        return TakeNetworkChaosInformationTest.class.getMethod(name);
    }

    private ArrayList<Map<String, Object>> getToxicList(TakeNetworkChaosInformation takeNetworkChaosInformation) throws NoSuchFieldException, IllegalAccessException {

        Field field = takeNetworkChaosInformation.getClass().getDeclaredField("toxics");
        field.setAccessible(true);
        return (ArrayList<Map<String, Object>>) field.get(takeNetworkChaosInformation);
    }

    private String readJSONFileAsString(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(file);

        return mapper.writeValueAsString(jsonNode);
    }

    private String getFilePath(ReporterConfiguration reporterConfiguration, String methodName) {
        String path = reporterConfiguration.getRootDir() + "/reports/chaos/" + methodName +".json";

        return path.replace("/", File.separator);
    }
}
