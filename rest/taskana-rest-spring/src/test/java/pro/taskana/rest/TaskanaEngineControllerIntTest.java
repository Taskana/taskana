package pro.taskana.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.rest.resource.TaskanaUserInfoRepresentationModel;

/** Test TaskanaEngineController. */
@TaskanaSpringBootTest
class TaskanaEngineControllerIntTest {

  private static RestTemplate template;
  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testDomains() {
    ResponseEntity<List<String>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_DOMAIN),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).contains("DOMAIN_A");
  }

  @Test
  void testClassificationTypes() {
    ResponseEntity<List<String>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATION_TYPES),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsOnly("TASK", "DOCUMENT");
  }

  @Test
  void testClassificationCategories() {
    ResponseEntity<List<String>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATION_CATEGORIES),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).containsOnly("MANUAL", "EXTERNAL", "AUTOMATIC", "PROCESS");
  }

  @Test
  void testGetCurrentUserInfo() {
    ResponseEntity<TaskanaUserInfoRepresentationModel> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CURRENT_USER),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskanaUserInfoRepresentationModel.class));
    assertThat(response.getBody().getUserId()).isEqualTo("teamlead_1");
    assertThat(response.getBody().getGroupIds()).contains("businessadmin");
    assertThat(response.getBody().getRoles()).contains(TaskanaRole.BUSINESS_ADMIN);
    assertThat(response.getBody().getRoles()).doesNotContain(TaskanaRole.ADMIN);
  }
}
