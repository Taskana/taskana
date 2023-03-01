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
package pro.taskana.task.internal.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.AttachmentSummary;

/** Attachment entity. */
public class AttachmentImpl extends AttachmentSummaryImpl implements Attachment {

  private Map<String, String> customAttributes = new HashMap<>();

  public AttachmentImpl() {}

  private AttachmentImpl(AttachmentImpl copyFrom) {
    super(copyFrom);
    customAttributes = new HashMap<>(copyFrom.customAttributes);
  }

  @Override
  public Map<String, String> getCustomAttributeMap() {
    return getCustomAttributes();
  }

  @Override
  public void setCustomAttributeMap(Map<String, String> customAttributes) {
    setCustomAttributes(customAttributes);
  }

  @Override
  public AttachmentSummary asSummary() {
    AttachmentSummaryImpl summary = new AttachmentSummaryImpl();
    summary.setClassificationSummary(this.classificationSummary);
    summary.setCreated(this.created);
    summary.setId(this.id);
    summary.setModified(this.modified);
    summary.setReceived(this.received);
    summary.setTaskId(this.taskId);
    summary.setChannel(this.channel);
    summary.setObjectReference(this.objectReference);
    return summary;
  }

  public Map<String, String> getCustomAttributes() {
    if (customAttributes == null) {
      customAttributes = new HashMap<>();
    }
    return customAttributes;
  }

  public void setCustomAttributes(Map<String, String> customAttributes) {
    this.customAttributes = customAttributes;
  }

  @Override
  public AttachmentImpl copy() {
    return new AttachmentImpl(this);
  }

  @Override
  protected boolean canEqual(Object other) {
    return (!(other instanceof AttachmentImpl));
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), customAttributes);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof AttachmentImpl)) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    AttachmentImpl other = (AttachmentImpl) obj;
    if (other.canEqual(this)) {
      return false;
    }
    return Objects.equals(customAttributes, other.customAttributes);
  }

  @Override
  public String toString() {
    return "AttachmentImpl [id="
        + id
        + ", taskId="
        + taskId
        + ", created="
        + created
        + ", modified="
        + modified
        + ", classificationSummary="
        + classificationSummary
        + ", objectReference="
        + objectReference
        + ", channel="
        + channel
        + ", received="
        + received
        + ", customAttributes="
        + customAttributes
        + "]";
  }
}
