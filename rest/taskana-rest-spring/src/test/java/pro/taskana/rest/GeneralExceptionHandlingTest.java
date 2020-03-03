package pro.taskana.rest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testDeleteNonExisitingClassificationExceptionIsLogged() {
    assertThatThrownBy(
        () ->
              template.exchange(
                  restHelper.toUrl(Mapping.URL_CLASSIFICATIONS_ID, "non-existing-id"),
                  HttpMethod.DELETE,
                  restHelper.defaultRequest(),
                  ParameterizedTypeReference.forType(ClassificationSummaryListResource.class)))
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("non-existing-id");
  }
}
