package pro.taskana.task.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.ClassificationQueryImpl;
import pro.taskana.classification.internal.ClassificationServiceImpl;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.JunitHelper;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/**
 * Unit Test for TaskServiceImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

  private TaskServiceImpl cut;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private TaskMapper taskMapperMock;

  @Mock private ObjectReferenceMapper objectReferenceMapperMock;

  @Mock private WorkbasketService workbasketServiceMock;

  @Mock private ClassificationServiceImpl classificationServiceImplMock;

  @Mock private AttachmentMapper attachmentMapperMock;

  @Mock private ClassificationQueryImpl classificationQueryImplMock;

  @Mock private SqlSession sqlSessionMock;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    when(taskanaEngineMock.getWorkbasketService()).thenReturn(workbasketServiceMock);
    when(taskanaEngineMock.getClassificationService()).thenReturn(classificationServiceImplMock);
    cut = new TaskServiceImpl(internalTaskanaEngineMock, taskMapperMock, attachmentMapperMock);
  }

  @Test
  void testTaskSummaryEqualsHashCode() throws InterruptedException {
    Classification classification = createDummyClassification();
    Workbasket wb = createWorkbasket("WB-ID", "WB-Key");
    ObjectReference objectReference = JunitHelper.createDefaultObjRef();
    TaskImpl taskBefore = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
    taskBefore.setPrimaryObjRef(objectReference);
    Thread.sleep(10);
    TaskImpl taskAfter = createUnitTestTask("ID", "taskName", wb.getKey(), classification);
    taskAfter.setPrimaryObjRef(objectReference);
    TaskSummary summaryBefore = taskBefore.asSummary();
    TaskSummary summaryAfter = taskAfter.asSummary();

    assertNotEquals(summaryBefore, summaryAfter);
    assertNotEquals(summaryBefore.hashCode(), summaryAfter.hashCode());

    taskAfter.setCreated(taskBefore.getCreated());
    taskAfter.setModified(taskBefore.getModified());
    summaryAfter = taskAfter.asSummary();
    assertEquals(summaryBefore, summaryAfter);
    assertEquals(summaryBefore.hashCode(), summaryAfter.hashCode());

    taskBefore.setModified(null);
    summaryBefore = taskBefore.asSummary();
    assertNotEquals(summaryBefore, summaryAfter);
    assertNotEquals(summaryBefore.hashCode(), summaryAfter.hashCode());

    taskAfter.setModified(null);
    summaryAfter = taskAfter.asSummary();
    assertEquals(summaryBefore, summaryAfter);
    assertEquals(summaryBefore.hashCode(), summaryAfter.hashCode());
  }

  static TaskImpl createUnitTestTask(
      String id, String name, String workbasketKey, Classification classification) {
    TaskImpl task = new TaskImpl();
    task.setId(id);
    task.setExternalId(id);
    task.setName(name);
    task.setWorkbasketKey(workbasketKey);
    task.setDomain("");
    task.setAttachments(new ArrayList<>());
    Instant now = Instant.now().minus(Duration.ofMinutes(1L));
    task.setCreated(now);
    task.setModified(now);
    if (classification == null) {
      classification = createDummyClassification();
    }
    task.setClassificationSummary(classification.asSummary());
    task.setClassificationKey(classification.getKey());
    task.setDomain(classification.getDomain());
    return task;
  }

  static Classification createDummyClassification() {
    ClassificationImpl classification = new ClassificationImpl();
    classification.setName("dummy-classification");
    classification.setDomain("dummy-domain");
    classification.setKey("dummy-classification-key");
    classification.setId("DummyClassificationId");
    return classification;
  }

  static WorkbasketImpl createWorkbasket(String id, String key) {
    WorkbasketImpl workbasket = new WorkbasketImpl();
    workbasket.setId(id);
    workbasket.setDomain("Domain1");
    workbasket.setKey(key);
    workbasket.setName("Workbasket " + id);
    return workbasket;
  }
}
