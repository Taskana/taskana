package pro.taskana.simplehistory.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.simplehistory.impl.mappings.HistoryEventMapper;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;

/**
 * Unit Test for SimpleHistoryServiceImplTest.
 *
 * @author MMR
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TaskanaHistoryEngineImpl.class)
@PowerMockIgnore("javax.management.*")
public class SimpleHistoryServiceImplTest {

  @InjectMocks private SimpleHistoryServiceImpl cutSpy;

  @Mock private HistoryEventMapper historyEventMapperMock;

  @Mock private HistoryQueryMapper historyQueryMapperMock;

  @Mock private TaskanaHistoryEngineImpl taskanaHistoryEngineMock;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfiguration;

  @Mock private SqlSessionManager sqlSessionManagerMock;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testInitializeSimpleHistoryService() throws SQLException {
    doReturn(historyEventMapperMock)
        .when(sqlSessionManagerMock)
        .getMapper(HistoryEventMapper.class);
    doReturn(historyQueryMapperMock)
        .when(sqlSessionManagerMock)
        .getMapper(HistoryQueryMapper.class);
    doReturn(sqlSessionManagerMock).when(taskanaHistoryEngineMock).getSqlSession();
    PowerMockito.mockStatic(TaskanaHistoryEngineImpl.class);
    Mockito.when(TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngineConfiguration))
        .thenReturn(taskanaHistoryEngineMock);
    cutSpy.initialize(taskanaEngineConfiguration);

    verify(sqlSessionManagerMock, times(2)).getMapper(any());
    verify(taskanaHistoryEngineMock, times(2)).getSqlSession();
  }

  @Test
  public void testInitializeSimpleHistoryServiceWithNonDefaultSchemaName() throws SQLException {

    doReturn(historyEventMapperMock)
        .when(sqlSessionManagerMock)
        .getMapper(HistoryEventMapper.class);
    doReturn(historyQueryMapperMock)
        .when(sqlSessionManagerMock)
        .getMapper(HistoryQueryMapper.class);
    doReturn(sqlSessionManagerMock).when(taskanaHistoryEngineMock).getSqlSession();
    PowerMockito.mockStatic(TaskanaHistoryEngineImpl.class);
    Mockito.when(TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngineConfiguration))
        .thenReturn(taskanaHistoryEngineMock);
    cutSpy.initialize(taskanaEngineConfiguration);

    verify(sqlSessionManagerMock, times(2)).getMapper(any());
    verify(taskanaHistoryEngineMock, times(2)).getSqlSession();
  }

  @Test
  public void testCreateEvent() throws SQLException {
    HistoryEventImpl expectedWb =
        AbstractAccTest.createHistoryEvent("wbKey1", "taskId1", "type1", "Some comment", "wbKey2");
    doNothing().when(historyEventMapperMock).insert(expectedWb);

    cutSpy.create(expectedWb);
    verify(taskanaHistoryEngineMock, times(1)).openConnection();
    verify(historyEventMapperMock, times(1)).insert(expectedWb);
    verify(taskanaHistoryEngineMock, times(1)).returnConnection();
    assertNotNull(expectedWb.getCreated());
  }

  @Test
  public void testQueryEvent() throws SQLException {
    List<HistoryEventImpl> returnList = new ArrayList<>();
    returnList.add(
        AbstractAccTest.createHistoryEvent("wbKey1", "taskId1", "type1", "Some comment", "wbKey2"));
    doReturn(returnList).when(historyQueryMapperMock).queryHistoryEvent(any());

    final List<HistoryEventImpl> result = cutSpy.createHistoryQuery().taskIdIn("taskId1").list();

    verify(taskanaHistoryEngineMock, times(1)).openConnection();
    verify(historyQueryMapperMock, times(1)).queryHistoryEvent(any());
    verify(taskanaHistoryEngineMock, times(1)).returnConnection();
    assertEquals(returnList.size(), result.size());
    assertEquals(returnList.get(0).getWorkbasketKey(), result.get(0).getWorkbasketKey());
  }
}
