package acceptance.task;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;

import helper.AbstractAccTest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for all "query tasks with sorting" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksWithSortingAccTest extends AbstractAccTest {

  private static SortDirection asc = SortDirection.ASCENDING;
  private static SortDirection desc = SortDirection.DESCENDING;

  QueryTasksWithSortingAccTest() {
    super();
  }

  @WithAccessId(user = "admin")
  @Test
  void testSortByModifiedAndDomain() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
            .orderByModified(desc)
            .orderByDomain(null)
            .list();

    assertThat(results).hasSize(25);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertThat(previousSummary.getModified().isBefore(taskSummary.getModified())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_sortByTaskIdDesc_When_TaskQueryFilterIsApplied() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
            .orderByTaskId(desc)
            .list();

    // test is only valid with at least 2 results
    assertThat(results).hasSizeGreaterThan(2);

    List<String> idsDesc =
        results.stream()
            .map(TaskSummary::getId)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

    for (int i = 0; i < results.size(); i++) {
      assertThat(results.get(i).getId()).isEqualTo(idsDesc.get(i));
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_sortByTaskIdAsc_When_TaskQueryFilterIsApplied() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
            .orderByTaskId(null)
            .list();

    // test is only valid with at least 2 results
    assertThat(results).hasSizeGreaterThan(2);

    List<String> idsAsc =
        results.stream().map(TaskSummary::getId).sorted().collect(Collectors.toList());

    for (int i = 0; i < results.size(); i++) {
      assertThat(results.get(i).getId()).isEqualTo(idsAsc.get(i));
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_sortByWorkbasketNameAsc_When_TaskQueryFilterIsApplied() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results = taskService.createTaskQuery().orderByWorkbasketName(asc).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(TaskSummary::getWorkbasketSummary)
        .extracting(WorkbasketSummary::getName)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_sortByWorkbasketNameDsc_When_TaskQueryFilterIsApplied() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results = taskService.createTaskQuery().orderByWorkbasketName(desc).list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .extracting(TaskSummary::getWorkbasketSummary)
        .extracting(WorkbasketSummary::getName)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @WithAccessId(user = "admin")
  @Test
  void testSortByDomainNameAndCreated() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .orderByDomain(asc)
            .orderByName(asc)
            .orderByCreated(null)
            .list();

    assertThat(results)
        .hasSizeGreaterThan(2)
        .isSortedAccordingTo(
            Comparator.comparing(TaskSummary::getDomain, CASE_INSENSITIVE_ORDER)
                .thenComparing(TaskSummary::getName, CASE_INSENSITIVE_ORDER)
                .thenComparing(TaskSummary::getCreated));
  }

  @WithAccessId(user = "admin")
  @Test
  void testSortByPorSystemNoteDueAndOwner() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
            .orderByPrimaryObjectReferenceSystem(SortDirection.DESCENDING)
            .orderByNote(null)
            .orderByDue(null)
            .orderByOwner(asc)
            .list();

    assertThat(results).hasSize(25);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertThat(
                taskSummary
                        .getPrimaryObjRef()
                        .getSystem()
                        .compareToIgnoreCase(previousSummary.getPrimaryObjRef().getSystem())
                    <= 0)
            .isTrue();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testSortByPorSystemInstanceParentProcPlannedAndState() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
            .orderByPrimaryObjectReferenceSystemInstance(desc)
            .orderByParentBusinessProcessId(asc)
            .orderByPlanned(asc)
            .orderByState(asc)
            .list();

    assertThat(results).hasSize(25);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertThat(
                taskSummary
                        .getPrimaryObjRef()
                        .getSystemInstance()
                        .compareToIgnoreCase(previousSummary.getPrimaryObjRef().getSystemInstance())
                    <= 0)
            .isTrue();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testSortByPorCompanyAndClaimed() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER-B-2", "DOMAIN_B"))
            .orderByPrimaryObjectReferenceCompany(desc)
            .orderByClaimed(asc)
            .list();

    assertThat(results).hasSize(25);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      // System.out.println("porCompany: " + taskSummary.getPrimaryObjRef().getCompany() + ",
      // claimed: "
      // + taskSummary.getClaimed());
      if (previousSummary != null) {
        assertThat(
                taskSummary
                        .getPrimaryObjRef()
                        .getCompany()
                        .compareToIgnoreCase(previousSummary.getPrimaryObjRef().getCompany())
                    <= 0)
            .isTrue();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
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

    assertThat(results).hasSize(22);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertThat(
                taskSummary
                        .getWorkbasketSummary()
                        .getKey()
                        .compareToIgnoreCase(previousSummary.getWorkbasketSummary().getKey())
                    >= 0)
            .isTrue();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
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

    assertThat(results).hasSize(22);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      if (previousSummary != null) {
        assertThat(
                taskSummary
                        .getBusinessProcessId()
                        .compareToIgnoreCase(previousSummary.getBusinessProcessId())
                    >= 0)
            .isTrue();
      }
      previousSummary = taskSummary;
    }
  }
}
