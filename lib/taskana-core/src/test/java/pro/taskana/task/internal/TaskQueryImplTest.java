package pro.taskana.task.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.configuration.DB;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskSummaryImpl;

/** Test for TaskQueryImpl. */
@ExtendWith(MockitoExtension.class)
class TaskQueryImplTest {

  @Mock TaskServiceImpl taskServiceMock;

  @Mock private InternalTaskanaEngine internalTaskanaEngine;
  @Mock private TaskanaEngine taskanaEngine;
  @Mock private SqlSession sqlSession;
  @Mock private CurrentUserContext currentUserContext;

  private TaskQueryImpl taskQueryImpl;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngine.getEngine()).thenReturn(taskanaEngine);
    when(taskanaEngine.getTaskService()).thenReturn(taskServiceMock);
    when(taskanaEngine.getCurrentUserContext()).thenReturn(currentUserContext);

    Configuration configuration = new org.apache.ibatis.session.Configuration();
    configuration.setDatabaseId(DB.H2.dbProductId);
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.getConfiguration()).thenReturn(configuration);

    taskQueryImpl = new TaskQueryImpl(internalTaskanaEngine);
  }

  @Test
  void should_ReturnList_When_BuilderIsUsed() {
    when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());
    List<TaskSummary> intermediate = new ArrayList<>();
    intermediate.add(new TaskSummaryImpl());
    when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

    List<TaskSummary> result =
        taskQueryImpl
            .nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .list();
    assertThat(result).isNotNull();
  }

  @Test
  void should_ReturnListWithOffset_When_BuilderIsUsed() {
    when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());
    List<TaskSummary> intermediate = new ArrayList<>();
    intermediate.add(new TaskSummaryImpl());
    when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

    List<TaskSummary> result =
        taskQueryImpl
            .nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .list(1, 1);
    assertThat(result).isNotNull();
  }

  @Test
  void should_ReturnOneItem_When_BuilderIsUsed() {
    when(sqlSession.selectOne(any(), any())).thenReturn(new TaskSummaryImpl());
    List<TaskSummary> intermediate = new ArrayList<>();
    intermediate.add(new TaskSummaryImpl());

    when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

    TaskSummary result =
        taskQueryImpl
            .nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .single();
    assertThat(result).isNotNull();
  }
}
