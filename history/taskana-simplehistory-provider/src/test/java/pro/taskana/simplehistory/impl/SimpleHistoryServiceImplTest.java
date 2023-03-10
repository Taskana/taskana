package pro.taskana.simplehistory.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import acceptance.AbstractAccTest;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.simplehistory.impl.task.TaskHistoryEventMapper;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQueryMapper;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEventType;

/** Unit Test for SimpleHistoryServiceImplTest. */
@ExtendWith(MockitoExtension.class)
class SimpleHistoryServiceImplTest {

  @InjectMocks @Spy private SimpleHistoryServiceImpl cutSpy;

  @Mock private TaskHistoryEventMapper taskHistoryEventMapperMock;

  @Mock private TaskHistoryQueryMapper taskHistoryQueryMapperMock;

  @Mock private WorkbasketHistoryEventMapper workbasketHistoryEventMapperMock;

  @Mock private WorkbasketHistoryQueryMapper workbasketHistoryQueryMapperMock;

  @Mock private TaskanaHistoryEngineImpl taskanaHistoryEngineMock;

  @Mock private TaskanaConfiguration taskanaConfiguration;

  @Mock private TaskanaEngine taskanaEngine;

  @Mock private SqlSessionManager sqlSessionManagerMock;

  @Mock private SqlSession sqlSessionMock;

  @Test
  void should_VerifyMethodInvocations_When_CreateTaskHistoryEvent() throws Exception {
    TaskHistoryEvent expectedWb =
        AbstractAccTest.createTaskHistoryEvent(
            "wbKey1", "taskId1", "type1", "wbKey2", "someUserId", "someDetails");

    cutSpy.create(expectedWb);
    verify(taskanaHistoryEngineMock, times(1)).openConnection();
    verify(taskHistoryEventMapperMock, times(1)).insert(expectedWb);
    verify(taskanaHistoryEngineMock, times(1)).returnConnection();
    assertThat(expectedWb.getCreated()).isNotNull();
  }

  @Test
  void should_VerifyMethodInvocations_When_CreateWorkbasketHisoryEvent() throws Exception {
    WorkbasketHistoryEvent expectedEvent =
        AbstractAccTest.createWorkbasketHistoryEvent(
            "wbKey1", WorkbasketHistoryEventType.CREATED.getName(), "someUserId", "someDetails");

    cutSpy.create(expectedEvent);
    verify(taskanaHistoryEngineMock, times(1)).openConnection();
    verify(workbasketHistoryEventMapperMock, times(1)).insert(expectedEvent);
    verify(taskanaHistoryEngineMock, times(1)).returnConnection();
    assertThat(expectedEvent.getCreated()).isNotNull();
  }

  @Test
  void should_VerifyMethodInvocations_When_QueryTaskHistoryEvent() throws Exception {
    List<TaskHistoryEvent> returnList = new ArrayList<>();
    returnList.add(
        AbstractAccTest.createTaskHistoryEvent(
            "wbKey1", "taskId1", "type1", "wbKey2", "someUserId", "someDetails"));

    when(taskanaHistoryEngineMock.getConfiguration()).thenReturn(taskanaConfiguration);
    when(taskanaConfiguration.isAddAdditionalUserInfo()).thenReturn(false);

    when(taskanaHistoryEngineMock.getSqlSession()).thenReturn(sqlSessionMock);
    when(sqlSessionMock.selectList(any(), any())).thenReturn(new ArrayList<>(returnList));

    final List<TaskHistoryEvent> result =
        cutSpy.createTaskHistoryQuery().taskIdIn("taskId1").list();

    verify(taskanaHistoryEngineMock, times(1)).openConnection();
    verify(taskanaHistoryEngineMock, times(1)).getSqlSession();
    verify(sqlSessionMock, times(1)).selectList(any(), any());

    verify(taskanaHistoryEngineMock, times(1)).returnConnection();
    assertThat(result).hasSize(returnList.size());
    assertThat(result.get(0).getWorkbasketKey()).isEqualTo(returnList.get(0).getWorkbasketKey());
  }

  @Test
  void should_VerifyMethodInvocations_When_QueryWorkbasketHisoryEvent() throws Exception {
    List<WorkbasketHistoryEvent> returnList = new ArrayList<>();
    returnList.add(
        AbstractAccTest.createWorkbasketHistoryEvent(
            "wbKey1", WorkbasketHistoryEventType.CREATED.getName(), "someUserId", "someDetails"));
    when(taskanaHistoryEngineMock.getSqlSession()).thenReturn(sqlSessionMock);
    when(sqlSessionMock.selectList(any(), any())).thenReturn(new ArrayList<>(returnList));

    final List<WorkbasketHistoryEvent> result =
        cutSpy.createWorkbasketHistoryQuery().keyIn("wbKey1").list();

    verify(taskanaHistoryEngineMock, times(1)).openConnection();
    verify(taskanaHistoryEngineMock, times(1)).getSqlSession();
    verify(sqlSessionMock, times(1)).selectList(any(), any());
    verify(taskanaHistoryEngineMock, times(1)).returnConnection();
    assertThat(result).hasSize(returnList.size());
    assertThat(result.get(0).getKey()).isEqualTo(returnList.get(0).getKey());
  }
}
