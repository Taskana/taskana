package pro.taskana.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.ClassificationSummary;

/**
 * Test for ClassificationQueryImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationQueryImplTest {

    @InjectMocks
    private ClassificationQueryImpl classificationQueryImpl;

    @Mock
    private InternalTaskanaEngine internalTaskanaEngine;

    @Mock
    private SqlSession sqlSession;

    @Test
    public void should_ReturnList_when_BuilderIsUsed() {
        when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

        List<ClassificationSummary> result = classificationQueryImpl.nameIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .priorityIn(1, 2)
            .parentIdIn("superId")
            .list();
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed() {
        when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<ClassificationSummary> result = classificationQueryImpl.nameIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .priorityIn(1, 2)
            .parentIdIn("superId")
            .list(1, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() {
        when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new ClassificationSummaryImpl());

        ClassificationSummary result = classificationQueryImpl.nameIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .priorityIn(1, 2)
            .parentIdIn("superId")
            .single();
        Assert.assertNotNull(result);
    }
}
