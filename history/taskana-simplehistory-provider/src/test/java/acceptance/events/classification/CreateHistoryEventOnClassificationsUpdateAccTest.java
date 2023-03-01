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

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEventType;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnClassificationsUpdateAccTest extends AbstractAccTest {

  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final ClassificationService classificationService =
      taskanaEngine.getClassificationService();
  private final ClassificationHistoryEventMapper classificationHistoryEventMapper =
      getClassificationHistoryEventMapper();

  @WithAccessId(user = "businessadmin")
  @Test
  void should_CreateClassificationUpdatedHistoryEvent_When_ClassificationIsUpdated()
      throws Exception {

    Classification classification =
        classificationService.getClassification("CLI:000000000000000000000000000000000017");

    List<ClassificationHistoryEvent> events =
        historyService
            .createClassificationHistoryQuery()
            .classificationIdIn(classification.getId())
            .list();

    assertThat(events).isEmpty();

    classification.setName("new name");
    classification.setDescription("new description");
    classification.setCategory("EXTERNAL");
    classification.setCustomField(ClassificationCustomField.CUSTOM_1, "new custom 1");
    classification.setCustomField(ClassificationCustomField.CUSTOM_2, "new custom 2");
    classification.setCustomField(ClassificationCustomField.CUSTOM_3, "new custom 3");
    classification.setCustomField(ClassificationCustomField.CUSTOM_4, "new custom 4");
    classificationService.updateClassification(classification);

    events =
        historyService
            .createClassificationHistoryQuery()
            .classificationIdIn(classification.getId())
            .list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();
    String details = classificationHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(eventType).isEqualTo(WorkbasketHistoryEventType.UPDATED.getName());

    assertThat(details).contains("\"newValue\":\"new description\"");
  }
}
