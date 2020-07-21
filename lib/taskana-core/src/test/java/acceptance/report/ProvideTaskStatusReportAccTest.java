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
  void should_augmentDisplayNames_When_ReportIsBuild() throws Exception {
    TaskStatusReport report = MONITOR_SERVICE.createTaskStatusReportBuilder().buildReport();

    assertThat(report.getRows()).hasSize(3);
    assertThat(report.getRow("DOMAIN_A").getDisplayName()).isEqualTo("DOMAIN_A");
    assertThat(report.getRow("DOMAIN_B").getDisplayName()).isEqualTo("DOMAIN_B");
    assertThat(report.getRow("DOMAIN_C").getDisplayName()).isEqualTo("DOMAIN_C");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testCompleteTaskStatusReport() throws Exception {
    TaskStatusReport report = MONITOR_SERVICE.createTaskStatusReportBuilder().buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    Row<TaskQueryItem> row1 = report.getRow("DOMAIN_A");
    assertThat(row1.getCells()).isEqualTo(new int[] {22, 4, 0, 0, 0});
    assertThat(row1.getTotalValue()).isEqualTo(26);

    Row<TaskQueryItem> row2 = report.getRow("DOMAIN_B");
    assertThat(row2.getCells()).isEqualTo(new int[] {9, 3, 0, 0, 0});
    assertThat(row2.getTotalValue()).isEqualTo(12);

    Row<TaskQueryItem> row3 = report.getRow("DOMAIN_C");
    assertThat(row3.getCells()).isEqualTo(new int[] {10, 2, 0, 0, 0});
    assertThat(row3.getTotalValue()).isEqualTo(12);

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
    assertThat(report.rowSize()).isEqualTo(2);

    Row<TaskQueryItem> row1 = report.getRow("DOMAIN_A");
    assertThat(row1.getCells()).isEqualTo(new int[] {22, 4, 0, 0, 0});
    assertThat(row1.getTotalValue()).isEqualTo(26);

    Row<TaskQueryItem> row2 = report.getRow("DOMAIN_C");
    assertThat(row2.getCells()).isEqualTo(new int[] {10, 2, 0, 0, 0});
    assertThat(row2.getTotalValue()).isEqualTo(12);

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

    Row<TaskQueryItem> row1 = report.getRow("DOMAIN_A");
    assertThat(row1.getCells()).isEqualTo(new int[] {22});
    assertThat(row1.getTotalValue()).isEqualTo(22);

    Row<TaskQueryItem> row2 = report.getRow("DOMAIN_B");
    assertThat(row2.getCells()).isEqualTo(new int[] {9});
    assertThat(row2.getTotalValue()).isEqualTo(9);

    Row<TaskQueryItem> row3 = report.getRow("DOMAIN_C");
    assertThat(row3.getCells()).isEqualTo(new int[] {10});
    assertThat(row3.getTotalValue()).isEqualTo(10);

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
}
