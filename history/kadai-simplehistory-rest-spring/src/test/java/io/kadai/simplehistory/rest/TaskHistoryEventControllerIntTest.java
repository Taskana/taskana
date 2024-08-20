package io.kadai.simplehistory.rest;

import static io.kadai.rest.test.RestHelper.TEMPLATE;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.common.rest.models.PageMetadata;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.rest.test.RestHelper;
import io.kadai.simplehistory.rest.models.TaskHistoryEventPagedRepresentationModel;
import io.kadai.simplehistory.rest.models.TaskHistoryEventRepresentationModel;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.util.UriComponentsBuilder;

/** Controller for integration test. */
@KadaiSpringBootTest
class TaskHistoryEventControllerIntTest {

  private static final ParameterizedTypeReference<TaskHistoryEventPagedRepresentationModel>
      TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE =
          new ParameterizedTypeReference<TaskHistoryEventPagedRepresentationModel>() {};

  private static final ParameterizedTypeReference<TaskHistoryEventRepresentationModel>
      TASK_HISTORY_EVENT_REPRESENTATION_MODEL_TYPE =
          new ParameterizedTypeReference<TaskHistoryEventRepresentationModel>() {};

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
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(45);
  }

  @Test
  void should_GenerateSelfLink_When_TaskHistoryEventsAreRequested() {
    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS),
            HttpMethod.GET,
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);
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
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);
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
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);
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
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent())
        .extracting(TaskHistoryEventRepresentationModel::getTaskHistoryId)
        .containsExactly("THI:000000000000000000000000000000000000");
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
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
                TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining(currentTime)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
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
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);
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
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);

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
  void should_GenerateSelfLink_When_SpecificTaskHistoryEventIsRequested()
      throws UnsupportedEncodingException {
    String id = "THI:000000000000000000000000000000000000";
    String expectedUrl =
        UriComponentsBuilder.fromPath(HistoryRestEndpoints.URL_HISTORY_EVENTS_ID)
            .buildAndExpand(URLEncoder.encode(id, StandardCharsets.UTF_8))
            .toUriString();

    ResponseEntity<TaskHistoryEventPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS_ID, id),
            HttpMethod.GET,
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);
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
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            TASK_HISTORY_EVENT_REPRESENTATION_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getDetails()).isNotNull();
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFilterParams() {

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(HistoryRestEndpoints.URL_HISTORY_EVENTS)
                    + "?domain=DOMAIN_A"
                    + "&illegalParam=illegal"
                    + "&anotherIllegalParam=stillIllegal"
                    + "&sort-by=TASK_ID&order=DESCENDING&page-size=5&page=2",
                HttpMethod.GET,
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
                TASK_HISTORY_EVENT_PAGED_REPRESENTATION_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining(
            "Unknown request parameters found: [anotherIllegalParam, illegalParam]")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  // endregion
}
