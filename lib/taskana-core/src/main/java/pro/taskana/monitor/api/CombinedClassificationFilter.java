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
package pro.taskana.monitor.api;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.Task;

/**
 * The CombinedClassificationFilter is a pair of a classificationId for a {@linkplain Task} and a
 * classificationId for the corresponding {@linkplain Attachment}. Such pair can only be created for
 * tasks that have no more than one {@linkplain Attachment}. The pair is used to filter the
 * {@linkplain WorkbasketReport} by the {@linkplain Classification} of the {@linkplain Attachment}.
 * To filter by the {@linkplain Classification} of the {@linkplain Task}, the
 * attachmentClassificationId of the CombinedClassificationFilter should be NULL.
 */
public class CombinedClassificationFilter {

  private String taskClassificationId;
  private String attachmentClassificationId;

  public CombinedClassificationFilter(String taskClassificationId) {
    this.taskClassificationId = taskClassificationId;
  }

  public CombinedClassificationFilter(
      String taskClassificationId, String attachmentClassificationId) {
    this.taskClassificationId = taskClassificationId;
    this.attachmentClassificationId = attachmentClassificationId;
  }

  public String getTaskClassificationId() {
    return this.taskClassificationId;
  }

  public void setTaskClassificationId(String taskClassificationId) {
    this.taskClassificationId = taskClassificationId;
  }

  public String getAttachmentClassificationId() {
    return this.attachmentClassificationId;
  }

  public void setAttachmentClassificationId(String attachmentClassificationId) {
    this.attachmentClassificationId = attachmentClassificationId;
  }

  @Override
  public String toString() {
    return "CombinedClassificationFilter [taskClassificationId="
        + taskClassificationId
        + ", attachmentClassificationId="
        + attachmentClassificationId
        + "]";
  }
}
