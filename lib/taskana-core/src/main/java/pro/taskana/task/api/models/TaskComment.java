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
package pro.taskana.task.api.models;

import java.time.Instant;

/** TaskComment-Interface to specify attributes of the TaskComment. */
public interface TaskComment {

  /**
   * Returns the id of the TaskComment.
   *
   * @return taskId
   */
  String getId();

  /**
   * Returns the id of the associated Task.
   *
   * @return taskId
   */
  String getTaskId();

  /**
   * Returns the id of the creator of the TaskComment.
   *
   * @return creator
   */
  String getCreator();

  /**
   * Returns the long name of the creator of the TaskComment.
   *
   * @return the long name of the creator
   */
  String getCreatorFullName();

  /**
   * Returns the content of the TaskComment.
   *
   * @return textField
   */
  String getTextField();

  /**
   * Sets the content of the TaskComment.
   *
   * @param textField the textField
   */
  void setTextField(String textField);

  /**
   * Returns the time when the TaskComment was created.
   *
   * @return the created Instant
   */
  Instant getCreated();

  /**
   * Returns the time when the TaskComment was last modified.
   *
   * @return the last modified Instant
   */
  Instant getModified();

  /**
   * Duplicates this TaskComment without the id.
   *
   * @return a copy of this TaskComment
   */
  TaskComment copy();
}
