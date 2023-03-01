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
package pro.taskana.common.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Returning type for a bulk db interaction with errors. This wrapper is storing them with a
 * matching object ID.
 *
 * @param <K> unique keys for the logs.
 * @param <V> type of the stored informations
 */
public class BulkOperationResults<K, V extends Exception> {

  private final Map<K, V> errorMap = new HashMap<>();

  /**
   * Returning a list of current errors as map. If there are no errors the result will be empty.
   *
   * @return map of errors which can't be null.
   */
  public Map<K, V> getErrorMap() {
    return this.errorMap;
  }

  /**
   * Adding an appearing error to the map and list them by a unique ID as key.
   *
   * @param objectId unique key of a entity.
   * @param error occurred error of a interaction with the entity
   */
  public void addError(K objectId, V error) {
    this.errorMap.put(objectId, error);
  }

  /**
   * Returning the status of a bulk-error-log.
   *
   * @return true if there are logged errors.
   */
  public boolean containsErrors() {
    return !errorMap.isEmpty();
  }

  /**
   * Returns the stored error for a unique ID or NULL if there is no error stored or ID invalid.
   *
   * @param idKey which is mapped with an error
   * @return stored error for ID
   */
  public V getErrorForId(K idKey) {
    return errorMap.get(idKey);
  }

  /**
   * Returns the IDs of the Object with failed requests.
   *
   * @return a List of IDs that could not be processed successfully.
   */
  public List<K> getFailedIds() {
    return new ArrayList<>(this.errorMap.keySet());
  }

  /** Clearing the map - all entries will be removed. */
  public void clearErrors() {
    this.errorMap.clear();
  }

  /**
   * Add all errors from another BulkOperationResult to this.
   *
   * @param log the other log
   */
  public void addAllErrors(BulkOperationResults<? extends K, ? extends V> log) {
    if (log != null) {
      errorMap.putAll(log.errorMap);
    }
  }

  /**
   * Map from any exception type to Exception.
   *
   * @return map of errors which can't be null.
   */
  public BulkOperationResults<K, Exception> mapBulkOperationResults() {
    BulkOperationResults<K, Exception> bulkLogMapped = new BulkOperationResults<>();
    bulkLogMapped.addAllErrors(this);
    return bulkLogMapped;
  }

  @Override
  public String toString() {
    return "BulkOperationResults [errorMap=" + errorMap + "]";
  }
}
