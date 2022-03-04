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

  protected RestHelper restHelper = new RestHelper(8080);
  protected static final String DEPENDENCY_VERSION = "5.0.1-SNAPSHOT";

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
        new HttpEntity<>(restHelper.generateHeadersForUser("teamlead-1")),
        ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
  }

  protected ResponseEntity<TaskRepresentationModel> performCreateTaskRestCall() {
    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    return RestHelper.TEMPLATE.exchange(
        restHelper.toUrl("/taskana" + RestEndpoints.URL_TASKS),
        HttpMethod.POST,
        new HttpEntity<>(taskRepresentationModel, restHelper.generateHeadersForUser("teamlead-1")),
        ParameterizedTypeReference.forType(TaskRepresentationModel.class));
  }

  protected String parseServerLog() throws Exception {

    // TO-DO: make log4j log into rollingFile from log4j.xml
    File file = new File("target/wildfly-13.0.0.Final/standalone/log/server.log");

    BufferedReader br = new BufferedReader(new FileReader(file));

    String str;
    StringBuilder stringBuilder = new StringBuilder();
    while ((str = br.readLine()) != null) {
      stringBuilder.append(str);
    }
    return stringBuilder.toString();
  }
}
