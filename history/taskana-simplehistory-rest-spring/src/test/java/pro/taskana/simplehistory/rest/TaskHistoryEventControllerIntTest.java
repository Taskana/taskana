package pro.taskana.simplehistory.rest;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.time.Instant;
import java.time.LocalDate;
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
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.common.rest.models.PageMetadata;
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

  // region Get Task History Events

  @Test
  void should_GetAllHistoryEvents_When_UrlIsVisited() {
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(45);
  }

  @Test
  void should_GenerateSelfLink_When_TaskHistoryEventsAreRequested() {
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isPresent()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(HistoryRestEndpoints.URL_HISTORY_EVENTS);
  }

  @Test
  void should_ContainQueryParametersInComputedSelfLink_When_TaskHistoryEventsAreRequested() {
    String parameters = "?domain=DOMAIN_A&domain=DOMAIN_B";
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS + parameters),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isPresent()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(HistoryRestEndpoints.URL_HISTORY_EVENTS + parameters);
  }

  @Test
  void should_SortEventsByBusinessProcessIdDesc_When_SortByAndOrderQueryParametersAreDeclared() {
    String parameters = "?sort-by=BUSINESS_PROCESS_ID&order=DESCENDING";
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS + parameters),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent())
        .extracting(TaskHistoryEventRepresentationModel::getBusinessProcessId)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void should_ApplyBusinessProcessIdFilter_When_QueryParameterIsProvided() {
    String parameters = "?business-process-id=BPI:01";
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS + parameters),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent())
        .extracting(TaskHistoryEventRepresentationModel::getTaskHistoryId)
        .containsExactly("THI:000000000000000000000000000000000000");
  }

  @Test
  @Disabled("no solution for this")
  void should_ReturnBadStatusErrorCode_When_InvalidQueryParameterIsUsed() {
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
  void should_ReturnBadStatusErrorCode_When_CreatedQueryParameterIsWrongFormatted() {
    String currentTime = "wrong format";
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
  void should_ApplyCreatedFilter_When_QueryParametersAreProvided() {
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
  void should_ApplyPaging_When_PagingIsRequested() {
    String expectedFirstPageParameters = "?sort-by=TASK_HISTORY_EVENT_ID&page-size=3&page=1";
    String expectedLastPageParameters = "?sort-by=TASK_HISTORY_EVENT_ID&page-size=3&page=15";
    String expectedPrevPageParameters = "?sort-by=TASK_HISTORY_EVENT_ID&page-size=3&page=2";
    String expectedNextPageParameters = "?sort-by=TASK_HISTORY_EVENT_ID&page-size=3&page=4";

    String parameters = "?sort-by=TASK_HISTORY_EVENT_ID&page-size=3&page=3";

    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS + parameters),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent())
        .extracting(TaskHistoryEventRepresentationModel::getTaskHistoryId)
        .containsExactly(
            "THI:000000000000000000000000000000000006",
            "THI:000000000000000000000000000000000007",
            "THI:000000000000000000000000000000000008");

    assertThat(response.getBody().getPageMetadata()).isEqualTo(new PageMetadata(3, 45, 15, 3));

    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST))
        .isPresent()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(HistoryRestEndpoints.URL_HISTORY_EVENTS + expectedFirstPageParameters);
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST))
        .isPresent()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(HistoryRestEndpoints.URL_HISTORY_EVENTS + expectedLastPageParameters);
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV))
        .isPresent()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(HistoryRestEndpoints.URL_HISTORY_EVENTS + expectedPrevPageParameters);
    assertThat(response.getBody().getLink(IanaLinkRelations.NEXT))
        .isPresent()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(HistoryRestEndpoints.URL_HISTORY_EVENTS + expectedNextPageParameters);
  }

  // endregion

  // region Get Specific Task History Event

  @Test
  void should_GenerateSelfLink_When_SpecificTaskHistoryEventIsRequested() {
    String id = "THI:000000000000000000000000000000000000";
    String expectedUrl =
        UriComponentsBuilder.fromPath(HistoryRestEndpoints.URL_HISTORY_EVENTS_ID)
            .buildAndExpand(id)
            .toUriString();

    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS_ID, id),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isPresent()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(expectedUrl);
  }

  @Test
  void should_GetSpecificTaskHistoryEventWithDetails_When_SingleEventIsQueried() {
    ResponseEntity<TaskHistoryEventRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(
                HistoryRestEndpoints.URL_HISTORY_EVENTS_ID,
                "THI:000000000000000000000000000000000000"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskHistoryEventRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDetails()).isNotNull();
  }

  // endregion
}
