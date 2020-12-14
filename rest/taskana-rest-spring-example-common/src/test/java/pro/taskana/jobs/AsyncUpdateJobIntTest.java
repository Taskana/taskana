package pro.taskana.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.rest.assembler.ClassificationRepresentationModelAssembler;
import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.rest.assembler.TaskRepresentationModelAssembler;
import pro.taskana.task.rest.models.TaskRepresentationModel;

/** Test async updates. */
@TaskanaSpringBootTest
class AsyncUpdateJobIntTest {

  private static final String CLASSIFICATION_ID = "CLI:100000000000000000000000000000000003";

  private final ClassificationRepresentationModelAssembler
      classificationRepresentationModelAssembler;
  private final TaskRepresentationModelAssembler taskRepresentationModelAssembler;
  private final JobScheduler jobScheduler;
  private final RestHelper restHelper;

  @Autowired
  AsyncUpdateJobIntTest(
      ClassificationRepresentationModelAssembler classificationRepresentationModelAssembler,
      TaskRepresentationModelAssembler taskRepresentationModelAssembler,
      JobScheduler jobScheduler,
      RestHelper restHelper) {
    this.classificationRepresentationModelAssembler = classificationRepresentationModelAssembler;
    this.taskRepresentationModelAssembler = taskRepresentationModelAssembler;
    this.jobScheduler = jobScheduler;
    this.restHelper = restHelper;
  }

  @Test
  void testUpdateClassificationPrioServiceLevel() throws InvalidArgumentException {

    // 1st step: get old classification :
    final Instant before = Instant.now();

    ResponseEntity<ClassificationRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS_ID, CLASSIFICATION_ID),
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    ClassificationRepresentationModel classification = response.getBody();
    assertThat(classification.getLink(IanaLinkRelations.SELF)).isNotNull();

    // 2nd step: modify classification and trigger update
    classification.removeLinks();
    classification.setServiceLevel("P5D");
    classification.setPriority(1000);

    TEMPLATE.exchange(
        restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS_ID, CLASSIFICATION_ID),
        HttpMethod.PUT,
        new HttpEntity<>(classification, restHelper.getHeadersTeamlead_1()),
        ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));

    // trigger jobs twice to refresh all entries. first entry on the first call and follow up on the
    // seconds call
    jobScheduler.triggerJobs();
    jobScheduler.triggerJobs();

    // verify the classification modified timestamp is after 'before'
    ResponseEntity<ClassificationRepresentationModel> repeatedResponse =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS_ID, CLASSIFICATION_ID),
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));

    assertThat(repeatedResponse.getBody()).isNotNull();

    ClassificationRepresentationModel modifiedClassificationRepresentationModel =
        repeatedResponse.getBody();
    Classification modifiedClassification =
        classificationRepresentationModelAssembler.toEntityModel(
            modifiedClassificationRepresentationModel);

    assertThat(before).isBefore(modifiedClassification.getModified());

    List<String> affectedTasks =
        List.of(
            "TKI:000000000000000000000000000000000003",
            "TKI:000000000000000000000000000000000004",
            "TKI:000000000000000000000000000000000005",
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000007",
            "TKI:000000000000000000000000000000000008",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000010",
            "TKI:000000000000000000000000000000000011",
            "TKI:000000000000000000000000000000000012",
            "TKI:000000000000000000000000000000000013",
            "TKI:000000000000000000000000000000000014",
            "TKI:000000000000000000000000000000000015",
            "TKI:000000000000000000000000000000000016",
            "TKI:000000000000000000000000000000000017",
            "TKI:000000000000000000000000000000000018",
            "TKI:000000000000000000000000000000000019",
            "TKI:000000000000000000000000000000000020",
            "TKI:000000000000000000000000000000000021",
            "TKI:000000000000000000000000000000000022",
            "TKI:000000000000000000000000000000000023",
            "TKI:000000000000000000000000000000000024",
            "TKI:000000000000000000000000000000000025",
            "TKI:000000000000000000000000000000000026",
            "TKI:000000000000000000000000000000000027",
            "TKI:000000000000000000000000000000000028",
            "TKI:000000000000000000000000000000000029",
            "TKI:000000000000000000000000000000000030",
            "TKI:000000000000000000000000000000000031",
            "TKI:000000000000000000000000000000000032",
            "TKI:000000000000000000000000000000000033",
            "TKI:000000000000000000000000000000000034",
            "TKI:000000000000000000000000000000000035",
            "TKI:000000000000000000000000000000000100",
            "TKI:000000000000000000000000000000000101",
            "TKI:000000000000000000000000000000000102",
            "TKI:000000000000000000000000000000000103");
    for (String taskId : affectedTasks) {
      verifyTaskIsModifiedAfterOrEquals(taskId, before);
    }
  }

  private void verifyTaskIsModifiedAfterOrEquals(String taskId, Instant before)
      throws InvalidArgumentException {

    ResponseEntity<TaskRepresentationModel> taskResponse =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS_ID, taskId),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));

    TaskRepresentationModel taskRepresentationModel = taskResponse.getBody();
    assertThat(taskRepresentationModel).isNotNull();
    Task task = taskRepresentationModelAssembler.toEntityModel(taskRepresentationModel);

    Instant modified = task.getModified();
    assertThat(before).as("Task " + task.getId() + " has not been refreshed.").isBefore(modified);
  }
}
