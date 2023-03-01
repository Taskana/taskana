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
package pro.taskana.classification.api.exceptions;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;

/**
 * This exception is thrown when a {@linkplain Classification} does already exits, but was tried to
 * be created with the same {@linkplain Classification#getId() id} and {@linkplain
 * Classification#getDomain() domain}.
 */
public class ClassificationAlreadyExistException extends TaskanaException {

  public static final String ERROR_KEY = "CLASSIFICATION_ALREADY_EXISTS";
  private final String domain;
  private final String classificationKey;

  public ClassificationAlreadyExistException(Classification classification) {
    this(classification.getKey(), classification.getDomain());
  }

  public ClassificationAlreadyExistException(String key, String domain) {
    super(
        String.format("A Classification with key '%s' already exists in domain '%s'.", key, domain),
        ErrorCode.of(ERROR_KEY, MapCreator.of("classificationKey", key, "domain", domain)));
    classificationKey = key;
    this.domain = domain;
  }

  public String getDomain() {
    return domain;
  }

  public String getClassificationKey() {
    return classificationKey;
  }
}
