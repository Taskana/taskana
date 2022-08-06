package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.api.BaseQuery.SortDirection.DESCENDING;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for all "query tasks by workbasket with pagination" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksWithPaginationAccTest extends AbstractAccTest {

  @Nested
  class PaginationTest {

    @WithAccessId(user = "admin")
    @Test
    void testQueryAllPaged() {
      TaskQuery taskQuery = taskanaEngine.getTaskService().createTaskQuery();
      long numberOfTasks = taskQuery.count();
      assertThat(numberOfTasks).isEqualTo(99);
      List<TaskSummary> tasks = taskQuery.orderByDue(DESCENDING).list();
      assertThat(tasks).hasSize(99);
      List<TaskSummary> tasksp = taskQuery.orderByDue(DESCENDING).listPage(4, 5);
      assertThat(tasksp).hasSize(5);
      tasksp = taskQuery.orderByDue(DESCENDING).listPage(5, 5);
      assertThat(tasksp).hasSize(5);
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class OffsetAndLimit {
      @WithAccessId(user = "teamlead-1")
      @Test
      void testGetFirstPageOfTaskQueryWithOffset() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery().list(0, 10);
        assertThat(results).hasSize(10);
      }

      @WithAccessId(user = "teamlead-1")
      @Test
      void testSecondPageOfTaskQueryWithOffset() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery().list(10, 10);
        assertThat(results).hasSize(10);
      }

      @WithAccessId(user = "teamlead-1")
      @Test
      void testListOffsetAndLimitOutOfBounds() {
        TaskService taskService = taskanaEngine.getTaskService();

        // both will be 0, working
        List<TaskSummary> results = taskService.createTaskQuery().list(-1, -3);
        assertThat(results).isEmpty();

        // limit will be 0
        results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
                .list(1, -3);
        assertThat(results).isEmpty();

        // offset will be 0
        results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
                .list(-1, 3);
        assertThat(results).hasSize(3);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class ListPage {
      @WithAccessId(user = "teamlead-1")
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
        assertThat(results).hasSize(4);

        // Getting full page
        pageNumber = 4;
        pageSize = 1;
        results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
                .listPage(pageNumber, pageSize);
        assertThat(results).hasSize(1);

        // Getting last results on 1 big page
        pageNumber = 1;
        pageSize = 100;
        results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
                .listPage(pageNumber, pageSize);
        assertThat(results).hasSize(22);

        // Getting last results on multiple pages
        pageNumber = 3;
        pageSize = 10;
        results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
                .listPage(pageNumber, pageSize);
        assertThat(results).hasSize(2);
      }

      @WithAccessId(user = "teamlead-1")
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
        assertThat(results).isEmpty();

        // Negative size will be 0 = 0 results
        pageNumber = 2;
        pageSize = -1;
        results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
                .listPage(pageNumber, pageSize);
        assertThat(results).isEmpty();

        // Negative page = first page
        pageNumber = -1;
        pageSize = 10;
        results =
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("GPK_KSC", "DOMAIN_A"))
                .listPage(pageNumber, pageSize);
        assertThat(results).hasSize(10);
      }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Count {
      @Disabled()
      @WithAccessId(user = "teamlead-1")
      @Test
      void testCountOfTaskQuery() {
        TaskService taskService = taskanaEngine.getTaskService();
        long count = taskService.createTaskQuery().count();
        assertThat(count).isEqualTo(22L);
      }
    }
  }
}
