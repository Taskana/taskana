/*-
 * #%L
 * pro.taskana:taskana-rest-spring
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
}
