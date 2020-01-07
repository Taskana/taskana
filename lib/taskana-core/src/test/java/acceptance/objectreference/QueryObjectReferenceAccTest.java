package acceptance.objectreference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static pro.taskana.ObjectReferenceQueryColumnName.COMPANY;
import static pro.taskana.ObjectReferenceQueryColumnName.SYSTEM;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;

import pro.taskana.ObjectReference;
import pro.taskana.TaskQuery;

/** Acceptance test for all "get classification" scenarios. */
class QueryObjectReferenceAccTest extends AbstractAccTest {

  QueryObjectReferenceAccTest() {
    super();
  }

  @Test
  void testQueryObjectReferenceValuesForColumnName() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();
    List<String> columnValues = taskQuery.createObjectReferenceQuery().listValues(COMPANY, null);
    assertEquals(3, columnValues.size());

    columnValues = taskQuery.createObjectReferenceQuery().listValues(SYSTEM, null);
    assertEquals(3, columnValues.size());

    columnValues =
        taskQuery.createObjectReferenceQuery().systemIn("System1").listValues(SYSTEM, null);
    assertEquals(1, columnValues.size());
  }

  @Test
  void testFindObjectReferenceByCompany() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().companyIn("Company1", "Company2").list();

    assertNotNull(objectReferenceList);
    assertEquals(2, objectReferenceList.size());
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

    assertNotNull(objectReferenceList);
    assertEquals(1, objectReferenceList.size());
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

    assertNotNull(objectReferenceList);
    assertEquals(1, objectReferenceList.size());
  }

  @Test
  void testFindObjectReferenceByType() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().typeIn("Type2", "Type3").list();

    assertNotNull(objectReferenceList);
    assertEquals(2, objectReferenceList.size());
  }

  @Test
  void testFindObjectReferenceByValue() {
    TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();

    List<ObjectReference> objectReferenceList =
        taskQuery.createObjectReferenceQuery().valueIn("Value1", "Value3").list();

    assertNotNull(objectReferenceList);
    assertEquals(2, objectReferenceList.size());
  }
}
