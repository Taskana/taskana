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

import pro.taskana.classification.api.models.ClassificationSummary;

/**
 * Interface for AttachmentSummaries. This is a specific short model-object which only contains the
 * most important information.
 */
public interface AttachmentSummary {

  /**
   * Returns the id of the {@linkplain Attachment}.
   *
   * @return attachmentId
   */
  String getId();

  /**
   * Returns the id of the associated {@linkplain Task}.
   *
   * @return taskId
   */
  String getTaskId();

  /**
   * Returns the time when the {@linkplain Attachment} was created.
   *
   * @return the created Instant
   */
  Instant getCreated();

  /**
   * Returns the time when the {@linkplain Attachment} was last modified.
   *
   * @return the last modified Instant
   */
  Instant getModified();

  /**
   * Returns the {@linkplain ObjectReference primaryObjectReference} of the {@linkplain Attachment}.
   *
   * @return {@linkplain ObjectReference primaryObjectReference} of the {@linkplain Attachment}
   */
  ObjectReference getObjectReference();

  /**
   * Returns the channel on which the {@linkplain Attachment} was received.
   *
   * @return the channel
   */
  String getChannel();

  /**
   * Returns the {@linkplain ClassificationSummary classificationSummary} of the {@linkplain
   * Attachment}.
   *
   * @return {@linkplain ClassificationSummary classificationSummary}
   */
  ClassificationSummary getClassificationSummary();

  /**
   * Returns the time when the {@linkplain Attachment} was received.
   *
   * @return the received Instant
   */
  Instant getReceived();

  /**
   * Duplicates this AttachmentSummary without the id and taskId.
   *
   * @return a copy of this AttachmentSummary
   */
  AttachmentSummary copy();
}
