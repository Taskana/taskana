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
package pro.taskana.classification.api.models;

import pro.taskana.classification.api.ClassificationCustomField;

/**
 * Interface for ClassificationSummaries. This is a specific short model-object which only requieres
 * the most important information. Detailed information can be load afterwards via id.
 */
public interface ClassificationSummary {

  /**
   * Returns the id of the Classification.
   *
   * @return the id of the Classification
   */
  String getId();

  /**
   * Returns the key of the Classification.
   *
   * @return the key of the Classification
   */
  String getKey();

  /**
   * Returns the category of the Classification.
   *
   * @return the category of the Classification
   */
  String getCategory();

  /**
   * Returns the type of the Classification.
   *
   * @return the type of the Classification
   */
  String getType();

  /**
   * Returns the domain of the Classification.
   *
   * @return the domain of the Classification
   */
  String getDomain();

  /**
   * Returns the name of the Classification.
   *
   * @return the name of the Classification
   */
  String getName();

  /**
   * Returns the id of the parent Classification.
   *
   * @return parentId
   */
  String getParentId();

  /**
   * Returns the key of the parent Classification.
   *
   * @return parentKey
   */
  String getParentKey();

  /**
   * Returns the serviceLevel of the Classification. It is a String in ISO-8601 duration format. See
   * the parse() method of {@code Duration} for details.
   *
   * @return serviceLevel
   */
  String getServiceLevel();

  /**
   * Returns the applicationEntryPoint of the Classification.
   *
   * @return applicationEntryPoint
   */
  String getApplicationEntryPoint();

  /**
   * Returns the priority of the Classification.
   *
   * @return priority
   */
  int getPriority();

  /**
   * Returns the value of the specified {@linkplain ClassificationCustomField
   * ClassificationCustomField} of the Classification.
   *
   * @param customField identifies which {@linkplain ClassificationCustomField
   *     ClassificationCustomField} is requested
   * @return the value for the given {@linkplain ClassificationCustomField
   *     ClassificationCustomField}
   * @deprecated Use {@linkplain #getCustomField(ClassificationCustomField)} instead
   */
  @Deprecated
  String getCustomAttribute(ClassificationCustomField customField);

  /**
   * Returns the value of the specified {@linkplain ClassificationCustomField
   * ClassificationCustomField} of the classification.
   *
   * @param customField identifies which {@linkplain ClassificationCustomField
   *     ClassificationCustomField} is requested
   * @return the value for the given {@linkplain ClassificationCustomField
   *     ClassificationCustomField}
   */
  String getCustomField(ClassificationCustomField customField);

  /**
   * Duplicates this ClassificationSummary without the id.
   *
   * @return a copy of this ClassificationSummary
   */
  ClassificationSummary copy();
}
