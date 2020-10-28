package pro.taskana.workbasket.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;

/** Test for WorkbasketAccessItemQueryImpl. */
@ExtendWith(MockitoExtension.class)
class WorkbasketAccessItemQueryImplTest {

  @InjectMocks private WorkbasketAccessItemQueryImpl workbasketAccessItemQueryImpl;

  @Mock private InternalTaskanaEngine internalTaskanaEngine;

  @Mock private SqlSession sqlSession;

  @Test
  void should_ReturnList_When_BuilderIsUsed() {
    when(internalTaskanaEngine.openAndReturnConnection(any())).thenReturn(new ArrayList<>());

    List<WorkbasketAccessItem> result =
        workbasketAccessItemQueryImpl.accessIdIn("test", "asd").list();
    assertThat(result).isNotNull();
  }

  @Test
  void should_ReturnListWithOffset_When_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

    List<WorkbasketAccessItem> result =
        workbasketAccessItemQueryImpl.accessIdIn("test", "asd").list(1, 1);
    assertThat(result).isNotNull();
  }

  @Test
  void should_ReturnOneItem_When_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectOne(any(), any())).thenReturn(new WorkbasketAccessItemImpl());

    WorkbasketAccessItem result = workbasketAccessItemQueryImpl.accessIdIn("test", "asd").single();
    assertThat(result).isNotNull();
  }
}
