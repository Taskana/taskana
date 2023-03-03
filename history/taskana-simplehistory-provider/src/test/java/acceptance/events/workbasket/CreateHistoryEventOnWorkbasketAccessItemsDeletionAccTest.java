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
import static pro.taskana.common.api.BaseQuery.SortDirection.ASCENDING;

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
import pro.taskana.workbasket.api.WorkbasketService;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnWorkbasketAccessItemsDeletionAccTest extends AbstractAccTest {

  private final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final WorkbasketHistoryEventMapper workbasketHistoryEventMapper =
      getWorkbasketHistoryEventMapper();

  @WithAccessId(user = "admin")
  @Test
  void should_CreateWorkbasketAccessItemDeletedHistoryEvents_When_AccessItemsAreDeleted()
      throws Exception {

    final String accessId = "teamlead-1";

    String[] workbasketIds =
        new String[] {
          "WBI:100000000000000000000000000000000001",
          "WBI:100000000000000000000000000000000004",
          "WBI:100000000000000000000000000000000005",
          "WBI:100000000000000000000000000000000010"
        };

    List<WorkbasketHistoryEvent> events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketIds).list();

    assertThat(events).isEmpty();

    workbasketService.deleteWorkbasketAccessItemsForAccessId(accessId);

    events =
        historyService
            .createWorkbasketHistoryQuery()
            .workbasketIdIn(workbasketIds)
            .orderByWorkbasketId(ASCENDING)
            .list();

    assertThat(events).hasSize(4);

    String details = workbasketHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(events)
        .extracting(WorkbasketHistoryEvent::getEventType)
        .containsOnly(WorkbasketHistoryEventType.ACCESS_ITEM_DELETED.getName());

    assertThat(details).contains("\"oldValue\":\"WBI:100000000000000000000000000000000001\"");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_NotCreateWorkbasketAccessItemDeletedHistoryEvents_When_ProvidingInvalidAccessId()
      throws Exception {

    final String workbasketId = "WBI:100000000000000000000000000000000011";

    final String accessId = "NonExistingWorkbasketAccessItemID";

    List<WorkbasketHistoryEvent> events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketId).list();

    assertThat(events).isEmpty();

    workbasketService.deleteWorkbasketAccessItemsForAccessId(accessId);

    events = historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketId).list();

    assertThat(events).isEmpty();
  }
}
