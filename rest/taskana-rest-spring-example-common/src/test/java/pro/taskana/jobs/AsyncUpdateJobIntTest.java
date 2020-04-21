package pro.taskana.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.rest.Mapping;
import pro.taskana.rest.RestConfiguration;
import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.ClassificationResourceAssembler;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.TaskResourceAssembler;
import pro.taskana.task.api.models.Task;

/** Test async updates. */
@ActiveProfiles({"test"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = RestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AsyncUpdateJobIntTest {

  private static final String CLASSIFICATION_ID = "CLI:100000000000000000000000000000000003";

  @SuppressWarnings("checkstyle:DeclarationOrder")
  static RestTemplate template;

  @Autowired ClassificationResourceAssembler classificationResourceAssembler;
  @Autowired TaskResourceAssembler taskResourceAssembler;
  @Autowired JobScheduler jobScheduler;
  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testUpdateClassificationPrioServiceLevel() throws Exception {

    // 1st step: get old classification :
    final Instant before = Instant.now();
    final ObjectMapper mapper = new ObjectMapper();

    ResponseEntity<ClassificationResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS_ID, CLASSIFICATION_ID),
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class));

    assertThat(response.getBody()).isNotNull();
    ClassificationResource classification = response.getBody();
    assertThat(classification.getLink(IanaLinkRelations.SELF)).isNotNull();

    // 2nd step: modify classification and trigger update
    classification.removeLinks();
    classification.setServiceLevel("P5D");
    classification.setPriority(1000);

    template.put(
        restHelper.toUrl(Mapping.URL_CLASSIFICATIONS_ID, CLASSIFICATION_ID),
        new HttpEntity<>(mapper.writeValueAsString(classification), restHelper.getHeaders()));

    // trigger jobs twice to refresh all entries. first entry on the first call and follow up on the
    // seconds call
    jobScheduler.triggerJobs();
    jobScheduler.triggerJobs();

    // verify the classification modified timestamp is after 'before'
    ResponseEntity<ClassificationResource> repeatedResponse =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS_ID, CLASSIFICATION_ID),
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class));

    assertThat(repeatedResponse.getBody()).isNotNull();

    ClassificationResource modifiedClassificationResource = repeatedResponse.getBody();
    Classification modifiedClassification =
        classificationResourceAssembler.toModel(modifiedClassificationResource);

    assertThat(before).isBefore(modifiedClassification.getModified());

    List<String> affectedTasks =
        new ArrayList<>(
            Arrays.asList(
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
                "TKI:000000000000000000000000000000000103"));
    for (String taskId : affectedTasks) {
      verifyTaskIsModifiedAfterOrEquals(taskId, before);
    }
  }

  private void verifyTaskIsModifiedAfterOrEquals(String taskId, Instant before)
      throws InvalidArgumentException {

    ResponseEntity<TaskResource> taskResponse =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS_ID, taskId),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskResource.class));

    TaskResource taskResource = taskResponse.getBody();
    Task task = taskResourceAssembler.toModel(taskResource);

    Instant modified = task.getModified();
    assertThat(before).as("Task " + task.getId() + " has not been refreshed.").isBefore(modified);
  }
}
