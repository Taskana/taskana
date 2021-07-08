package pro.taskana.classification.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.classification.rest.models.ClassificationSummaryPagedRepresentationModel;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;

/** Test {@link ClassificationController}. */
@TaskanaSpringBootTest
class ClassificationControllerIntTest {

  private static final ParameterizedTypeReference<ClassificationSummaryPagedRepresentationModel>
      CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<ClassificationSummaryPagedRepresentationModel>() {};

  private static final ParameterizedTypeReference<ClassificationRepresentationModel>
      CLASSIFICATION_REPRESENTATION_MODEL_TYPE =
          new ParameterizedTypeReference<ClassificationRepresentationModel>() {};

  private static final ParameterizedTypeReference<ClassificationSummaryRepresentationModel>
      CLASSIFICATION_SUMMARY_REPRESENTATION_MODEL_TYPE =
          new ParameterizedTypeReference<ClassificationSummaryRepresentationModel>() {};

  @Autowired RestHelper restHelper;

  @Test
  void testGetClassification() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000002");
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ResponseEntity<ClassificationRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, CLASSIFICATION_REPRESENTATION_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType()).isEqualTo(MediaTypes.HAL_JSON);
  }

  @Test
  void testGetAllClassifications() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS);
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ResponseEntity<ClassificationSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetAllClassificationsFilterByCustomAttribute() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS) + "?domain=DOMAIN_A&custom-1-like=RVNR";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ResponseEntity<ClassificationSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(33);
  }

  @Test
  void testGetAllClassificationsKeepingFilters() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS)
            + "?domain=DOMAIN_A&sort-by=KEY&order=ASCENDING";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ResponseEntity<ClassificationSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith("/api/v1/classifications?domain=DOMAIN_A&sort-by=KEY&order=ASCENDING");
    assertThat(response.getBody().getContent()).hasSize(37);
    assertThat(response.getBody().getContent().iterator().next().getKey()).isEqualTo("A12");
  }

  @Test
  void testGetSecondPageSortedByKey() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS)
            + "?domain=DOMAIN_A&sort-by=KEY&order=ASCENDING&page-size=5&page=2";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ResponseEntity<ClassificationSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(5);
    assertThat(response.getBody().getContent().iterator().next().getKey()).isEqualTo("L1050");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith(
            "/api/v1/classifications?"
                + "domain=DOMAIN_A&sort-by=KEY&order=ASCENDING&page-size=5&page=2");
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.NEXT)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
  }

  @Test
  @DirtiesContext
  void testCreateClassification() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\","
            + "\"name\":\"new classification\",\"type\":\"TASK\", \"serviceLevel\":\"P1D\"}";
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS);
    HttpEntity<String> auth =
        new HttpEntity<>(newClassification, restHelper.getHeadersTeamlead_1());

    ResponseEntity<ClassificationRepresentationModel> responseEntity =
        TEMPLATE.exchange(url, HttpMethod.POST, auth, CLASSIFICATION_REPRESENTATION_MODEL_TYPE);
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS_2\","
            + "\"name\":\"new classification\",\"type\":\"TASK\", \"serviceLevel\":\"P1D\"}";
    HttpEntity<String> auth2 =
        new HttpEntity<>(newClassification, restHelper.getHeadersTeamlead_1());

    responseEntity =
        TEMPLATE.exchange(url, HttpMethod.POST, auth2, CLASSIFICATION_REPRESENTATION_MODEL_TYPE);
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  @DirtiesContext
  void should_ThrowNotAuthorized_WhenUserIsNotInRoleAdminOrBusinessAdmin_whileCreating() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\","
            + "\"name\":\"new classification\",\"type\":\"TASK\"}";
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS);
    HttpEntity<String> auth = new HttpEntity<>(newClassification, restHelper.getHeadersUser_1_1());

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.POST, auth, CLASSIFICATION_REPRESENTATION_MODEL_TYPE);
        };

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithParentId() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_B\",\"key\":\"NEW_CLASS_P1\","
            + "\"name\":\"new classification\",\"type\":\"TASK\",\"serviceLevel\":\"P1D\","
            + "\"parentId\":\"CLI:200000000000000000000000000000000015\"}";
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS);
    HttpEntity<String> auth =
        new HttpEntity<>(newClassification, restHelper.getHeadersTeamlead_1());

    ResponseEntity<ClassificationRepresentationModel> responseEntity =
        TEMPLATE.exchange(url, HttpMethod.POST, auth, CLASSIFICATION_REPRESENTATION_MODEL_TYPE);

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
            + "\"type\":\"TASK\",\"parentKey\":\"T2100\",\"serviceLevel\":\"P1D\"}";
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS);
    HttpEntity<String> auth =
        new HttpEntity<>(newClassification, restHelper.getHeadersTeamlead_1());

    ResponseEntity<ClassificationRepresentationModel> responseEntity =
        TEMPLATE.exchange(url, HttpMethod.POST, auth, CLASSIFICATION_REPRESENTATION_MODEL_TYPE);

    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithParentKeyInDomain_aShouldCreateAClassificationInRootDomain() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\","
            + "\"key\":\"NEW_CLASS_P2\",\"name\":\"new classification\","
            + "\"type\":\"TASK\",\"parentKey\":\"T2100\",\"serviceLevel\":\"P1D\"}";
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS);
    HttpEntity<String> auth =
        new HttpEntity<>(newClassification, restHelper.getHeadersTeamlead_1());

    ResponseEntity<ClassificationRepresentationModel> responseEntity =
        TEMPLATE.exchange(url, HttpMethod.POST, auth, CLASSIFICATION_REPRESENTATION_MODEL_TYPE);
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    HttpEntity<Object> auth2 = new HttpEntity<>(restHelper.getHeadersTeamlead_1());
    ResponseEntity<ClassificationSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth2, CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    boolean foundClassificationCreated = false;
    for (ClassificationSummaryRepresentationModel classification :
        response.getBody().getContent()) {
      if ("NEW_CLASS_P2".equals(classification.getKey())
          && "".equals(classification.getDomain())
          && "T2100".equals(classification.getParentKey())) {
        foundClassificationCreated = true;
        break;
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
            + "\"parentKey\":\"T2000\",\"serviceLevel\":\"P1D\"}";
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS);
    HttpEntity<String> auth =
        new HttpEntity<>(newClassification, restHelper.getHeadersBusinessAdmin());

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.POST, auth, CLASSIFICATION_REPRESENTATION_MODEL_TYPE);
        };

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithClassificationIdReturnsError400() {
    String newClassification =
        "{\"classificationId\":\"someId\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\","
            + "\"name\":\"new classification\",\"type\":\"TASK\",\"serviceLevel\":\"P1D\"}";
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS);
    HttpEntity<String> auth =
        new HttpEntity<>(newClassification, restHelper.getHeadersBusinessAdmin());

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.POST, auth, CLASSIFICATION_REPRESENTATION_MODEL_TYPE);
        };

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetClassificationWithSpecialCharacter() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000009");
    HttpEntity<String> auth = new HttpEntity<>(restHelper.getHeadersAdmin());

    ResponseEntity<ClassificationSummaryRepresentationModel> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, CLASSIFICATION_SUMMARY_REPRESENTATION_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getName()).isEqualTo("Zustimmungserklärung");
  }

  @Test
  @DirtiesContext
  void testDeleteClassification() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_CLASSIFICATIONS_ID, "CLI:200000000000000000000000000000000004");
    HttpEntity<String> auth = new HttpEntity<>(restHelper.getHeadersBusinessAdmin());

    ResponseEntity<ClassificationSummaryRepresentationModel> response =
        TEMPLATE.exchange(
            url, HttpMethod.DELETE, auth, CLASSIFICATION_SUMMARY_REPRESENTATION_MODEL_TYPE);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url, HttpMethod.GET, auth, CLASSIFICATION_SUMMARY_REPRESENTATION_MODEL_TYPE);
    assertThatThrownBy(httpCall).isInstanceOf(HttpClientErrorException.class);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFilterParams() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS)
            + "?domain=DOMAIN_A"
            + "&illegalParam=illegal"
            + "&anotherIllegalParam=stillIllegal"
            + "&sort-by=NAME&order=DESCENDING&page-size=5&page=2";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.GET, auth, CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);
        };

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpServerErrorException.class)
        .hasMessageContaining(
            "Unkown request parameters found: [anotherIllegalParam, illegalParam]")
        .extracting(ex -> ((HttpServerErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
