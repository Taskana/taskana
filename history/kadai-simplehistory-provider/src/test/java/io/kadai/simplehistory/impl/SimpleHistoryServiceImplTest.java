package io.kadai.simplehistory.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import acceptance.AbstractAccTest;
import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.simplehistory.impl.task.TaskHistoryEventMapper;
import io.kadai.simplehistory.impl.task.TaskHistoryQueryMapper;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryQueryMapper;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
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

/** Unit Test for SimpleHistoryServiceImplTest. */
@ExtendWith(MockitoExtension.class)
class SimpleHistoryServiceImplTest {

  @InjectMocks @Spy private SimpleHistoryServiceImpl cutSpy;

  @Mock private TaskHistoryEventMapper taskHistoryEventMapperMock;

  @Mock private TaskHistoryQueryMapper taskHistoryQueryMapperMock;

  @Mock private WorkbasketHistoryEventMapper workbasketHistoryEventMapperMock;

  @Mock private WorkbasketHistoryQueryMapper workbasketHistoryQueryMapperMock;
  @Mock private KadaiConfiguration kadaiConfiguration;

  @Mock private KadaiEngine kadaiEngine;

  @Mock private InternalKadaiEngine internalKadaiEngine;

  @Mock private SqlSessionManager sqlSessionManagerMock;

  @Mock private SqlSession sqlSessionMock;

  @Test
  void should_VerifyMethodInvocations_When_CreateTaskHistoryEvent() throws Exception {
    TaskHistoryEvent expectedWb =
        AbstractAccTest.createTaskHistoryEvent(
            "wbKey1", "taskId1", "type1", "wbKey2", "someUserId", "someDetails");

    cutSpy.create(expectedWb);
    verify(taskHistoryEventMapperMock, times(1)).insert(expectedWb);
    assertThat(expectedWb.getCreated()).isNotNull();
  }

  @Test
  void should_VerifyMethodInvocations_When_CreateWorkbasketHisoryEvent() throws Exception {
    WorkbasketHistoryEvent expectedEvent =
        AbstractAccTest.createWorkbasketHistoryEvent(
            "wbKey1", WorkbasketHistoryEventType.CREATED.getName(), "someUserId", "someDetails");

    cutSpy.create(expectedEvent);
    verify(workbasketHistoryEventMapperMock, times(1)).insert(expectedEvent);
    assertThat(expectedEvent.getCreated()).isNotNull();
  }

  @Test
  void should_VerifyMethodInvocations_When_QueryTaskHistoryEvent() throws Exception {
    List<TaskHistoryEvent> returnList = new ArrayList<>();
    returnList.add(
        AbstractAccTest.createTaskHistoryEvent(
            "wbKey1", "taskId1", "type1", "wbKey2", "someUserId", "someDetails"));

    when(kadaiConfiguration.isAddAdditionalUserInfo()).thenReturn(false);

    when(internalKadaiEngine.getSqlSession()).thenReturn(sqlSessionMock);
    when(sqlSessionMock.selectList(any(), any())).thenReturn(new ArrayList<>(returnList));

    when(internalKadaiEngine.getEngine()).thenReturn(kadaiEngine);
    when(kadaiEngine.getConfiguration()).thenReturn(kadaiConfiguration);
    final List<TaskHistoryEvent> result =
        cutSpy.createTaskHistoryQuery().taskIdIn("taskId1").list();

    verify(internalKadaiEngine, times(1)).openConnection();
    verify(internalKadaiEngine, times(1)).getSqlSession();
    verify(sqlSessionMock, times(1)).selectList(any(), any());

    verify(internalKadaiEngine, times(1)).returnConnection();
    assertThat(result).hasSize(returnList.size());
    assertThat(result.get(0).getWorkbasketKey()).isEqualTo(returnList.get(0).getWorkbasketKey());
  }

  @Test
  void should_VerifyMethodInvocations_When_QueryWorkbasketHisoryEvent() throws Exception {
    List<WorkbasketHistoryEvent> returnList = new ArrayList<>();
    returnList.add(
        AbstractAccTest.createWorkbasketHistoryEvent(
            "wbKey1", WorkbasketHistoryEventType.CREATED.getName(), "someUserId", "someDetails"));
    when(sqlSessionMock.selectList(any(), any())).thenReturn(new ArrayList<>(returnList));
    when(internalKadaiEngine.getSqlSession()).thenReturn(sqlSessionMock);
    final List<WorkbasketHistoryEvent> result =
        cutSpy.createWorkbasketHistoryQuery().keyIn("wbKey1").list();

    verify(internalKadaiEngine, times(1)).openConnection();
    verify(internalKadaiEngine, times(1)).getSqlSession();
    verify(sqlSessionMock, times(1)).selectList(any(), any());
    verify(internalKadaiEngine, times(1)).returnConnection();
    assertThat(result).hasSize(returnList.size());
    assertThat(result.get(0).getKey()).isEqualTo(returnList.get(0).getKey());
  }
}
