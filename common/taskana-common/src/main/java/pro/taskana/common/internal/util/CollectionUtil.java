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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CollectionUtil {

  private CollectionUtil() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Splits a collection with objects of type T into chunks of a certain size.
   *
   * @param <T> type of elements inside collection
   * @param inputCollection collection to be divided
   * @param size maximal number of elements inside chunk
   * @return list containing the chunks
   */
  public static <T> Collection<List<T>> partitionBasedOnSize(
      Collection<T> inputCollection, int size) {
    final AtomicInteger counter = new AtomicInteger(0);
    return inputCollection.stream()
        .collect(Collectors.groupingBy(s -> counter.getAndIncrement() / size))
        .values();
  }
}
