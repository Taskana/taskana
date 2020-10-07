package pro.taskana.workbasket.internal;

import static org.assertj.core.api.Assertions.assertThat;
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
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.workbasket.api.models.WorkbasketSummary;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

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

  @Mock private CurrentUserContext currentUserContext;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngine.getEngine()).thenReturn(taskanaEngine);
    when(taskanaEngine.getCurrentUserContext()).thenReturn(currentUserContext);
  }

  @Test
  void should_ReturnList_When_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

    List<WorkbasketSummary> result =
        workbasketQueryImpl
            .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
            .keyLike("GPK_%")
            .list();
    assertThat(result).isNotNull();
  }

  @Test
  void should_ReturnListWithOffset_When_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

    List<WorkbasketSummary> result =
        workbasketQueryImpl
            .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
            .keyLike("GPK_%")
            .list(1, 1);
    assertThat(result).isNotNull();
  }

  @Test
  void should_ReturnOneItem_When_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectOne(any(), any())).thenReturn(new WorkbasketSummaryImpl());

    WorkbasketSummary result =
        workbasketQueryImpl
            .nameIn("Gruppenpostkorb KSC 1", "Gruppenpostkorb KSC 2")
            .keyLike("GPK_%")
            .single();
    assertThat(result).isNotNull();
  }
}
