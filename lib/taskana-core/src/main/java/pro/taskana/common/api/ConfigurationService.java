/*-
 * #%L
 * pro.taskana:taskana-core
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
package pro.taskana.common.api;

import java.util.Map;
import java.util.Optional;

/** The Configuration Service manages all custom configuration options. */
public interface ConfigurationService {

  /**
   * Retrieve a specific value from all custom attributes.
   *
   * @param attribute the attribute key
   * @return the attribute value or nothing if the attribute does not exist
   */
  Optional<Object> getValue(String attribute);

  /**
   * Retrieve all custom attributes from the database.
   *
   * @return the custom attributes from the database
   */
  Map<String, Object> getAllCustomAttributes();

  /**
   * Override all custom attributes with the provided one.
   *
   * @param customAttributes the new custom attributes which should be persisted
   */
  void setAllCustomAttributes(Map<String, ?> customAttributes);
}
