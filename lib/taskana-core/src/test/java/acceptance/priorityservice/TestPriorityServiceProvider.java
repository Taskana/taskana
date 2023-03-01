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
package acceptance.priorityservice;

import java.time.Duration;
import java.time.Instant;
import java.util.OptionalInt;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.models.TaskSummary;

public class TestPriorityServiceProvider implements PriorityServiceProvider {

  private static final int MULTIPLIER = 10;

  private WorkingTimeCalculator calculator;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {
    calculator = taskanaEngine.getWorkingTimeCalculator();
  }

  @Override
  public OptionalInt calculatePriority(TaskSummary taskSummary) {

    long priority;
    try {
      priority =
          calculator.workingTimeBetween(taskSummary.getCreated(), Instant.now()).toMinutes() + 1;
    } catch (Exception e) {
      priority = Duration.between(taskSummary.getCreated(), Instant.now()).toMinutes();
    }

    if (Boolean.parseBoolean(taskSummary.getCustomField(TaskCustomField.CUSTOM_6))) {
      priority *= MULTIPLIER;
    }

    return OptionalInt.of(Math.toIntExact(priority));
  }
}
