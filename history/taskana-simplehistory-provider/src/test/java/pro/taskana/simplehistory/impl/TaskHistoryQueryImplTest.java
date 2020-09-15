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
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/** Unit Test for SimpleHistoryServiceImplTest. */
@ExtendWith(MockitoExtension.class)
class TaskHistoryQueryImplTest {

  private static final String ID_PREFIX_HISTORY_EVENT = "HEI";

  private TaskHistoryQueryImpl historyQueryImpl;

  @Mock private TaskanaHistoryEngineImpl taskanaHistoryEngineMock;

  @Mock private SqlSession sqlSessionMock;

  @BeforeEach
  void setup() {
    historyQueryImpl = new TaskHistoryQueryImpl(taskanaHistoryEngineMock);
  }

  @Test
  void should_ReturnList_When_CallingListMethodOnTaskHistoryQuery() throws Exception {
    List<TaskHistoryEvent> returnList = new ArrayList<>();
    returnList.add(createHistoryEvent("abcd", "T22", "car", "BV", "this was important", null));
    TimeInterval interval = new TimeInterval(Instant.now().minusNanos(1000), Instant.now());

    doNothing().when(taskanaHistoryEngineMock).openConnection();
    doNothing().when(taskanaHistoryEngineMock).returnConnection();
    when(taskanaHistoryEngineMock.getSqlSession()).thenReturn(sqlSessionMock);
    when(sqlSessionMock.selectList(any(), any())).thenReturn(new ArrayList<>(returnList));

    List<TaskHistoryEvent> result =
        historyQueryImpl
            .taskIdIn("TKI:01")
            .workbasketKeyIn(
                "T22", "some_long_long, long loooooooooooooooooooooooooooooooooooong String.")
            .userIdIn("BV")
            .createdWithin(interval)
            .list();

    validateMockitoUsage();
    assertThat(result).isEqualTo(returnList);
  }

  private TaskHistoryEvent createHistoryEvent(
      String taskId,
      String workbasketKey,
      String type,
      String userId,
      String details,
      Instant created) {
    TaskHistoryEvent he = new TaskHistoryEvent();
    he.setId(IdGenerator.generateWithPrefix(ID_PREFIX_HISTORY_EVENT));
    he.setUserId(userId);
    he.setDetails(details);
    he.setTaskId(taskId);
    he.setWorkbasketKey(workbasketKey);
    he.setEventType(type);
    he.setCreated(created);
    return he;
  }
}
