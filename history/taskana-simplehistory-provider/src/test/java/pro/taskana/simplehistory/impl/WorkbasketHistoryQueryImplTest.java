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
package pro.taskana.simplehistory.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEventType;

/** Unit Test for WorkbasketHistoryQueryImplTest. */
@ExtendWith(MockitoExtension.class)
class WorkbasketHistoryQueryImplTest {

  @Mock private TaskanaHistoryEngineImpl taskanaHistoryEngineMock;

  private WorkbasketHistoryQueryImpl historyQueryImpl;

  @Mock private SqlSession sqlSessionMock;

  @BeforeEach
  void setup() {
    historyQueryImpl = new WorkbasketHistoryQueryImpl(taskanaHistoryEngineMock);
  }

  @Test
  void should_ReturnList_When_CallingListMethodOnWorkbasketHistoryQuery() throws Exception {
    List<WorkbasketHistoryEvent> returnList = new ArrayList<>();
    returnList.add(
        createHistoryEvent(
            "abcd",
            WorkbasketHistoryEventType.CREATED.getName(),
            "someUserId",
            "someDetails",
            null));
    TimeInterval interval = new TimeInterval(Instant.now().minusNanos(1000), Instant.now());

    doNothing().when(taskanaHistoryEngineMock).openConnection();
    doNothing().when(taskanaHistoryEngineMock).returnConnection();
    when(taskanaHistoryEngineMock.getSqlSession()).thenReturn(sqlSessionMock);
    when(sqlSessionMock.selectList(any(), any())).thenReturn(new ArrayList<>(returnList));

    List<WorkbasketHistoryEvent> result =
        historyQueryImpl
            .workbasketIdIn("WBI:01")
            .keyIn("abcd", "some_random_string")
            .userIdIn("someUserId")
            .createdWithin(interval)
            .list();

    validateMockitoUsage();
    assertThat(result).isEqualTo(returnList);
  }

  private WorkbasketHistoryEvent createHistoryEvent(
      String workbasketKey, String type, String userId, String details, Instant created) {
    WorkbasketHistoryEvent he = new WorkbasketHistoryEvent();
    he.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_WORKBASKET_HISTORY_EVENT));
    he.setUserId(userId);
    he.setDetails(details);
    he.setKey(workbasketKey);
    he.setEventType(type);
    he.setCreated(created);
    return he;
  }
}
