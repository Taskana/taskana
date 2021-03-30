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
import org.springframework.web.client.HttpClientErrorException.BadRequest;

import pro.taskana.classification.rest.models.ClassificationSummaryPagedRepresentationModel;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryPagedRepresentationModel;

/** Test general Exception Handling. */
@TaskanaSpringBootTest
class GeneralExceptionHandlingTest {

  private static final ParameterizedTypeReference<WorkbasketSummaryPagedRepresentationModel>
      WORKBASKET_SUMMARY_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<WorkbasketSummaryPagedRepresentationModel>() {};

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
                new HttpEntity<>(restHelper.getHeadersTeamlead_1()),
                ParameterizedTypeReference.forType(
                    ClassificationSummaryPagedRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("non-existing-id");
  }

  @Test
  void should_ThrowExpressiveError_When_InvalidEnumValueIsProvided() {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + "?type=GROU";
    HttpEntity<String> auth = new HttpEntity<>(restHelper.getHeadersAdmin());

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.GET, auth, WORKBASKET_SUMMARY_PAGE_MODEL_TYPE);
        };

    // Inside the complete message of the exception, thrown when querying the REST controller with
    // a wrong enum value (for example 'GROU' instead of 'GROUP'), there will be found following
    // information about this issue:
    //
    //     nested exception is org.springframework.core.convert.ConversionFailedException: Failed to
    //     convert from type [java.lang.String] to type [pro.taskana.workbasket.api.WorkbasketType]
    //     for value 'GROU'; nested exception is java.lang.IllegalArgumentException: No enum
    //     constant pro.taskana.workbasket.api.WorkbasketType.GROU
    //
    // Unfortunately the message of the exception thrown here is cut and thus there is no info
    // about this problem. In case of querying the REST controller directly there will.
    assertThatThrownBy(httpCall)
        .isInstanceOf(BadRequest.class)
        .hasMessageContaining(
            "\"status\":400,\"error\":\"BAD_REQUEST\",\"exception\":\"org.springframework.web."
                + "method.annotation.ModelAttributeMethodProcessor$1\",\"message\":\"org."
                + "springframework");
  }
}
