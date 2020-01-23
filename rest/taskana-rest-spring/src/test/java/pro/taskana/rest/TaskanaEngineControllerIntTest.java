package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaRole;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.TaskanaUserInfoResource;

/** Test TaskanaEngineController. */
@TaskanaSpringBootTest
class TaskanaEngineControllerIntTest {

  private static RestTemplate template;
  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.getRestTemplate();
  }

  @Test
  void testDomains() {
    ResponseEntity<List<String>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_DOMAIN),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertTrue(response.getBody().contains("DOMAIN_A"));
  }

  @Test
  void testClassificationTypes() {
    ResponseEntity<List<String>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONTYPES),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertTrue(response.getBody().contains("TASK"));
    assertTrue(response.getBody().contains("DOCUMENT"));
    assertFalse(response.getBody().contains("UNKNOWN"));
  }

  @Test
  void testClassificationCategories() {
    ResponseEntity<List<String>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONCATEGORIES),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertTrue(response.getBody().contains("MANUAL"));
    assertTrue(response.getBody().contains("EXTERNAL"));
    assertTrue(response.getBody().contains("AUTOMATIC"));
    assertTrue(response.getBody().contains("PROCESS"));
    assertFalse(response.getBody().contains("UNKNOWN"));
  }

  @Test
  void testGetCurrentUserInfo() {
    ResponseEntity<TaskanaUserInfoResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CURRENTUSER),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskanaUserInfoResource.class));
    assertEquals("teamlead_1", response.getBody().getUserId());
    assertTrue(response.getBody().getGroupIds().contains("businessadmin"));
    assertTrue(response.getBody().getRoles().contains(TaskanaRole.BUSINESS_ADMIN));
    assertFalse(response.getBody().getRoles().contains(TaskanaRole.ADMIN));
  }
}
