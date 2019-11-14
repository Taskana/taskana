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

import pro.taskana.WorkbasketAccessItem;

/**
 * Test for WorkbasketAccessItemQueryImpl.
 *
 * @author jsa
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkbasketAccessItemQueryImplTest {

    @InjectMocks
    private WorkbasketAccessItemQueryImpl workbasketAccessItemQueryImpl;

    @Mock
    private InternalTaskanaEngine internalTaskanaEngine;

    @Mock
    private SqlSession sqlSession;

    @Test
    public void should_ReturnList_when_BuilderIsUsed() {
        when(internalTaskanaEngine.openAndReturnConnection(any())).thenReturn(new ArrayList<>());

        List<WorkbasketAccessItem> result = workbasketAccessItemQueryImpl.accessIdIn("test", "asd")
            .list();
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed() {
        when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<WorkbasketAccessItem> result = workbasketAccessItemQueryImpl.accessIdIn("test", "asd")
            .list(1, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() {
        when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new WorkbasketAccessItemImpl());

        WorkbasketAccessItem result = workbasketAccessItemQueryImpl.accessIdIn("test", "asd")
            .single();
        Assert.assertNotNull(result);
    }
}
