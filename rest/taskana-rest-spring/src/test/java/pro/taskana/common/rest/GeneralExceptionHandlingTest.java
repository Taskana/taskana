package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;

/** Test general Exception Handling. */
@TaskanaSpringBootTest
class GeneralExceptionHandlingTest {

  private static final ParameterizedTypeReference<
          TaskanaPagedModel<ClassificationSummaryRepresentationModel>>
      CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<
              TaskanaPagedModel<ClassificationSummaryRepresentationModel>>() {};
  private static RestTemplate template;
  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testDeleteNonExisitingClassificationExceptionIsLogged() {
    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS_ID, "non-existing-id"),
              HttpMethod.DELETE,
              restHelper.defaultRequest(),
              CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);
        };
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("non-existing-id");
  }
}
