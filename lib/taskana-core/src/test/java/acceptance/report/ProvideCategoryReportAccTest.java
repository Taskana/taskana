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

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.CategoryReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.CustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "category report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideCategoryReportAccTest extends AbstractReportAccTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProvideCategoryReportAccTest.class);

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    //    Assertions.assertThrows(
    //        NotAuthorizedException.class,
    //        () -> monitorService.createCategoryReportBuilder().buildReport());
    ThrowingCallable call =
        () -> {
          monitorService.createCategoryReportBuilder().buildReport();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfCategoryReport() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    CategoryReport report = monitorService.createCategoryReportBuilder().buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    assertThat(report.getRow("EXTERN").getTotalValue()).isEqualTo(33);
    assertThat(report.getRow("AUTOMATIC").getTotalValue()).isEqualTo(7);
    assertThat(report.getRow("MANUAL").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("EXTERN").getCells().length).isEqualTo(0);
    assertThat(report.getRow("AUTOMATIC").getCells().length).isEqualTo(0);
    assertThat(report.getRow("MANUAL").getCells().length).isEqualTo(0);
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCategoryReportWithReportLineItemDefinitions() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    CategoryReport report =
        monitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    final int sumLineCount = IntStream.of(report.getSumRow().getCells()).sum();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    assertThat(report.getRow("EXTERN").getTotalValue()).isEqualTo(33);
    assertThat(report.getRow("AUTOMATIC").getTotalValue()).isEqualTo(7);
    assertThat(report.getRow("MANUAL").getTotalValue()).isEqualTo(10);

    int[] sumRow = report.getSumRow().getCells();
    assertThat(sumRow).isEqualTo(new int[] {10, 9, 11, 0, 4, 0, 7, 4, 5});
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
    assertThat(sumLineCount).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCategoryReport() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    CategoryReport report =
        monitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("EXTERN").getCells();
    assertThat(row1).isEqualTo(new int[] {15, 8, 2, 6, 2});

    int[] row2 = report.getRow("AUTOMATIC").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 1, 0, 1, 3});

    int[] row3 = report.getRow("MANUAL").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 2, 2, 0, 4});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCategoryReportNotInWorkingDays() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    CategoryReport report =
        monitorService.createCategoryReportBuilder().withColumnHeaders(columnHeaders).buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("EXTERN").getCells();
    assertThat(row1).isEqualTo(new int[] {23, 0, 2, 0, 8});

    int[] row2 = report.getRow("AUTOMATIC").getCells();
    assertThat(row2).isEqualTo(new int[] {3, 0, 0, 0, 4});

    int[] row3 = report.getRow("MANUAL").getCells();
    assertThat(row3).isEqualTo(new int[] {4, 0, 2, 0, 4});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCategoryReportWithWorkbasketFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    CategoryReport report =
        monitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .workbasketIdIn(workbasketIds)
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("EXTERN").getCells();
    assertThat(row1).isEqualTo(new int[] {10, 2, 0, 0, 0});

    int[] row2 = report.getRow("AUTOMATIC").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 1, 0, 1, 1});

    int[] row3 = report.getRow("MANUAL").getCells();
    assertThat(row3).isEqualTo(new int[] {1, 0, 1, 0, 1});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCategoryReportWithStateFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TaskState> states = Collections.singletonList(TaskState.READY);
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    CategoryReport report =
        monitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .stateIn(states)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("EXTERN").getCells();
    assertThat(row1).isEqualTo(new int[] {15, 8, 2, 6, 0});

    int[] row2 = report.getRow("AUTOMATIC").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 1, 0, 1, 0});

    int[] row3 = report.getRow("MANUAL").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 2, 2, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCategoryReportWithCategoryFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    CategoryReport report =
        monitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .categoryIn(categories)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(2);

    int[] row1 = report.getRow("AUTOMATIC").getCells();
    assertThat(row1).isEqualTo(new int[] {2, 1, 0, 1, 3});

    int[] row2 = report.getRow("MANUAL").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 2, 2, 0, 4});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCategoryReportWithDomainFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> domains = Collections.singletonList("DOMAIN_A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    CategoryReport report =
        monitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("EXTERN").getCells();
    assertThat(row1).isEqualTo(new int[] {8, 4, 2, 4, 0});

    int[] row2 = report.getRow("AUTOMATIC").getCells();
    assertThat(row2).isEqualTo(new int[] {1, 0, 0, 1, 1});

    int[] row3 = report.getRow("MANUAL").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 0, 0, 0, 3});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCategoryReportWithCustomFieldValueFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    CategoryReport report =
        monitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("EXTERN").getCells();
    assertThat(row1).isEqualTo(new int[] {9, 3, 1, 3, 0});

    int[] row2 = report.getRow("AUTOMATIC").getCells();
    assertThat(row2).isEqualTo(new int[] {1, 0, 0, 1, 1});

    int[] row3 = report.getRow("MANUAL").getCells();
    assertThat(row3).isEqualTo(new int[] {1, 1, 2, 0, 2});
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

  private String reportToString(CategoryReport report) {
    return reportToString(report, null);
  }

  private String reportToString(
      CategoryReport report, List<TimeIntervalColumnHeader> columnHeaders) {
    final String formatColumWidth = "| %-7s ";
    final String formatFirstColumn = "| %-36s  %-4s ";
    final String formatFirstColumnFirstLine = "| %-29s %12s ";
    final String formatFirstColumnSumLine = "| %-36s  %-5s";
    int reportWidth = columnHeaders == null ? 46 : columnHeaders.size() * 10 + 46;

    StringBuilder builder = new StringBuilder();
    builder.append("\n");
    for (int i = 0; i < reportWidth; i++) {
      builder.append("-");
    }
    builder.append("\n");
    builder.append(String.format(formatFirstColumnFirstLine, "Categories", "Total"));
    if (columnHeaders != null) {
      for (TimeIntervalColumnHeader def : columnHeaders) {
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
      if (columnHeaders != null) {
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
