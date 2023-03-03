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
package acceptance;

import static pro.taskana.common.api.SharedConstants.MASTER_DOMAIN;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.TaskImpl;

public class CreateTaskModelHelper {

  public static Classification createDummyClassification() {
    ClassificationImpl classification = new ClassificationImpl();
    classification.setName("dummy-classification");
    classification.setDomain("dummy-domain");
    classification.setKey("dummy-classification-key");
    classification.setId("DummyClassificationId");
    return classification;
  }

  public static AttachmentImpl createAttachment(String id, String taskId) {
    AttachmentImpl attachment = new AttachmentImpl();
    attachment.setId(id);
    attachment.setTaskId(taskId);
    return attachment;
  }

  public static TaskImpl createUnitTestTask(
      String id, String name, String workbasketKey, Classification classification) {
    TaskImpl task = new TaskImpl();
    task.setId(id);
    task.setExternalId(id);
    task.setName(name);
    task.setWorkbasketKey(workbasketKey);
    task.setDomain(MASTER_DOMAIN);
    task.setAttachments(new ArrayList<>());
    Instant now = Instant.now().minus(Duration.ofMinutes(1L));
    task.setReceived(now);
    task.setCreated(now);
    task.setModified(now);
    task.setState(TaskState.READY);
    if (classification == null) {
      classification = createDummyClassification();
    }
    task.setClassificationSummary(classification.asSummary());
    task.setClassificationKey(classification.getKey());
    task.setDomain(classification.getDomain());
    return task;
  }
}
