package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.CustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "workbasket level report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideWorkbasketReportAccTest extends AbstractReportAccTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProvideWorkbasketReportAccTest.class);

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getMonitorService();
    ThrowingCallable call =
        () -> {
          monitorService.createWorkbasketReportBuilder().buildReport();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfWorkbasketReportBasedOnDueDate()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    WorkbasketReport report = monitorService.createWorkbasketReportBuilder().buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    assertThat(report.getRow("USER_1_1").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER_1_2").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER_1_3").getTotalValue()).isEqualTo(10);

    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetWorkbasketReportWithReportLineItemDefinitions()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    final int sumLineCount = IntStream.of(report.getSumRow().getCells()).sum();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    assertThat(report.getRow("USER_1_1").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER_1_2").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER_1_3").getTotalValue()).isEqualTo(10);

    int[] sumRow = report.getSumRow().getCells();
    assertThat(sumRow).isEqualTo(new int[] {10, 9, 11, 0, 4, 0, 7, 4, 5});

    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
    assertThat(sumLineCount).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReport() throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("USER_1_1").getCells();
    assertThat(row1).isEqualTo(new int[] {13, 3, 1, 1, 2});

    int[] row2 = report.getRow("USER_1_2").getCells();
    assertThat(row2).isEqualTo(new int[] {4, 6, 3, 6, 1});

    int[] row3 = report.getRow("USER_1_3").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 2, 0, 0, 6});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportNotInWorkingDays()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("USER_1_1").getCells();
    assertThat(row1).isEqualTo(new int[] {16, 0, 1, 0, 3});

    int[] row2 = report.getRow("USER_1_2").getCells();
    assertThat(row2).isEqualTo(new int[] {10, 0, 3, 0, 7});

    int[] row3 = report.getRow("USER_1_3").getCells();
    assertThat(row3).isEqualTo(new int[] {4, 0, 0, 0, 6});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportWithWorkbasketFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .workbasketIdIn(workbasketIds)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(1);

    int[] row1 = report.getRow("USER_1_1").getCells();
    assertThat(row1).isEqualTo(new int[] {13, 3, 1, 1, 2});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportWithStateFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TaskState> states = Collections.singletonList(TaskState.READY);
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .stateIn(states)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("USER_1_1").getCells();
    assertThat(row1).isEqualTo(new int[] {13, 3, 1, 1, 0});

    int[] row2 = report.getRow("USER_1_2").getCells();
    assertThat(row2).isEqualTo(new int[] {4, 6, 3, 6, 0});

    int[] row3 = report.getRow("USER_1_3").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 2, 0, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportWithCategoryFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .categoryIn(categories)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("USER_1_1").getCells();
    assertThat(row1).isEqualTo(new int[] {3, 1, 1, 1, 2});

    int[] row2 = report.getRow("USER_1_2").getCells();
    assertThat(row2).isEqualTo(new int[] {1, 1, 1, 0, 1});

    int[] row3 = report.getRow("USER_1_3").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 1, 0, 0, 4});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportWithDomainFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> domains = Collections.singletonList("DOMAIN_A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("USER_1_1").getCells();
    assertThat(row1).isEqualTo(new int[] {8, 1, 0, 1, 2});

    int[] row2 = report.getRow("USER_1_2").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 2, 2, 4, 0});

    int[] row3 = report.getRow("USER_1_3").getCells();
    assertThat(row3).isEqualTo(new int[] {1, 1, 0, 0, 2});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportWithCustomFieldValueFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("USER_1_1").getCells();
    assertThat(row1).isEqualTo(new int[] {6, 1, 1, 1, 1});

    int[] row2 = report.getRow("USER_1_2").getCells();
    assertThat(row2).isEqualTo(new int[] {3, 2, 2, 3, 1});

    int[] row3 = report.getRow("USER_1_3").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 1, 0, 0, 1});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportForSelectedClassifications()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();
    List<CombinedClassificationFilter> combinedClassificationFilter = new ArrayList<>();
    combinedClassificationFilter.add(
        new CombinedClassificationFilter(
            "CLI:000000000000000000000000000000000003",
            "CLI:000000000000000000000000000000000008"));
    combinedClassificationFilter.add(
        new CombinedClassificationFilter(
            "CLI:000000000000000000000000000000000003",
            "CLI:000000000000000000000000000000000009"));
    combinedClassificationFilter.add(
        new CombinedClassificationFilter(
            "CLI:000000000000000000000000000000000002",
            "CLI:000000000000000000000000000000000007"));
    combinedClassificationFilter.add(
        new CombinedClassificationFilter("CLI:000000000000000000000000000000000005"));

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .combinedClassificationFilterIn(combinedClassificationFilter)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("USER_1_1").getCells();
    assertThat(row1).isEqualTo(new int[] {3, 3, 0, 1, 1});

    int[] row2 = report.getRow("USER_1_2").getCells();
    assertThat(row2).isEqualTo(new int[] {0, 2, 1, 6, 0});

    int[] row3 = report.getRow("USER_1_3").getCells();
    assertThat(row3).isEqualTo(new int[] {1, 0, 0, 0, 3});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfWorkbasketReportBasedOnPlannedDateWithReportLineItemDefinitions()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    WorkbasketReport report =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildPlannedDateBasedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    assertThat(report.getRow("USER_1_1").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER_1_2").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER_1_3").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("USER_1_1").getCells()[2]).isEqualTo(2);
    assertThat(report.getRow("USER_1_2").getCells()[1]).isEqualTo(1);

    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  private List<TimeIntervalColumnHeader> getListOfColumnHeaders() {
    List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
    columnHeaders.add(new TimeIntervalColumnHeader(Integer.MIN_VALUE, -11));
    columnHeaders.add(new TimeIntervalColumnHeader(-10, -6));
    columnHeaders.add(new TimeIntervalColumnHeader(-5, -2));
    columnHeaders.add(new TimeIntervalColumnHeader(-1));
    columnHeaders.add(new TimeIntervalColumnHeader(0));
    columnHeaders.add(new TimeIntervalColumnHeader(1));
    columnHeaders.add(new TimeIntervalColumnHeader(2, 5));
    columnHeaders.add(new TimeIntervalColumnHeader(6, 10));
    columnHeaders.add(new TimeIntervalColumnHeader(11, Integer.MAX_VALUE));
    return columnHeaders;
  }

  private List<TimeIntervalColumnHeader> getShortListOfColumnHeaders() {
    List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
    columnHeaders.add(new TimeIntervalColumnHeader(Integer.MIN_VALUE, -6));
    columnHeaders.add(new TimeIntervalColumnHeader(-5, -1));
    columnHeaders.add(new TimeIntervalColumnHeader(0));
    columnHeaders.add(new TimeIntervalColumnHeader(1, 5));
    columnHeaders.add(new TimeIntervalColumnHeader(6, Integer.MAX_VALUE));
    return columnHeaders;
  }

  private String reportToString(WorkbasketReport report) {
    return reportToString(report, null);
  }

  private String reportToString(
      WorkbasketReport report, List<TimeIntervalColumnHeader> reportLineItemDefinitions) {
    String formatColumWidth = "| %-7s ";
    String formatFirstColumn = "| %-36s  %-4s ";
    final String formatFirstColumnFirstLine = "| %-29s %12s ";
    final String formatFirstColumnSumLine = "| %-36s  %-5s";
    int reportWidth =
        reportLineItemDefinitions == null ? 46 : reportLineItemDefinitions.size() * 10 + 46;

    StringBuilder builder = new StringBuilder();
    builder.append("\n");
    for (int i = 0; i < reportWidth; i++) {
      builder.append("-");
    }
    builder.append("\n");
    builder.append(String.format(formatFirstColumnFirstLine, "Workbaskets", "Total"));
    if (reportLineItemDefinitions != null) {
      for (TimeIntervalColumnHeader def : reportLineItemDefinitions) {
        if (def.getLowerAgeLimit() == Integer.MIN_VALUE) {
          builder.append(String.format(formatColumWidth, "< " + def.getUpperAgeLimit()));
        } else if (def.getUpperAgeLimit() == Integer.MAX_VALUE) {
          builder.append(String.format(formatColumWidth, "> " + def.getLowerAgeLimit()));
        } else if (def.getLowerAgeLimit() == def.getUpperAgeLimit()) {
          if (def.getLowerAgeLimit() == 0) {
            builder.append(String.format(formatColumWidth, "today"));
          } else {
            builder.append(String.format(formatColumWidth, def.getLowerAgeLimit()));
          }
        } else {
          builder.append(
              String.format(
                  formatColumWidth, def.getLowerAgeLimit() + ".." + def.getUpperAgeLimit()));
        }
      }
    }
    builder.append("|\n");

    for (int i = 0; i < reportWidth; i++) {
      builder.append("-");
    }
    builder.append("\n");

    for (String rl : report.rowTitles()) {
      builder.append(String.format(formatFirstColumn, rl, report.getRow(rl).getTotalValue()));
      if (reportLineItemDefinitions != null) {
        for (int cell : report.getRow(rl).getCells()) {
          builder.append(String.format(formatColumWidth, cell));
        }
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
      builder.append(String.format(formatColumWidth, cell));
    }
    builder.append("|\n");
    for (int i = 0; i < reportWidth; i++) {
      builder.append("-");
    }
    builder.append("\n");
    return builder.toString();
  }
}
