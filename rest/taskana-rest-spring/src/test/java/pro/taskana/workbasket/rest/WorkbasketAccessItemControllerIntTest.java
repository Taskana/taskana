package pro.taskana.workbasket.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

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
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS);
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetWorkbasketAccessItemsKeepingFilters() {
    String parameters =
        "?sort-by=WORKBASKET_KEY&order=ASCENDING&page-size=9&access-id=user-1-1&page=1";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + parameters;
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isNotEmpty()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(parameters);
  }

  @Test
  void testGetSecondPageSortedByWorkbasketKey() {
    String parameters =
        "?sort-by=WORKBASKET_KEY&order=ASCENDING&page=2&page-size=9&access-id=user-1-1";
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + parameters;
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<WorkbasketAccessItemPagedRepresentationModel> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().iterator().next().getAccessId())
        .isEqualTo("user-1-1");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF))
        .isNotEmpty()
        .get()
        .extracting(Link::getHref)
        .asString()
        .endsWith(parameters);
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotEmpty();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotEmpty();
    assertThat(response.getBody().getPageMetadata().getSize()).isEqualTo(9);
    assertThat(response.getBody().getPageMetadata().getTotalElements()).isEqualTo(1);
    assertThat(response.getBody().getPageMetadata().getTotalPages()).isEqualTo(1);
    assertThat(response.getBody().getPageMetadata().getNumber()).isEqualTo(1);
  }

  @Test
  void should_DeleteAllAccessItemForUser_ifValidAccessIdOfUserIsSupplied() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS) + "?access-id=teamlead-2";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<Void> response =
        TEMPLATE.exchange(
            url, HttpMethod.DELETE, auth, ParameterizedTypeReference.forType(Void.class));
    assertThat(response.getBody()).isNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFilterParams() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
            + "?access-id=teamlead-2"
            + "&illegalParam=illegal"
            + "&anotherIllegalParam=stillIllegal"
            + "&sort-by=WORKBASKET_KEY&order=DESCENDING&page-size=5&page=2";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url, HttpMethod.GET, auth, WORKBASKET_ACCESS_ITEM_PAGED_REPRESENTATION_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining(
            "Unknown request parameters found: [anotherIllegalParam, illegalParam]")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
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
          String url =
              restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
                  + "?access-id="
                  + accessId;
          HttpEntity<Object> auth =
              new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

          ThrowingCallable httpCall =
              () ->
                  TEMPLATE.exchange(
                      url, HttpMethod.DELETE, auth, ParameterizedTypeReference.forType(Void.class));
          assertThatThrownBy(httpCall)
              .isInstanceOf(HttpStatusCodeException.class)
              .extracting(HttpStatusCodeException.class::cast)
              .extracting(HttpStatusCodeException::getStatusCode)
              .isEqualTo(HttpStatus.BAD_REQUEST);
        };

    return DynamicTest.stream(accessIds.iterator(), s -> String.format("for user '%s'", s), test);
  }
}
