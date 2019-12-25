package pro.taskana.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.DB;

/**
 * Test for TaskQueryImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
class TaskQueryImplTest {

    @Mock
    TaskServiceImpl taskServiceMock;

    private TaskQueryImpl taskQueryImpl;
    @Mock
    private InternalTaskanaEngine internalTaskanaEngine;
    @Mock
    private TaskanaEngine taskanaEngine;
    @Mock
    private SqlSession sqlSession;

    @BeforeEach
    void setup() {
        when(internalTaskanaEngine.getEngine()).thenReturn(taskanaEngine);
        when(taskanaEngine.getTaskService()).thenReturn(taskServiceMock);

        Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setDatabaseId(DB.H2.dbProductId);
        when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.getConfiguration()).thenReturn(configuration);

        taskQueryImpl = new TaskQueryImpl(internalTaskanaEngine);
    }

    @Test
    void should_ReturnList_when_BuilderIsUsed() {
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());
        List<TaskSummary> intermediate = new ArrayList<>();
        intermediate.add(new TaskSummaryImpl());
        when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

        List<TaskSummary> result = taskQueryImpl.nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .list();
        assertNotNull(result);
    }

    @Test
    void should_ReturnListWithOffset_when_BuilderIsUsed() {
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());
        List<TaskSummary> intermediate = new ArrayList<>();
        intermediate.add(new TaskSummaryImpl());
        when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

        List<TaskSummary> result = taskQueryImpl.nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .list(1, 1);
        assertNotNull(result);
    }

    @Test
    void should_ReturnOneItem_when_BuilderIsUsed() {
        when(sqlSession.selectOne(any(), any())).thenReturn(new TaskSummaryImpl());
        List<TaskSummary> intermediate = new ArrayList<>();
        intermediate.add(new TaskSummaryImpl());

        when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

        TaskSummary result = taskQueryImpl.nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .single();
        assertNotNull(result);
    }
}
