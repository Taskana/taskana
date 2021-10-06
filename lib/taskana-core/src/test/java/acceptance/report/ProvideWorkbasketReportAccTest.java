package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "workbasket level report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideWorkbasketReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = taskanaEngine.getMonitorService();

  @Test
  void testRoleCheck() {
    assertThatThrownBy(() -> MONITOR_SERVICE.createWorkbasketReportBuilder().buildReport())
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_AugmentDisplayNames_When_ReportIsBuild() throws Exception {
    WorkbasketReport report = MONITOR_SERVICE.createWorkbasketReportBuilder().buildReport();
    assertThat(report.getRows()).hasSize(5);
    assertThat(report.getRow("USER-1-1").getDisplayName()).isEqualTo("PPK User 1 KSC 1");
    assertThat(report.getRow("USER-1-2").getDisplayName()).isEqualTo("PPK User 1 KSC 2");
    assertThat(report.getRow("USER-1-3").getDisplayName()).isEqualTo("PPK User 1 KSC 3");
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_NotThrowSqlExceptionDuringAugmentation_When_ReportContainsNoRows() {
    WorkbasketReport.Builder builder =
        MONITOR_SERVICE.createWorkbasketReportBuilder().domainIn(List.of("DOES_NOT_EXIST"));
    ThrowingCallable test =
        () -> {
          WorkbasketReport report = builder.buildReport();
          assertThat(report).isNotNull();
          assertThat(report.rowSize()).isZero();
        };
    assertThatCode(test).doesNotThrowAnyException();
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfWorkbasketReportBasedOnDueDate() throws Exception {
    WorkbasketReport report = MONITOR_SERVICE.createWorkbasketReportBuilder().buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    assertThat(report.getRow("USER-1-1").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER-1-2").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER-1-3").getTotalValue()).isEqualTo(10);

    assertThat(report.getSumRow().getTotalValue()).isEqualTo(54);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_FilterTasksAccordingToClassificationId_When_ClassificationIdFilterIsApplied()
      throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .classificationIdIn(List.of("CLI:000000000000000000000000000000000001"))
            .buildReport();
    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(2);
    assertThat(report.getRow("USER-1-1").getCells())
        .isEqualTo(new int[] {6, 0, 0, 0, 0, 0, 0, 0, 0});
    assertThat(report.getRow("USER-1-2").getCells())
        .isEqualTo(new int[] {1, 2, 0, 0, 1, 0, 0, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetWorkbasketReportWithReportLineItemDefinitions() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    assertThat(report.getRow("USER-1-1").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER-1-2").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER-1-3").getTotalValue()).isEqualTo(10);

    int[] sumRow = report.getSumRow().getCells();
    assertThat(sumRow).isEqualTo(new int[] {10, 9, 11, 0, 4, 0, 8, 7, 5});
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(54);
  }

  @WithAccessId(user = "monitor")
  @TestFactory
  Stream<DynamicTest> should_NotThrowError_When_BuildReportForTaskState() {
    Iterator<TaskTimestamp> iterator = Arrays.stream(TaskTimestamp.values()).iterator();
    ThrowingConsumer<TaskTimestamp> test =
        timestamp -> {
          ThrowingCallable callable =
              () -> MONITOR_SERVICE.createWorkbasketReportBuilder().buildReport(timestamp);
          assertThatCode(callable).doesNotThrowAnyException();
        };
    return DynamicTest.stream(iterator, t -> "for TaskState " + t, test);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReport() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {13, 3, 1, 1, 2});

    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {4, 6, 3, 6, 1});

    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 2, 0, 0, 6});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ComputeNumbersAccordingToPlannedDate_When_BuildReportForPlanned() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .buildReport(TaskTimestamp.PLANNED);

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {0, 2, 18, 0, 0});

    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {0, 1, 19, 0, 0});

    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 0, 10, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportNotInWorkingDays() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {16, 0, 1, 0, 3});

    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {10, 0, 3, 0, 7});

    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {4, 0, 0, 0, 6});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportWithWorkbasketFilter() throws Exception {
    List<String> workbasketIds = List.of("WBI:000000000000000000000000000000000001");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .workbasketIdIn(workbasketIds)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(1);

    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {13, 3, 1, 1, 2});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportWithStateFilter() throws Exception {
    List<TaskState> states = List.of(TaskState.READY);
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .stateIn(states)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(4);

    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {13, 3, 1, 1, 0});

    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {4, 6, 3, 6, 0});

    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 2, 0, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportWithCategoryFilter() throws Exception {
    List<String> categories = List.of("AUTOMATIC", "MANUAL");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .classificationCategoryIn(categories)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(4);

    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {3, 1, 1, 1, 2});

    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {1, 1, 1, 0, 1});

    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 1, 0, 0, 4});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportWithDomainFilter() throws Exception {
    List<String> domains = List.of("DOMAIN_A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(4);

    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {8, 1, 0, 1, 2});

    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {2, 2, 2, 4, 0});

    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {1, 1, 0, 0, 2});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithCustomAttributeIn() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeIn(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A")
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);

    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {6, 1, 1, 1, 1});

    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {3, 2, 2, 3, 1});

    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {2, 1, 0, 0, 1});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithCustomAttributeNotIn()
      throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeNotIn(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A")
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {7, 2, 0, 0, 1});

    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {1, 4, 1, 3, 0});

    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 1, 0, 0, 5});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithCustomAttributeLike()
      throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .customAttributeLike(TaskCustomField.CUSTOM_1, "%ftsstelle A")
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(3);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfWorkbasketReportForSelectedClassifications() throws Exception {
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
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .combinedClassificationFilterIn(combinedClassificationFilter)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {3, 3, 0, 1, 1});

    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {0, 2, 1, 6, 0});

    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {1, 0, 0, 0, 3});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfWorkbasketReportBasedOnPlannedDateWithReportLineItemDefinitions()
      throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    WorkbasketReport report =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildReport(TaskTimestamp.PLANNED);

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    assertThat(report.getRow("USER-1-1").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER-1-2").getTotalValue()).isEqualTo(20);
    assertThat(report.getRow("USER-1-3").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("USER-1-1").getCells()[2]).isEqualTo(2);
    assertThat(report.getRow("USER-1-2").getCells()[1]).isEqualTo(1);

    assertThat(report.getSumRow().getTotalValue()).isEqualTo(54);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnCustomFieldValues_When_CombinedClassificationFilterIsApplied()
      throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<CombinedClassificationFilter> combinedClassificationFilters =
        List.of(
            new CombinedClassificationFilter(
                "CLI:000000000000000000000000000000000001",
                "CLI:000000000000000000000000000000000006"));

    List<String> customValues =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .combinedClassificationFilterIn(combinedClassificationFilters)
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_1);

    assertThat(customValues)
        .containsExactlyInAnyOrder(
            "Geschaeftsstelle A", "Geschaeftsstelle B", "Geschaeftsstelle C");
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
