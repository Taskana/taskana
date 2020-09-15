package acceptance.report;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "task status report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideTaskStatusReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = taskanaEngine.getMonitorService();

  @BeforeEach
  void reset() throws Exception {
    resetDb();
  }

  @Test
  void should_ThrowException_IfUserIsNotAuthorized() {
    assertThatThrownBy(() -> MONITOR_SERVICE.createTaskStatusReportBuilder().buildReport())
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "unknown")
  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_ThrowException_IfUserIsNotAdminOrMonitor() {
    assertThatThrownBy(() -> MONITOR_SERVICE.createTaskStatusReportBuilder().buildReport())
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "monitor")
  @TestTemplate
  void should_BuildReport_IfUserIsAdminOrMonitor() {
    assertThatCode(() -> MONITOR_SERVICE.createTaskStatusReportBuilder().buildReport())
        .doesNotThrowAnyException();
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_AugmentDisplayNames_When_ReportIsBuild() throws Exception {
    TaskStatusReport report = MONITOR_SERVICE.createTaskStatusReportBuilder().buildReport();

    assertThat(report.getRows()).hasSize(3);
    assertThat(report.getRow("USER-1-1").getDisplayName()).isEqualTo("PPK User 1 KSC 1");
    assertThat(report.getRow("USER-1-2").getDisplayName()).isEqualTo("PPK User 1 KSC 2");
    assertThat(report.getRow("USER-1-3").getDisplayName()).isEqualTo("PPK User 1 KSC 3");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testCompleteTaskStatusReport() throws Exception {
    TaskStatusReport report = MONITOR_SERVICE.createTaskStatusReportBuilder().buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    Row<TaskQueryItem> row1 = report.getRow("USER-1-1");
    assertThat(row1.getCells()).isEqualTo(new int[] {18, 2, 0, 0, 0});
    assertThat(row1.getTotalValue()).isEqualTo(20);

    Row<TaskQueryItem> row2 = report.getRow("USER-1-2");
    assertThat(row2.getCells()).isEqualTo(new int[] {19, 1, 0, 0, 0});
    assertThat(row2.getTotalValue()).isEqualTo(20);

    Row<TaskQueryItem> row3 = report.getRow("USER-1-3");
    assertThat(row3.getCells()).isEqualTo(new int[] {4, 6, 0, 0, 0});
    assertThat(row3.getTotalValue()).isEqualTo(10);

    Row<TaskQueryItem> sumRow = report.getSumRow();
    assertThat(sumRow.getCells()).isEqualTo(new int[] {41, 9, 0, 0, 0});
    assertThat(sumRow.getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testCompleteTaskStatusReportWithDomainFilter() throws Exception {
    TaskStatusReport report =
        MONITOR_SERVICE
            .createTaskStatusReportBuilder()
            .domainIn(asList("DOMAIN_C", "DOMAIN_A"))
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    Row<TaskQueryItem> row1 = report.getRow("USER-1-1");
    assertThat(row1.getCells()).isEqualTo(new int[] {15, 2, 0, 0, 0});
    assertThat(row1.getTotalValue()).isEqualTo(17);

    Row<TaskQueryItem> row2 = report.getRow("USER-1-2");
    assertThat(row2.getCells()).isEqualTo(new int[] {14, 1, 0, 0, 0});
    assertThat(row2.getTotalValue()).isEqualTo(15);

    Row<TaskQueryItem> row3 = report.getRow("USER-1-3");
    assertThat(row3.getCells()).isEqualTo(new int[] {3, 3, 0, 0, 0});
    assertThat(row3.getTotalValue()).isEqualTo(6);

    Row<TaskQueryItem> sumRow = report.getSumRow();
    assertThat(sumRow.getCells()).isEqualTo(new int[] {32, 6, 0, 0, 0});
    assertThat(sumRow.getTotalValue()).isEqualTo(38);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testCompleteTaskStatusReportWithStateFilter() throws Exception {
    TaskStatusReport report =
        MONITOR_SERVICE
            .createTaskStatusReportBuilder()
            .stateIn(Collections.singletonList(TaskState.READY))
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    Row<TaskQueryItem> row1 = report.getRow("USER-1-1");
    assertThat(row1.getCells()).isEqualTo(new int[] {18});
    assertThat(row1.getTotalValue()).isEqualTo(18);

    Row<TaskQueryItem> row2 = report.getRow("USER-1-2");
    assertThat(row2.getCells()).isEqualTo(new int[] {19});
    assertThat(row2.getTotalValue()).isEqualTo(19);

    Row<TaskQueryItem> row3 = report.getRow("USER-1-3");
    assertThat(row3.getCells()).isEqualTo(new int[] {4});
    assertThat(row3.getTotalValue()).isEqualTo(4);

    Row<TaskQueryItem> sumRow = report.getSumRow();
    assertThat(sumRow.getCells()).isEqualTo(new int[] {41});
    assertThat(sumRow.getTotalValue()).isEqualTo(41);
  }

  @WithAccessId(user = "monitor", groups = "admin")
  @Test
  void testCompleteTaskStatusReportWithStates() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    taskService.terminateTask("TKI:000000000000000000000000000000000010");
    taskService.terminateTask("TKI:000000000000000000000000000000000011");
    taskService.terminateTask("TKI:000000000000000000000000000000000012");
    taskService.cancelTask("TKI:000000000000000000000000000000000013");
    taskService.cancelTask("TKI:000000000000000000000000000000000014");
    TaskStatusReport report =
        MONITOR_SERVICE
            .createTaskStatusReportBuilder()
            .stateIn(
                Arrays.asList(
                    TaskState.READY,
                    TaskState.CLAIMED,
                    TaskState.COMPLETED,
                    TaskState.CANCELLED,
                    TaskState.TERMINATED))
            .buildReport();
    int[] summaryNumbers = report.getSumRow().getCells();
    assertThat(summaryNumbers.length).isEqualTo(5);
    assertThat(summaryNumbers[3]).isEqualTo(2); // number of cancelled tasks
    assertThat(summaryNumbers[4]).isEqualTo(3); // number of terminated tasks
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_FilterTasksByWorkbasket_When_BuilderIsFilteredWithWorkbasketIds() throws Exception {
    TaskStatusReport report =
        MONITOR_SERVICE
            .createTaskStatusReportBuilder()
            .workbasketIdsIn(Collections.singletonList("WBI:000000000000000000000000000000000003"))
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(1);

    Row<TaskQueryItem> row1 = report.getRow("USER-1-3");
    assertThat(row1.getCells()).isEqualTo(new int[] {4, 6, 0, 0, 0});
    assertThat(row1.getTotalValue()).isEqualTo(10);

    Row<TaskQueryItem> sumRow = report.getSumRow();
    assertThat(sumRow.getCells()).isEqualTo(new int[] {4, 6, 0, 0, 0});
    assertThat(sumRow.getTotalValue()).isEqualTo(10);
  }
}
