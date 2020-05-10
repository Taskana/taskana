package pro.taskana.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.DistributionTargetListResource;
import pro.taskana.rest.resource.DistributionTargetResource;
import pro.taskana.rest.resource.WorkbasketAccessItemListResource;
import pro.taskana.rest.resource.WorkbasketResource;
import pro.taskana.rest.resource.WorkbasketSummaryListResource;
import pro.taskana.workbasket.api.WorkbasketType;

/** Test WorkbasketController. */
@TaskanaSpringBootTest
class WorkbasketControllerIntTest {

  private static RestTemplate template;
  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testGetWorkbasket() {
    ResponseEntity<WorkbasketResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000006"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketResource.class));
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType().toString())
        .isEqualTo(MediaTypes.HAL_JSON_VALUE);
  }

  @Test
  void testGetAllWorkbaskets() {
    ResponseEntity<WorkbasketSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetAllWorkbasketsBusinessAdminHasOpenPermission() {
    ResponseEntity<WorkbasketSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET) + "?required-permission=OPEN",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void testGetAllWorkbasketsKeepingFilters() {
    String parameters = "?type=PERSONAL&sort-by=key&order=desc";
    ResponseEntity<WorkbasketSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
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
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(Mapping.URL_WORKBASKET) + "?invalid=PERSONAL",
              HttpMethod.GET,
              restHelper.defaultRequest(),
              ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
        };
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("[invalid]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testUpdateWorkbasketWithConcurrentModificationShouldThrowException() {

    String workbasketId = "WBI:100000000000000000000000000000000001";

    final ObjectMapper mapper = new ObjectMapper();

    ResponseEntity<WorkbasketResource> initialWorkbasketResourceRequestResponse =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET_ID, workbasketId),
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeaders()),
            ParameterizedTypeReference.forType(WorkbasketResource.class));

    WorkbasketResource workbasketResource = initialWorkbasketResourceRequestResponse.getBody();

    workbasketResource.setKey("GPK_KSC");
    workbasketResource.setDomain("DOMAIN_A");
    workbasketResource.setType(WorkbasketType.PERSONAL);
    workbasketResource.setName("was auch immer");
    workbasketResource.setOwner("Joerg");
    workbasketResource.setModified(String.valueOf(Instant.now()));

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(Mapping.URL_WORKBASKET_ID, workbasketId),
              HttpMethod.PUT,
              new HttpEntity<>(
                  mapper.writeValueAsString(workbasketResource), restHelper.getHeaders()),
              ParameterizedTypeReference.forType(WorkbasketResource.class));
        };
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void testUpdateWorkbasketOfNonExistingWorkbasketShouldThrowException() {

    String workbasketId = "WBI:100004857400039500000999999999999999";

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(Mapping.URL_WORKBASKET_ID, workbasketId),
              HttpMethod.GET,
              new HttpEntity<String>(restHelper.getHeaders()),
              ParameterizedTypeReference.forType(WorkbasketResource.class));
        };
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.NOT_FOUND);
  }

  @Test
  void testGetSecondPageSortedByKey() {

    String parameters = "?sort-by=key&order=desc&page=2&page-size=5";
    ResponseEntity<WorkbasketSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
    assertThat(response.getBody().getContent()).hasSize(5);
    assertThat(response.getBody().getContent().iterator().next().getKey()).isEqualTo("USER_1_1");
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
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET_ID, workbasketID),
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
            template.exchange(
                restHelper.toUrl(Mapping.URL_WORKBASKET_ID, workbasketWithNonCompletedTasks),
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
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_WORKBASKET_ID_DISTRIBUTION, "WBI:100000000000000000000000000000000007"),
            HttpMethod.DELETE,
            restHelper.defaultRequest(),
            Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ResponseEntity<DistributionTargetListResource> response2 =
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_WORKBASKET_ID_DISTRIBUTION, "WBI:100000000000000000000000000000000002"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(DistributionTargetListResource.class));
    assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(
            response2.getBody().getContent().stream()
                .map(DistributionTargetResource::getWorkbasketId)
                .noneMatch("WBI:100000000000000000000000000000000007"::equals))
        .isTrue();
  }

  @Test
  void testGetWorkbasketAccessItems() {
    ResponseEntity<WorkbasketAccessItemListResource> response =
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_WORKBASKET_ID_ACCESSITEMS, "WBI:100000000000000000000000000000000005"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType().toString())
        .isEqualTo(MediaTypes.HAL_JSON_VALUE);
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void testGetWorkbasketDistributionTargets() {
    ResponseEntity<DistributionTargetListResource> response =
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_WORKBASKET_ID_DISTRIBUTION, "WBI:100000000000000000000000000000000001"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(DistributionTargetListResource.class));
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType().toString())
        .isEqualTo(MediaTypes.HAL_JSON_VALUE);
    assertThat(response.getBody().getContent()).hasSize(4);
  }
}
