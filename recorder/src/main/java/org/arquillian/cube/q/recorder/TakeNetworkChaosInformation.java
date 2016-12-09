package org.arquillian.cube.q.recorder;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.arquillian.cube.q.api.Q;
import org.arquillian.cube.q.toxic.QNetworkChaosToxic;
import org.arquillian.cube.q.toxic.client.ToxiProxyClient;
import org.arquillian.cube.q.toxic.event.ToxicCreated;

import org.arquillian.cube.q.toxic.event.ToxicUpdated;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.event.PropertyReportEvent;
import org.arquillian.recorder.reporter.model.entry.FileEntry;
import org.arquillian.recorder.reporter.model.entry.GroupEntry;

import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.After;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that reports general information about network toxics.
 */
public class TakeNetworkChaosInformation {

    private static final String CREATE = "create";
    private static final String UPDATE = "update";

    @Inject
    Event<PropertyReportEvent> propertyReportEvent;

    private List<Map<String, Object>> toxics = new ArrayList<>();

    public void captureToxicDetailsAfterCreate(@Observes ToxicCreated toxicCreatedEvent, QNetworkChaosToxic.ToxicAction toxicAction) {
        final ToxiProxyClient.BaseToxic toxic = toxicCreatedEvent.getToxic();
        final Q.RunCondition runCondition = toxicCreatedEvent.getRunCondition();
        addToxicInfotoToxics(toxicAction, toxic, runCondition, CREATE);
    }

    public void captureToxicDetailsAfterUpdate(@Observes ToxicUpdated toxicUpdatedEvent, QNetworkChaosToxic.ToxicAction toxicAction) {
        final ToxiProxyClient.BaseToxic toxic = toxicUpdatedEvent.getToxic();
        addToxicInfotoToxics(toxicAction, toxic, null, UPDATE);
    }

    public void addToxicInfotoToxics(QNetworkChaosToxic.ToxicAction toxicAction, ToxiProxyClient.BaseToxic toxic, Q.RunCondition runCondition, String phase) {
        final String actionOn = toxicAction.getName();
        final String toxicType = toxic.getClass().getSimpleName();

        Map<String, Object> toxicInfo = new LinkedHashMap<>();
        toxicInfo.put("actionon", actionOn);
        toxicInfo.put("type", toxicType);
        toxicInfo.put("phase", phase);
        toxicInfo.put("toxic", toxic);
        if (runCondition != null) {
            toxicInfo.put("runcondition", runCondition);
        }

        toxics.add(toxicInfo);
    }


    public void reportToxicConfiguration(@Observes After event, ReporterConfiguration reporterConfiguration) throws IOException {

        final String fileName = event.getTestMethod().getName() + ".json";

        final FileEntry fileEntry = createFileEntrywithJsonSchema(reporterConfiguration, fileName);

        GroupEntry groupEntry = new GroupEntry();
        groupEntry.getPropertyEntries().add(fileEntry);

        propertyReportEvent.fire(new PropertyReportEvent(groupEntry));
        toxics.clear();
    }

    private FileEntry createFileEntrywithJsonSchema(ReporterConfiguration reporterConfiguration, String fileName) throws IOException {
        File jsonFile = new File(createDirectoryByNameChaos(reporterConfiguration.getRootDir()), fileName);

        if (!toxics.isEmpty()) {
            createJsonSchemaAndWriteToFile(jsonFile);
        }

        final Path rootDir = Paths.get(reporterConfiguration.getRootDir().getName());
        final Path relativize = rootDir.relativize(jsonFile.toPath());

        FileEntry fileEntry = new FileEntry();
        fileEntry.setPath(relativize.toString());
        fileEntry.setType("json");

        return fileEntry;
    }

    private void createJsonSchemaAndWriteToFile(File file) throws IOException {
        JsonNodeFactory jsonNodeFactory = new JsonNodeFactory(false);
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator generator = jsonFactory.createGenerator(file, JsonEncoding.UTF8);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(toxics);
        ArrayNode toxic = (ArrayNode) mapper.readTree(jsonString);

        ObjectNode root = jsonNodeFactory.objectNode();
        root.set("services", toxic);

        mapper.writeTree(generator, root);
    }

    private File createDirectoryByNameChaos(File rootDirectory) {
        final Path reportChaos = Paths.get("reports", "chaos");
        final Path chaosDir = rootDirectory.toPath().resolve(reportChaos);

        try {
            Files.createDirectories(chaosDir);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Could not created chaos directory at %s", chaosDir));
        }

        return chaosDir.toFile();
    }

}
