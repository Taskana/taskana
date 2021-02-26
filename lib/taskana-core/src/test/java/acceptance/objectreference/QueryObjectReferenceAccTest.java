package acceptance.objectreference;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.task.api.ObjectReferenceQueryColumnName.COMPANY;
import static pro.taskana.task.api.ObjectReferenceQueryColumnName.SYSTEM;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;

import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.models.ObjectReference;

/** Acceptance test for all "get classification" scenarios. */
class QueryObjectReferenceAccTest extends AbstractAccTest {

  @Test
  void testQueryObjectReferenceValuesForColumnName() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();
    List<String> columnValues = taskQuery.createObjectReferenceQuery().listValues(COMPANY, null);
    assertThat(columnValues).hasSize(3);

    columnValues = taskQuery.createObjectReferenceQuery().listValues(SYSTEM, null);
    assertThat(columnValues).hasSize(3);

    columnValues =
        taskQuery.createObjectReferenceQuery().systemIn("System1").listValues(SYSTEM, null);
    assertThat(columnValues).hasSize(1);
  }

  @Test
  void testFindObjectReferenceByCompany() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().companyIn("Company1", "Company2").list();

    assertThat(objectReferenceList).hasSize(2);
  }

  @Test
  void testFindObjectReferenceBySystem() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery
            .createObjectReferenceQuery()
            .companyIn("Company1", "Company2")
            .systemIn("System2")
            .list();

    assertThat(objectReferenceList).hasSize(1);
  }

  @Test
  void testFindObjectReferenceBySystemInstance() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery
            .createObjectReferenceQuery()
            .companyIn("Company1", "Company2")
            .systemInstanceIn("Instance1")
            .list();

    assertThat(objectReferenceList).hasSize(1);
  }

  @Test
  void testFindObjectReferenceByType() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().typeIn("Type2", "Type3").list();

    assertThat(objectReferenceList).hasSize(2);
  }

  @Test
  void testFindObjectReferenceByValue() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().valueIn("Value1", "Value3").list();

    assertThat(objectReferenceList).hasSize(2);
  }
}
