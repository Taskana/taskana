package pro.taskana.workbasket.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.time.Instant;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.web.client.HttpClientErrorException;

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
  void testGetWorkbasket() {
    final String url =
        restHelper.toUrl(
            RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000006");
    ResponseEntity<WorkbasketRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isEqualTo(Optional.of(Link.of(url)));
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
  }

  @Test
  void testGetAllWorkbaskets() {
    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetAllWorkbasketsBusinessAdminHasOpenPermission() {
    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + "?required-permission=OPEN",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllWorkbasketsKeepingFilters() {
    String parameters = "?type=PERSONAL&sort-by=KEY&order=DESCENDING";
    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(
            response
                .getBody()
                .getRequiredLink(IanaLinkRelations.SELF)
                .getHref()
                .endsWith(parameters))
        .isTrue();
  }

  @Test
  @Disabled("no solution for this yet")
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + "?invalid=PERSONAL",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(
                    WorkbasketSummaryPagedRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("[invalid]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testUpdateWorkbasketWithConcurrentModificationShouldThrowException() {

    String workbasketId = "WBI:100000000000000000000000000000000001";

    ResponseEntity<WorkbasketRepresentationModel> initialWorkbasketResourceRequestResponse =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ID, workbasketId),
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
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

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ID, workbasketId),
                HttpMethod.PUT,
                new HttpEntity<>(workbasketRepresentationModel, restHelper.getHeadersTeamlead_1()),
                ParameterizedTypeReference.forType(WorkbasketRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void testUpdateWorkbasketOfNonExistingWorkbasketShouldThrowException() {

    String workbasketId = "WBI:100004857400039500000999999999999999";

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ID, workbasketId),
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersBusinessAdmin()),
                ParameterizedTypeReference.forType(WorkbasketRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetSecondPageSortedByKey() {

    String parameters = "?sort-by=KEY&order=DESCENDING&page-size=5&page=2";
    ResponseEntity<WorkbasketSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryPagedRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(5);
    assertThat(response.getBody().getContent().iterator().next().getKey()).isEqualTo("USER-1-1");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.NEXT)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
    assertThat(
            response
                .getBody()
                .getRequiredLink(IanaLinkRelations.SELF)
                .getHref()
                .endsWith(parameters))
        .isTrue();
  }

  @Test
  void testMarkWorkbasketForDeletionAsBusinessAdminWithoutExplicitReadPermission() {

    String workbasketID = "WBI:100000000000000000000000000000000005";

    ResponseEntity<?> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ID, workbasketID),
            HttpMethod.DELETE,
            new HttpEntity<>(restHelper.getHeadersBusinessAdmin()),
            Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
  }

  @Test
  void statusCode423ShouldBeReturnedIfWorkbasketContainsNonCompletedTasks() {
    String workbasketWithNonCompletedTasks = "WBI:100000000000000000000000000000000004";

    ThrowingCallable call =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ID, workbasketWithNonCompletedTasks),
                HttpMethod.DELETE,
                new HttpEntity<>(restHelper.getHeadersBusinessAdmin()),
                Void.class);
    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.LOCKED);
  }

  @Test
  void testRemoveWorkbasketAsDistributionTarget() {
    ResponseEntity<?> response =
        TEMPLATE.exchange(
            restHelper.toUrl(
                RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
                "WBI:100000000000000000000000000000000007"),
            HttpMethod.DELETE,
            restHelper.defaultRequest(),
            Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<DistributionTargetsCollectionRepresentationModel> response2 =
        TEMPLATE.exchange(
            restHelper.toUrl(
                RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
                "WBI:100000000000000000000000000000000002"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
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
    ResponseEntity<WorkbasketAccessItemCollectionRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(
                RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS,
                "WBI:100000000000000000000000000000000005"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(
                WorkbasketAccessItemCollectionRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void testGetWorkbasketDistributionTargets() {
    ResponseEntity<DistributionTargetsCollectionRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(
                RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
                "WBI:100000000000000000000000000000000001"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(
                DistributionTargetsCollectionRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
    assertThat(response.getBody().getContent()).hasSize(4);
  }
}
