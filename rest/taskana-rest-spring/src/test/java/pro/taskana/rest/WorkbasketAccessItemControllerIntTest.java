package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
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
    template = RestHelper.getRestTemplate();
  }

  @Test
  void testGetAllWorkbasketAccessItems() {
    ResponseEntity<WorkbasketAccessItemListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETACCESSITEMS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
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
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertTrue(response.getBody().getLink(Link.REL_SELF).getHref().endsWith(parameters));
  }

  @Test
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    try {
      template.exchange(
          restHelper.toUrl(Mapping.URL_WORKBASKETACCESSITEMS)
              + "?sort-by=workbasket-key&order=asc&page=1&page-size=9&invalid=user_1_1",
          HttpMethod.GET,
          restHelper.defaultRequest(),
          ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
      fail();
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
      assertTrue(e.getResponseBodyAsString().contains("[invalid]"));
    }
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
    assertEquals(1, response.getBody().getContent().size());
    assertEquals("user_1_1", response.getBody().getContent().iterator().next().accessId);
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertTrue(response.getBody().getLink(Link.REL_SELF).getHref().endsWith(parameters));
    assertNotNull(response.getBody().getLink(Link.REL_FIRST));
    assertNotNull(response.getBody().getLink(Link.REL_LAST));
    assertEquals(9, response.getBody().getMetadata().getSize());
    assertEquals(1, response.getBody().getMetadata().getTotalElements());
    assertEquals(1, response.getBody().getMetadata().getTotalPages());
    assertEquals(1, response.getBody().getMetadata().getNumber());
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
    assertNull(response.getBody());
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
  }

  @Test
  void testGetBadRequestIfTryingToDeleteAccessItemsForGroup() {
    String parameters = "?access-id=cn=DevelopersGroup,ou=groups,o=TaskanaTest";

    HttpClientErrorException e =
        Assertions.assertThrows(
            HttpClientErrorException.class,
            () ->
                template.exchange(
                    restHelper.toUrl(Mapping.URL_WORKBASKETACCESSITEMS) + parameters,
                    HttpMethod.DELETE,
                    restHelper.defaultRequest(),
                    ParameterizedTypeReference.forType(Void.class)));

    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
  }
}
