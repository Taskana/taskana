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
 * This exception is thrown when the {@linkplain Classification#getServiceLevel() service level} of
 * the {@linkplain Classification} has not the required format. The {@linkplain
 * Classification#getServiceLevel() service level} has to be a positive ISO-8601 duration format and
 * TASKANA only supports whole days. The format must be 'PnD'.
 */
public class MalformedServiceLevelException extends TaskanaException {

  public static final String ERROR_KEY = "CLASSIFICATION_SERVICE_LEVEL_MALFORMED";
  private final String serviceLevel;
  private final String classificationKey;
  private final String domain;

  public MalformedServiceLevelException(
      String serviceLevel, String classificationKey, String domain) {
    super(
        String.format(
            "The provided service level '%s' of the "
                + "Classification with key '%s' and domain '%s' is invalid."
                + "The service level has to be a positive ISO-8601 duration format. "
                + "Furthermore, TASKANA only supports whole days; "
                + "the service level must be in the format 'PnD'",
            serviceLevel, classificationKey, domain),
        ErrorCode.of(
            ERROR_KEY,
            MapCreator.of(
                "classificationKey",
                classificationKey,
                "domain",
                domain,
                "serviceLevel",
                serviceLevel)));
    this.serviceLevel = serviceLevel;
    this.classificationKey = classificationKey;
    this.domain = domain;
  }

  public String getServiceLevel() {
    return serviceLevel;
  }

  public String getClassificationKey() {
    return classificationKey;
  }

  public String getDomain() {
    return domain;
  }
}
