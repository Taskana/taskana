package pro.taskana.task.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.task.api.models.ObjectReference;

/** Test for ObjectReferenceQueryImpl. */
@ExtendWith(MockitoExtension.class)
class ObjectReferenceQueryImplTest {

  ObjectReferenceQueryImpl objectReferenceQueryImpl;

  @Mock InternalTaskanaEngine taskanaEngine;

  @Mock SqlSession sqlSession;

  @BeforeEach
  void setup() {
    objectReferenceQueryImpl = new ObjectReferenceQueryImpl(taskanaEngine);
  }

  @Test
  void should_ReturnList_When_BuilderIsUsed() {
    when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectList(any(), any())).thenReturn(new ArrayList<>());

    List<ObjectReference> result =
        objectReferenceQueryImpl
            .valueIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .systemInstanceIn("1", "2")
            .systemIn("superId")
            .list();
    assertThat(result).isNotNull();
  }

  @Test
  void should_ReturnListWithOffset_When_BuilderIsUsed() {
    when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectList(any(), any(), any())).thenReturn(new ArrayList<>());

    List<ObjectReference> result =
        objectReferenceQueryImpl
            .valueIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .systemInstanceIn("1", "2")
            .systemIn("superId")
            .list(1, 1);
    assertThat(result).isNotNull();
  }

  @Test
  void should_ReturnOneItem_When_BuilderIsUsed() {
    when(taskanaEngine.getSqlSession()).thenReturn(sqlSession);
    when(sqlSession.selectOne(any(), any())).thenReturn(new ObjectReference());

    ObjectReference result =
        objectReferenceQueryImpl
            .valueIn("test", "asd", "blubber")
            .typeIn("cool", "bla")
            .systemInstanceIn("1", "2")
            .systemIn("superId")
            .single();
    assertThat(result).isNotNull();
  }
}
