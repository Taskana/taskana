package pro.taskana.common.rest.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import pro.taskana.common.api.exceptions.InvalidArgumentException;

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
    checkExactParam(request, "owner-is-null");
  }

  private static void checkExactParam(HttpServletRequest request, String queryParameter) {
    String queryString = request.getQueryString();
    boolean containParam = queryString != null && queryString.contains(queryParameter);
    if (containParam) {
      Pattern pattern = Pattern.compile("\\b" + queryParameter + "(&|$)");
      Matcher matcher = pattern.matcher(queryString);

      boolean hasExactParam = matcher.find();
      if (!hasExactParam) {
        throw new InvalidArgumentException(
            "It is prohibited to use the param " + queryParameter + " with values.");
      }
    }
  }
}
