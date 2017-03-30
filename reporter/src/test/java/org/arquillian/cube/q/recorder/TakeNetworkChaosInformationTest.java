package org.arquillian.cube.q.recorder;

import org.arquillian.cube.q.toxic.QNetworkChaosToxic;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient;
import org.arquillian.cube.q.toxic.event.ToxicCreated;
import org.arquillian.cube.q.toxic.event.ToxicUpdated;
import org.arquillian.reporter.api.builder.BuilderLoader;
import org.arquillian.reporter.api.event.SectionEvent;
import org.arquillian.reporter.api.model.entry.FileEntry;
import org.arquillian.reporter.api.model.entry.KeyValueEntry;
import org.arquillian.reporter.api.model.report.Report;
import org.arquillian.reporter.api.model.report.TestMethodReport;
import org.arquillian.reporter.config.ReporterConfiguration;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.jayway.jsonassert.impl.matcher.IsCollectionWithSize.hasSize;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.isJson;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.withJsonPath;
import static org.arquillian.cube.q.api.NetworkChaos.RateType.rate;
import static org.arquillian.cube.q.api.NetworkChaos.TimeoutType.timeoutInMillis;
import static org.arquillian.reporter.impl.asserts.ReportAssert.assertThatReport;
import static org.arquillian.reporter.impl.asserts.SectionAssert.assertThatSection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TakeNetworkChaosInformationTest {

    @Mock
    private QNetworkChaosToxic.ToxicAction toxicAction;

    @Mock
    private Event<SectionEvent> sectionEvent;

    @Captor
    private ArgumentCaptor<SectionEvent> sectionEventArgumentCaptor;

    @Before
    public void configureToxicAction() {
        when(toxicAction.getName()).thenReturn("helloworld");
        BuilderLoader.load();
    }

    @Test
    public void should_capture_toxic_details_after_create() throws NoSuchFieldException, IllegalAccessException {

        //given
        ToxiProxyClient.Timeout timeout =
            new ToxiProxyClient.Timeout("timeout_downstream", "DOWNSTREAM", 1.0f, timeoutInMillis(1000));
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
        ToxiProxyClient.Bandwidth bandwidth =
            new ToxiProxyClient.Bandwidth("bandwidth_downstream", "DOWNSTREAM", 1.0f, rate(1000));
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
    public void should_create_json_for_toxic_creation_and_write_it_to_file()
        throws IOException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        //given
        ToxiProxyClient.Timeout timeout =
            new ToxiProxyClient.Timeout("timeout_downstream", "DOWNSTREAM", 1.0f, timeoutInMillis(1000));
        ToxicCreated toxicCreated = new ToxicCreated(timeout);
        ReporterConfiguration reporterConfiguration = ReporterConfiguration.fromMap(new LinkedHashMap<>());
        Method method = getMethod("should_create_json_for_toxic_updation_and_write_to_file");
        String path = getFilePath(reporterConfiguration, method.getName());

        TakeNetworkChaosInformation takeNetworkChaosInformation = new TakeNetworkChaosInformation();
        takeNetworkChaosInformation.sectionEvent = sectionEvent;

        //when
        takeNetworkChaosInformation.captureToxicDetailsAfterCreate(toxicCreated, toxicAction);
        takeNetworkChaosInformation.reportToxicConfiguration(new After(TakeNetworkChaosInformationTest.class, method),
            reporterConfiguration);

        //then
        File json = new File(path);
        Assert.assertThat(json, isJson());
        Assert.assertThat(json, isJson(CoreMatchers.allOf(
            withJsonPath("$.services", hasSize(1)),
            withJsonPath("$.services[0].actionon", equalTo("helloworld")),
            withJsonPath("$.services[0].type", equalTo("Timeout")),
            withJsonPath("$.services[0].phase", equalTo("create")),
            withJsonPath("$.services[0].toxic.name", equalTo("timeout_downstream")),
            withJsonPath("$.services[0].toxic.stream", equalTo("DOWNSTREAM")),
            withJsonPath("$.services[0].toxic.toxcicity", equalTo(1.0)),
            withJsonPath("$.services[0].toxic.timeout", equalTo(1000)))));
    }

    @Test
    public void should_create_json_for_toxic_updation_and_write_to_file()
        throws IOException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        //given
        ToxiProxyClient.Timeout timeout =
            new ToxiProxyClient.Timeout("timeout_downstream", "DOWNSTREAM", 1.0f, timeoutInMillis(1000));
        ToxicUpdated toxicUpdated = new ToxicUpdated(timeout);
        ReporterConfiguration reporterConfiguration = ReporterConfiguration.fromMap(new LinkedHashMap<>());

        Method method = getMethod("should_create_json_for_toxic_updation_and_write_to_file");
        String path = getFilePath(reporterConfiguration, method.getName());

        TakeNetworkChaosInformation takeNetworkChaosInformation = new TakeNetworkChaosInformation();
        takeNetworkChaosInformation.sectionEvent = sectionEvent;

        //when
        takeNetworkChaosInformation.captureToxicDetailsAfterUpdate(toxicUpdated, toxicAction);
        takeNetworkChaosInformation.reportToxicConfiguration(new After(TakeNetworkChaosInformationTest.class,
                method),
            reporterConfiguration);

        //then
        File json = new File(path);
        Assert.assertThat(json, isJson());
        Assert.assertThat(json, isJson(CoreMatchers.allOf(
            withJsonPath("$.services", hasSize(1)),
            withJsonPath("$.services[0].actionon", equalTo("helloworld")),
            withJsonPath("$.services[0].type", equalTo("Timeout")),
            withJsonPath("$.services[0].phase", equalTo("update")),
            withJsonPath("$.services[0].toxic.name", equalTo("timeout_downstream")),
            withJsonPath("$.services[0].toxic.stream", equalTo("DOWNSTREAM")),
            withJsonPath("$.services[0].toxic.toxcicity", equalTo(1.0)),
            withJsonPath("$.services[0].toxic.timeout", equalTo(1000)))));
    }

    @Test
    public void should_report_json_file_with_toxicity_params_after_test() throws NoSuchMethodException, IOException {

        TakeNetworkChaosInformation takeNetworkChaosInformation = new TakeNetworkChaosInformation();
        takeNetworkChaosInformation.sectionEvent = sectionEvent;

        ReporterConfiguration reporterConfiguration = ReporterConfiguration.fromMap(new LinkedHashMap<>());
        final Method method = getMethod("should_report_json_file_with_toxicity_params_after_test");
        takeNetworkChaosInformation.reportToxicConfiguration(new After(TakeNetworkChaosInformationTest.class,
            method), reporterConfiguration);

        verify(sectionEvent).fire(sectionEventArgumentCaptor.capture());

        final SectionEvent sectionEvent = sectionEventArgumentCaptor.getValue();
        final String methodName = method.getName();

        assertThatSection(sectionEvent)
            .hasSectionId(methodName)
            .hasReportOfTypeThatIsAssignableFrom(TestMethodReport.class);

        final Report report = sectionEvent.getReport();

        assertThatReport(report)
            .hasName(methodName)
            .hasNumberOfEntries(1)
            .hasEntriesContaining(new KeyValueEntry(NetworkChaosInformationReportKey.TOXICITY_DETAILS_PATH,
                new FileEntry("reports/chaos/should_report_json_file_with_toxicity_params_after_test.json")));
    }

    private Method getMethod(String name) throws NoSuchMethodException {
        return TakeNetworkChaosInformationTest.class.getMethod(name);
    }

    private ArrayList<Map<String, Object>> getToxicList(TakeNetworkChaosInformation takeNetworkChaosInformation)
        throws NoSuchFieldException, IllegalAccessException {

        Field field = takeNetworkChaosInformation.getClass().getDeclaredField("toxics");
        field.setAccessible(true);
        return (ArrayList<Map<String, Object>>) field.get(takeNetworkChaosInformation);
    }

    private String getFilePath(ReporterConfiguration reporterConfiguration, String methodName) {
        String path = reporterConfiguration.getRootDirectory() + "/reports/chaos/" + methodName + ".json";

        return path.replace("/", File.separator);
    }
}
