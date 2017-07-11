package org.taskana.impl.persistence;

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
import org.taskana.exceptions.NotAuthorizedException;
import org.taskana.impl.TaskanaEngineImpl;
import org.taskana.model.Classification;

/**
 * Test for ClassificationQueryImpl.
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationQueryImplTest {

    ClassificationQueryImpl classificationQueryImpl;

    @Mock
    TaskanaEngineImpl taskanaEngine;

    @Mock
    SqlSession sqlSession;

    @Before
    public void setup() {
        classificationQueryImpl = new ClassificationQueryImpl(taskanaEngine);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws NotAuthorizedException {
        when(taskanaEngine.getSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

        List<Classification> result = classificationQueryImpl.name("test", "asd", "blubber").type("cool", "bla").priority(1, 2)
                .parentClassification("superId").list();
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed() throws NotAuthorizedException {
        when(taskanaEngine.getSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<Classification> result = classificationQueryImpl.name("test", "asd", "blubber").type("cool", "bla").priority(1, 2)
                .parentClassification("superId").list(1, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() throws NotAuthorizedException {
        when(taskanaEngine.getSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new Classification());

        Classification result = classificationQueryImpl.name("test", "asd", "blubber").type("cool", "bla").priority(1, 2)
                .parentClassification("superId").single();
        Assert.assertNotNull(result);
    }
}
