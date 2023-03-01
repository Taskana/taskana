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
import java.util.Map;

import pro.taskana.classification.api.models.ClassificationSummary;

/** Attachment-Interface to specify attributes of an Attachment. */
public interface Attachment extends AttachmentSummary {

  /**
   * Sets the {@linkplain ObjectReference primaryObjectReference} of the Attachment.
   *
   * @param objectReference the {@linkplain ObjectReference primaryObjectReference} of the
   *     Attachment
   */
  void setObjectReference(ObjectReference objectReference);

  /**
   * Set the {@linkplain ClassificationSummary classificationSummary} for this Attachment.
   *
   * @param classificationSummary the {@linkplain ClassificationSummary} for this Attachment
   */
  void setClassificationSummary(ClassificationSummary classificationSummary);

  /**
   * Sets the time when the Attachment was received.
   *
   * @param received the time when the Attachment was received as Instant
   */
  void setReceived(Instant received);

  /**
   * Sets the channel on which the Attachment was received.
   *
   * @param channel the channel on which the Attachment was received
   */
  void setChannel(String channel);

  /**
   * Returns the custom attributes of this Attachment.
   *
   * @return customAttributes as {@linkplain Map}
   */
  Map<String, String> getCustomAttributeMap();

  /**
   * Sets the custom attribute Map of the Attachment.
   *
   * @param customAttributes a {@linkplain Map} that contains the custom attributes of the
   *     Attachment as key, value pairs
   */
  void setCustomAttributeMap(Map<String, String> customAttributes);

  /**
   * Returns a summary of the current Attachment.
   *
   * @return the {@linkplain AttachmentSummary} object for the current Attachment
   */
  AttachmentSummary asSummary();

  /**
   * Duplicates this Attachment without the id and taskId.
   *
   * @return a copy of this Attachment
   */
  Attachment copy();
}
