package pro.taskana.simplehistory.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEventType;

/** Unit Test for ClassificationQueryImplTest. */
@ExtendWith(MockitoExtension.class)
class ClassificationHistoryQueryImplTest {

  private ClassificationHistoryQueryImpl historyQueryImpl;

  @Mock private TaskanaHistoryEngineImpl taskanaHistoryEngineMock;

  @Mock private SqlSession sqlSessionMock;

  @BeforeEach
  void setup() {
    historyQueryImpl = new ClassificationHistoryQueryImpl(taskanaHistoryEngineMock);
  }

  @Test
  void should_returnList_When_CallingListMethodOnTaskHistoryQuery() throws Exception {
    List<ClassificationHistoryEvent> returnList = new ArrayList<>();
    returnList.add(
        createHistoryEvent(
            ClassificationHistoryEventType.CREATED.getName(), "admin", "someDetails"));

    doNothing().when(taskanaHistoryEngineMock).openConnection();
    doNothing().when(taskanaHistoryEngineMock).returnConnection();
    when(taskanaHistoryEngineMock.getSqlSession()).thenReturn(sqlSessionMock);
    when(sqlSessionMock.selectList(any(), any())).thenReturn(new ArrayList<>(returnList));

    List<ClassificationHistoryEvent> result =
        historyQueryImpl
            .userIdIn("admin")
            .typeIn(ClassificationHistoryEventType.CREATED.getName())
            .list();

    validateMockitoUsage();
    assertThat(result).isEqualTo(returnList);
  }

  private ClassificationHistoryEvent createHistoryEvent(
      String type, String userId, String details) {
    ClassificationHistoryEvent he = new ClassificationHistoryEvent();
    he.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_CLASSIFICATION_HISTORY_EVENT));
    he.setUserId(userId);
    he.setDetails(details);
    he.setEventType(type);
    return he;
  }
}
