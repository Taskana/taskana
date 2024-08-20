package io.kadai.common.rest;

import static io.kadai.rest.test.RestHelper.TEMPLATE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kadai.classification.rest.models.ClassificationSummaryPagedRepresentationModel;
import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.rest.KadaiRestExceptionHandler.MalformedQueryParameter;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.rest.test.RestHelper;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.rest.models.WorkbasketSummaryPagedRepresentationModel;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpStatusCodeException;

/** Test general Exception Handling. */
@KadaiSpringBootTest
class GeneralExceptionHandlingTest {

  private final RestHelper restHelper;
  private final ObjectMapper objectMapper;

  @Autowired
  GeneralExceptionHandlingTest(RestHelper restHelper, ObjectMapper objectMapper) {
    this.restHelper = restHelper;
    this.objectMapper = objectMapper;
  }

  @Test
  void testDeleteNonExisitingClassificationExceptionIsLogged() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS_ID, "non-existing-id");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.DELETE,
                auth,
                ParameterizedTypeReference.forType(
                    ClassificationSummaryPagedRepresentationModel.class));

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining("non-existing-id");
  }

  @Test
  void should_ThrowExpressiveError_When_AQueryParameterIsInvalid() throws Exception {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + "?required-permission=GROU";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.forType(
                    WorkbasketSummaryPagedRepresentationModel.class));

    List<String> expectedValues =
        Arrays.stream(WorkbasketPermission.values())
            .map(Object::toString)
            .collect(Collectors.toList());
    ErrorCode errorCode =
        ErrorCode.of(
            "QUERY_PARAMETER_MALFORMED",
            Map.of(
                "malformedQueryParameters",
                List.of(new MalformedQueryParameter("required-permission", "GROU", expectedValues))
                    .toArray(new MalformedQueryParameter[0])));

    assertThatThrownBy(httpCall)
        .isInstanceOf(BadRequest.class)
        .extracting(BadRequest.class::cast)
        .extracting(BadRequest::getResponseBodyAsString)
        .asString()
        .contains(objectMapper.writeValueAsString(errorCode));
  }

  @Test
  void should_CombineErrors_When_SameQueryParameterDeclarationsAreInvalidMultipleTimes()
      throws Exception {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + "?type=GROU&type=invalid";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.forType(
                    WorkbasketSummaryPagedRepresentationModel.class));

    List<String> expectedValuesForQueryParameterType =
        Arrays.stream(WorkbasketType.values()).map(Object::toString).collect(Collectors.toList());
    ErrorCode errorCode =
        ErrorCode.of(
            "QUERY_PARAMETER_MALFORMED",
            Map.of(
                "malformedQueryParameters",
                List.of(
                        new MalformedQueryParameter(
                            "type", "GROU", expectedValuesForQueryParameterType),
                        new MalformedQueryParameter(
                            "type", "invalid", expectedValuesForQueryParameterType))
                    .toArray(new MalformedQueryParameter[0])));

    assertThatThrownBy(httpCall)
        .isInstanceOf(BadRequest.class)
        .extracting(BadRequest.class::cast)
        .extracting(BadRequest::getResponseBodyAsString)
        .asString()
        .contains(objectMapper.writeValueAsString(errorCode));
  }

  @Test
  void should_FilterOutValidQueryParameters_When_OnlySomeQueryParametersDeclarationsAreInvalid()
      throws Exception {
    String url = restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + "?type=GROUP&type=invalid";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.forType(
                    WorkbasketSummaryPagedRepresentationModel.class));

    List<String> expectedValuesForQueryParameterType =
        Arrays.stream(WorkbasketType.values()).map(Object::toString).collect(Collectors.toList());
    ErrorCode errorCode =
        ErrorCode.of(
            "QUERY_PARAMETER_MALFORMED",
            Map.of(
                "malformedQueryParameters",
                List.of(
                        new MalformedQueryParameter(
                            "type", "invalid", expectedValuesForQueryParameterType))
                    .toArray(new MalformedQueryParameter[0])));

    assertThatThrownBy(httpCall)
        .isInstanceOf(BadRequest.class)
        .extracting(BadRequest.class::cast)
        .extracting(BadRequest::getResponseBodyAsString)
        .asString()
        .contains(objectMapper.writeValueAsString(errorCode));
  }

  @Test
  void should_CombineErrors_When_DifferentQueryParametersAreInvalid() throws Exception {
    String url =
        restHelper.toUrl(RestEndpoints.URL_WORKBASKET) + "?type=GROU&required-permission=invalid";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.forType(
                    WorkbasketSummaryPagedRepresentationModel.class));

    List<String> expectedValuesForQueryParameterType =
        Arrays.stream(WorkbasketType.values()).map(Object::toString).collect(Collectors.toList());
    List<String> expectedValuesForQueryParameterRequiredPermission =
        Arrays.stream(WorkbasketPermission.values())
            .map(Object::toString)
            .collect(Collectors.toList());
    ErrorCode errorCode =
        ErrorCode.of(
            "QUERY_PARAMETER_MALFORMED",
            Map.of(
                "malformedQueryParameters",
                List.of(
                        new MalformedQueryParameter(
                            "type", "GROU", expectedValuesForQueryParameterType),
                        new MalformedQueryParameter(
                            "required-permission",
                            "invalid",
                            expectedValuesForQueryParameterRequiredPermission))
                    .toArray(new MalformedQueryParameter[0])));

    assertThatThrownBy(httpCall)
        .isInstanceOf(BadRequest.class)
        .extracting(BadRequest.class::cast)
        .extracting(BadRequest::getResponseBodyAsString)
        .asString()
        .contains(objectMapper.writeValueAsString(errorCode));
  }
}
