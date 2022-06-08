package pro.taskana.example.wildfly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.simplehistory.rest.HistoryRestEndpoints;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventPagedRepresentationModel;
import pro.taskana.task.rest.models.ObjectReferenceRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

@TaskanaSpringBootTest
public class AbstractAccTest {

  protected static final String DEPENDENCY_VERSION = "5.2.1-SNAPSHOT";

  static {
    Runtime.getRuntime().addShutdownHook(new Thread(AbstractAccTest::stopPostgresDb));

    startPostgresDb();
  }

  protected RestHelper restHelper = new RestHelper(8080);

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
        restHelper.toUrl("/taskana" + HistoryRestEndpoints.URL_HISTORY_EVENTS),
        HttpMethod.GET,
        new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
        ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
  }

  protected ResponseEntity<TaskRepresentationModel> performCreateTaskRestCall() {
    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    return RestHelper.TEMPLATE.exchange(
        restHelper.toUrl("/taskana" + RestEndpoints.URL_TASKS),
        HttpMethod.POST,
        new HttpEntity<>(taskRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1")),
        ParameterizedTypeReference.forType(TaskRepresentationModel.class));
  }

  protected String parseServerLog() throws Exception {

    // TO-DO: make log4j log into rollingFile from log4j.xml
    File file = new File("target/wildfly-15.0.1.Final/standalone/log/server.log");

    BufferedReader br = new BufferedReader(new FileReader(file));

    String str;
    StringBuilder stringBuilder = new StringBuilder();
    while ((str = br.readLine()) != null) {
      stringBuilder.append(str);
    }
    return stringBuilder.toString();
  }

  private static void stopPostgresDb() {
    try {
      boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
      ProcessBuilder builder = new ProcessBuilder();
      if (isWindows) {
        builder.command(
            "cmd.exe", "/c", "docker-compose -f ../../docker-databases/docker-compose.yml down -v");
      } else {
        builder.command(
            "sh", "-c", "docker-compose -f ../../docker-databases/docker-compose.yml down -v");
      }
      Process process = builder.start();
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new RuntimeException("could not start postgres db!");
      }
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
            "docker-compose -f ../../docker-databases/docker-compose.yml up -d",
            "taskana-postgres_10");
      } else {
        builder.command(
            "sh",
            "-c",
            "docker-compose -f ../../docker-databases/docker-compose.yml up -d",
            "taskana-postgres_10");
      }
      Process process = builder.start();
      int exitCode = process.waitFor();
      if (exitCode != 0) {
        throw new RuntimeException("could not start postgres db!");
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
