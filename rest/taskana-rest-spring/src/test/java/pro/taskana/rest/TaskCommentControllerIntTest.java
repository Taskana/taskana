package pro.taskana.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
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
  void should_FailToReturnTaskComment_When_TaskCommentIsNotExisting() {

    String urlToNonExistingTaskComment =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "Non existing task comment Id");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              urlToNonExistingTaskComment,
              HttpMethod.GET,
              new HttpEntity<String>(restHelper.getHeadersAdmin()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void should_FailToReturnTaskComments_When_TaskIstNotVisible() {

    String urlToNotVisibleTask =
        restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000004");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              urlToNotVisibleTask,
              HttpMethod.GET,
              new HttpEntity<String>(restHelper.getHeadersUser_1_1()),
              ParameterizedTypeReference.forType(TaskCommentListResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void should_FailToReturnTaskComment_When_TaskIstNotVisible() {

    String urlToNotVisibleTask =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000012");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              urlToNotVisibleTask,
              HttpMethod.GET,
              new HttpEntity<String>(restHelper.getHeadersUser_1_2()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void should_FailToCreateTaskComment_When_TaskIsNotVisible() {

    TaskCommentResource taskCommentResourceToCreate = new TaskCommentResource();
    taskCommentResourceToCreate.setTaskId("TKI:000000000000000000000000000000000000");
    taskCommentResourceToCreate.setTextField("newly created task comment");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(
                  Mapping.URL_TASK_GET_POST_COMMENTS, "TKI:000000000000000000000000000000000000"),
              HttpMethod.POST,
              new HttpEntity<>(taskCommentResourceToCreate, restHelper.getHeadersUser_1_1()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToCreateTaskComment_When_TaskIdIsNonExisting() {

    TaskCommentResource taskCommentResourceToCreate = new TaskCommentResource();
    taskCommentResourceToCreate.setTaskId("DefinatelyNotExistingId");
    taskCommentResourceToCreate.setTextField("newly created task comment");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(Mapping.URL_TASK_GET_POST_COMMENTS, "DefinatelyNotExistingId"),
              HttpMethod.POST,
              new HttpEntity<>(taskCommentResourceToCreate, restHelper.getHeadersAdmin()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);

  }


  @Test
  void should_FailToUpdateTaskComment_When_TaskCommentWasModifiedConcurrently() {

    final ObjectMapper mapper = new ObjectMapper();

    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");

    ResponseEntity<TaskCommentResource> getTaskCommentResponse =
        template.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskCommentResource.class));
    assertThat(getTaskCommentResponse.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getCreator()).isEqualTo("user_1_1");
    assertThat(getTaskCommentResponse.getBody().getTextField()).isEqualTo("some text in textfield");

    TaskCommentResource taskCommentResourceToUpdate = getTaskCommentResponse.getBody();
    taskCommentResourceToUpdate.setModified(Instant.now().toString());

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              url,
              HttpMethod.PUT,
              new HttpEntity<>(
                  mapper.writeValueAsString(taskCommentResourceToUpdate),
                  restHelper.getHeadersUser_1_1()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void should_FailToUpdateTaskComment_When_UserHasNoAuthorization() {

    final ObjectMapper mapper = new ObjectMapper();

    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");

    ResponseEntity<TaskCommentResource> getTaskCommentResponse =
        template.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersUser_1_1()),
            ParameterizedTypeReference.forType(TaskCommentResource.class));
    assertThat(getTaskCommentResponse.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getCreator()).isEqualTo("user_1_1");
    assertThat(getTaskCommentResponse.getBody().getTextField()).isEqualTo("some text in textfield");

    TaskCommentResource taskCommentResourceToUpdate = getTaskCommentResponse.getBody();
    taskCommentResourceToUpdate.setTextField("updated textfield");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              url,
              HttpMethod.PUT,
              new HttpEntity<>(
                  mapper.writeValueAsString(taskCommentResourceToUpdate),
                  restHelper.getHeadersUser_1_2()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToUpdateTaskComment_When_TaskIsNotExisting() {
    final ObjectMapper mapper = new ObjectMapper();

    TaskCommentResource taskCommentResourceToUpdate = new TaskCommentResource();
    taskCommentResourceToUpdate.setTaskId("TKI:000000000000000000000000000000009999");
    taskCommentResourceToUpdate.setTaskCommentId("TCI:000000000000000000000000000000000000");
    taskCommentResourceToUpdate.setTextField("updated text");

    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              url,
              HttpMethod.PUT,
              new HttpEntity<>(
                  mapper.writeValueAsString(taskCommentResourceToUpdate),
                  restHelper.getHeadersUser_1_1()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void should_FailToUpdateTaskComment_When_TaskCommentIdInResourceDoesNotMatchPathVariable() {

    final ObjectMapper mapper = new ObjectMapper();

    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");

    ResponseEntity<TaskCommentResource> getTaskCommentResponse =
        template.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskCommentResource.class));
    assertThat(getTaskCommentResponse.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getCreator()).isEqualTo("user_1_1");
    assertThat(getTaskCommentResponse.getBody().getTextField()).isEqualTo("some text in textfield");

    TaskCommentResource taskCommentResourceToUpdate = getTaskCommentResponse.getBody();
    taskCommentResourceToUpdate.setTextField("updated text");
    taskCommentResourceToUpdate.setTaskCommentId("DifferentTaskCommentId");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              url,
              HttpMethod.PUT,
              new HttpEntity<>(
                  mapper.writeValueAsString(taskCommentResourceToUpdate),
                  restHelper.getHeadersUser_1_1()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void should_FailToDeleteTaskComment_When_UserHasNoAuthorization() {

    ResponseEntity<TaskCommentListResource> getTaskCommentsBeforeDeleteionResponse =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000001"),
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskCommentListResource.class));
    assertThat(getTaskCommentsBeforeDeleteionResponse.getBody().getContent()).hasSize(2);

    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000004");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              url,
              HttpMethod.DELETE,
              new HttpEntity<String>(restHelper.getHeadersUser_1_2()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIsNotExisting() {

    String url = restHelper.toUrl(Mapping.URL_TASK_COMMENT, "NotExistingTaskComment");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              url,
              HttpMethod.DELETE,
              new HttpEntity<String>(restHelper.getHeadersAdmin()),
              ParameterizedTypeReference.forType(TaskCommentResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
