package pro.taskana.classification.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.classification.api.Classification;
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
    verifyNoMoreInteractions(
        classificationMapperMock,
        internalTaskanaEngineMock,
        taskanaEngineMock,
        classificationQueryImplMock);
  }

  @Test
  void testThrowExceptionIdIfClassificationIsCreatedWithAnExplicitId() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    InvalidArgumentException invalidArgumentException =
        Assertions.assertThrows(
            InvalidArgumentException.class,
            () -> {
              Classification classification = createDummyClassification();
              when(internalTaskanaEngineMock.domainExists(any())).thenReturn(true);
              cutSpy.createClassification(classification);
            });

    assertEquals(
        invalidArgumentException.getMessage(), "ClassificationId should be null on creation");
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
