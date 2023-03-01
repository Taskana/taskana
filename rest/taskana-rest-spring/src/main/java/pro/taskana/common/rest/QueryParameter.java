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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pro.taskana.common.api.IntInterval;
import pro.taskana.common.api.TimeInterval;

public interface QueryParameter<E, R> {

  R apply(E entity);

  default String[] wrapElementsInLikeStatement(String[] list) {
    return Arrays.stream(list).map(item -> "%" + item + "%").toArray(String[]::new);
  }

  default TimeInterval[] extractTimeIntervals(Instant[] instants) {
    List<TimeInterval> timeIntervalsList = new ArrayList<>();
    for (int i = 0; i < instants.length - 1; i += 2) {
      Instant left = instants[i];
      Instant right = instants[i + 1];
      if (left != null || right != null) {
        timeIntervalsList.add(new TimeInterval(left, right));
      }
    }

    return timeIntervalsList.toArray(new TimeInterval[0]);
  }

  default IntInterval[] extractIntIntervals(Integer[] boundaries) {
    List<IntInterval> intervalsList = new ArrayList<>();
    for (int i = 0; i < boundaries.length - 1; i += 2) {
      Integer left = boundaries[i];
      Integer right = boundaries[i + 1];
      if (left != null || right != null) {
        intervalsList.add(new IntInterval(left, right));
      }
    }
    return intervalsList.toArray(new IntInterval[0]);
  }
}
