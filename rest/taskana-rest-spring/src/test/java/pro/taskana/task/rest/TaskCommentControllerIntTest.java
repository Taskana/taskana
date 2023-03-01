/*-
 * #%L
 * pro.taskana:taskana-rest-spring
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.task.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.rest.test.RestHelper.TEMPLATE;

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

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.RestHelper;
import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.task.rest.models.TaskCommentCollectionRepresentationModel;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;

/** Test TaskCommentController. */
@TaskanaSpringBootTest
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
        .hasMessageContaining("TASK_COMMENT_CREATOR_MISMATCHED")
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
