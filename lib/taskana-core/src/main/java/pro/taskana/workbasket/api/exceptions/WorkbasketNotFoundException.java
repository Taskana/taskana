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
package pro.taskana.workbasket.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.workbasket.api.models.Workbasket;

/** This exception is thrown when a specific {@linkplain Workbasket} is not in the database. */
public class WorkbasketNotFoundException extends TaskanaException {

  public static final String ERROR_KEY_ID = "WORKBASKET_WITH_ID_NOT_FOUND";
  public static final String ERROR_KEY_KEY_DOMAIN = "WORKBASKET_WITH_KEY_NOT_FOUND";
  private final String id;
  private final String key;
  private final String domain;

  public WorkbasketNotFoundException(String id) {
    super(
        String.format("Workbasket with id '%s' was not found.", id),
        ErrorCode.of(ERROR_KEY_ID, MapCreator.of("workbasketId", id)));
    this.id = id;
    key = null;
    domain = null;
  }

  public WorkbasketNotFoundException(String key, String domain) {
    super(
        String.format("Workbasket with key '%s' and domain '%s' was not found.", key, domain),
        ErrorCode.of(ERROR_KEY_KEY_DOMAIN, MapCreator.of("workbasketKey", key, "domain", domain)));
    id = null;
    this.key = key;
    this.domain = domain;
  }

  public String getId() {
    return id;
  }

  public String getKey() {
    return key;
  }

  public String getDomain() {
    return domain;
  }
}
