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
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.CustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideClassificationReportAccTest extends AbstractReportAccTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProvideClassificationReportAccTest.class);

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    ThrowingCallable call =
        () -> {
          monitorService.createClassificationReportBuilder().buildReport();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfClassificationReport() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    ClassificationReport report = monitorService.createClassificationReportBuilder().buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    assertThat(report.getRow("L10000").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("L20000").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("L30000").getTotalValue()).isEqualTo(7);
    assertThat(report.getRow("L40000").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("L50000").getTotalValue()).isEqualTo(13);
    assertThat(report.getRow("L10000").getCells().length).isEqualTo(0);
    assertThat(report.getRow("L20000").getCells().length).isEqualTo(0);
    assertThat(report.getRow("L30000").getCells().length).isEqualTo(0);
    assertThat(report.getRow("L40000").getCells().length).isEqualTo(0);
    assertThat(report.getRow("L50000").getCells().length).isEqualTo(0);
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetClassificationReportWithReportLineItemDefinitions() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnsHeaders();

    ClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    final int sumLineCount = IntStream.of(report.getSumRow().getCells()).sum();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    assertThat(report.getRow("L10000").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("L20000").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("L30000").getTotalValue()).isEqualTo(7);
    assertThat(report.getRow("L40000").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("L50000").getTotalValue()).isEqualTo(13);

    assertThat(report.getSumRow().getCells()[0]).isEqualTo(10);
    assertThat(report.getSumRow().getCells()[1]).isEqualTo(9);
    assertThat(report.getSumRow().getCells()[2]).isEqualTo(11);
    assertThat(report.getSumRow().getCells()[3]).isEqualTo(0);
    assertThat(report.getSumRow().getCells()[4]).isEqualTo(4);
    assertThat(report.getSumRow().getCells()[5]).isEqualTo(0);
    assertThat(report.getSumRow().getCells()[6]).isEqualTo(7);
    assertThat(report.getSumRow().getCells()[7]).isEqualTo(4);
    assertThat(report.getSumRow().getCells()[8]).isEqualTo(5);
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
    assertThat(sumLineCount).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfClassificationReport() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("L10000").getCells();
    assertThat(row1).isEqualTo(new int[] {7, 2, 1, 0, 0});

    int[] row2 = report.getRow("L20000").getCells();
    assertThat(row2).isEqualTo(new int[] {5, 3, 1, 1, 0});

    int[] row3 = report.getRow("L30000").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 1, 0, 1, 3});

    int[] row4 = report.getRow("L40000").getCells();
    assertThat(row4).isEqualTo(new int[] {2, 2, 2, 0, 4});

    int[] row5 = report.getRow("L50000").getCells();
    assertThat(row5).isEqualTo(new int[] {3, 3, 0, 5, 2});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfClassificationReportNotInWorkingDays() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("L10000").getCells();
    assertThat(row1).isEqualTo(new int[] {9, 0, 1, 0, 0});

    int[] row2 = report.getRow("L20000").getCells();
    assertThat(row2).isEqualTo(new int[] {8, 0, 1, 0, 1});

    int[] row3 = report.getRow("L30000").getCells();
    assertThat(row3).isEqualTo(new int[] {3, 0, 0, 0, 4});

    int[] row4 = report.getRow("L40000").getCells();
    assertThat(row4).isEqualTo(new int[] {4, 0, 2, 0, 4});

    int[] row5 = report.getRow("L50000").getCells();
    assertThat(row5).isEqualTo(new int[] {6, 0, 0, 0, 7});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfClassificationReportWithWorkbasketFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .workbasketIdIn(workbasketIds)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("L10000").getCells();
    assertThat(row1).isEqualTo(new int[] {6, 0, 0, 0, 0});

    int[] row2 = report.getRow("L20000").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 0, 0, 0, 0});

    int[] row3 = report.getRow("L30000").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 1, 0, 1, 1});

    int[] row4 = report.getRow("L40000").getCells();
    assertThat(row4).isEqualTo(new int[] {1, 0, 1, 0, 1});

    int[] row5 = report.getRow("L50000").getCells();
    assertThat(row5).isEqualTo(new int[] {2, 2, 0, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfClassificationReportWithStateFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TaskState> states = Collections.singletonList(TaskState.READY);
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .stateIn(states)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("L10000").getCells();
    assertThat(row1).isEqualTo(new int[] {7, 2, 1, 0, 0});

    int[] row2 = report.getRow("L20000").getCells();
    assertThat(row2).isEqualTo(new int[] {5, 3, 1, 1, 0});

    int[] row3 = report.getRow("L30000").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 1, 0, 1, 0});

    int[] row4 = report.getRow("L40000").getCells();
    assertThat(row4).isEqualTo(new int[] {2, 2, 2, 0, 0});

    int[] row5 = report.getRow("L50000").getCells();
    assertThat(row5).isEqualTo(new int[] {3, 3, 0, 5, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfClassificationReportWithCategoryFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .categoryIn(categories)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(2);

    int[] row1 = report.getRow("L30000").getCells();
    assertThat(row1).isEqualTo(new int[] {2, 1, 0, 1, 3});

    int[] row2 = report.getRow("L40000").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 2, 2, 0, 4});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfClassificationReportWithDomainFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> domains = Collections.singletonList("DOMAIN_A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("L10000").getCells();
    assertThat(row1).isEqualTo(new int[] {5, 2, 1, 0, 0});

    int[] row2 = report.getRow("L20000").getCells();
    assertThat(row2).isEqualTo(new int[] {3, 1, 1, 1, 0});

    int[] row3 = report.getRow("L30000").getCells();
    assertThat(row3).isEqualTo(new int[] {1, 0, 0, 1, 1});

    int[] row4 = report.getRow("L40000").getCells();
    assertThat(row4).isEqualTo(new int[] {2, 0, 0, 0, 3});

    int[] row5 = report.getRow("L50000").getCells();
    assertThat(row5).isEqualTo(new int[] {0, 1, 0, 3, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfClassificationReportWithCustomFieldValueFilter() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("L10000").getCells();
    assertThat(row1).isEqualTo(new int[] {4, 0, 0, 0, 0});

    int[] row2 = report.getRow("L20000").getCells();
    assertThat(row2).isEqualTo(new int[] {4, 1, 1, 1, 0});

    int[] row3 = report.getRow("L30000").getCells();
    assertThat(row3).isEqualTo(new int[] {1, 0, 0, 1, 1});

    int[] row4 = report.getRow("L40000").getCells();
    assertThat(row4).isEqualTo(new int[] {1, 1, 2, 0, 2});

    int[] row5 = report.getRow("L50000").getCells();
    assertThat(row5).isEqualTo(new int[] {1, 2, 0, 2, 0});
  }

  private List<TimeIntervalColumnHeader> getListOfColumnsHeaders() {
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
    List<TimeIntervalColumnHeader> reportLineItemDefinitions = new ArrayList<>();
    reportLineItemDefinitions.add(new TimeIntervalColumnHeader(Integer.MIN_VALUE, -6));
    reportLineItemDefinitions.add(new TimeIntervalColumnHeader(-5, -1));
    reportLineItemDefinitions.add(new TimeIntervalColumnHeader(0));
    reportLineItemDefinitions.add(new TimeIntervalColumnHeader(1, 5));
    reportLineItemDefinitions.add(new TimeIntervalColumnHeader(6, Integer.MAX_VALUE));
    return reportLineItemDefinitions;
  }

  private String reportToString(ClassificationReport report) {
    return reportToString(report, null);
  }

  private String reportToString(
      ClassificationReport report, List<TimeIntervalColumnHeader> columnHeaders) {
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
    builder.append(String.format(formatFirstColumnFirstLine, "Classifications", "Total"));
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
