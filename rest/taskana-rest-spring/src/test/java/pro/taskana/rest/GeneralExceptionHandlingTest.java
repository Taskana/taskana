package pro.taskana.rest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.ClassificationSummaryListResource;

/** Test general Exception Handling. */
@TaskanaSpringBootTest
class GeneralExceptionHandlingTest {

  private static RestTemplate template;
  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.getRestTemplate();
  }

  @Test
  void testDeleteNonExisitingClassificationExceptionIsLogged() {

    HttpClientErrorException ex =
        Assertions.assertThrows(
            HttpClientErrorException.class,
            () ->
                template.exchange(
                    restHelper.toUrl(Mapping.URL_CLASSIFICATIONS_ID, "non-existing-id"),
                    HttpMethod.DELETE,
                    restHelper.defaultRequest(),
                    ParameterizedTypeReference.forType(ClassificationSummaryListResource.class)));
    Assertions.assertTrue(ex.getResponseBodyAsString().contains("non-existing-id"));
  }
}
