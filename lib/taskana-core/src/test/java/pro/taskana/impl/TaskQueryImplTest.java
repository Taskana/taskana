package pro.taskana.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.TaskState;
import pro.taskana.TaskSummary;

/**
 * Test for TaskQueryImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskQueryImplTest {

    @InjectMocks
    private TaskQueryImpl taskQueryImpl;

    @Mock
    private TaskanaEngineImpl taskanaEngine;

    @Mock
    private SqlSession sqlSession;

    @Mock
    private SqlSessionManager sqlSessionManager;

    @Mock
    ClassificationServiceImpl classificationService;

    @Mock
    TaskServiceImpl taskServiceMock;

    @Before
    public void setup() {
        when(taskanaEngine.getTaskService()).thenReturn(taskServiceMock);

        Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setDatabaseId("h2");
        this.taskanaEngine.sessionManager = sqlSessionManager;
        when(taskanaEngine.sessionManager.getConfiguration()).thenReturn(configuration);

        taskQueryImpl = new TaskQueryImpl(taskanaEngine);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());
        List<TaskSummary> intermediate = new ArrayList<>();
        intermediate.add(new TaskSummaryImpl());
        doReturn(intermediate).when(taskServiceMock).augmentTaskSummariesByContainedSummaries(any());

        List<TaskSummary> result = taskQueryImpl.nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .list();
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());
        List<TaskSummary> intermediate = new ArrayList<>();
        intermediate.add(new TaskSummaryImpl());
        doReturn(intermediate).when(taskServiceMock).augmentTaskSummariesByContainedSummaries(any());

        List<TaskSummary> result = taskQueryImpl.nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .list(1, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new TaskSummaryImpl());
        List<TaskSummary> intermediate = new ArrayList<>();
        intermediate.add(new TaskSummaryImpl());
        doReturn(intermediate).when(taskServiceMock).augmentTaskSummariesByContainedSummaries(any());
        // when(taskServiceMock.augmentTaskSummariesByContainedSummaries(any())).thenReturn(intermediate);

        TaskSummary result = taskQueryImpl.nameIn("test", "asd", "blubber")
            .priorityIn(1, 2)
            .stateIn(TaskState.CLAIMED, TaskState.COMPLETED)
            .single();
        Assert.assertNotNull(result);
    }
}
