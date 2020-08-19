package pro.taskana.task.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.ClassificationServiceImpl;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;

/**
 * Unit Test for TaskServiceImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

  private TaskServiceImpl cut;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private TaskMapper taskMapperMock;

  @Mock private TaskCommentMapper taskCommentMapperMock;

  @Mock private AttachmentMapper attachmentMapperMock;

  @Mock private WorkbasketService workbasketServiceMock;

  @Mock private ClassificationServiceImpl classificationServiceImplMock;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    when(taskanaEngineMock.getWorkbasketService()).thenReturn(workbasketServiceMock);
    when(taskanaEngineMock.getClassificationService()).thenReturn(classificationServiceImplMock);
    cut =
        new TaskServiceImpl(
            internalTaskanaEngineMock, taskMapperMock, taskCommentMapperMock, attachmentMapperMock);
  }

  @Test
  void testTaskSummaryEqualsHashCode() throws Exception {
    Classification classification = CreateTaskModelHelper.createDummyClassification();
    Workbasket wb = CreateTaskModelHelper.createWorkbasket("WB-ID", "WB-Key");
    ObjectReference objectReference = JunitHelper.createDefaultObjRef();
    TaskImpl taskBefore =
        CreateTaskModelHelper.createUnitTestTask("ID", "taskName", wb.getKey(), classification);
    taskBefore.setPrimaryObjRef(objectReference);
    Thread.sleep(10);
    TaskImpl taskAfter =
        CreateTaskModelHelper.createUnitTestTask("ID", "taskName", wb.getKey(), classification);
    taskAfter.setPrimaryObjRef(objectReference);
    TaskSummary summaryBefore = taskBefore.asSummary();
    TaskSummary summaryAfter = taskAfter.asSummary();

    assertThat(summaryAfter).isNotEqualTo(summaryBefore);
    assertThat(summaryAfter.hashCode()).isNotEqualTo(summaryBefore.hashCode());

    taskAfter.setCreated(taskBefore.getCreated());
    taskAfter.setModified(taskBefore.getModified());
    summaryAfter = taskAfter.asSummary();
    assertThat(summaryAfter).isEqualTo(summaryBefore);
    assertThat(summaryAfter.hashCode()).isEqualTo(summaryBefore.hashCode());

    taskBefore.setModified(null);
    summaryBefore = taskBefore.asSummary();
    assertThat(summaryAfter).isNotEqualTo(summaryBefore);
    assertThat(summaryAfter.hashCode()).isNotEqualTo(summaryBefore.hashCode());

    taskAfter.setModified(null);
    summaryAfter = taskAfter.asSummary();
    assertThat(summaryAfter).isEqualTo(summaryBefore);
    assertThat(summaryAfter.hashCode()).isEqualTo(summaryBefore.hashCode());
  }
}
