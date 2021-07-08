package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for all "query tasks by object reference" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByObjectReferenceAccTest extends AbstractAccTest {

  private static final TaskService TASK_SERVICE = taskanaEngine.getTaskService();

  @WithAccessId(user = "admin")
  @Test
  void testQueryTasksByExcactValueOfObjectReference() {
    List<TaskSummary> results =
        TASK_SERVICE.createTaskQuery().primaryObjectReferenceValueIn("11223344", "22334455").list();
    assertThat(results).hasSize(33);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyObjectReferencesFilter_When_ValueIsSet() {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setValue("11223344");
    List<TaskSummary> results =
        TASK_SERVICE.createTaskQuery().primaryObjectReferenceIn(objectReference).list();
    assertThat(results).hasSize(21);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyObjectReferencesFilter_When_TypeIsSet() {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setType("SDNR");
    List<TaskSummary> results =
        TASK_SERVICE.createTaskQuery().primaryObjectReferenceIn(objectReference).list();
    assertThat(results).hasSize(46);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyObjectReferencesFilter_When_CompanyIsSet() {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setCompany("MyCompany1");
    List<TaskSummary> results =
        TASK_SERVICE.createTaskQuery().primaryObjectReferenceIn(objectReference).list();
    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyObjectReferencesFilter_When_SystemIsSet() {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setSystem("MySystem1");
    List<TaskSummary> results =
        TASK_SERVICE.createTaskQuery().primaryObjectReferenceIn(objectReference).list();
    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyObjectReferencesFilter_When_SystemInstanceIsSet() {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setSystemInstance("MyInstance1");
    List<TaskSummary> results =
        TASK_SERVICE.createTaskQuery().primaryObjectReferenceIn(objectReference).list();
    assertThat(results).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyObjectReferencesFilter_When_MultipleObjectReferencesExist() {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setType("SDNR");
    ObjectReference objectReference1 = new ObjectReference();
    objectReference1.setValue("11223344");
    List<TaskSummary> results =
        TASK_SERVICE
            .createTaskQuery()
            .primaryObjectReferenceIn(objectReference, objectReference1)
            .list();
    assertThat(results).hasSize(57);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ApplyObjectReferencesFilter_When_MultipleFieldsAreSet() {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setCompany("00");
    objectReference.setSystem("PASyste2");
    objectReference.setSystemInstance("00");
    objectReference.setType("VNR");
    objectReference.setValue("67890123");
    List<TaskSummary> results =
        TASK_SERVICE.createTaskQuery().primaryObjectReferenceIn(objectReference).list();
    assertThat(results).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CountWithObjectReferencesFilter_When_MultipleFieldsAreSet() {
    ObjectReference objectReference = new ObjectReference();
    objectReference.setCompany("00");
    objectReference.setSystem("PASyste2");
    objectReference.setSystemInstance("00");
    objectReference.setType("VNR");
    objectReference.setValue("67890123");
    long count = TASK_SERVICE.createTaskQuery().primaryObjectReferenceIn(objectReference).count();
    assertThat(count).isEqualTo(1);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTasksByExactValueAndTypeOfObjectReference() {
    List<TaskSummary> results =
        TASK_SERVICE
            .createTaskQuery()
            .primaryObjectReferenceTypeIn("SDNR")
            .primaryObjectReferenceValueIn("11223344")
            .list();
    assertThat(results).hasSize(10);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTasksByValueLikeOfObjectReference() {
    List<TaskSummary> results =
        TASK_SERVICE.createTaskQuery().primaryObjectReferenceValueLike("%567%").list();
    assertThat(results).hasSize(10);
  }
}
