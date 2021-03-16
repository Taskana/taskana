package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import pro.taskana.classification.rest.models.ClassificationSummaryPagedRepresentationModel;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;

/** Test general Exception Handling. */
@TaskanaSpringBootTest
class GeneralExceptionHandlingTest {

  private final RestHelper restHelper;

  @Autowired
  GeneralExceptionHandlingTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testDeleteNonExisitingClassificationExceptionIsLogged() {
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS_ID, "non-existing-id"),
                HttpMethod.DELETE,
                new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
                ParameterizedTypeReference.forType(
                    ClassificationSummaryPagedRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("non-existing-id");
  }
}
