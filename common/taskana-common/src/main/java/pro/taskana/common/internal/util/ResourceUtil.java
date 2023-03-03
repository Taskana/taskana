/*-
 * #%L
 * pro.taskana:taskana-common
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
package pro.taskana.common.internal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ResourceUtil {

  private ResourceUtil() {
    throw new IllegalStateException("utility class");
  }

  public static String readResourceAsString(Class<?> clazz, String resource) throws IOException {
    try (InputStream fileStream = clazz.getResourceAsStream(resource)) {
      if (fileStream == null) {
        return null;
      }
      try (Reader inputStreamReader = new InputStreamReader(fileStream, StandardCharsets.UTF_8);
          BufferedReader reader = new BufferedReader(inputStreamReader)) {
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
      }
    }
  }
}
