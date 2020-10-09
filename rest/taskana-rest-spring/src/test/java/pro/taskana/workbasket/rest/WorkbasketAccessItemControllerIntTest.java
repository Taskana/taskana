package pro.taskana.workbasket.rest;

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

import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemRepresentationModel;

/** Test WorkbasketAccessItemController. */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@TaskanaSpringBootTest
class WorkbasketAccessItemControllerIntTest {

  private static final ParameterizedTypeReference<
          TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>>
      WORKBASKET_ACCESS_ITEM_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<
              TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>>() {};
  private static RestTemplate template;
  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testGetAllWorkbasketAccessItems() {
    ResponseEntity<TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET_ACCESS_ITEMS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            WORKBASKET_ACCESS_ITEM_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetWorkbasketAccessItemsKeepingFilters() {
    String parameters = "?sort-by=workbasket-key&order=asc&page-size=9&access-ids=user-1-1&page=1";
    ResponseEntity<TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            WORKBASKET_ACCESS_ITEM_PAGE_MODEL_TYPE);
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
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    ThrowingCallable httpCall =
        () ->
            template.exchange(
                restHelper.toUrl(Mapping.URL_WORKBASKET_ACCESS_ITEMS)
                    + "?sort-by=workbasket-key&order=asc&page=1&page-size=9&invalid=user-1-1",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                WORKBASKET_ACCESS_ITEM_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("[invalid]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetSecondPageSortedByWorkbasketKey() {
    String parameters = "?sort-by=workbasket-key&order=asc&page=2&page-size=9&access-ids=user-1-1";
    ResponseEntity<TaskanaPagedModel<WorkbasketAccessItemRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            WORKBASKET_ACCESS_ITEM_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().iterator().next().getAccessId())
        .isEqualTo("user-1-1");
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
  void should_DeleteAllAccessItemForUser_ifValidAccessIdOfUserIsSupplied() {

    String parameters = "?access-id=teamlead-2";
    ResponseEntity<Void> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
            HttpMethod.DELETE,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(Void.class));
    assertThat(response.getBody()).isNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void should_ReturnBadRequest_ifAccessIdIsSubStringOfUser() {
    String parameters = "?access-id=user-1";
    ThrowingCallable httpCall =
        () ->
            template.exchange(
                restHelper.toUrl(Mapping.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
                HttpMethod.DELETE,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(Void.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnBadRequest_ifAccessIdIsGroup() {
    String parameters = "?access-id=cn=monitor-users,cn=groups,ou=test,o=taskana";
    ThrowingCallable httpCall =
        () ->
            template.exchange(
                restHelper.toUrl(Mapping.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
                HttpMethod.DELETE,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(Void.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnBadRequest_ifAccessIdIsOrganizationalGroup() {
    String parameters = "?access-id=cn=organisationseinheit ksc,cn=organisation,ou=test,o=taskana";
    ThrowingCallable httpCall =
        () ->
            template.exchange(
                restHelper.toUrl(Mapping.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
                HttpMethod.DELETE,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(Void.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
