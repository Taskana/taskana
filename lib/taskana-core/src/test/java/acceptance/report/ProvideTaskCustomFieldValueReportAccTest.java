package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.TaskCustomFieldValueReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideTaskCustomFieldValueReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = taskanaEngine.getMonitorService();

  @Test
  void testRoleCheck() {
    ThrowingCallable call =
        () -> {
          MONITOR_SERVICE
              .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
              .buildReport();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_AugmentDisplayNames_When_ReportIsBuild() throws Exception {
    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .buildReport();

    assertThat(report.getRows()).hasSize(3);
    assertThat(report.getRow("Geschaeftsstelle A").getDisplayName())
        .isEqualTo("Geschaeftsstelle A");
    assertThat(report.getRow("Geschaeftsstelle B").getDisplayName())
        .isEqualTo("Geschaeftsstelle B");
    assertThat(report.getRow("Geschaeftsstelle C").getDisplayName())
        .isEqualTo("Geschaeftsstelle C");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfCustomFieldValueReportForCustom1() throws Exception {
    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    assertThat(report.getRow("Geschaeftsstelle A").getTotalValue()).isEqualTo(25);
    assertThat(report.getRow("Geschaeftsstelle B").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("Geschaeftsstelle C").getTotalValue()).isEqualTo(15);
    assertThat(report.getRow("Geschaeftsstelle A").getCells()).isEmpty();
    assertThat(report.getRow("Geschaeftsstelle B").getCells()).isEmpty();
    assertThat(report.getRow("Geschaeftsstelle C").getCells()).isEmpty();

    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfCustomFieldValueReportForCustom2() throws Exception {
    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_2)
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(2);

    assertThat(report.getRow("Vollkasko").getTotalValue()).isEqualTo(21);
    assertThat(report.getRow("Teilkasko").getTotalValue()).isEqualTo(29);

    assertThat(report.getRow("Vollkasko").getCells()).isEmpty();
    assertThat(report.getRow("Teilkasko").getCells()).isEmpty();

    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomFieldValueReportWithReportLineItemDefinitions() throws Exception {
    TaskCustomField taskCustomField = TaskCustomField.CUSTOM_1;
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(taskCustomField)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    assertThat(report.getRow("Geschaeftsstelle A").getTotalValue()).isEqualTo(25);
    assertThat(report.getRow("Geschaeftsstelle B").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("Geschaeftsstelle C").getTotalValue()).isEqualTo(15);

    assertThat(report.getSumRow().getCells()).isEqualTo(new int[] {10, 9, 11, 0, 4, 0, 7, 4, 5});

    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @TestFactory
  Stream<DynamicTest> should_NotThrowError_When_BuildReportForTaskState() {
    Iterator<TaskTimestamp> iterator = Arrays.stream(TaskTimestamp.values()).iterator();
    ThrowingConsumer<TaskTimestamp> test =
        timestamp -> {
          ThrowingCallable callable =
              () ->
                  MONITOR_SERVICE
                      .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
                      .buildReport(timestamp);
          assertThatCode(callable).doesNotThrowAnyException();
        };
    return DynamicTest.stream(iterator, t -> "for TaskState " + t, test);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCustomFieldValueReport() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("Geschaeftsstelle A").getCells();
    assertThat(row1).isEqualTo(new int[] {11, 4, 3, 4, 3});

    int[] row2 = report.getRow("Geschaeftsstelle B").getCells();
    assertThat(row2).isEqualTo(new int[] {5, 3, 0, 2, 0});

    int[] row3 = report.getRow("Geschaeftsstelle C").getCells();
    assertThat(row3).isEqualTo(new int[] {3, 4, 1, 1, 6});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ComputeNumbersAccordingToPlannedDate_When_BuildReportForPlanned() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport(TaskTimestamp.PLANNED);

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("Geschaeftsstelle A").getCells();
    assertThat(row1).isEqualTo(new int[] {0, 1, 24, 0, 0});

    int[] row2 = report.getRow("Geschaeftsstelle B").getCells();
    assertThat(row2).isEqualTo(new int[] {0, 1, 9, 0, 0});

    int[] row3 = report.getRow("Geschaeftsstelle C").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 1, 14, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCustomFieldValueReportNotInWorkingDays() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("Geschaeftsstelle A").getCells();
    assertThat(row1).isEqualTo(new int[] {15, 0, 3, 0, 7});

    int[] row2 = report.getRow("Geschaeftsstelle B").getCells();
    assertThat(row2).isEqualTo(new int[] {8, 0, 0, 0, 2});

    int[] row3 = report.getRow("Geschaeftsstelle C").getCells();
    assertThat(row3).isEqualTo(new int[] {7, 0, 1, 0, 7});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCustomFieldValueReportWithWorkbasketFilter() throws Exception {
    List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .workbasketIdIn(workbasketIds)
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("Geschaeftsstelle A").getCells();
    assertThat(row1).isEqualTo(new int[] {6, 1, 1, 1, 1});

    int[] row2 = report.getRow("Geschaeftsstelle B").getCells();
    assertThat(row2).isEqualTo(new int[] {4, 1, 0, 0, 0});

    int[] row3 = report.getRow("Geschaeftsstelle C").getCells();
    assertThat(row3).isEqualTo(new int[] {3, 1, 0, 0, 1});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCustomFieldValueReportWithStateFilter() throws Exception {
    List<TaskState> states = Collections.singletonList(TaskState.READY);
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .stateIn(states)
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("Geschaeftsstelle A").getCells();
    assertThat(row1).isEqualTo(new int[] {11, 4, 3, 4, 0});

    int[] row2 = report.getRow("Geschaeftsstelle B").getCells();
    assertThat(row2).isEqualTo(new int[] {5, 3, 0, 2, 0});

    int[] row3 = report.getRow("Geschaeftsstelle C").getCells();
    assertThat(row3).isEqualTo(new int[] {3, 4, 1, 1, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCustomFieldValueReportWithCategoryFilter() throws Exception {
    List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .classificationCategoryIn(categories)
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("Geschaeftsstelle A").getCells();
    assertThat(row1).isEqualTo(new int[] {2, 1, 2, 1, 3});

    int[] row2 = report.getRow("Geschaeftsstelle B").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 0, 0, 0, 0});

    int[] row3 = report.getRow("Geschaeftsstelle C").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 2, 0, 0, 4});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCustomFieldValueReportWithDomainFilter() throws Exception {
    List<String> domains = Collections.singletonList("DOMAIN_A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .domainIn(domains)
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("Geschaeftsstelle A").getCells();
    assertThat(row1).isEqualTo(new int[] {8, 1, 1, 4, 1});

    int[] row2 = report.getRow("Geschaeftsstelle B").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 2, 0, 1, 0});

    int[] row3 = report.getRow("Geschaeftsstelle C").getCells();
    assertThat(row3).isEqualTo(new int[] {1, 1, 1, 0, 3});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCustomFieldValueReportWithCustomFieldValueFilter() throws Exception {
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    TaskCustomFieldValueReport report =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(1);

    int[] row1 = report.getRow("Geschaeftsstelle A").getCells();
    assertThat(row1).isEqualTo(new int[] {11, 4, 3, 4, 3});
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
}
