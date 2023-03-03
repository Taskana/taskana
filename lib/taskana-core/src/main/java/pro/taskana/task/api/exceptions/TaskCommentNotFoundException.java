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
package pro.taskana.task.api.exceptions;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.task.api.models.TaskComment;

/** This exception is thrown when a specific {@linkplain TaskComment} is not in the database. */
public class TaskCommentNotFoundException extends TaskanaException {

  public static final String ERROR_KEY = "TASK_COMMENT_NOT_FOUND";
  private final String taskCommentId;

  public TaskCommentNotFoundException(String taskCommentId) {
    super(
        String.format("TaskComment with id '%s' was not found", taskCommentId),
        ErrorCode.of(ERROR_KEY, MapCreator.of("taskCommentId", taskCommentId)));
    this.taskCommentId = taskCommentId;
  }

  public String getTaskCommentId() {
    return taskCommentId;
  }
}
