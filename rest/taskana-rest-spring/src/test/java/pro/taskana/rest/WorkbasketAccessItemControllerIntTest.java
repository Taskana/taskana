package pro.taskana.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.WorkbasketAccessItemListResource;

/** Test WorkbasketAccessItemController. */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@TaskanaSpringBootTest
class WorkbasketAccessItemControllerIntTest {

  private static RestTemplate template;
  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testGetAllWorkbasketAccessItems() {
    ResponseEntity<WorkbasketAccessItemListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETACCESSITEMS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetWorkbasketAccessItemsKeepingFilters() {
    String parameters = "?sort-by=workbasket-key&order=asc&page=1&page-size=9&access-ids=user_1_1";
    ResponseEntity<WorkbasketAccessItemListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETACCESSITEMS) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
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
              restHelper.toUrl(Mapping.URL_WORKBASKETACCESSITEMS)
                  + "?sort-by=workbasket-key&order=asc&page=1&page-size=9&invalid=user_1_1",
              HttpMethod.GET,
              restHelper.defaultRequest(),
              ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
        };
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("[invalid]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetSecondPageSortedByWorkbasketKey() {
    String parameters = "?sort-by=workbasket-key&order=asc&page=2&page-size=9&access-ids=user_1_1";
    ResponseEntity<WorkbasketAccessItemListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETACCESSITEMS) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().iterator().next().getAccessId())
        .isEqualTo("user_1_1");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(
            response
                .getBody()
                .getRequiredLink(IanaLinkRelations.SELF)
                .getHref()
                .endsWith(parameters))
        .isTrue();
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getMetadata().getSize()).isEqualTo(9);
    assertThat(response.getBody().getMetadata().getTotalElements()).isEqualTo(1);
    assertThat(response.getBody().getMetadata().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getMetadata().getNumber()).isEqualTo(1);
  }

  @Test
  void testRemoveWorkbasketAccessItemsOfUser() {

    String parameters = "?access-id=user_1_1";
    ResponseEntity<Void> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETACCESSITEMS) + parameters,
            HttpMethod.DELETE,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(Void.class));
    assertThat(response.getBody()).isNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void testGetBadRequestIfTryingToDeleteAccessItemsForGroup() {
    String parameters = "?access-id=cn=DevelopersGroup,ou=groups,o=TaskanaTest";
    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(Mapping.URL_WORKBASKETACCESSITEMS) + parameters,
              HttpMethod.DELETE,
              restHelper.defaultRequest(),
              ParameterizedTypeReference.forType(Void.class));
        };
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
