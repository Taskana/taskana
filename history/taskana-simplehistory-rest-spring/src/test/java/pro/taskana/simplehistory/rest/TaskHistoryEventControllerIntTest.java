package pro.taskana.simplehistory.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventListResource;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventRepresentationModel;

/** Controller for integration test. */
@TaskanaSpringBootTest
class TaskHistoryEventControllerIntTest {

  private static final RestTemplate TEMPLATE = RestHelper.TEMPLATE;

  private final RestHelper restHelper;

  @Autowired
  TaskHistoryEventControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testGetAllHistoryEvent() {
    ResponseEntity<TaskHistoryEventListResource> response =
        TEMPLATE.exchange(
            restHelper.toUrl("/api/v1/task-history-event"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(45);
  }

  @Test
  void testGetAllHistoryEventDescendingOrder() {
    String url =
        "/api/v1/task-history-event?sort-by=business-process-id&order=desc&page-size=3&page=1";
    ResponseEntity<TaskHistoryEventListResource> response =
        TEMPLATE.exchange(
            restHelper.toUrl(url),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isNotNull()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(url);
  }

  @Test
  void should_ReturnSpecificTaskHistoryEventWithoutDetails_When_ListIsQueried() {
    String url =
        "/api/v1/task-history-event?business-process-id=BPI:01"
            + "&sort-by=business-process-id&order=asc&page-size=6&page=1";
    ResponseEntity<TaskHistoryEventListResource> response =
        TEMPLATE.exchange(
            restHelper.toUrl(url),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getMetadata()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().iterator().next().getDetails()).isNull();
  }

  @Test
  void should_ReturnSpecificTaskHistoryEventWithDetails_When_SingleEventIsQueried() {
    ResponseEntity<TaskHistoryEventRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl("/api/v1/task-history-event/HEI:000000000000000000000000000000000000"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getDetails()).isNotNull();
  }

  @Test
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl("/api/v1/task-history-event?invalid=BPI:01"),
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("[invalid]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetHistoryEventOfDate() {
    String currentTime = LocalDateTime.now().toString();
    final String finalCurrentTime = currentTime;
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl("/api/v1/task-history-event?created=" + finalCurrentTime),
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining(currentTime)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    // correct Format 'yyyy-MM-dd'
    currentTime = currentTime.substring(0, 10);
    ResponseEntity<TaskHistoryEventListResource> response =
        TEMPLATE.exchange(
            restHelper.toUrl("/api/v1/task-history-event?created=" + currentTime),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(23);
  }

  @Test
  void testGetSecondPageSortedByKey() {
    String url = "/api/v1/task-history-event?sort-by=workbasket-key&order=desc&page=2&page-size=2";
    ResponseEntity<TaskHistoryEventListResource> response =
        TEMPLATE.exchange(
            restHelper.toUrl(url),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(2);
    assertThat(response.getBody().getContent().iterator().next().getWorkbasketKey())
        .isEqualTo("WBI:100000000000000000000000000000000002");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isNotNull()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(url);
    assertThat(response.getBody().getLink("allTaskHistoryEvent"))
        .isNotNull()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith("/api/v1/task-history-event");

    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
  }
}
