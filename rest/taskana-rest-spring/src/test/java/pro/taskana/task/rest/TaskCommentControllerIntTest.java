package pro.taskana.task.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.time.Instant;
import java.util.Comparator;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;

/** Test TaskCommentController. */
@TaskanaSpringBootTest
class TaskCommentControllerIntTest {

  private static final ParameterizedTypeReference<TaskanaPagedModel<TaskCommentRepresentationModel>>
      TASK_COMMENT_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<TaskanaPagedModel<TaskCommentRepresentationModel>>() {};

  private final RestHelper restHelper;

  @Autowired
  TaskCommentControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void should_FailToReturnTaskComment_When_TaskCommentIsNotExisting() {

    String urlToNonExistingTaskComment =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "Non existing task comment Id");

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                urlToNonExistingTaskComment,
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersAdmin()),
                ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void should_FailToReturnTaskComments_When_TaskIstNotVisible() {

    String urlToNotVisibleTask =
        restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000004");

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                urlToNotVisibleTask,
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersUser_1_1()),
                TASK_COMMENT_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_ReturnSortedAndOrederedTaskCommentsSortedByModified_When_UsingSortAndOrderParams() {

    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000000");

    ResponseEntity<TaskanaPagedModel<TaskCommentRepresentationModel>>
        getTaskCommentsSortedByModifiedOrderedByDescendingResponse =
            TEMPLATE.exchange(
                url + "?sort-by=modified&order=desc",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersAdmin()),
                TASK_COMMENT_PAGE_MODEL_TYPE);

    assertThat(getTaskCommentsSortedByModifiedOrderedByDescendingResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsSortedByModifiedOrderedByDescendingResponse.getBody().getContent())
        .hasSize(3)
        .extracting(TaskCommentRepresentationModel::getModified)
        .isSortedAccordingTo(Comparator.reverseOrder());

    ResponseEntity<TaskanaPagedModel<TaskCommentRepresentationModel>>
        getTaskCommentsSortedByModifiedOrderedByAscendingResponse =
            TEMPLATE.exchange(
                url + "?sort-by=modified",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersAdmin()),
                TASK_COMMENT_PAGE_MODEL_TYPE);

    assertThat(getTaskCommentsSortedByModifiedOrderedByAscendingResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsSortedByModifiedOrderedByAscendingResponse.getBody().getContent())
        .hasSize(3)
        .extracting(TaskCommentRepresentationModel::getModified)
        .isSortedAccordingTo(Comparator.naturalOrder());

    ResponseEntity<TaskanaPagedModel<TaskCommentRepresentationModel>>
        getTaskCommentsSortedByCreatedOrderedByDescendingResponse =
            TEMPLATE.exchange(
                url + "?sort-by=created&order=desc",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersAdmin()),
                TASK_COMMENT_PAGE_MODEL_TYPE);

    assertThat(getTaskCommentsSortedByCreatedOrderedByDescendingResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsSortedByCreatedOrderedByDescendingResponse.getBody().getContent())
        .hasSize(3)
        .extracting(TaskCommentRepresentationModel::getCreated)
        .isSortedAccordingTo(Comparator.reverseOrder());

    ResponseEntity<TaskanaPagedModel<TaskCommentRepresentationModel>>
        getTaskCommentsSortedByCreatedOrderedByAscendingResponse =
            TEMPLATE.exchange(
                url + "?sort-by=created",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersAdmin()),
                TASK_COMMENT_PAGE_MODEL_TYPE);

    assertThat(getTaskCommentsSortedByCreatedOrderedByAscendingResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsSortedByCreatedOrderedByAscendingResponse.getBody().getContent())
        .hasSize(3)
        .extracting(TaskCommentRepresentationModel::getCreated)
        .isSortedAccordingTo(Comparator.naturalOrder());
  }

  @Test
  void should_ThrowException_When_UsingInvalidSortParam() {

    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000000");

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url + "?sort-by=invalidSortParam",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersUser_1_1()),
                TASK_COMMENT_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_FailToReturnTaskComment_When_TaskIstNotVisible() {

    String urlToNotVisibleTask =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000012");

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                urlToNotVisibleTask,
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersUser_1_2()),
                ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToCreateTaskComment_When_TaskIsNotVisible() {

    TaskCommentRepresentationModel taskCommentRepresentationModelToCreate =
        new TaskCommentRepresentationModel();
    taskCommentRepresentationModelToCreate.setTaskId("TKI:000000000000000000000000000000000000");
    taskCommentRepresentationModelToCreate.setTextField("newly created task comment");

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(
                    Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000000"),
                HttpMethod.POST,
                new HttpEntity<>(
                    taskCommentRepresentationModelToCreate, restHelper.getHeadersUser_b_1()),
                ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
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
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(Mapping.URL_TASK_COMMENTS, "DefinatelyNotExistingId"),
                HttpMethod.POST,
                new HttpEntity<>(
                    taskCommentRepresentationModelToCreate, restHelper.getHeadersAdmin()),
                ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void should_FailToUpdateTaskComment_When_TaskCommentWasModifiedConcurrently() {
    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");

    ResponseEntity<TaskCommentRepresentationModel> getTaskCommentResponse =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    TaskCommentRepresentationModel taskCommentToUpdate = getTaskCommentResponse.getBody();
    assertThat(taskCommentToUpdate).isNotNull();
    assertThat(taskCommentToUpdate.getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(taskCommentToUpdate.getCreator()).isEqualTo("user-1-1");
    assertThat(taskCommentToUpdate.getTextField()).isEqualTo("some text in textfield");

    taskCommentToUpdate.setModified(Instant.now());

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(taskCommentToUpdate, restHelper.getHeadersUser_1_1()),
                ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void should_FailToUpdateTaskComment_When_UserHasNoAuthorization() {
    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");

    ResponseEntity<TaskCommentRepresentationModel> getTaskCommentResponse =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersUser_1_1()),
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    TaskCommentRepresentationModel taskComment = getTaskCommentResponse.getBody();
    assertThat(taskComment).isNotNull();
    assertThat(taskComment.getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(taskComment.getCreator()).isEqualTo("user-1-1");
    assertThat(taskComment.getTextField()).isEqualTo("some text in textfield");

    taskComment.setTextField("updated textfield");

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(taskComment, restHelper.getHeadersUser_1_2()),
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
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThat(getTaskCommentResponse.getBody()).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getCreator()).isEqualTo("user-1-1");
    assertThat(getTaskCommentResponse.getBody().getTextField()).isEqualTo("some text in textfield");

    TaskCommentRepresentationModel taskCommentRepresentationModelToUpdate =
        getTaskCommentResponse.getBody();
    taskCommentRepresentationModelToUpdate.setTextField("updated text");
    taskCommentRepresentationModelToUpdate.setTaskCommentId("DifferentTaskCommentId");

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(
                    taskCommentRepresentationModelToUpdate, restHelper.getHeadersUser_1_1()),
                ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_FailToDeleteTaskComment_When_UserHasNoAuthorization() {

    ResponseEntity<TaskanaPagedModel<TaskCommentRepresentationModel>>
        getTaskCommentsBeforeDeleteionResponse =
            TEMPLATE.exchange(
                restHelper.toUrl(
                    Mapping.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000001"),
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersUser_1_2()),
                TASK_COMMENT_PAGE_MODEL_TYPE);
    assertThat(getTaskCommentsBeforeDeleteionResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsBeforeDeleteionResponse.getBody().getContent()).hasSize(2);

    String url =
        restHelper.toUrl(Mapping.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000004");

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<String>(restHelper.getHeadersUser_1_2()),
                ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("TaskComment creator and current user must match.")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIsNotExisting() {

    String url = restHelper.toUrl(Mapping.URL_TASK_COMMENT, "NotExistingTaskComment");

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<String>(restHelper.getHeadersAdmin()),
                ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
