package pro.taskana.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.ClassificationRepresentationModel;
import pro.taskana.rest.resource.ClassificationSummaryRepresentationModel;
import pro.taskana.rest.resource.TaskanaPagedModel;

/**
 * Test ClassificationController.
 *
 * @author bbr
 */
@TaskanaSpringBootTest
class ClassificationControllerIntTest {

  private static final ParameterizedTypeReference<
          TaskanaPagedModel<ClassificationSummaryRepresentationModel>>
      CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<
              TaskanaPagedModel<ClassificationSummaryRepresentationModel>>() {};
  static RestTemplate template = RestHelper.TEMPLATE;
  @Autowired RestHelper restHelper;

  @Test
  void testGetClassification() {
    ResponseEntity<ClassificationRepresentationModel> response =
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000002"),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getHeaders().getContentType().toString())
        .isEqualTo(MediaTypes.HAL_JSON_VALUE);
  }

  @Test
  void testGetAllClassifications() {
    ResponseEntity<TaskanaPagedModel<ClassificationSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
  }

  @Test
  void testGetAllClassificationsFilterByCustomAttribute() {
    ResponseEntity<TaskanaPagedModel<ClassificationSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS) + "?domain=DOMAIN_A&custom-1-like=RVNR",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(13);
  }

  @Test
  void testGetAllClassificationsKeepingFilters() {
    ResponseEntity<TaskanaPagedModel<ClassificationSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS)
                + "?domain=DOMAIN_A&sort-by=key&order=asc",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(
            response
                .getBody()
                .getRequiredLink(IanaLinkRelations.SELF)
                .getHref()
                .endsWith("/api/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc"))
        .isTrue();
    assertThat(response.getBody().getContent()).hasSize(17);
    assertThat(response.getBody().getContent().iterator().next().getKey()).isEqualTo("A12");
  }

  @Test
  void testGetSecondPageSortedByKey() {
    ResponseEntity<TaskanaPagedModel<ClassificationSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS)
                + "?domain=DOMAIN_A&sort-by=key&order=asc&page=2&page-size=5",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody().getContent()).hasSize(5);
    assertThat(response.getBody().getContent().iterator().next().getKey()).isEqualTo("L1050");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(
            response
                .getBody()
                .getRequiredLink(IanaLinkRelations.SELF)
                .getHref()
                .endsWith(
                    "/api/v1/classifications?"
                        + "domain=DOMAIN_A&sort-by=key&order=asc&page-size=5&page=2"))
        .isTrue();
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
            + "\"name\":\"new classification\",\"type\":\"TASK\"}";

    ResponseEntity<ClassificationRepresentationModel> responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));

    assertThat(responseEntity).isNotNull();
    assertThat(HttpStatus.CREATED).isEqualTo(responseEntity.getStatusCode());

    newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS_2\","
            + "\"name\":\"new classification\",\"type\":\"TASK\"}";

    responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));

    assertThat(HttpStatus.CREATED).isEqualTo(responseEntity.getStatusCode());
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithParentId() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\","
            + "\"domain\":\"DOMAIN_B\",\"key\":\"NEW_CLASS_P1\","
            + "\"name\":\"new classification\",\"type\":\"TASK\","
            + "\"parentId\":\"CLI:200000000000000000000000000000000015\"}";

    ResponseEntity<ClassificationRepresentationModel> responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));

    assertThat(responseEntity).isNotNull();
    assertThat(HttpStatus.CREATED).isEqualTo(responseEntity.getStatusCode());
  }

  @Test
  @DirtiesContext
  @SuppressWarnings("checkstyle:LineLength")
  void testCreateClassificationWithParentKey() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_B\","
            + "\"key\":\"NEW_CLASS_P2\",\"name\":\"new classification\","
            + "\"type\":\"TASK\",\"parentKey\":\"T2100\"}";

    ResponseEntity<ClassificationRepresentationModel> responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));

    assertThat(responseEntity).isNotNull();
    assertThat(HttpStatus.CREATED).isEqualTo(responseEntity.getStatusCode());
  }

  @Test
  @DirtiesContext
  void testCreateClassificationWithParentKeyInDomain_aShouldCreateAClassificationInRootDomain() {
    String newClassification =
        "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\","
            + "\"key\":\"NEW_CLASS_P2\",\"name\":\"new classification\","
            + "\"type\":\"TASK\",\"parentKey\":\"T2100\"}";

    ResponseEntity<ClassificationRepresentationModel> responseEntity =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.POST,
            new HttpEntity<>(newClassification, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));

    assertThat(responseEntity).isNotNull();
    assertThat(HttpStatus.CREATED).isEqualTo(responseEntity.getStatusCode());

    ResponseEntity<TaskanaPagedModel<ClassificationSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);
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
            + "\"parentKey\":\"T2000\"}";

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
              HttpMethod.POST,
              new HttpEntity<>(newClassification, restHelper.getHeaders()),
              ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));
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
            + "\"name\":\"new classification\",\"type\":\"TASK\"}";

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(Mapping.URL_CLASSIFICATIONS),
              HttpMethod.POST,
              new HttpEntity<>(newClassification, restHelper.getHeaders()),
              ParameterizedTypeReference.forType(ClassificationRepresentationModel.class));
        };
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetClassificationWithSpecialCharacter() {

    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersAdmin());
    ResponseEntity<ClassificationSummaryRepresentationModel> response =
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000009"),
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(ClassificationSummaryRepresentationModel.class));
    assertThat(response.getBody().getName()).isEqualTo("Zustimmungserklärung");
  }

  @Test
  @DirtiesContext
  void testDeleteClassification() {
    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeaders());

    ResponseEntity<ClassificationSummaryRepresentationModel> response =
        template.exchange(
            restHelper.toUrl(
                Mapping.URL_CLASSIFICATIONS_ID, "CLI:200000000000000000000000000000000004"),
            HttpMethod.DELETE,
            request,
            ParameterizedTypeReference.forType(ClassificationSummaryRepresentationModel.class));
    assertThat(HttpStatus.NO_CONTENT).isEqualTo(response.getStatusCode());
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(
                  Mapping.URL_CLASSIFICATIONS_ID, "CLI:200000000000000000000000000000000004"),
              HttpMethod.GET,
              request,
              ParameterizedTypeReference.forType(ClassificationSummaryRepresentationModel.class));
        };
    assertThatThrownBy(httpCall).isInstanceOf(HttpClientErrorException.class);
  }
}
