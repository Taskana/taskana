package pro.taskana.workbasket.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.workbasket.api.WorkbasketSummary;

/**
 * Test for WorkbasketQueryImpl.
 *
 * @author jsa
 */
@ExtendWith(MockitoExtension.class)
class WorkbasketQueryImplTest {

  @InjectMocks private WorkbasketQueryImpl workbasketQueryImpl;

  @Mock private InternalTaskanaEngine internalTaskanaEngine;

  @Mock private TaskanaEngine taskanaEngine;

  @Mock private SqlSession sqlSession;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngine.getEngine()).thenReturn(taskanaEngine);
  }

  @Test
  void should_ReturnList_when_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

    List<WorkbasketSummary> result =
        workbasketQueryImpl
            .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
            .keyLike("GPK_%")
            .list();
    assertNotNull(result);
  }

  @Test
  void should_ReturnListWithOffset_when_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

    List<WorkbasketSummary> result =
        workbasketQueryImpl
            .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
            .keyLike("GPK_%")
            .list(1, 1);
    assertNotNull(result);
  }

  @Test
  void should_ReturnOneItem_when_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectOne(any(), any())).thenReturn(new WorkbasketSummaryImpl());

    WorkbasketSummary result =
        workbasketQueryImpl
            .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
            .keyLike("GPK_%")
            .single();
    assertNotNull(result);
  }
}
