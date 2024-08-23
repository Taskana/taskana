package io.kadai.example.wildfly;

import io.kadai.classification.rest.models.ClassificationSummaryRepresentationModel;
import io.kadai.common.rest.RestEndpoints;
import io.kadai.common.test.rest.KadaiSpringBootTest;
import io.kadai.common.test.rest.RestHelper;
import io.kadai.simplehistory.rest.HistoryRestEndpoints;
import io.kadai.simplehistory.rest.models.TaskHistoryEventPagedRepresentationModel;
import io.kadai.task.rest.models.ObjectReferenceRepresentationModel;
import io.kadai.task.rest.models.TaskRepresentationModel;
import io.kadai.workbasket.rest.models.WorkbasketSummaryRepresentationModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@KadaiSpringBootTest
public class AbstractAccTest {

  protected static final String DEPENDENCY_VERSION = "8.2.1-SNAPSHOT";

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAccTest.class);

  static {
    Runtime.getRuntime().addShutdownHook(new Thread(AbstractAccTest::stopPostgresDb));

    startPostgresDb();
  }

  protected RestHelper restHelper = new RestHelper(8080);

  private static void stopPostgresDb() {
    try {
      boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
      ProcessBuilder builder = new ProcessBuilder();
      if (isWindows) {
        builder.command(
            "cmd.exe", "/c", "docker compose -f ../../docker-databases/docker-compose.yml down -v");
      } else {
        builder.command(
            "sh", "-c", "docker compose -f ../../docker-databases/docker-compose.yml down -v");
      }
      Process process = builder.start();
      LOGGER.info("Stopping POSTGRES...");
      assertSuccessExitCode(process);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void startPostgresDb() {
    try {
      boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
      ProcessBuilder builder = new ProcessBuilder();
      if (isWindows) {
        builder.command(
            "cmd.exe",
            "/c",
            "docker compose -f ../../docker-databases/docker-compose.yml up -d "
                + "kadai-postgres_14");
      } else {
        builder.command(
            "sh",
            "-c",
            "docker compose -f ../../docker-databases/docker-compose.yml up -d "
                + "kadai-postgres_14");
      }
      Process process = builder.start();
      LOGGER.info("Starting POSTGRES...");
      assertSuccessExitCode(process);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void assertSuccessExitCode(Process process) throws InterruptedException {
    int exitCode = process.waitFor();
    if (exitCode != 0) {
      String standardOutput = process.inputReader().lines().collect(Collectors.joining("\n"));
      String standardError = process.errorReader().lines().collect(Collectors.joining("\n"));
      throw new RuntimeException(
          "Could not start postgres db! exit code: "
              + exitCode
              + ", standardOutput: "
              + standardOutput
              + ", standardError: "
              + standardError);
    }
  }

  protected TaskRepresentationModel getTaskResourceSample() {
    ClassificationSummaryRepresentationModel classificationResource =
        new ClassificationSummaryRepresentationModel();
    classificationResource.setKey("L11010");
    WorkbasketSummaryRepresentationModel workbasketSummary =
        new WorkbasketSummaryRepresentationModel();
    workbasketSummary.setWorkbasketId("WBI:100000000000000000000000000000000004");

    ObjectReferenceRepresentationModel objectReference = new ObjectReferenceRepresentationModel();
    objectReference.setCompany("MyCompany1");
    objectReference.setSystem("MySystem1");
    objectReference.setSystemInstance("MyInstance1");
    objectReference.setType("MyType1");
    objectReference.setValue("00000001");

    TaskRepresentationModel taskRepresentationModel = new TaskRepresentationModel();
    taskRepresentationModel.setClassificationSummary(classificationResource);
    taskRepresentationModel.setWorkbasketSummary(workbasketSummary);
    taskRepresentationModel.setPrimaryObjRef(objectReference);
    return taskRepresentationModel;
  }

  protected ResponseEntity<TaskHistoryEventPagedRepresentationModel>
      performGetHistoryEventsRestCall() {
    return RestHelper.TEMPLATE.exchange(
        restHelper.toUrl("/kadai" + HistoryRestEndpoints.URL_HISTORY_EVENTS),
        HttpMethod.GET,
        new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
        ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
  }

  protected ResponseEntity<TaskRepresentationModel> performCreateTaskRestCall() {
    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    return RestHelper.TEMPLATE.exchange(
        restHelper.toUrl("/kadai" + RestEndpoints.URL_TASKS),
        HttpMethod.POST,
        new HttpEntity<>(taskRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1")),
        ParameterizedTypeReference.forType(TaskRepresentationModel.class));
  }

  protected String parseServerLog() throws Exception {

    // TO-DO: make log4j log into rollingFile from log4j.xml
    File file = new File("target/wildfly-31.0.1.Final/standalone/log/server.log");

    BufferedReader br = new BufferedReader(new FileReader(file));

    String str;
    StringBuilder stringBuilder = new StringBuilder();
    while ((str = br.readLine()) != null) {
      stringBuilder.append(str);
    }
    return stringBuilder.toString();
  }
}
