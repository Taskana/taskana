package io.kadai.common.rest;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.common.api.KadaiRole;
import io.kadai.common.rest.models.CustomAttributesRepresentationModel;
import io.kadai.common.rest.models.KadaiUserInfoRepresentationModel;
import io.kadai.common.rest.models.VersionRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.rest.test.RestHelper;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/** Test KadaiEngineController. */
@KadaiSpringBootTest
class KadaiEngineControllerIntTest {

  private static final RestTemplate TEMPLATE = RestHelper.TEMPLATE;

  private final RestHelper restHelper;

  @Autowired
  KadaiEngineControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testDomains() {
    String url = restHelper.toUrl(RestEndpoints.URL_DOMAIN);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).contains("DOMAIN_A");
  }

  @Test
  void testClassificationTypes() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_TYPES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsExactlyInAnyOrder("TASK", "DOCUMENT");
  }

  @Test
  void should_ReturnAllClassifications_When_GetClassificationCategories_isCalledWithoutType() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody())
        .containsExactlyInAnyOrder("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS", "EXTERNAL");
  }

  @Test
  void should_ReturnOnlyClassificationsForTypeTask_When_GetClassificationCategories_isCalled() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES) + "?type=TASK";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsExactly("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS");
  }

  @Test
  void should_ReturnOnlyClassificationsForTypeDocument_When_GetClassificationCategories_isCalled() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES) + "?type=DOCUMENT";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<String>> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsExactly("EXTERNAL");
  }

  @Test
  void should_ReturnUserInformation_WhenCurrentUserNotAuthorizedToUseUserFromHeader() {
    HttpHeaders headers = RestHelper.generateHeadersForUser("user-2-1");
    headers.add("userid", "user-1-1");
    ResponseEntity<KadaiUserInfoRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_CURRENT_USER),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ParameterizedTypeReference.forType(KadaiUserInfoRepresentationModel.class));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    KadaiUserInfoRepresentationModel currentUser = response.getBody();
    assertThat(currentUser).isNotNull();
    assertThat(currentUser.getUserId()).isEqualTo("user-2-1");
    assertThat(currentUser.getGroupIds()).hasSize(3);
    assertThat(currentUser.getRoles()).hasSize(1);
  }

  @Test
  void testGetCurrentUserInfo() {
    String url = restHelper.toUrl(RestEndpoints.URL_CURRENT_USER);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<KadaiUserInfoRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(KadaiUserInfoRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getUserId()).isEqualTo("teamlead-1");
    assertThat(response.getBody().getGroupIds())
        .contains("cn=business-admins,cn=groups,ou=test,o=kadai");
    assertThat(response.getBody().getRoles())
        .contains(KadaiRole.BUSINESS_ADMIN)
        .doesNotContain(KadaiRole.ADMIN);
  }

  @Test
  void testGetCurrentUserInfoWithPermission() {
    String url = restHelper.toUrl(RestEndpoints.URL_CURRENT_USER);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    ResponseEntity<KadaiUserInfoRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(KadaiUserInfoRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getUserId()).isEqualTo("user-1-2");
    assertThat(response.getBody().getGroupIds())
        .containsExactlyInAnyOrder("cn=organisationseinheit ksc 1,cn=organisationseinheit "
                + "ksc,cn=organisation,ou=test,o=kadai",
            "cn=ksc-users,cn=groups,ou=test,o=kadai", "cn=g02,cn=groups,ou=test,o=kadai",
            "cn=g01,cn=groups,ou=test,o=kadai");
    assertThat(response.getBody().getRoles())
        .contains(KadaiRole.USER)
        .doesNotContain(KadaiRole.ADMIN);
  }

  @Test
  void should_ReturnCustomAttributes() {
    String url = restHelper.toUrl(RestEndpoints.URL_CUSTOM_ATTRIBUTES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<CustomAttributesRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(CustomAttributesRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void should_ReturnFalse_When_NoHistoryProvider() {
    String url = restHelper.toUrl(RestEndpoints.URL_HISTORY_ENABLED);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));
    ResponseEntity<Boolean> response =
        TEMPLATE.exchange(
            url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(Boolean.class));
    assertThat(response.getBody()).isFalse();
  }

  @Test
  void should_ReturnCurrentVersion() {
    String url = restHelper.toUrl(RestEndpoints.URL_VERSION);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));
    ResponseEntity<VersionRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(VersionRepresentationModel.class));
    assertThat(response.getBody().getVersion()).isNull();
  }

  @Test
  void should_SetCustomConfigurationAttributes() {
    String url = restHelper.toUrl(RestEndpoints.URL_CUSTOM_ATTRIBUTES);
    CustomAttributesRepresentationModel customAttributes =
        new CustomAttributesRepresentationModel(
            Map.of(
                "filter",
                "{ \"Tasks with state READY\": { \"state\": [\"READY\"]}, "
                    + "\"Tasks with state CLAIMED\": {\"state\": [\"CLAIMED\"] }}",
                "schema",
                Map.of(
                    "Filter",
                    Map.of(
                        "displayName",
                        "Filter for Task-Priority-Report",
                        "members",
                        Map.of(
                            "filter",
                            Map.of("displayName", "Filter values", "type", "json", "min", "1"))))));
    HttpEntity<CustomAttributesRepresentationModel> auth =
        new HttpEntity<>(customAttributes, RestHelper.generateHeadersForUser("admin"));
    ResponseEntity<CustomAttributesRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.PUT,
            auth,
            ParameterizedTypeReference.forType(CustomAttributesRepresentationModel.class));
    assertThat(response.getBody()).isEqualTo(customAttributes);
  }

  @Test
  void should_GetClassificationCategoriesByType() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES_BY_TYPES);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));
    ResponseEntity<Map<String, List<String>>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(Map.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).hasSize(2);
    assertThat(response.getBody().get("TASK"))
        .containsExactlyInAnyOrder("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS");
    assertThat(response.getBody().get("DOCUMENT")).containsExactlyInAnyOrder("EXTERNAL");
  }
}
