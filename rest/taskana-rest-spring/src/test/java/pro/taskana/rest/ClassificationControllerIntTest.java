package pro.taskana.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
  void testGetClassification() {
    ResponseEntity<ClassificationResource> response =
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000002"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
  }

  @Test
  void testGetAllClassifications() {
    ResponseEntity<ClassificationSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
  }

  @Test
  void testGetAllClassificationsFilterByCustomAttribute() {
    ResponseEntity<ClassificationSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS) + "?domain=DOMAIN_A&custom-1-like=RVNR",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent().size()).isEqualTo(13);
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
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_SELF).getHref())
        .endsWith("/api/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc");
    assertThat(response.getBody().getContent().size()).isEqualTo(17);
    assertThat(response.getBody().getContent().iterator().next().key).isEqualTo("A12");
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
    assertThat(response.getBody().getContent().size()).isEqualTo(5);
    assertThat(response.getBody().getContent().iterator().next().key).isEqualTo("L1050");
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_SELF).getHref())
        .endsWith(
            "/api/v1/classifications?"
                + "domain=DOMAIN_A&sort-by=key&order=asc&page=2&page-size=5");
    assertThat(response.getBody().getLink(Link.REL_FIRST)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_LAST)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_NEXT)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_PREVIOUS)).isNotNull();
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

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

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

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
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

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithParentKeyInDomain_aShouldCreateAClassificationInRootDomain() {
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

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    ResponseEntity<ClassificationSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    boolean foundClassificationCreated = false;
    for (ClassificationSummaryResource classification : response.getBody().getContent()) {
      if ("NEW_CLASS_P2".equals(classification.getKey())
          && "".equals(classification.getDomain())
          && "T2100".equals(classification.getParentKey())) {
        foundClassificationCreated = true;
      }
    }

    assertThat(foundClassificationCreated).isTrue();
  }

  @Test
  @DirtiesContext
  void testReturn400IfCreateClassificationWithIncompatibleParentIdAndKey() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_B\","
            + "\"key\":\"NEW_CLASS_P3\",\"name\":\"new classification\","
            + "\"type\":\"TASK\",\"parentId\":\"CLI:200000000000000000000000000000000015\","
            + "\"parentKey\":\"T2000\"}";
    
    assertThatThrownBy(
        () ->
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class)))
        .isInstanceOf(HttpClientErrorException.class)
        .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST);
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithClassificationIdReturnsError400() {
    String newClassification =
        "{\"classificationId\":\"someId\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\","
            + "\"name\":\"new classification\",\"type\":\"TASK\"}";

    assertThatThrownBy(() ->
      template.exchange(
          restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
          HttpMethod.POST,
          new HttpEntity<>(newClassification, restHelper.getHeaders()),
          ParameterizedTypeReference.forType(ClassificationResource.class)))
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException)ex).getStatusCode())
          .isEqualTo(HttpStatus.BAD_REQUEST);
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
    assertThat(response.getBody().name).isEqualTo("Zustimmungserklärung");
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
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    assertThatThrownBy(() ->
      template.exchange(
        restHelper.toUrl(
            Mapping.URL_CLASSIFICATIONS_ID, "CLI:200000000000000000000000000000000004"),
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(ClassificationSummaryResource.class)))
          .isInstanceOf(HttpClientErrorException.class);
  }
}
