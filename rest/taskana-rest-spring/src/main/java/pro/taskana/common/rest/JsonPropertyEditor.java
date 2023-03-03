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
package pro.taskana.common.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.beans.PropertyEditorSupport;
import java.net.URLDecoder;

public class JsonPropertyEditor extends PropertyEditorSupport {

  private final ObjectMapper objectMapper;
  private final Class<?> requiredType;

  public JsonPropertyEditor(ObjectMapper objectMapper, Class<?> requiredType) {
    this.objectMapper = objectMapper;
    this.requiredType = requiredType;
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    if (text != null && !text.isEmpty()) {
      try {
        setValue(objectMapper.readValue(URLDecoder.decode(text, "UTF-8"), requiredType));
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
  }
}
