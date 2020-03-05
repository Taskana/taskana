package pro.taskana.simplehistory.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.validateMockitoUsage;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.common.api.TimeInterval;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;

/**
 * Unit Test for SimpleHistoryServiceImplTest.
 *
 * @author BV
 */
@ExtendWith(MockitoExtension.class)
public class HistoryQueryImplTest {

  private HistoryQueryImpl historyQueryImpl;

  @Mock private TaskanaHistoryEngineImpl taskanaHistoryEngineMock;

  @Mock private HistoryQueryMapper historyQueryMock;

  @BeforeEach
  public void setup() {
    historyQueryImpl = new HistoryQueryImpl(taskanaHistoryEngineMock, historyQueryMock);
  }

  @Test
  public void testShouldReturnList() throws SQLException {
    List<HistoryEventImpl> returnList = new ArrayList<>();
    returnList.add(createHistoryEvent("abcd", "T22", "car", "BV", "this was important", null));
    TimeInterval interval = new TimeInterval(Instant.now().minusNanos(1000), Instant.now());

    doNothing().when(taskanaHistoryEngineMock).openConnection();
    doNothing().when(taskanaHistoryEngineMock).returnConnection();
    doReturn(returnList).when(historyQueryMock).queryHistoryEvent(historyQueryImpl);

    List<HistoryEventImpl> result =
        historyQueryImpl
            .taskIdIn("TKI:01")
            .workbasketKeyIn(
                "T22", "some_long_long, long loooooooooooooooooooooooooooooooooooong String.")
            .userIdIn("BV")
            .commentLike("%as important")
            .createdWithin(interval)
            .list();

    validateMockitoUsage();
    assertThat(result).isEqualTo(returnList);
  }

  private HistoryEventImpl createHistoryEvent(
      String taskId,
      String workbasketKey,
      String type,
      String userId,
      String comment,
      Instant created) {
    HistoryEventImpl he = new HistoryEventImpl(userId);
    he.setTaskId(taskId);
    he.setWorkbasketKey(workbasketKey);
    he.setEventType(type);
    he.setComment(comment);
    he.setCreated(created);
    return he;
  }
}
