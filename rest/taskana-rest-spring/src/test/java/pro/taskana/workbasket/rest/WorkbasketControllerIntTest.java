package pro.taskana.workbasket.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.rest.models.DistributionTargetsCollectionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemCollectionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryPagedRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

/** Test WorkbasketController. */
@TaskanaSpringBootTest
class WorkbasketControllerIntTest {

  private final RestHelper restHelper;

  @Autowired
  WorkbasketControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testGetWorkbasket() throws UnsupportedEncodingException {
    final String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI%3A100000000000000000000000000000000006");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketRepresentationModel> response =
        TEMPLATE.exchange(
            URLDecoder.decode(url, StandardCharsets.UTF_8),
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(WorkbasketRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).contains(Link.of(url));
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
  }

  @Test
  void testGetAllWorkbaskets() {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET);
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(WorkbasketSummaryPagedRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetAllWorkbasketsBusinessAdminHasOpenPermission() {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + "?required-permission=OPEN";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(WorkbasketSummaryPagedRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllWorkbasketsKeepingFilters() {
    String parameters = "?type=PERSONAL&sort-by=KEY&order=DESCENDING";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + parameters;
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(WorkbasketSummaryPagedRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith(parameters);
  }

  @Test
  void testUpdateWorkbasketWithConcurrentModificationShouldThrowException() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000001");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketRepresentationModel> initialWorkbasketResourceRequestResponse =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(WorkbasketRepresentationModel.class));
    WorkbasketRepresentationModel workbasketRepresentationModel =
        initialWorkbasketResourceRequestResponse.getBody();
    assertThat(workbasketRepresentationModel).isNotNull();

    workbasketRepresentationModel.setKey("GPK_KSC");
    workbasketRepresentationModel.setDomain("DOMAIN_A");
    workbasketRepresentationModel.setType(WorkbasketType.PERSONAL);
    workbasketRepresentationModel.setName("was auch immer");
    workbasketRepresentationModel.setOwner("Joerg");
    workbasketRepresentationModel.setModified(Instant.now());
    HttpEntity<WorkbasketRepresentationModel> auth2 =
        new HttpEntity<>(
            workbasketRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.PUT,
                auth2,
                ParameterizedTypeReference.<WorkbasketRepresentationModel>forType(
                    WorkbasketRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void testUpdateWorkbasketOfNonExistingWorkbasketShouldThrowException() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100004857400039500000999999999999999");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("businessadmin"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.<WorkbasketRepresentationModel>forType(
                    WorkbasketRepresentationModel.class));

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetSecondPageSortedByKey() {
    String parameters = "?sort-by=KEY&order=DESCENDING&page-size=5&page=2";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + parameters;
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(WorkbasketSummaryPagedRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(5);
    assertThat(response.getBody().getContent().iterator().next().getKey()).isEqualTo("USER-1-1");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.NEXT)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith(parameters);
  }

  @Test
  void testMarkWorkbasketForDeletionAsBusinessAdminWithoutExplicitReadPermission() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000005");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("businessadmin"));

    ResponseEntity<?> response = TEMPLATE.exchange(url, HttpMethod.DELETE, auth, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void statusCode423ShouldBeReturnedIfWorkbasketContainsNonCompletedTasks() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000004");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("businessadmin"));

    ThrowingCallable call = () -> TEMPLATE.exchange(url, HttpMethod.DELETE, auth, Void.class);

    assertThatThrownBy(call)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.LOCKED);
  }

  @Test
  void testRemoveWorkbasketAsDistributionTarget() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
            "WBI:100000000000000000000000000000000007");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<?> response = TEMPLATE.exchange(url, HttpMethod.DELETE, auth, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    String url2 =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
            "WBI:100000000000000000000000000000000002");
    ResponseEntity<DistributionTargetsCollectionRepresentationModel> response2 =
        TEMPLATE.exchange(
            url2,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(
                DistributionTargetsCollectionRepresentationModel.class));

    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response2.getBody()).isNotNull();
    assertThat(response2.getBody().getContent())
        .extracting(WorkbasketSummaryRepresentationModel::getWorkbasketId)
        .doesNotContain("WBI:100000000000000000000000000000000007");
  }

  @Test
  void testGetWorkbasketAccessItems() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS,
            "WBI:100000000000000000000000000000000005");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketAccessItemCollectionRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(
                WorkbasketAccessItemCollectionRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void testGetWorkbasketDistributionTargets() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
            "WBI:100000000000000000000000000000000001");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<DistributionTargetsCollectionRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(
                DistributionTargetsCollectionRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFilterParams() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_WORKBASKET)
            + "?type=PERSONAL"
            + "&illegalParam=illegal"
            + "&anotherIllegalParam=stillIllegal"
            + "&sort-by=KEY&order=DESCENDING&page-size=5&page=2";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.forType(
                    WorkbasketSummaryPagedRepresentationModel.class));

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining(
            "Unknown request parameters found: [anotherIllegalParam, illegalParam]")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
