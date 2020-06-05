package pro.taskana.simplehistory.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.simplehistory.impl.mappings.HistoryEventMapper;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;

/** Unit Test for SimpleHistoryServiceImplTest. */
@ExtendWith(MockitoExtension.class)
class SimpleHistoryServiceImplTest {

  @InjectMocks @Spy private SimpleHistoryServiceImpl cutSpy;

  @Mock private HistoryEventMapper historyEventMapperMock;

  @Mock private HistoryQueryMapper historyQueryMapperMock;

  @Mock private TaskanaHistoryEngineImpl taskanaHistoryEngineMock;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfiguration;

  @Mock private SqlSessionManager sqlSessionManagerMock;

  @Test
  void testInitializeSimpleHistoryService() {
    when(sqlSessionManagerMock.getMapper(HistoryEventMapper.class))
        .thenReturn(historyEventMapperMock);
    when(sqlSessionManagerMock.getMapper(HistoryQueryMapper.class))
        .thenReturn(historyQueryMapperMock);
    when(taskanaHistoryEngineMock.getSqlSession()).thenReturn(sqlSessionManagerMock);
    doReturn(taskanaHistoryEngineMock).when(cutSpy).getTaskanaEngine(taskanaEngineConfiguration);
    cutSpy.initialize(taskanaEngineConfiguration);

    verify(sqlSessionManagerMock, times(2)).getMapper(any());
    verify(taskanaHistoryEngineMock, times(2)).getSqlSession();
  }

  @Test
  void testCreateEvent() throws SQLException {
    HistoryEventImpl expectedWb =
        AbstractAccTest.createHistoryEvent(
            "wbKey1", "taskId1", "type1", "wbKey2", "someUserId", "someDetails");

    cutSpy.create(expectedWb);
    verify(taskanaHistoryEngineMock, times(1)).openConnection();
    verify(historyEventMapperMock, times(1)).insert(expectedWb);
    verify(taskanaHistoryEngineMock, times(1)).returnConnection();
    assertThat(expectedWb.getCreated()).isNotNull();
  }

  @Test
  void testQueryEvent() throws SQLException {
    List<HistoryEventImpl> returnList = new ArrayList<>();
    returnList.add(
        AbstractAccTest.createHistoryEvent(
            "wbKey1", "taskId1", "type1", "wbKey2", "someUserId", "someDetails"));
    when(historyQueryMapperMock.queryHistoryEvent(any())).thenReturn(returnList);

    final List<HistoryEventImpl> result = cutSpy.createHistoryQuery().taskIdIn("taskId1").list();

    verify(taskanaHistoryEngineMock, times(1)).openConnection();
    verify(historyQueryMapperMock, times(1)).queryHistoryEvent(any());
    verify(taskanaHistoryEngineMock, times(1)).returnConnection();
    assertThat(result).hasSize(returnList.size());
    assertThat(result.get(0).getWorkbasketKey()).isEqualTo(returnList.get(0).getWorkbasketKey());
  }
}
