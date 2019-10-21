package pro.taskana.simplehistory.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.validateMockitoUsage;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pro.taskana.TimeInterval;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;

/**
 * Unit Test for SimpleHistoryServiceImplTest.
 *
 * @author BV
 */
@RunWith(MockitoJUnitRunner.class)
public class HistoryQueryImplTest {

    private HistoryQueryImpl historyQueryImpl;

    @Mock
    private TaskanaHistoryEngineImpl taskanaHistoryEngineMock;

    @Mock
    private HistoryQueryMapper historyQueryMock;

    @Before
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

        List<HistoryEventImpl> result = historyQueryImpl
            .taskIdIn("TKI:01")
            .workbasketKeyIn("T22", "some_long_long, long loooooooooooooooooooooooooooooooooooong String.")
            .userIdIn("BV")
            .commentLike("%as important")
            .createdWithin(interval)
            .list();

        validateMockitoUsage();
        assertArrayEquals(returnList.toArray(), result.toArray());
    }

    private HistoryEventImpl createHistoryEvent(String taskId, String workbasketKey, String type, String userId,
        String comment, Instant created) {
        HistoryEventImpl he = new HistoryEventImpl();
        he.setTaskId(taskId);
        he.setWorkbasketKey(workbasketKey);
        he.setEventType(type);
        he.setUserId(userId);
        he.setComment(comment);
        he.setCreated(created);
        return he;
    }
}
