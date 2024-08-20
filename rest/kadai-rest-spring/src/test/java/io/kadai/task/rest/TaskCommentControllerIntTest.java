package io.kadai.task.rest;

import static io.kadai.rest.test.RestHelper.TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.common.rest.RestEndpoints;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.rest.test.RestHelper;
import io.kadai.task.rest.models.TaskCommentCollectionRepresentationModel;
import io.kadai.task.rest.models.TaskCommentRepresentationModel;
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
import org.springframework.web.client.HttpStatusCodeException;

/** Test TaskCommentController. */
@KadaiSpringBootTest
class TaskCommentControllerIntTest {

  private static final ParameterizedTypeReference<TaskCommentCollectionRepresentationModel>
      TASK_COMMENT_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<TaskCommentCollectionRepresentationModel>() {};
  private static final ParameterizedTypeReference<TaskCommentRepresentationModel>
      TASK_COMMENT_TYPE = new ParameterizedTypeReference<TaskCommentRepresentationModel>() {};

  private final RestHelper restHelper;

  @Autowired
  TaskCommentControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void should_ReturnTaskComment_When_GivenTaskCommentId() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskCommentRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void should_FailToReturnTaskComment_When_TaskCommentIsNotExisting() {
    String url = restHelper.toUrl(RestEndpoints.URL_TASK_COMMENT, "Non existing task comment Id");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_COMMENT_TYPE);
        };

    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void should_FailToReturnTaskComments_When_TaskIstNotVisible() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000004");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_COMMENT_PAGE_MODEL_TYPE);
        };

    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_ReturnSortedAndOrederedTaskCommentsSortedByModified_When_UsingSortAndOrderParams() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000000");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    String url1 = url + "?sort-by=MODIFIED&order=DESCENDING";
    ResponseEntity<TaskCommentCollectionRepresentationModel>
        getTaskCommentsSortedByModifiedOrderedByDescendingResponse =
            TEMPLATE.exchange(url1, HttpMethod.GET, auth, TASK_COMMENT_PAGE_MODEL_TYPE);
    assertThat(getTaskCommentsSortedByModifiedOrderedByDescendingResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsSortedByModifiedOrderedByDescendingResponse.getBody().getContent())
        .hasSize(3)
        .extracting(TaskCommentRepresentationModel::getModified)
        .isSortedAccordingTo(Comparator.reverseOrder());

    String url2 = url + "?sort-by=MODIFIED";
    ResponseEntity<TaskCommentCollectionRepresentationModel>
        getTaskCommentsSortedByModifiedOrderedByAscendingResponse =
            TEMPLATE.exchange(url2, HttpMethod.GET, auth, TASK_COMMENT_PAGE_MODEL_TYPE);
    assertThat(getTaskCommentsSortedByModifiedOrderedByAscendingResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsSortedByModifiedOrderedByAscendingResponse.getBody().getContent())
        .hasSize(3)
        .extracting(TaskCommentRepresentationModel::getModified)
        .isSortedAccordingTo(Comparator.naturalOrder());

    String url3 = url + "?sort-by=CREATED&order=DESCENDING";
    ResponseEntity<TaskCommentCollectionRepresentationModel>
        getTaskCommentsSortedByCreatedOrderedByDescendingResponse =
            TEMPLATE.exchange(url3, HttpMethod.GET, auth, TASK_COMMENT_PAGE_MODEL_TYPE);
    assertThat(getTaskCommentsSortedByCreatedOrderedByDescendingResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsSortedByCreatedOrderedByDescendingResponse.getBody().getContent())
        .hasSize(3)
        .extracting(TaskCommentRepresentationModel::getCreated)
        .isSortedAccordingTo(Comparator.reverseOrder());

    String url4 = url + "?sort-by=CREATED";
    ResponseEntity<TaskCommentCollectionRepresentationModel>
        getTaskCommentsSortedByCreatedOrderedByAscendingResponse =
            TEMPLATE.exchange(url4, HttpMethod.GET, auth, TASK_COMMENT_PAGE_MODEL_TYPE);
    assertThat(getTaskCommentsSortedByCreatedOrderedByAscendingResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsSortedByCreatedOrderedByAscendingResponse.getBody().getContent())
        .hasSize(3)
        .extracting(TaskCommentRepresentationModel::getCreated)
        .isSortedAccordingTo(Comparator.naturalOrder());
  }

  @Test
  void should_ThrowException_When_UsingInvalidSortParam() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000000");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(
              url + "?sort-by=invalidSortParam",
              HttpMethod.GET,
              auth,
              TASK_COMMENT_PAGE_MODEL_TYPE);
        };

    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_FailToReturnTaskComment_When_TaskIstNotVisible() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000012");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_COMMENT_TYPE);
        };

    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_CreateTaskComment_When_TaskIsVisible() {
    TaskCommentRepresentationModel taskCommentRepresentationModelToCreate =
        new TaskCommentRepresentationModel();
    taskCommentRepresentationModelToCreate.setTaskId("TKI:000000000000000000000000000000000004");
    taskCommentRepresentationModelToCreate.setTextField("newly created task comment");
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000004");
    HttpEntity<TaskCommentRepresentationModel> auth =
        new HttpEntity<>(
            taskCommentRepresentationModelToCreate, RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskCommentRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.POST,
            auth,
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTaskCommentId()).isNotNull();
    assertThat(response.getBody().getCreator()).isEqualTo("admin");
    assertThat(response.getBody().getTaskId())
        .isEqualTo("TKI:000000000000000000000000000000000004");
    assertThat(response.getBody().getTextField()).isEqualTo("newly created task comment");
    assertThat(response.getBody().getCreated()).isNotNull();
    assertThat(response.getBody().getModified()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void should_FailToCreateTaskComment_When_TaskIsNotVisible() {
    TaskCommentRepresentationModel taskCommentRepresentationModelToCreate =
        new TaskCommentRepresentationModel();
    taskCommentRepresentationModelToCreate.setTaskId("TKI:000000000000000000000000000000000000");
    taskCommentRepresentationModelToCreate.setTextField("newly created task comment");
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000000");
    HttpEntity<TaskCommentRepresentationModel> auth =
        new HttpEntity<>(
            taskCommentRepresentationModelToCreate, RestHelper.generateHeadersForUser("user-b-1"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_COMMENT_TYPE);
        };

    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToCreateTaskComment_When_TaskIdIsNonExisting() {
    TaskCommentRepresentationModel taskCommentRepresentationModelToCreate =
        new TaskCommentRepresentationModel();
    taskCommentRepresentationModelToCreate.setTaskId("DefinatelyNotExistingId");
    taskCommentRepresentationModelToCreate.setTextField("newly created task comment");
    String url = restHelper.toUrl(RestEndpoints.URL_TASK_COMMENTS, "DefinatelyNotExistingId");
    HttpEntity<TaskCommentRepresentationModel> auth =
        new HttpEntity<>(
            taskCommentRepresentationModelToCreate, RestHelper.generateHeadersForUser("admin"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_COMMENT_TYPE);
        };

    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void should_UpdateTaskComment() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000003");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskCommentRepresentationModel> getTaskCommentResponse =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_COMMENT_TYPE);
    TaskCommentRepresentationModel taskCommentToUpdate = getTaskCommentResponse.getBody();
    assertThat(taskCommentToUpdate).isNotNull();
    assertThat(taskCommentToUpdate.getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(taskCommentToUpdate.getCreator()).isEqualTo("user-1-2");
    assertThat(taskCommentToUpdate.getTextField()).isEqualTo("some text in textfield");

    taskCommentToUpdate.setTextField("updated text in textfield");
    HttpEntity<TaskCommentRepresentationModel> auth2 =
        new HttpEntity<>(taskCommentToUpdate, RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskCommentRepresentationModel> responseUpdate =
        TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_COMMENT_TYPE);

    assertThat(responseUpdate.getBody()).isNotNull();
    assertThat(responseUpdate.getBody().getTextField()).isEqualTo("updated text in textfield");
  }

  @Test
  void should_FailToUpdateTaskComment_When_TaskCommentWasModifiedConcurrently() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskCommentRepresentationModel> getTaskCommentResponse =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_COMMENT_TYPE);
    TaskCommentRepresentationModel taskCommentToUpdate = getTaskCommentResponse.getBody();
    assertThat(taskCommentToUpdate).isNotNull();
    assertThat(taskCommentToUpdate.getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(taskCommentToUpdate.getCreator()).isEqualTo("user-1-1");
    assertThat(taskCommentToUpdate.getTextField()).isEqualTo("some text in textfield");

    taskCommentToUpdate.setModified(Instant.now());
    HttpEntity<TaskCommentRepresentationModel> auth2 =
        new HttpEntity<>(taskCommentToUpdate, RestHelper.generateHeadersForUser("user-1-1"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_COMMENT_TYPE);
        };
    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void should_FailToUpdateTaskComment_When_UserHasNoAuthorization() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

    ResponseEntity<TaskCommentRepresentationModel> getTaskCommentResponse =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_COMMENT_TYPE);
    TaskCommentRepresentationModel taskComment = getTaskCommentResponse.getBody();
    assertThat(taskComment).isNotNull();
    assertThat(taskComment.getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(taskComment.getCreator()).isEqualTo("user-1-1");
    assertThat(taskComment.getTextField()).isEqualTo("some text in textfield");

    taskComment.setTextField("updated textfield");
    HttpEntity<TaskCommentRepresentationModel> auth2 =
        new HttpEntity<>(taskComment, RestHelper.generateHeadersForUser("user-1-2"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_COMMENT_TYPE);
        };
    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToUpdateTaskComment_When_TaskCommentIdInResourceDoesNotMatchPathVariable() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskCommentRepresentationModel> getTaskCommentResponse =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_COMMENT_TYPE);
    assertThat(getTaskCommentResponse.getBody()).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(getTaskCommentResponse.getBody().getCreator()).isEqualTo("user-1-1");
    assertThat(getTaskCommentResponse.getBody().getTextField()).isEqualTo("some text in textfield");

    TaskCommentRepresentationModel taskCommentRepresentationModelToUpdate =
        getTaskCommentResponse.getBody();
    taskCommentRepresentationModelToUpdate.setTextField("updated text");
    taskCommentRepresentationModelToUpdate.setTaskCommentId("DifferentTaskCommentId");
    HttpEntity<TaskCommentRepresentationModel> auth2 =
        new HttpEntity<>(
            taskCommentRepresentationModelToUpdate, RestHelper.generateHeadersForUser("user-1-1"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_COMMENT_TYPE);
        };
    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_DeleteTaskComment_When_UserHasAuthorization() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000006");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskCommentRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.DELETE,
            auth,
            ParameterizedTypeReference.forType(TaskCommentRepresentationModel.class));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ThrowingCallable httpCall =
        () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_COMMENT_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining("TASK_COMMENT_NOT_FOUND")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void should_FailToDeleteTaskComment_When_UserHasNoAuthorization() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000001");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    ResponseEntity<TaskCommentCollectionRepresentationModel>
        getTaskCommentsBeforeDeleteionResponse =
            TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_COMMENT_PAGE_MODEL_TYPE);
    assertThat(getTaskCommentsBeforeDeleteionResponse.getBody()).isNotNull();
    assertThat(getTaskCommentsBeforeDeleteionResponse.getBody().getContent()).hasSize(2);

    String url2 =
        restHelper.toUrl(
            RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000004");
    ThrowingCallable httpCall =
        () -> TEMPLATE.exchange(url2, HttpMethod.DELETE, auth, TASK_COMMENT_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining("NOT_AUTHORIZED_ON_TASK_COMMENT")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIsNotExisting() {

    String url = restHelper.toUrl(RestEndpoints.URL_TASK_COMMENT, "NotExistingTaskComment");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.DELETE, auth, TASK_COMMENT_TYPE);
        };

    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
