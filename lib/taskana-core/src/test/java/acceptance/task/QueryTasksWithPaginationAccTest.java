package acceptance.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.KeyDomain;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "query tasks by workbasket with pagination" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksWithPaginationAccTest extends AbstractAccTest {

  QueryTasksWithPaginationAccTest() {
    super();
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testGetFirstPageOfTaskQueryWithOffset() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(0, 10);
    assertThat(results.size(), equalTo(10));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testSecondPageOfTaskQueryWithOffset() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(10, 10);
    assertThat(results.size(), equalTo(10));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testListOffsetAndLimitOutOfBounds() {
    TaskService taskService = taskanaEngine.getTaskService();

    // both will be 0, working
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(-1, -3);
    assertThat(results.size(), equalTo(0));

    // limit will be 0
    results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(1, -3);
    assertThat(results.size(), equalTo(0));

    // offset will be 0
    results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .list(-1, 3);
    assertThat(results.size(), equalTo(3));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testPaginationWithPages() {
    TaskService taskService = taskanaEngine.getTaskService();

    // Getting full page
    int pageNumber = 2;
    int pageSize = 4;
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(4));

    // Getting full page
    pageNumber = 4;
    pageSize = 1;
    results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(1));

    // Getting last results on 1 big page
    pageNumber = 1;
    pageSize = 100;
    results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(22));

    // Getting last results on multiple pages
    pageNumber = 3;
    pageSize = 10;
    results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(2));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testPaginationNullAndNegativeLimitsIgnoring() {
    TaskService taskService = taskanaEngine.getTaskService();

    // 0 limit/size = 0 results
    int pageNumber = 2;
    int pageSize = 0;
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(0));

    // Negative size will be 0 = 0 results
    pageNumber = 2;
    pageSize = -1;
    results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(0));

    // Negative page = first page
    pageNumber = -1;
    pageSize = 10;
    results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .listPage(pageNumber, pageSize);
    assertThat(results.size(), equalTo(10));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testCountOfTaskQuery() {
    TaskService taskService = taskanaEngine.getTaskService();
    long count =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
            .count();
    assertThat(count, equalTo(22L));
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testCountOfTaskQueryWithAttachmentChannelFilter() {
    TaskService taskService = taskanaEngine.getTaskService();
    long count = taskService.createTaskQuery().attachmentChannelIn("ch6").count();
    assertThat(count, equalTo(2L));
  }
}
