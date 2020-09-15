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
import pro.taskana.monitor.api.reports.ClassificationCategoryReport;
import pro.taskana.monitor.api.reports.ClassificationCategoryReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "category report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideClassificationCategoryReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = taskanaEngine.getMonitorService();

  @Test
  void testRoleCheck() {
    ThrowingCallable call =
        () -> MONITOR_SERVICE.createClassificationCategoryReportBuilder().buildReport();
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_augmentDisplayNames_When_ReportIsBuild() throws Exception {
    ClassificationCategoryReport report =
        MONITOR_SERVICE.createClassificationCategoryReportBuilder().buildReport();

    assertThat(report.getRows()).hasSize(3);
    assertThat(report.getRow("AUTOMATIC").getDisplayName()).isEqualTo("AUTOMATIC");
    assertThat(report.getRow("EXTERN").getDisplayName()).isEqualTo("EXTERN");
    assertThat(report.getRow("MANUAL").getDisplayName()).isEqualTo("MANUAL");
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_NotThrowSqlExceptionDuringAugmentation_When_ReportContainsNoRows() {
    Builder builder =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .classificationIdIn(Collections.singletonList("DOES NOT EXIST"));
    ThrowingCallable test =
        () -> {
          ClassificationCategoryReport report = builder.buildReport();
          assertThat(report).isNotNull();
          assertThat(report.rowSize()).isZero();
        };
    assertThatCode(test).doesNotThrowAnyException();
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_FilterTasksAccordingToClassificationId_When_ClassificationIdFilterIsApplied()
      throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .classificationIdIn(
                Collections.singletonList("CLI:000000000000000000000000000000000001"))
            .buildReport();
    assertThat(report).isNotNull();

    assertThat(report.rowSize()).isOne();
    assertThat(report.getRow("EXTERN").getCells()).isEqualTo(new int[] {7, 2, 0, 0, 1, 0, 0, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfCategoryReport() throws Exception {
    ClassificationCategoryReport report =
        MONITOR_SERVICE.createClassificationCategoryReportBuilder().buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    assertThat(report.getRow("EXTERN").getTotalValue()).isEqualTo(33);
    assertThat(report.getRow("AUTOMATIC").getTotalValue()).isEqualTo(7);
    assertThat(report.getRow("MANUAL").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("EXTERN").getCells()).isEmpty();
    assertThat(report.getRow("AUTOMATIC").getCells()).isEmpty();
    assertThat(report.getRow("MANUAL").getCells()).isEmpty();
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCategoryReportWithReportLineItemDefinitions() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    assertThat(report.getRow("EXTERN").getTotalValue()).isEqualTo(33);
    assertThat(report.getRow("AUTOMATIC").getTotalValue()).isEqualTo(7);
    assertThat(report.getRow("MANUAL").getTotalValue()).isEqualTo(10);

    int[] sumRow = report.getSumRow().getCells();
    assertThat(sumRow).isEqualTo(new int[] {10, 9, 11, 0, 4, 0, 7, 4, 5});
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCategoryReport() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

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
  @TestFactory
  Stream<DynamicTest> should_NotThrowError_When_buildReportForTaskState() {
    Iterator<TaskTimestamp> iterator = Arrays.stream(TaskTimestamp.values()).iterator();
    ThrowingConsumer<TaskTimestamp> test =
        timestamp -> {
          ThrowingCallable callable =
              () ->
                  MONITOR_SERVICE
                      .createClassificationCategoryReportBuilder()
                      .buildReport(timestamp);
          assertThatCode(callable).doesNotThrowAnyException();
        };
    return DynamicTest.stream(iterator, t -> "for TaskState " + t, test);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_computeNumbersAccordingToPlannedDate_When_BuildReportForPlanned() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport(TaskTimestamp.PLANNED);

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("EXTERN").getCells();
    assertThat(row1).isEqualTo(new int[] {0, 3, 30, 0, 0});

    int[] row2 = report.getRow("AUTOMATIC").getCells();
    assertThat(row2).isEqualTo(new int[] {0, 0, 7, 0, 0});

    int[] row3 = report.getRow("MANUAL").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 0, 10, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfCategoryReportNotInWorkingDays() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildReport();

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
    List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .workbasketIdIn(workbasketIds)
            .buildReport();

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
    List<TaskState> states = Collections.singletonList(TaskState.READY);
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .stateIn(states)
            .inWorkingDays()
            .buildReport();

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
    List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .classificationCategoryIn(categories)
            .inWorkingDays()
            .buildReport();

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
    List<String> domains = Collections.singletonList("DOMAIN_A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .inWorkingDays()
            .buildReport();

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
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    ClassificationCategoryReport report =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .buildReport();

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
}
