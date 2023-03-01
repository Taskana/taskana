/*-
 * #%L
 * pro.taskana.history:taskana-simplehistory-provider
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
package acceptance.events.classification;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEventType;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnClassificationsCreationAccTest extends AbstractAccTest {

  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final ClassificationService classificationService =
      taskanaEngine.getClassificationService();
  private final ClassificationHistoryEventMapper classificationHistoryEventMapper =
      getClassificationHistoryEventMapper();

  @WithAccessId(user = "admin")
  @Test
  void should_CreateClassificationCreatedHistoryEvents_When_ClassificationIsDeleted()
      throws Exception {

    Classification newClassification =
        classificationService.newClassification("somekey", "DOMAIN_A", "TASK");
    newClassification.setDescription("some description");
    newClassification.setServiceLevel("P1D");
    newClassification = classificationService.createClassification(newClassification);

    List<ClassificationHistoryEvent> events =
        historyService
            .createClassificationHistoryQuery()
            .classificationIdIn(newClassification.getId())
            .list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();
    String details = classificationHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(eventType).isEqualTo(ClassificationHistoryEventType.CREATED.getName());

    assertThat(details).contains("\"newValue\":\"some description\"");
  }
}
