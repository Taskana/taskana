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

import pro.taskana.WorkbasketSummary;

/**
 * Test for WorkbasketQueryImpl.
 *
 * @author jsa
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkbasketQueryImplTest {

    private WorkbasketQueryImpl workbasketQueryImpl;

    @Mock
    private TaskanaEngineImpl taskanaEngine;

    @Mock
    private SqlSession sqlSession;

    @Before
    public void setup() {
        workbasketQueryImpl = new WorkbasketQueryImpl(taskanaEngine);
    }

    @Test
    public void should_ReturnList_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

        List<WorkbasketSummary> result = workbasketQueryImpl
                .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
                .keyLike("GPK_%")
                .list();
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnListWithOffset_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

        List<WorkbasketSummary> result = workbasketQueryImpl
                .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
                .keyLike("GPK_%")
                .list(1, 1);
        Assert.assertNotNull(result);
    }

    @Test
    public void should_ReturnOneItem_when_BuilderIsUsed() {
        when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
        when(sqlSession.selectOne(any(), any())).thenReturn(new WorkbasketSummaryImpl());

        WorkbasketSummary result = workbasketQueryImpl
                .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
                .keyLike("GPK_%")
                .single();
        Assert.assertNotNull(result);
    }
}
