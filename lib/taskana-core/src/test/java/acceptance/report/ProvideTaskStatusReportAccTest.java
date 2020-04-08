package acceptance.report;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.header.TaskStatusColumnHeader;
import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;

/** Acceptance test for all "task status report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideTaskStatusReportAccTest extends AbstractReportAccTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProvideWorkbasketReportAccTest.class);

  @BeforeEach
  public void reset() throws Exception {
    resetDb();
  }

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getMonitorService();
    ThrowingCallable call =
        () -> {
          monitorService.createTaskStatusReportBuilder().buildReport();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testCompleteTaskStatusReport() throws NotAuthorizedException, InvalidArgumentException {
    // given
    MonitorService monitorService = taskanaEngine.getMonitorService();
    // when
    TaskStatusReport report = monitorService.createTaskStatusReportBuilder().buildReport();
    // then
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }
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

  @WithAccessId(userName = "admin")
  @Test
  void testCompleteTaskStatusReportAsAdmin()
      throws NotAuthorizedException, InvalidArgumentException {
    MonitorService monitorService = taskanaEngine.getMonitorService();
    monitorService.createTaskStatusReportBuilder().buildReport();
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testCompleteTaskStatusReportWithDomainFilter()
      throws NotAuthorizedException, InvalidArgumentException {
    // given
    MonitorService monitorService = taskanaEngine.getMonitorService();
    // when
    TaskStatusReport report =
        monitorService
            .createTaskStatusReportBuilder()
            .domainIn(asList("DOMAIN_C", "DOMAIN_A"))
            .buildReport();
    // then
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }
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

  @WithAccessId(userName = "monitor")
  @Test
  void testCompleteTaskStatusReportWithStateFilter()
      throws NotAuthorizedException, InvalidArgumentException {
    // given
    MonitorService monitorService = taskanaEngine.getMonitorService();
    // when
    TaskStatusReport report =
        monitorService
            .createTaskStatusReportBuilder()
            .stateIn(Collections.singletonList(TaskState.READY))
            .buildReport();
    // then
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }
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

  @WithAccessId(
      userName = "monitor",
      groupNames = {"admin"})
  @Test
  void testCompleteTaskStatusReportWithStates()
      throws NotAuthorizedException, InvalidArgumentException, InvalidStateException,
          TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();
    taskService.terminateTask("TKI:000000000000000000000000000000000010");
    taskService.terminateTask("TKI:000000000000000000000000000000000011");
    taskService.terminateTask("TKI:000000000000000000000000000000000012");
    taskService.cancelTask("TKI:000000000000000000000000000000000013");
    taskService.cancelTask("TKI:000000000000000000000000000000000014");
    MonitorService monitorService = taskanaEngine.getMonitorService();
    TaskStatusReport report =
        monitorService
            .createTaskStatusReportBuilder()
            .stateIn(
                Arrays.asList(
                    TaskState.READY,
                    TaskState.CLAIMED,
                    TaskState.COMPLETED,
                    TaskState.CANCELLED,
                    TaskState.TERMINATED))
            .buildReport();
    // String rep = reportToString(report);
    // System.out.println(rep);
    int[] summaryNumbers = report.getSumRow().getCells();
    assertThat(summaryNumbers.length).isEqualTo(5);
    assertThat(summaryNumbers[3]).isEqualTo(2); // number of cancelled tasks
    assertThat(summaryNumbers[4]).isEqualTo(3); // number of terminated tasks
  }

  private String reportToString(TaskStatusReport report) {
    List<TaskStatusColumnHeader> columnHeaders = report.getColumnHeaders();
    String formatColumnWidth = "| %-7s ";
    String formatFirstColumn = "| %-36s  %-4s ";
    final String formatFirstColumnFirstLine = "| %-29s %12s ";
    final String formatFirstColumnSumLine = "| %-36s  %-5s";
    int reportWidth = columnHeaders.size() * 10 + 46;

    StringBuilder builder = new StringBuilder();
    builder.append("\n");
    for (int i = 0; i < reportWidth; i++) {
      builder.append("-");
    }
    builder.append("\n");
    builder.append(String.format(formatFirstColumnFirstLine, "Domain", "Total"));
    for (TaskStatusColumnHeader def : columnHeaders) {
      builder.append(String.format(formatColumnWidth, def.getDisplayName()));
    }
    builder.append("|\n");

    for (int i = 0; i < reportWidth; i++) {
      builder.append("-");
    }
    builder.append("\n");

    for (String rl : report.rowTitles()) {
      builder.append(String.format(formatFirstColumn, rl, report.getRow(rl).getTotalValue()));
      for (int cell : report.getRow(rl).getCells()) {
        builder.append(String.format(formatColumnWidth, cell));
      }
      builder.append("|\n");
      for (int i = 0; i < reportWidth; i++) {
        builder.append("-");
      }
      builder.append("\n");
    }
    builder.append(
        String.format(formatFirstColumnSumLine, "Total", report.getSumRow().getTotalValue()));
    for (int cell : report.getSumRow().getCells()) {
      builder.append(String.format(formatColumnWidth, cell));
    }
    builder.append("|\n");
    for (int i = 0; i < reportWidth; i++) {
      builder.append("-");
    }
    builder.append("\n");
    return builder.toString();
  }
}
