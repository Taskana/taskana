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
package pro.taskana.task.rest.models;

import java.time.Instant;
import org.springframework.hateoas.RepresentationModel;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.task.api.models.AttachmentSummary;

/** EntityModel class for {@link AttachmentSummary}. */
public class AttachmentSummaryRepresentationModel
    extends RepresentationModel<AttachmentSummaryRepresentationModel> {

  /** Unique Id. */
  protected String attachmentId;
  /** the referenced task id. */
  protected String taskId;
  /** The creation timestamp in the system. */
  protected Instant created;
  /** The timestamp of the last modification. */
  protected Instant modified;
  /** The timestamp of the entry date. */
  protected Instant received;
  /** The classification of this attachment. */
  protected ClassificationSummaryRepresentationModel classificationSummary;
  /** The Objects primary ObjectReference. */
  protected ObjectReferenceRepresentationModel objectReference;
  /** Determines on which channel this attachment was received. */
  protected String channel;

  public String getAttachmentId() {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId) {
    this.attachmentId = attachmentId;
  }

  public String getTaskId() {
    return taskId;
  }

  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  public Instant getCreated() {
    return created;
  }

  public void setCreated(Instant created) {
    this.created = created;
  }

  public Instant getModified() {
    return modified;
  }

  public void setModified(Instant modified) {
    this.modified = modified;
  }

  public Instant getReceived() {
    return received;
  }

  public void setReceived(Instant received) {
    this.received = received;
  }

  public ClassificationSummaryRepresentationModel getClassificationSummary() {
    return classificationSummary;
  }

  public void setClassificationSummary(
      ClassificationSummaryRepresentationModel classificationSummary) {
    this.classificationSummary = classificationSummary;
  }

  public ObjectReferenceRepresentationModel getObjectReference() {
    return objectReference;
  }

  public void setObjectReference(ObjectReferenceRepresentationModel objectReference) {
    this.objectReference = objectReference;
  }

  public String getChannel() {
    return channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }
}
