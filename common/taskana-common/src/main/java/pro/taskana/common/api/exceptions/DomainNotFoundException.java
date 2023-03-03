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
package pro.taskana.common.api.exceptions;

import pro.taskana.common.internal.util.MapCreator;

/** This exception is thrown when the specified domain is not found in the configuration. */
public class DomainNotFoundException extends TaskanaException {

  public static final String ERROR_KEY = "DOMAIN_NOT_FOUND";
  private final String domain;

  public DomainNotFoundException(String domain) {
    super(
        String.format("Domain '%s' does not exist in the configuration", domain),
        ErrorCode.of(ERROR_KEY, MapCreator.of("domain", domain)));
    this.domain = domain;
  }

  public String getDomain() {
    return domain;
  }
}
