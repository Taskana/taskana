package pro.taskana.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;

/**
 * Test for TaskQueryImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskQueryImplTest {

    @Mock
    TaskServiceImpl taskServiceMock;

    private TaskQueryImpl taskQueryImpl;
    @Mock
    private TaskanaEngine.Internal taskanaEngineInternal;
    @Mock
    private TaskanaEngine taskanaEngine;
    @Mock
    private SqlSession sqlSession;

    @Before
    public void setup() {
        when(taskanaEngineInternal.getEngine()).thenReturn(taskanaEngine);
        when(taskanaEngine.getTaskService()).thenReturn(taskServiceMock);

        Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setDatabaseId("h2");
        when(taskanaEngineInternal.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.getConfiguration()).thenReturn(configuration);

        taskQueryImpl = new TaskQueryImpl(taskanaEngineInternal);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() {
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());
        List<TaskSummary> intermediate = new ArrayList<>();
        intermediate.add(new TaskSummaryImpl());
        when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

        List<TaskSummary> result = taskQueryImpl.nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .list();
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed() {
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());
        List<TaskSummary> intermediate = new ArrayList<>();
        intermediate.add(new TaskSummaryImpl());
        when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

        List<TaskSummary> result = taskQueryImpl.nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .list(1, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() {
        when(sqlSession.selectOne(any(), any())).thenReturn(new TaskSummaryImpl());
        List<TaskSummary> intermediate = new ArrayList<>();
        intermediate.add(new TaskSummaryImpl());

        when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

        TaskSummary result = taskQueryImpl.nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .single();
        Assert.assertNotNull(result);
    }
}
