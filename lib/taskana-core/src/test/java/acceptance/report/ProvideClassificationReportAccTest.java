package acceptance.report;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.report.api.ClassificationReport;
import pro.taskana.report.api.TaskMonitorService;
import pro.taskana.report.internal.header.TimeIntervalColumnHeader;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.CustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideClassificationReportAccTest extends AbstractReportAccTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProvideClassificationReportAccTest.class);

  @Test
  void testRoleCheck() throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> taskMonitorService.createClassificationReportBuilder().buildReport());
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfClassificationReport()
      throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    ClassificationReport report =
        taskMonitorService.createClassificationReportBuilder().buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }

    assertNotNull(report);
    assertEquals(5, report.rowSize());

    assertEquals(10, report.getRow("L10000").getTotalValue());
    assertEquals(10, report.getRow("L20000").getTotalValue());
    assertEquals(7, report.getRow("L30000").getTotalValue());
    assertEquals(10, report.getRow("L40000").getTotalValue());
    assertEquals(13, report.getRow("L50000").getTotalValue());
    assertEquals(0, report.getRow("L10000").getCells().length);
    assertEquals(0, report.getRow("L20000").getCells().length);
    assertEquals(0, report.getRow("L30000").getCells().length);
    assertEquals(0, report.getRow("L40000").getCells().length);
    assertEquals(0, report.getRow("L50000").getCells().length);
    assertEquals(50, report.getSumRow().getTotalValue());
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetClassificationReportWithReportLineItemDefinitions()
      throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnsHeaders();

    ClassificationReport report =
        taskMonitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    final int sumLineCount = IntStream.of(report.getSumRow().getCells()).sum();

    assertNotNull(report);
    assertEquals(5, report.rowSize());

    assertEquals(10, report.getRow("L10000").getTotalValue());
    assertEquals(10, report.getRow("L20000").getTotalValue());
    assertEquals(7, report.getRow("L30000").getTotalValue());
    assertEquals(10, report.getRow("L40000").getTotalValue());
    assertEquals(13, report.getRow("L50000").getTotalValue());

    assertEquals(10, report.getSumRow().getCells()[0]);
    assertEquals(9, report.getSumRow().getCells()[1]);
    assertEquals(11, report.getSumRow().getCells()[2]);
    assertEquals(0, report.getSumRow().getCells()[3]);
    assertEquals(4, report.getSumRow().getCells()[4]);
    assertEquals(0, report.getSumRow().getCells()[5]);
    assertEquals(7, report.getSumRow().getCells()[6]);
    assertEquals(4, report.getSumRow().getCells()[7]);
    assertEquals(5, report.getSumRow().getCells()[8]);
    assertEquals(50, report.getSumRow().getTotalValue());
    assertEquals(50, sumLineCount);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testEachItemOfClassificationReport()
      throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        taskMonitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertNotNull(report);
    assertEquals(5, report.rowSize());

    int[] row1 = report.getRow("L10000").getCells();
    assertArrayEquals(new int[] {7, 2, 1, 0, 0}, row1);

    int[] row2 = report.getRow("L20000").getCells();
    assertArrayEquals(new int[] {5, 3, 1, 1, 0}, row2);

    int[] row3 = report.getRow("L30000").getCells();
    assertArrayEquals(new int[] {2, 1, 0, 1, 3}, row3);

    int[] row4 = report.getRow("L40000").getCells();
    assertArrayEquals(new int[] {2, 2, 2, 0, 4}, row4);

    int[] row5 = report.getRow("L50000").getCells();
    assertArrayEquals(new int[] {3, 3, 0, 5, 2}, row5);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testEachItemOfClassificationReportNotInWorkingDays()
      throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        taskMonitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertNotNull(report);
    assertEquals(5, report.rowSize());

    int[] row1 = report.getRow("L10000").getCells();
    assertArrayEquals(new int[] {9, 0, 1, 0, 0}, row1);

    int[] row2 = report.getRow("L20000").getCells();
    assertArrayEquals(new int[] {8, 0, 1, 0, 1}, row2);

    int[] row3 = report.getRow("L30000").getCells();
    assertArrayEquals(new int[] {3, 0, 0, 0, 4}, row3);

    int[] row4 = report.getRow("L40000").getCells();
    assertArrayEquals(new int[] {4, 0, 2, 0, 4}, row4);

    int[] row5 = report.getRow("L50000").getCells();
    assertArrayEquals(new int[] {6, 0, 0, 0, 7}, row5);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testEachItemOfClassificationReportWithWorkbasketFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        taskMonitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .workbasketIdIn(workbasketIds)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertNotNull(report);
    assertEquals(5, report.rowSize());

    int[] row1 = report.getRow("L10000").getCells();
    assertArrayEquals(new int[] {6, 0, 0, 0, 0}, row1);

    int[] row2 = report.getRow("L20000").getCells();
    assertArrayEquals(new int[] {2, 0, 0, 0, 0}, row2);

    int[] row3 = report.getRow("L30000").getCells();
    assertArrayEquals(new int[] {2, 1, 0, 1, 1}, row3);

    int[] row4 = report.getRow("L40000").getCells();
    assertArrayEquals(new int[] {1, 0, 1, 0, 1}, row4);

    int[] row5 = report.getRow("L50000").getCells();
    assertArrayEquals(new int[] {2, 2, 0, 0, 0}, row5);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testEachItemOfClassificationReportWithStateFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<TaskState> states = Collections.singletonList(TaskState.READY);
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        taskMonitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .stateIn(states)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertNotNull(report);
    assertEquals(5, report.rowSize());

    int[] row1 = report.getRow("L10000").getCells();
    assertArrayEquals(new int[] {7, 2, 1, 0, 0}, row1);

    int[] row2 = report.getRow("L20000").getCells();
    assertArrayEquals(new int[] {5, 3, 1, 1, 0}, row2);

    int[] row3 = report.getRow("L30000").getCells();
    assertArrayEquals(new int[] {2, 1, 0, 1, 0}, row3);

    int[] row4 = report.getRow("L40000").getCells();
    assertArrayEquals(new int[] {2, 2, 2, 0, 0}, row4);

    int[] row5 = report.getRow("L50000").getCells();
    assertArrayEquals(new int[] {3, 3, 0, 5, 0}, row5);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testEachItemOfClassificationReportWithCategoryFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        taskMonitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .categoryIn(categories)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertNotNull(report);
    assertEquals(2, report.rowSize());

    int[] row1 = report.getRow("L30000").getCells();
    assertArrayEquals(new int[] {2, 1, 0, 1, 3}, row1);

    int[] row2 = report.getRow("L40000").getCells();
    assertArrayEquals(new int[] {2, 2, 2, 0, 4}, row2);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testEachItemOfClassificationReportWithDomainFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<String> domains = Collections.singletonList("DOMAIN_A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        taskMonitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertNotNull(report);
    assertEquals(5, report.rowSize());

    int[] row1 = report.getRow("L10000").getCells();
    assertArrayEquals(new int[] {5, 2, 1, 0, 0}, row1);

    int[] row2 = report.getRow("L20000").getCells();
    assertArrayEquals(new int[] {3, 1, 1, 1, 0}, row2);

    int[] row3 = report.getRow("L30000").getCells();
    assertArrayEquals(new int[] {1, 0, 0, 1, 1}, row3);

    int[] row4 = report.getRow("L40000").getCells();
    assertArrayEquals(new int[] {2, 0, 0, 0, 3}, row4);

    int[] row5 = report.getRow("L50000").getCells();
    assertArrayEquals(new int[] {0, 1, 0, 3, 0}, row5);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testEachItemOfClassificationReportWithCustomFieldValueFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationReport report =
        taskMonitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .buildReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

    assertNotNull(report);
    assertEquals(5, report.rowSize());

    int[] row1 = report.getRow("L10000").getCells();
    assertArrayEquals(new int[] {4, 0, 0, 0, 0}, row1);

    int[] row2 = report.getRow("L20000").getCells();
    assertArrayEquals(new int[] {4, 1, 1, 1, 0}, row2);

    int[] row3 = report.getRow("L30000").getCells();
    assertArrayEquals(new int[] {1, 0, 0, 1, 1}, row3);

    int[] row4 = report.getRow("L40000").getCells();
    assertArrayEquals(new int[] {1, 1, 2, 0, 2}, row4);

    int[] row5 = report.getRow("L50000").getCells();
    assertArrayEquals(new int[] {1, 2, 0, 2, 0}, row5);
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
