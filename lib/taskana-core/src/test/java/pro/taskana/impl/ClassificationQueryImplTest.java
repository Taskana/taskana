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

import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * Test for ClassificationQueryImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationQueryImplTest {

    private ClassificationQueryImpl classificationQueryImpl;

    @Mock
    private TaskanaEngineImpl taskanaEngine;

    @Mock
    private SqlSession sqlSession;

    @Before
    public void setup() {
        classificationQueryImpl = new ClassificationQueryImpl(taskanaEngine);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() throws NotAuthorizedException, InvalidArgumentException {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

        List<ClassificationSummary> result = classificationQueryImpl.name("test", "asd", "blubber")
            .type("cool", "bla")
            .priority(1, 2)
            .parentClassificationKey("superId")
            .list();
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed()
        throws NotAuthorizedException, InvalidArgumentException {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<ClassificationSummary> result = classificationQueryImpl.name("test", "asd", "blubber")
            .type("cool", "bla")
            .priority(1, 2)
            .parentClassificationKey("superId")
            .list(1, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() throws NotAuthorizedException, InvalidArgumentException {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new ClassificationSummaryImpl());

        ClassificationSummary result = classificationQueryImpl.name("test", "asd", "blubber")
            .type("cool", "bla")
            .priority(1, 2)
            .parentClassificationKey("superId")
            .single();
        Assert.assertNotNull(result);
    }
}
