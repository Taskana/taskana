package pro.taskana.task.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.common.internal.JunitHelper;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.internal.models.TaskImpl;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;

/**
 * Unit Test for TaskServiceImpl.
 *
 * @author EH
 */
class TaskServiceImplTest {

  @Test
  void testTaskSummaryEqualsHashCode() throws InterruptedException {
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
