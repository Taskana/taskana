package acceptance.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.TaskSummary;

/** Acceptance test for all "query tasks with sorting" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksWithSortingAccTest extends AbstractAccTest {

  private static SortDirection asc = SortDirection.ASCENDING;
  private static SortDirection desc = SortDirection.DESCENDING;

  QueryTasksWithSortingAccTest() {
    super();
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testSortByModifiedAndDomain() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByModified(desc)
            .orderByDomain(null)
            .list();

    assertThat(results.size(), equalTo(25));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertFalse(previousSummary.getModified().isBefore(taskSummary.getModified()));
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testSortByTaskIdDesc() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByTaskId(desc)
            .list();

    // test is only valid with at least 2 results
    Assertions.assertTrue(results.size() > 2);

    List<String> idsDesc =
        results.stream()
            .map(TaskSummary::getTaskId)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

    for (int i = 0; i < results.size(); i++) {
      assertEquals(idsDesc.get(i), results.get(i).getTaskId());
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testSortByTaskIdAsc() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByTaskId(null)
            .list();

    // test is only valid with at least 2 results
    Assertions.assertTrue(results.size() > 2);

    List<String> idsAsc =
        results.stream().map(TaskSummary::getTaskId).sorted().collect(Collectors.toList());

    for (int i = 0; i < results.size(); i++) {
      assertEquals(idsAsc.get(i), results.get(i).getTaskId());
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testSortByDomainNameAndCreated() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByDomain(asc)
            .orderByName(asc)
            .orderByCreated(null)
            .list();

    assertThat(results.size(), equalTo(25));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      // System.out.println("domain: " + taskSummary.getDomain() + ", name: " +
      // taskSummary.getName() + ",
      // created: " + taskSummary.getCreated());
      if (previousSummary != null) {
        assertTrue(taskSummary.getDomain().compareToIgnoreCase(previousSummary.getDomain()) >= 0);
        if (taskSummary.getDomain().equals(previousSummary.getDomain())) {
          assertTrue(taskSummary.getName().compareToIgnoreCase(previousSummary.getName()) >= 0);
          if (taskSummary.getName().equals(previousSummary.getName())) {
            assertFalse(taskSummary.getCreated().isBefore(previousSummary.getCreated()));
          }
        }
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testSortByPorSystemNoteDueAndOwner() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByPrimaryObjectReferenceSystem(SortDirection.DESCENDING)
            .orderByNote(null)
            .orderByDue(null)
            .orderByOwner(asc)
            .list();

    assertThat(results.size(), equalTo(25));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertTrue(
            taskSummary
                    .getPrimaryObjRef()
                    .getSystem()
                    .compareToIgnoreCase(previousSummary.getPrimaryObjRef().getSystem())
                <= 0);
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testSortByPorSystemInstanceParentProcPlannedAndState() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByPrimaryObjectReferenceSystemInstance(desc)
            .orderByParentBusinessProcessId(asc)
            .orderByPlanned(asc)
            .orderByState(asc)
            .list();

    assertThat(results.size(), equalTo(25));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertTrue(
            taskSummary
                    .getPrimaryObjRef()
                    .getSystemInstance()
                    .compareToIgnoreCase(previousSummary.getPrimaryObjRef().getSystemInstance())
                <= 0);
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testSortByPorCompanyAndClaimed() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByPrimaryObjectReferenceCompany(desc)
            .orderByClaimed(asc)
            .list();

    assertThat(results.size(), equalTo(25));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      // System.out.println("porCompany: " + taskSummary.getPrimaryObjRef().getCompany() + ",
      // claimed: "
      // + taskSummary.getClaimed());
      if (previousSummary != null) {
        assertTrue(
            taskSummary
                    .getPrimaryObjRef()
                    .getCompany()
                    .compareToIgnoreCase(previousSummary.getPrimaryObjRef().getCompany())
                <= 0);
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testSortByWbKeyPrioPorValueAndCompleted() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .stateIn(TaskState.READY)
            .orderByWorkbasketKey(null)
            .workbasketIdIn("WBI:100000000000000000000000000000000015")
            .orderByPriority(desc)
            .orderByPrimaryObjectReferenceValue(asc)
            .orderByCompleted(desc)
            .list();

    assertThat(results.size(), equalTo(22));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertTrue(
            taskSummary
                    .getWorkbasketSummary()
                    .getKey()
                    .compareToIgnoreCase(previousSummary.getWorkbasketSummary().getKey())
                >= 0);
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testSortBpIdClassificationIdDescriptionAndPorType() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .stateIn(TaskState.READY)
            .workbasketIdIn("WBI:100000000000000000000000000000000015")
            .orderByBusinessProcessId(asc)
            .orderByClassificationKey(null)
            .orderByPrimaryObjectReferenceType(SortDirection.DESCENDING)
            .list();

    assertThat(results.size(), equalTo(22));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertTrue(
            taskSummary
                    .getBusinessProcessId()
                    .compareToIgnoreCase(previousSummary.getBusinessProcessId())
                >= 0);
      }
      previousSummary = taskSummary;
    }
  }
}
