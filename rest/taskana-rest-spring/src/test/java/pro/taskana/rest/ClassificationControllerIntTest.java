package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.ClassificationSummaryListResource;
import pro.taskana.rest.resource.ClassificationSummaryResource;

/**
 * Test ClassificationController.
 *
 * @author bbr
 */
@TaskanaSpringBootTest
class ClassificationControllerIntTest {

  static RestTemplate template = RestHelper.getRestTemplate();
  @Autowired RestHelper restHelper;

  @Test
  void testGetAllClassifications() {
    ResponseEntity<ClassificationSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
  }

  @Test
  void testGetAllClassificationsFilterByCustomAttribute() {
    ResponseEntity<ClassificationSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS) + "?domain=DOMAIN_A&custom-1-like=RVNR",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertEquals(13, response.getBody().getContent().size());
  }

  @Test
  void testGetAllClassificationsKeepingFilters() {
    ResponseEntity<ClassificationSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS)
                + "?domain=DOMAIN_A&sort-by=key&order=asc",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertTrue(
        response
            .getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/api/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc"));
    assertEquals(17, response.getBody().getContent().size());
    assertEquals("A12", response.getBody().getContent().iterator().next().key);
  }

  @Test
  void testGetSecondPageSortedByKey() {
    ResponseEntity<ClassificationSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS)
                + "?domain=DOMAIN_A&sort-by=key&order=asc&page=2&page-size=5",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
    assertEquals(5, response.getBody().getContent().size());
    assertEquals("L1050", response.getBody().getContent().iterator().next().key);
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertTrue(
        response
            .getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(
                "/api/v1/classifications?"
                    + "domain=DOMAIN_A&sort-by=key&order=asc&page=2&page-size=5"));
    assertNotNull(response.getBody().getLink(Link.REL_FIRST));
    assertNotNull(response.getBody().getLink(Link.REL_LAST));
    assertNotNull(response.getBody().getLink(Link.REL_NEXT));
    assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
  }

  @Test
  @DirtiesContext
  void testCreateClassification() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\","
            + "\"name\":\"new classification\",\"type\":\"TASK\"}";

    ResponseEntity<ClassificationResource> responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class));

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

    newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS_2\","
            + "\"name\":\"new classification\",\"type\":\"TASK\"}";

    responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class));

    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithParentId() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_B\",\"key\":\"NEW_CLASS_P1\","
            + "\"name\":\"new classification\",\"type\":\"TASK\","
            + "\"parentId\":\"CLI:200000000000000000000000000000000015\"}";

    ResponseEntity<ClassificationResource> responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class));

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
  }

  @Test
  @DirtiesContext
  @SuppressWarnings("checkstyle:LineLength")
  void testCreateClassificationWithParentKey() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_B\","
            + "\"key\":\"NEW_CLASS_P2\",\"name\":\"new classification\","
            + "\"type\":\"TASK\",\"parentKey\":\"T2100\"}";

    ResponseEntity<ClassificationResource> responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class));

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithParentKeyInDomain_aShouldCreateAClassificationInRootDomain()
      throws IOException {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\","
            + "\"key\":\"NEW_CLASS_P2\",\"name\":\"new classification\","
            + "\"type\":\"TASK\",\"parentKey\":\"T2100\"}";

    ResponseEntity<ClassificationResource> responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class));

    assertNotNull(responseEntity);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());

    ResponseEntity<ClassificationSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    boolean foundClassificationCreated = false;
    for (ClassificationSummaryResource classification : response.getBody().getContent()) {
      if ("NEW_CLASS_P2".equals(classification.getKey())
          && "".equals(classification.getDomain())
          && "T2100".equals(classification.getParentKey())) {
        foundClassificationCreated = true;
      }
    }

    assertEquals(true, foundClassificationCreated);
  }

  @Test
  @DirtiesContext
  void testReturn400IfCreateClassificationWithIncompatibleParentIdAndKey() throws IOException {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_B\","
            + "\"key\":\"NEW_CLASS_P3\",\"name\":\"new classification\","
            + "\"type\":\"TASK\",\"parentId\":\"CLI:200000000000000000000000000000000015\","
            + "\"parentKey\":\"T2000\"}";

    HttpClientErrorException e =
        Assertions.assertThrows(
            HttpClientErrorException.class,
            () ->
                template.exchange(
                    restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
                    HttpMethod.POST,
                    new HttpEntity<>(newClassification, restHelper.getHeaders()),
                    ParameterizedTypeReference.forType(ClassificationResource.class)));

    assertNotNull(e);
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithClassificationIdReturnsError400() throws IOException {
    String newClassification =
        "{\"classificationId\":\"someId\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\","
            + "\"name\":\"new classification\",\"type\":\"TASK\"}";

    HttpClientErrorException e =
        Assertions.assertThrows(
            HttpClientErrorException.class,
            () ->
                template.exchange(
                    restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
                    HttpMethod.POST,
                    new HttpEntity<>(newClassification, restHelper.getHeaders()),
                    ParameterizedTypeReference.forType(ClassificationResource.class)));

    assertNotNull(e);
    assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
  }

  @Test
  void testGetClassificationWithSpecialCharacter() {

    HttpEntity<String> request = new HttpEntity<String>(restHelper.getHeadersAdmin());
    ResponseEntity<ClassificationSummaryResource> response =
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000009"),
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(ClassificationSummaryResource.class));
    assertEquals("Zustimmungserklärung", response.getBody().name);
  }

  @Test
  @DirtiesContext
  void testDeleteClassification() {
    HttpEntity<String> request = new HttpEntity<String>(restHelper.getHeaders());

    ResponseEntity<ClassificationSummaryResource> response =
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_CLASSIFICATIONS_ID, "CLI:200000000000000000000000000000000004"),
            HttpMethod.DELETE,
            request,
            ParameterizedTypeReference.forType(ClassificationSummaryResource.class));
    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

    assertThrows(
        HttpClientErrorException.class,
        () -> {
          template.exchange(
              restHelper.toUrl(
                  Mapping.URL_CLASSIFICATIONS_ID, "CLI:200000000000000000000000000000000004"),
              HttpMethod.GET,
              request,
              ParameterizedTypeReference.forType(ClassificationSummaryResource.class));
        });
  }
}
