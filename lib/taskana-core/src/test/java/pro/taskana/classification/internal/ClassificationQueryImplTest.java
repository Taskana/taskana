package pro.taskana.classification.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import pro.taskana.classification.api.ClassificationSummary;
import pro.taskana.common.internal.InternalTaskanaEngine;

/**
 * Test for ClassificationQueryImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
class ClassificationQueryImplTest {

  @InjectMocks private ClassificationQueryImpl classificationQueryImpl;

  @Mock private InternalTaskanaEngine internalTaskanaEngine;

  @Mock private SqlSession sqlSession;

  @Test
  void should_ReturnList_when_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

    List<ClassificationSummary> result =
        classificationQueryImpl
            .nameIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .priorityIn(1, 2)
            .parentIdIn("superId")
            .list();
    assertNotNull(result);
  }

  @Test
  void should_ReturnListWithOffset_when_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

    List<ClassificationSummary> result =
        classificationQueryImpl
            .nameIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .priorityIn(1, 2)
            .parentIdIn("superId")
            .list(1, 1);
    assertNotNull(result);
  }

  @Test
  void should_ReturnOneItem_when_BuilderIsUsed() {
    when(internalTaskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectOne(any(), any())).thenReturn(new ClassificationSummaryImpl());

    ClassificationSummary result =
        classificationQueryImpl
            .nameIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .priorityIn(1, 2)
            .parentIdIn("superId")
            .single();
    assertNotNull(result);
  }
}
