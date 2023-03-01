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
package acceptance.events.workbasket;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnWorkbasketUpdateAccTest extends AbstractAccTest {

  private final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final WorkbasketHistoryEventMapper workbasketHistoryEventMapper =
      getWorkbasketHistoryEventMapper();

  @WithAccessId(user = "businessadmin")
  @Test
  void should_CreateWorkbasketUpdatedHistoryEvent_When_WorkbasketIsUpdated() throws Exception {

    Workbasket workbasket = workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");

    List<WorkbasketHistoryEvent> events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasket.getId()).list();

    assertThat(events).isEmpty();

    workbasket.setName("new name");
    workbasket.setDescription("new description");
    workbasket.setType(WorkbasketType.TOPIC);
    workbasket.setOrgLevel1("new level 1");
    workbasket.setOrgLevel2("new level 2");
    workbasket.setOrgLevel3("new level 3");
    workbasket.setOrgLevel4("new level 4");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_1, "new custom 1");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_2, "new custom 2");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_3, "new custom 3");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_4, "new custom 4");
    workbasket.setDescription("new description");
    workbasketService.updateWorkbasket(workbasket);

    events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasket.getId()).list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();
    String details = workbasketHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(eventType).isEqualTo(WorkbasketHistoryEventType.UPDATED.getName());

    assertThat(details).contains("\"new level 1\"");
  }
}
