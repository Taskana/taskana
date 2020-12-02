package pro.taskana.simplehistory.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventPagedRepresentationModel;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventRepresentationModel;

/** Controller for integration test. */
@TaskanaSpringBootTest
class TaskHistoryEventControllerIntTest {

  private final RestHelper restHelper;

  @Autowired
  TaskHistoryEventControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testGetAllHistoryEvent() {
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(45);
  }

  @Test
  void testGetAllHistoryEventDescendingOrder() {
    String parameters = "?sort-by=BUSINESS_PROCESS_ID&order=DESCENDING&page-size=3&page=1";
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS + parameters),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isNotNull()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(parameters);
  }

  @Test
  void should_ReturnSpecificTaskHistoryEventWithoutDetails_When_ListIsQueried() {
    String parameters =
        "?business-process-id=BPI:01" + "&sort-by=BUSINESS_PROCESS_ID&page-size=6&page=1";
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS + parameters),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getPageMetadata()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().iterator().next().getDetails()).isNull();
  }

  @Test
  void should_ReturnSpecificTaskHistoryEventWithDetails_When_SingleEventIsQueried() {
    ResponseEntity<TaskHistoryEventRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(
                HistoryRestEndpoints.URL_HISTORY_EVENTS_ID,
                "THI:000000000000000000000000000000000000"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getDetails()).isNotNull();
  }

  @Test
  @Disabled("no solution for this")
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS + "?invalid=BPI:01"),
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("[invalid]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @Disabled("Jörg pls fix this")
  void testGetHistoryEventWrongCreatedFormat() {
    String currentTime = LocalDateTime.now().toString();
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(
                    HistoryRestEndpoints.URL_HISTORY_EVENTS + "?created=" + currentTime),
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining(currentTime)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetHistoryEventOfDate() {
    Instant now = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(
                HistoryRestEndpoints.URL_HISTORY_EVENTS + "?created=" + now + "&created="),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(23);
  }

  @Test
  void testGetSecondPageSortedByKey() {
    String parameters = "?sort-by=WORKBASKET_KEY&order=DESCENDING&page=2&page-size=2";
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS + parameters),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(2);
    assertThat(response.getBody().getContent().iterator().next().getWorkbasketKey())
        .isEqualTo("WBI:100000000000000000000000000000000002");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isNotNull()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(parameters);

    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.NEXT)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
  }
}
