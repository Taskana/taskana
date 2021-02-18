package pro.taskana.common.rest.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;

public class QueryParamsValidator {

  private QueryParamsValidator() {
    throw new IllegalStateException("Utility class");
  }

  public static void validateParams(HttpServletRequest request, Class... filterOrSortingClazz) {

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
      throw new IllegalArgumentException("Unkown request parameters found: " + providedParams);
    }
  }
}
