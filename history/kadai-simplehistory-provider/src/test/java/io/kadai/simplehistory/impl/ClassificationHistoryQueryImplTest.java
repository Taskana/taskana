package io.kadai.simplehistory.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.common.internal.util.IdGenerator;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEvent;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEventType;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit Test for ClassificationQueryImplTest. */
@ExtendWith(MockitoExtension.class)
class ClassificationHistoryQueryImplTest {

  private ClassificationHistoryQueryImpl historyQueryImpl;

  @Mock private InternalKadaiEngine internalKadaiEngineMock;

  @Mock private SqlSession sqlSessionMock;

  @BeforeEach
  void setup() {
    historyQueryImpl = new ClassificationHistoryQueryImpl(internalKadaiEngineMock);
  }

  @Test
  void should_returnList_When_CallingListMethodOnTaskHistoryQuery() throws Exception {
    List<ClassificationHistoryEvent> returnList = new ArrayList<>();
    returnList.add(
        createHistoryEvent(
            ClassificationHistoryEventType.CREATED.getName(), "admin", "someDetails"));

    doNothing().when(internalKadaiEngineMock).openConnection();
    doNothing().when(internalKadaiEngineMock).returnConnection();
    when(internalKadaiEngineMock.getSqlSession()).thenReturn(sqlSessionMock);
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
