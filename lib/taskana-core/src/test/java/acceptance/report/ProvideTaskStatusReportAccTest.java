package acceptance.report;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.report.api.TaskMonitorService;
import pro.taskana.report.api.TaskStatusReport;
import pro.taskana.report.api.header.TaskStatusColumnHeader;
import pro.taskana.report.api.item.TaskQueryItem;
import pro.taskana.report.api.structure.Row;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "task status report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideTaskStatusReportAccTest extends AbstractReportAccTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProvideWorkbasketReportAccTest.class);

  @Test
  void testRoleCheck() {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> taskMonitorService.createTaskStatusReportBuilder().buildReport());
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testCompleteTaskStatusReport() throws NotAuthorizedException, InvalidArgumentException {
    // given
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
    // when
    TaskStatusReport report = taskMonitorService.createTaskStatusReportBuilder().buildReport();
    // then
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }
    assertNotNull(report);
    assertEquals(3, report.rowSize());

    Row<TaskQueryItem> row1 = report.getRow("DOMAIN_A");
    assertArrayEquals(new int[] {22, 4, 0}, row1.getCells());
    assertEquals(26, row1.getTotalValue());

    Row<TaskQueryItem> row2 = report.getRow("DOMAIN_B");
    assertArrayEquals(new int[] {9, 3, 0}, row2.getCells());
    assertEquals(12, row2.getTotalValue());

    Row<TaskQueryItem> row3 = report.getRow("DOMAIN_C");
    assertArrayEquals(new int[] {10, 2, 0}, row3.getCells());
    assertEquals(12, row3.getTotalValue());

    Row<TaskQueryItem> sumRow = report.getSumRow();
    assertArrayEquals(new int[] {41, 9, 0}, sumRow.getCells());
    assertEquals(50, sumRow.getTotalValue());
  }

  @WithAccessId(userName = "admin")
  @Test
  void testCompleteTaskStatusReportAsAdmin()
      throws NotAuthorizedException, InvalidArgumentException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
    taskMonitorService.createTaskStatusReportBuilder().buildReport();
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testCompleteTaskStatusReportWithDomainFilter()
      throws NotAuthorizedException, InvalidArgumentException {
    // given
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
    // when
    TaskStatusReport report =
        taskMonitorService
            .createTaskStatusReportBuilder()
            .domainIn(asList("DOMAIN_C", "DOMAIN_A"))
            .buildReport();
    // then
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }
    assertNotNull(report);
    assertEquals(2, report.rowSize());

    Row<TaskQueryItem> row1 = report.getRow("DOMAIN_A");
    assertArrayEquals(new int[] {22, 4, 0}, row1.getCells());
    assertEquals(26, row1.getTotalValue());

    Row<TaskQueryItem> row2 = report.getRow("DOMAIN_C");
    assertArrayEquals(new int[] {10, 2, 0}, row2.getCells());
    assertEquals(12, row2.getTotalValue());

    Row<TaskQueryItem> sumRow = report.getSumRow();
    assertArrayEquals(new int[] {32, 6, 0}, sumRow.getCells());
    assertEquals(38, sumRow.getTotalValue());
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testCompleteTaskStatusReportWithStateFilter()
      throws NotAuthorizedException, InvalidArgumentException {
    // given
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();
    // when
    TaskStatusReport report =
        taskMonitorService
            .createTaskStatusReportBuilder()
            .stateIn(Collections.singletonList(TaskState.READY))
            .buildReport();
    // then
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }
    assertNotNull(report);
    assertEquals(3, report.rowSize());

    Row<TaskQueryItem> row1 = report.getRow("DOMAIN_A");
    assertArrayEquals(new int[] {22}, row1.getCells());
    assertEquals(22, row1.getTotalValue());

    Row<TaskQueryItem> row2 = report.getRow("DOMAIN_B");
    assertArrayEquals(new int[] {9}, row2.getCells());
    assertEquals(9, row2.getTotalValue());

    Row<TaskQueryItem> row3 = report.getRow("DOMAIN_C");
    assertArrayEquals(new int[] {10}, row3.getCells());
    assertEquals(10, row3.getTotalValue());

    Row<TaskQueryItem> sumRow = report.getSumRow();
    assertArrayEquals(new int[] {41}, sumRow.getCells());
    assertEquals(41, sumRow.getTotalValue());
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
