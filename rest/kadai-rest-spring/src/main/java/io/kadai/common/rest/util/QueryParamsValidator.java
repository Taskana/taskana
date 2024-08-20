package io.kadai.common.rest.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class QueryParamsValidator {

  private QueryParamsValidator() {
    throw new IllegalStateException("Utility class");
  }

  public static void validateParams(HttpServletRequest request, Class<?>... filterOrSortingClazz) {
    Set<String> allowedParams =
        Stream.of(filterOrSortingClazz)
            .flatMap(clazz -> Stream.of(clazz.getDeclaredFields()))
            .map(
                field ->
                    Optional.ofNullable(field.getDeclaredAnnotation(JsonProperty.class))
                        .map(JsonProperty::value)
                        .orElseGet(field::getName))
            .collect(Collectors.toSet());

    Set<String> providedParams = new HashSet<>(request.getParameterMap().keySet());

    providedParams.removeIf(allowedParams::contains);

    if (!providedParams.isEmpty()) {
      throw new IllegalArgumentException("Unknown request parameters found: " + providedParams);
    }
  }

  public static boolean hasQueryParameterValues(HttpServletRequest request, String queryParameter) {

    Map<String, String[]> queryParametersMap = request.getParameterMap();

    if (queryParametersMap.isEmpty()) {
      return false;
    }

    String[] queryParameterValues = queryParametersMap.get(queryParameter);

    if (queryParameterValues == null) {
      return false;
    }

    boolean hasQueryParameterNotEmptyValues =
        Arrays.stream(queryParameterValues).anyMatch(value -> !value.isBlank());

    /* Workaround to manage the case "query-param=".
    It should be safe enough to use because we have checked all other possibilities before. */
    boolean hasQueryParameterEmptyValues = request.getQueryString().contains(queryParameter + "=");

    return hasQueryParameterNotEmptyValues || hasQueryParameterEmptyValues;
  }

  public static boolean hasQueryParameterValuesOrIsNotTrue(
      HttpServletRequest request, String queryParameter) {

    Map<String, String[]> queryParametersMap = request.getParameterMap();

    if (queryParametersMap.isEmpty()) {
      return false;
    }

    String[] queryParameterValues = queryParametersMap.get(queryParameter);

    if (queryParameterValues == null) {
      return false;
    }

    boolean hasQueryParameterProhibitedValues =
        Arrays.stream(queryParameterValues)
            .anyMatch(value -> !value.isBlank() && !Boolean.parseBoolean(value));

    /* Workaround to manage the case "query-param=".
    It should be safe enough to use because we have checked all other possibilities before. */
    boolean hasQueryParameterEmptyValues =
        Arrays.stream(queryParameterValues).allMatch(String::isBlank)
            && request.getQueryString().contains(queryParameter + "=");

    return hasQueryParameterProhibitedValues || hasQueryParameterEmptyValues;
  }
}
