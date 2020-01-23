package pro.taskana.rest;

import static org.junit.Assert.assertEquals;

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
import pro.taskana.rest.resource.AccessIdResource;

@TaskanaSpringBootTest
class AccessIdControllerIntTest {

  private static RestTemplate template;

  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.getRestTemplate();
  }

  @Test
  void testQueryGroupsByDn() {
    ResponseEntity<List<AccessIdResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID)
                + "?search-for=cn=developersgroup,ou=groups,o=taskanatest",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertEquals(1, response.getBody().size());
  }

  @Test
  void testQueryGroupsByCn() {
    ResponseEntity<List<AccessIdResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=developer",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertEquals(1, response.getBody().size());
  }
}
