package pro.taskana.task.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.RestHelper;
import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;

/** Test TaskCommentController. */
@TaskanaSpringBootTest
class TaskCommentControllerIntTest {

  private static final ParameterizedTypeReference<TaskanaPagedModel<TaskCommentRepresentationModel>>
      TASK_COMMENT_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<TaskanaPagedModel<TaskCommentRepresentationModel>>() {};
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
              ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
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
              TASK_COMMENT_PAGE_MODEL_TYPE);
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
              ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void should_FailToCreateTaskComment_When_TaskIsNotVisible() {

    TaskCommentRepresentationModel taskCommentRepresentationModelToCreate =
        new TaskCommentRepresentationModel();
    taskCommentRepresentationModelToCreate.setTaskId("TKI:000000000000000000000000000000000000");
    taskCommentRepresentationModelToCreate.setTextField("newly created task comment");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(
                  Mapping.URL_TASK_GET_POST_COMMENTS, "TKI:000000000000000000000000000000000000"),
              HttpMethod.POST,
              new HttpEntity<>(
                  taskCommentRepresentationModelToCreate, restHelper.getHeadersUser_1_1()),
              ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToCreateTaskComment_When_TaskIdIsNonExisting() {

    TaskCommentRepresentationModel taskCommentRepresentationModelToCreate =
        new TaskCommentRepresentationModel();
    taskCommentRepresentationModelToCreate.setTaskId("DefinatelyNotExistingId");
    taskCommentRepresentationModelToCreate.setTextField("newly created task comment");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(Mapping.URL_TASK_GET_POST_COMMENTS, "DefinatelyNotExistingId"),
              HttpMethod.POST,
              new HttpEntity<>(
                  taskCommentRepresentationModelToCreate, restHelper.getHeadersAdmin()),
              ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void should_FailToUpdateTaskComment_When_TaskCommentWasModifiedConcurrently() {
    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");

    ResponseEntity<TaskCommentRepresentationModel> getTaskCommentResponse =
        template.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThat(getTaskCommentResponse.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getCreator()).isEqualTo("user_1_1");
    assertThat(getTaskCommentResponse.getBody().getTextField()).isEqualTo("some text in textfield");

    TaskCommentRepresentationModel taskCommentRepresentationModelToUpdate =
        getTaskCommentResponse.getBody();
    taskCommentRepresentationModelToUpdate.setModified(Instant.now().toString());

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              url,
              HttpMethod.PUT,
              new HttpEntity<>(
                  taskCommentRepresentationModelToUpdate,
                  restHelper.getHeadersUser_1_1()),
              ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void should_FailToUpdateTaskComment_When_UserHasNoAuthorization() {
    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");

    ResponseEntity<TaskCommentRepresentationModel> getTaskCommentResponse =
        template.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersUser_1_1()),
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThat(getTaskCommentResponse.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getCreator()).isEqualTo("user_1_1");
    assertThat(getTaskCommentResponse.getBody().getTextField()).isEqualTo("some text in textfield");

    TaskCommentRepresentationModel taskCommentRepresentationModelToUpdate =
        getTaskCommentResponse.getBody();
    taskCommentRepresentationModelToUpdate.setTextField("updated textfield");

    ThrowingCallable httpCall =
        () -> template.exchange(
            url,
            HttpMethod.PUT,
            new HttpEntity<>(
                taskCommentRepresentationModelToUpdate,
                restHelper.getHeadersUser_1_2()),
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToUpdateTaskComment_When_TaskCommentIdInResourceDoesNotMatchPathVariable() {


    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");

    ResponseEntity<TaskCommentRepresentationModel> getTaskCommentResponse =
        template.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThat(getTaskCommentResponse.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getCreator()).isEqualTo("user_1_1");
    assertThat(getTaskCommentResponse.getBody().getTextField()).isEqualTo("some text in textfield");

    TaskCommentRepresentationModel taskCommentRepresentationModelToUpdate =
        getTaskCommentResponse.getBody();
    taskCommentRepresentationModelToUpdate.setTextField("updated text");
    taskCommentRepresentationModelToUpdate.setTaskCommentId("DifferentTaskCommentId");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              url,
              HttpMethod.PUT,
              new HttpEntity<>(
                  taskCommentRepresentationModelToUpdate,
                  restHelper.getHeadersUser_1_1()),
              ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Disabled("Disabled until Authorization check is up!")
  @Test
  void should_FailToDeleteTaskComment_When_UserHasNoAuthorization() {

    ResponseEntity<TaskanaPagedModel<TaskCommentRepresentationModel>>
        getTaskCommentsBeforeDeleteionResponse =
            template.exchange(
                restHelper.toUrl(
                    Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000001"),
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersAdmin()),
                TASK_COMMENT_PAGE_MODEL_TYPE);
    assertThat(getTaskCommentsBeforeDeleteionResponse.getBody().getContent()).hasSize(2);

    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000004");

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              url,
              HttpMethod.DELETE,
              new HttpEntity<String>(restHelper.getHeadersUser_1_2()),
              ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
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
              ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
