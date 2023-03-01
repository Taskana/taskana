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

/**
 * This exception is thrown when an already existing {@linkplain Workbasket} was tried to be
 * created.
 */
public class WorkbasketAlreadyExistException extends TaskanaException {

  public static final String ERROR_KEY = "WORKBASKET_ALREADY_EXISTS";
  private final String key;
  private final String domain;

  public WorkbasketAlreadyExistException(String key, String domain) {
    super(
        String.format("A Workbasket with key '%s' already exists in domain '%s'.", key, domain),
        ErrorCode.of(ERROR_KEY, MapCreator.of("workbasketKey", key, "domain", domain)));
    this.key = key;
    this.domain = domain;
  }

  public String getKey() {
    return key;
  }

  public String getDomain() {
    return domain;
  }
}
