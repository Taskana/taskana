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
package pro.taskana.spi.history.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/**
 * This exception is thrown when the {@linkplain TaskHistoryEvent} with the specified {@linkplain
 * TaskHistoryEvent#getId() id} was not found.
 */
public class TaskanaHistoryEventNotFoundException extends TaskanaException {

  public static final String ERROR_KEY = "HISTORY_EVENT_NOT_FOUND";
  private final String historyEventId;

  public TaskanaHistoryEventNotFoundException(String historyEventId) {
    super(
        String.format("TaskHistoryEvent with id '%s' was not found", historyEventId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("historyEventId", historyEventId)));
    this.historyEventId = historyEventId;
  }

  public String getHistoryEventId() {
    return historyEventId;
  }
}
