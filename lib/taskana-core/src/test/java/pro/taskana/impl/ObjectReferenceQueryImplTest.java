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

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.ObjectReference;

/**
 * Test for ObjectReferenceQueryImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class ObjectReferenceQueryImplTest {

    ObjectReferenceQueryImpl objectReferenceQueryImpl;

    @Mock
    TaskanaEngineImpl taskanaEngine;

    @Mock
    SqlSession sqlSession;

    @Before
    public void setup() {
        objectReferenceQueryImpl = new ObjectReferenceQueryImpl(taskanaEngine);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws NotAuthorizedException, InvalidArgumentException {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

        List<ObjectReference> result = objectReferenceQueryImpl.value("test", "asd", "blubber")
            .type("cool", "bla")
            .systemInstance("1", "2")
            .system("superId")
            .list();
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed()
        throws NotAuthorizedException, InvalidArgumentException {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<ObjectReference> result = objectReferenceQueryImpl.value("test", "asd", "blubber")
            .type("cool", "bla")
            .systemInstance("1", "2")
            .system("superId")
            .list(1, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() throws NotAuthorizedException, InvalidArgumentException {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new ObjectReference());

        ObjectReference result = objectReferenceQueryImpl.value("test", "asd", "blubber")
            .type("cool", "bla")
            .systemInstance("1", "2")
            .system("superId")
            .single();
        Assert.assertNotNull(result);
    }
}
