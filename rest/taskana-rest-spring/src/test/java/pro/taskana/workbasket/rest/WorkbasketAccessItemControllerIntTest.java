package pro.taskana.workbasket.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemPagedRepresentationModel;

/** Test WorkbasketAccessItemController. */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TaskanaSpringBootTest
class WorkbasketAccessItemControllerIntTest {

  private static final ParameterizedTypeReference<WorkbasketAccessItemPagedRepresentationModel>
      WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE =
          new ParameterizedTypeReference<WorkbasketAccessItemPagedRepresentationModel>() {};

  private final RestHelper restHelper;

  @Autowired
  WorkbasketAccessItemControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testGetAllWorkbasketAccessItems() {
    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetWorkbasketAccessItemsKeepingFilters() {
    String parameters =
        "?sort-by=WORKBASKET_KEY&order=ASCENDING&page-size=9&access-id=user-1-1&page=1";
    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE);
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
  @Disabled("currently no solution for this.")
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
                    + "?sort-by=WORKBASKET_KEY&order=ASCENDING&page=1&page-size=9&invalid=user-1-1",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("[invalid]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetSecondPageSortedByWorkbasketKey() {
    String parameters =
        "?sort-by=WORKBASKET_KEY&order=ASCENDING&page=2&page-size=9&access-id=user-1-1";
    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
            HttpMethod.GET,
            restHelper.defaultRequest(),
            WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE);
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
    assertThat(response.getBody().getPageMetadata().getSize()).isEqualTo(9);
    assertThat(response.getBody().getPageMetadata().getTotalElements()).isEqualTo(1);
    assertThat(response.getBody().getPageMetadata().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getPageMetadata().getNumber()).isEqualTo(1);
  }

  @Test
  void should_DeleteAllAccessItemForUser_ifValidAccessIdOfUserIsSupplied() {

    String parameters = "?access-id=teamlead-2";
    ResponseEntity<Void> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
            HttpMethod.DELETE,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(Void.class));
    assertThat(response.getBody()).isNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFilterParams() {

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
                    + "?access-id=teamlead-2"
                    + "&illegalParam=illegal"
                    + "&anotherIllegalParam=stillIllegal"
                    + "&sort-by=WORKBASKET_KEY&order=DESCENDING&page-size=5&page=2",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining(
            "Unkown request parameters found: [anotherIllegalParam, illegalParam]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @TestFactory
  Stream<DynamicTest> should_ReturnBadRequest_When_AccessIdIsInvalid() {
    List<String> accessIds =
        List.of(
            "cn=organisationseinheit ksc,cn=organisation,ou=test,o=taskana",
            "cn=monitor-users,cn=groups,ou=test,o=taskana",
            "user-1");

    ThrowingConsumer<String> test =
        accessId -> {
          String parameters = "?access-id=" + accessId;
          ThrowingCallable httpCall =
              () ->
                  TEMPLATE.exchange(
                      restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + parameters,
                      HttpMethod.DELETE,
                      restHelper.defaultRequest(),
                      ParameterizedTypeReference.forType(Void.class));
          assertThatThrownBy(httpCall)
              .isInstanceOf(HttpClientErrorException.class)
              .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
              .isEqualTo(HttpStatus.BAD_REQUEST);
        };
    return DynamicTest.stream(accessIds.iterator(), s -> String.format("for user '%s'", s), test);
  }
}
