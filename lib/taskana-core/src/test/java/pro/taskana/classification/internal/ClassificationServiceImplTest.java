package pro.taskana.classification.internal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.InternalTaskanaEngine;

/**
 * Unit Test for ClassificationServiceImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
class ClassificationServiceImplTest {

  @Spy @InjectMocks private ClassificationServiceImpl cutSpy;
  @Mock private ClassificationMapper classificationMapperMock;
  @Mock private TaskanaEngine taskanaEngineMock;
  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;
  @Mock private ClassificationQueryImpl classificationQueryImplMock;

  @Test
  void testCreateClassificationQuery() {
    cutSpy.createClassificationQuery();
    verify(internalTaskanaEngineMock, times(1)).getHistoryEventManager();
    verifyNoMoreInteractions(
        classificationMapperMock,
        internalTaskanaEngineMock,
        taskanaEngineMock,
        classificationQueryImplMock);
  }

  @Test
  void testThrowExceptionIdIfClassificationIsCreatedWithAnExplicitId() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    ThrowingCallable call =
        () -> {
          Classification classification = createDummyClassification();
          when(internalTaskanaEngineMock.domainExists(any())).thenReturn(true);
          cutSpy.createClassification(classification);
        };
    assertThatThrownBy(call)
        .isInstanceOf(InvalidArgumentException.class)
        .hasMessage("ClassificationId should be null on creation");
  }

  private Classification createDummyClassification() {
    return this.createDummyClassification("ID: 1");
  }

  private Classification createDummyClassification(String id) {

    ClassificationImpl classificationImpl = new ClassificationImpl();
    classificationImpl.setDescription("A DUMMY FOR TESTING A SERVICE");
    classificationImpl.setName("SERVICE-DUMMY");
    classificationImpl.setDomain("DOMAIN_A");
    classificationImpl.setServiceLevel("P2D");
    classificationImpl.setId(id);
    classificationImpl.setKey("ABC111");
    classificationImpl.setParentId("");
    classificationImpl.setParentKey("");
    return classificationImpl;
  }
}
