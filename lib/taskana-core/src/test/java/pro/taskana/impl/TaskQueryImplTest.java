package pro.taskana.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.Task;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.TaskState;

/**
 * Test for TaskQueryImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class TaskQueryImplTest {

    private TaskQueryImpl taskQueryImpl;

    @Mock
    private TaskanaEngineImpl taskanaEngine;

    @Mock
    private SqlSession sqlSession;

    @Before
    public void setup() {
        taskQueryImpl = new TaskQueryImpl(taskanaEngine);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws NotAuthorizedException, InvalidArgumentException {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

        List<Task> result = taskQueryImpl.name("test", "asd", "blubber")
            .customFields("cool", "bla")
            .priority(1, 2)
            .state(TaskState.CLAIMED, TaskState.COMPLETED)
            .list();
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed()
        throws NotAuthorizedException, InvalidArgumentException {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<Task> result = taskQueryImpl.name("test", "asd", "blubber")
            .customFields("cool", "bla")
            .priority(1, 2)
            .state(TaskState.CLAIMED, TaskState.COMPLETED)
            .list(1, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() throws NotAuthorizedException, InvalidArgumentException {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new TaskImpl());

        Task result = taskQueryImpl.name("test", "asd", "blubber")
            .customFields("cool", "bla")
            .priority(1, 2)
            .state(TaskState.CLAIMED, TaskState.COMPLETED)
            .single();
        Assert.assertNotNull(result);
    }
}
