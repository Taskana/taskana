package acceptance.task.query;

import static io.kadai.task.api.ObjectReferenceQueryColumnName.COMPANY;
import static io.kadai.task.api.ObjectReferenceQueryColumnName.SYSTEM;
import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.task.api.TaskQuery;
import io.kadai.task.api.models.ObjectReference;
import java.util.List;
import org.junit.jupiter.api.Test;

/** Acceptance test for all "get classification" scenarios. */
class QueryObjectReferenceAccTest extends AbstractAccTest {

  @Test
  void testQueryObjectReferenceValuesForColumnName() {
    TaskQuery taskQuery = kadaiEngine.getTaskService().createTaskQuery();
    List<String> columnValues = taskQuery.createObjectReferenceQuery().listValues(COMPANY, null);
    assertThat(columnValues).hasSize(4);

    columnValues = taskQuery.createObjectReferenceQuery().listValues(SYSTEM, null);
    assertThat(columnValues).hasSize(4);

    columnValues =
        taskQuery.createObjectReferenceQuery().systemIn("System1").listValues(SYSTEM, null);
    assertThat(columnValues).hasSize(1);
  }

  @Test
  void testFindObjectReferenceByCompany() {
    TaskQuery taskQuery = kadaiEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().companyIn("Company1", "Company2").list();

    assertThat(objectReferenceList).hasSize(2);
  }

  @Test
  void testFindObjectReferenceBySystem() {
    TaskQuery taskQuery = kadaiEngine.getTaskService().createTaskQuery();

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
    TaskQuery taskQuery = kadaiEngine.getTaskService().createTaskQuery();

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
    TaskQuery taskQuery = kadaiEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().typeIn("Type2", "Type3").list();

    assertThat(objectReferenceList).hasSize(2);
  }

  @Test
  void testFindObjectReferenceByValue() {
    TaskQuery taskQuery = kadaiEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().valueIn("Value1", "Value3").list();

    assertThat(objectReferenceList).hasSize(2);
  }
}
