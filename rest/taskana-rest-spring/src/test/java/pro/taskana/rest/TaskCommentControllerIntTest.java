package pro.taskana.rest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.TaskCommentListResource;
import pro.taskana.rest.resource.TaskCommentResource;

/** Test TaskCommentController. */
@TaskanaSpringBootTest
class TaskCommentControllerIntTest {

  private static RestTemplate template;

  @Value("${taskana.schemaName:TASKANA}")
  public String schemaName;

  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testGetNonExistentCommentShouldFail() {

    String urlToNonExistingTaskComment =
        restHelper.toUrl(
            Mapping.URL_TASK_COMMENT,
            "TKI:000000000000000000000000000000000000",
            "Non existing task comment Id");

    assertThatThrownBy(
        () ->
                template.exchange(
                    urlToNonExistingTaskComment,
                    HttpMethod.GET,
                    new HttpEntity<String>(restHelper.getHeadersAdmin()),
                    ParameterizedTypeReference.forType(TaskCommentResource.class)))
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetCommentsForNonExistingTaskShouldFail() {

    String urlToNonExistingTask = restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "nonExistingTaskId");

    assertThatThrownBy(
        () ->
                template.exchange(
                    urlToNonExistingTask,
                    HttpMethod.GET,
                    new HttpEntity<String>(restHelper.getHeadersAdmin()),
                    ParameterizedTypeReference.forType(TaskCommentListResource.class)))
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void testGetTaskCommentsOfNotVisibleTaskShouldFail() {

    String urlToNotVisibleTask =
        restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000004");

    ResponseEntity<TaskCommentListResource> response =
        template.exchange(
            urlToNotVisibleTask,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersUser_1_1()),
            ParameterizedTypeReference.forType(TaskCommentListResource.class));
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void testGetTaskCommentOfNotVisibleTaskShouldFail() {

    String urlToNotVisibleTask =
        restHelper.toUrl(
            Mapping.URL_TASK_COMMENT,
            "TKI:000000000000000000000000000000000004",
            "TCI:000000000000000000000000000000000013");

    ResponseEntity<TaskCommentResource> response =
        template.exchange(
            urlToNotVisibleTask,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersUser_1_1()),
            ParameterizedTypeReference.forType(TaskCommentResource.class));
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void testCreateTaskCommentForNotVisibleTaskShouldFail() {

    TaskCommentResource taskCommentResourceToCreate = new TaskCommentResource();
    taskCommentResourceToCreate.setTaskId("TKI:000000000000000000000000000000000004");
    taskCommentResourceToCreate.setTextField("newly created task comment");

    assertThatThrownBy(
        () ->
                template.exchange(
                    restHelper.toUrl(
                        Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000004"),
                    HttpMethod.POST,
                    new HttpEntity<>(taskCommentResourceToCreate, restHelper.getHeadersUser_1_1()),
                    ParameterizedTypeReference.forType(TaskCommentResource.class)))
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testCreateTaskCommentForNotExistingTaskShouldFail() {

    TaskCommentResource taskCommentResourceToCreate = new TaskCommentResource();
    taskCommentResourceToCreate.setTaskId("DefinatelyNotExistingId");
    taskCommentResourceToCreate.setTextField("newly created task comment");

    assertThatThrownBy(
        () ->
                template.exchange(
                    restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "NotExistingTaskId"),
                    HttpMethod.POST,
                    new HttpEntity<>(taskCommentResourceToCreate, restHelper.getHeadersUser_1_1()),
                    ParameterizedTypeReference.forType(TaskCommentResource.class)))
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);

    TaskCommentResource taskCommentResourceToCreate2 = new TaskCommentResource();
    taskCommentResourceToCreate.setTaskId(null);
    taskCommentResourceToCreate.setTextField("newly created task comment");

    assertThatThrownBy(
        () ->
                template.exchange(
                    restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "NotExistingTaskId"),
                    HttpMethod.POST,
                    new HttpEntity<>(taskCommentResourceToCreate2, restHelper.getHeadersUser_1_1()),
                    ParameterizedTypeReference.forType(TaskCommentResource.class)))
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);

    TaskCommentResource taskCommentResourceToCreate3 = new TaskCommentResource();
    taskCommentResourceToCreate.setTaskId("");
    taskCommentResourceToCreate.setTextField("newly created task comment");

    assertThatThrownBy(
        () ->
                template.exchange(
                    restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "NotExistingTaskId"),
                    HttpMethod.POST,
                    new HttpEntity<>(taskCommentResourceToCreate3, restHelper.getHeadersUser_1_1()),
                    ParameterizedTypeReference.forType(TaskCommentResource.class)))
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testUpdateTaskCommentWithConcurrentModificationShouldFail() {}

  @Test
  void testUpdateTaskCommentWithNoAuthorizationShouldFail() {}

  @Test
  void testUpdateTaskCommentOfNotExistingTaskShouldFail() {}
}
