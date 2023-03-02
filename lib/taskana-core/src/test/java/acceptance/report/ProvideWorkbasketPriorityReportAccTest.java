package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.api.IntInterval;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.WorkbasketPriorityReport;
import pro.taskana.monitor.api.reports.header.PriorityColumnHeader;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.WorkbasketType;

@ExtendWith(JaasExtension.class)
class ProvideWorkbasketPriorityReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = taskanaEngine.getMonitorService();

  private static final List<PriorityColumnHeader> LOW_TEST_HEADERS =
      Arrays.asList(
          new PriorityColumnHeader(Integer.MIN_VALUE, 1),
          new PriorityColumnHeader(2, Integer.MAX_VALUE));

  private static final List<PriorityColumnHeader> DEFAULT_TEST_HEADERS =
      Arrays.asList(
          new PriorityColumnHeader(Integer.MIN_VALUE, 249),
          new PriorityColumnHeader(250, 500),
          new PriorityColumnHeader(501, Integer.MAX_VALUE));

  @WithAccessId(user = "admin")
  @WithAccessId(user = "monitor")
  @TestTemplate
  void should_NotThrowExceptions_When_UserIsAdminOrMonitor() {
    assertThatCode(() -> MONITOR_SERVICE.createWorkbasketPriorityReportBuilder().buildReport())
        .doesNotThrowAnyException();
  }

  @WithAccessId(user = "user-1-1")
  @WithAccessId(user = "businessadmin")
  @TestTemplate
  void should_ThrowMismatchedRoleException_When_UserDoesNotHaveCorrectRole() {
    assertThatThrownBy(() -> MONITOR_SERVICE.createWorkbasketPriorityReportBuilder().buildReport())
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_BuildReport_When_UserIsAuthorized() throws Exception {
    WorkbasketPriorityReport priorityReport =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(LOW_TEST_HEADERS)
            .buildReport();
    int[] expectedCells = {47, 9};
    assertThat(priorityReport)
        .extracting(Report::getSumRow)
        .extracting(Row::getCells)
        .isEqualTo(expectedCells);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_OnlyIncludeWantedWorkbasketTypesReport_When_UsingWorkbasketTypeIn() throws Exception {
    WorkbasketPriorityReport priorityReport =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .workbasketTypeIn(WorkbasketType.GROUP, WorkbasketType.TOPIC)
            .buildReport();
    int[] expectedCells = {0, 2, 2};
    assertThat(priorityReport)
        .extracting(Report::getSumRow)
        .extracting(Row::getCells)
        .isEqualTo(expectedCells);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithCustomAttributeIn() throws Exception {
    WorkbasketPriorityReport report =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .customAttributeIn(TaskCustomField.CUSTOM_1, "Geschaeftsstelle B")
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(11);

    assertThat(report.rowSize()).isEqualTo(3);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {5, 0, 0});
    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {5, 0, 0});
    int[] row3 = report.getRow("GPK-1").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 1, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithCustomAttributeNotIn()
      throws Exception {
    WorkbasketPriorityReport report =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .customAttributeNotIn(
                TaskCustomField.CUSTOM_1, "Geschaeftsstelle A", "Geschaeftsstelle C")
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(11);

    assertThat(report.rowSize()).isEqualTo(3);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {5, 0, 0});
    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {5, 0, 0});
    int[] row3 = report.getRow("GPK-1").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 1, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithCustomAttributeLike()
      throws Exception {
    WorkbasketPriorityReport report =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .customAttributeLike(TaskCustomField.CUSTOM_1, "%ftsstelle B")
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.getSumRow().getTotalValue()).isEqualTo(11);

    assertThat(report.rowSize()).isEqualTo(3);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {5, 0, 0});
    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {5, 0, 0});
    int[] row3 = report.getRow("GPK-1").getCells();
    assertThat(row3).isEqualTo(new int[] {0, 1, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithStateIn() throws Exception {
    List<TaskState> states = List.of(TaskState.READY);
    WorkbasketPriorityReport report =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .stateIn(states)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(4);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {18, 0, 0});
    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {19, 0, 0});
    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {4, 0, 0});
    int[] row4 = report.getRow("GPK-1").getCells();
    assertThat(row4).isEqualTo(new int[] {0, 1, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithDomainIn() throws Exception {
    List<String> domains = List.of("DOMAIN_A");
    WorkbasketPriorityReport report =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .domainIn(domains)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(4);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {12, 0, 0});
    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {10, 0, 0});
    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {4, 0, 0});
    int[] row4 = report.getRow("TPK-VIP-1").getCells();
    assertThat(row4).isEqualTo(new int[] {0, 1, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithClassificationCategoryIn()
      throws Exception {
    List<String> categories = List.of("AUTOMATIC", "MANUAL");
    WorkbasketPriorityReport report =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .classificationCategoryIn(categories)
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(4);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {8, 0, 0});
    int[] row2 = report.getRow("USER-1-2").getCells();
    assertThat(row2).isEqualTo(new int[] {6, 0, 0});
    int[] row3 = report.getRow("USER-1-3").getCells();
    assertThat(row3).isEqualTo(new int[] {5, 0, 0});
    int[] row4 = report.getRow("TPK-VIP-1").getCells();
    assertThat(row4).isEqualTo(new int[] {0, 1, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithAllCustomFieldsIn() throws Exception {
    WorkbasketPriorityReport report =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .customAttributeIn(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A")
            .customAttributeIn(TaskCustomField.CUSTOM_2, "Vollkasko")
            .customAttributeIn(TaskCustomField.CUSTOM_3, "")
            .customAttributeIn(TaskCustomField.CUSTOM_4, "")
            .customAttributeIn(TaskCustomField.CUSTOM_5, "")
            .customAttributeIn(TaskCustomField.CUSTOM_6, "")
            .customAttributeIn(TaskCustomField.CUSTOM_7, "")
            .customAttributeIn(TaskCustomField.CUSTOM_8, "")
            .customAttributeIn(TaskCustomField.CUSTOM_9, "")
            .customAttributeIn(TaskCustomField.CUSTOM_10, "")
            .customAttributeIn(TaskCustomField.CUSTOM_11, "")
            .customAttributeIn(TaskCustomField.CUSTOM_12, "")
            .customAttributeIn(TaskCustomField.CUSTOM_13, "")
            .customAttributeIn(TaskCustomField.CUSTOM_14, "")
            .customAttributeIn(TaskCustomField.CUSTOM_15, "")
            .customAttributeIn(TaskCustomField.CUSTOM_16, "VALUE_01")
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(1);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {1, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithAllCustomFieldsLike()
      throws Exception {
    WorkbasketPriorityReport report =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .customAttributeLike(TaskCustomField.CUSTOM_1, "Geschaeftsstelle %")
            .customAttributeLike(TaskCustomField.CUSTOM_2, "Vollkask%")
            .customAttributeLike(TaskCustomField.CUSTOM_3, "")
            .customAttributeLike(TaskCustomField.CUSTOM_4, "")
            .customAttributeLike(TaskCustomField.CUSTOM_5, "")
            .customAttributeLike(TaskCustomField.CUSTOM_6, "")
            .customAttributeLike(TaskCustomField.CUSTOM_7, "")
            .customAttributeLike(TaskCustomField.CUSTOM_8, "")
            .customAttributeLike(TaskCustomField.CUSTOM_9, "")
            .customAttributeLike(TaskCustomField.CUSTOM_10, "")
            .customAttributeLike(TaskCustomField.CUSTOM_11, "")
            .customAttributeLike(TaskCustomField.CUSTOM_12, "")
            .customAttributeLike(TaskCustomField.CUSTOM_13, "")
            .customAttributeLike(TaskCustomField.CUSTOM_14, "")
            .customAttributeLike(TaskCustomField.CUSTOM_15, "")
            .customAttributeLike(TaskCustomField.CUSTOM_16, "%ALUE_01")
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(1);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {1, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnItemsOfWorkbasketReport_When_FilteringWithAllCustomFieldsNotIn()
      throws Exception {
    WorkbasketPriorityReport report =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(DEFAULT_TEST_HEADERS)
            .customAttributeNotIn(
                TaskCustomField.CUSTOM_1, "Geschaeftsstelle A", "Geschaeftsstelle C")
            .customAttributeNotIn(TaskCustomField.CUSTOM_2, "Teilkasko")
            .customAttributeNotIn(TaskCustomField.CUSTOM_3, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_4, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_5, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_6, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_7, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_8, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_9, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_10, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_11, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_12, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_13, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_14, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_15, "INVALID")
            .customAttributeNotIn(TaskCustomField.CUSTOM_16, "VALUE_24", "VALUE_25")
            .inWorkingDays()
            .buildReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(1);
    int[] row1 = report.getRow("USER-1-1").getCells();
    assertThat(row1).isEqualTo(new int[] {1, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @TestFactory
  Stream<DynamicTest> should_ApplyFilter_When_QueryingForCustomIntIn() {
    List<Pair<TaskCustomIntField, Integer>> testCases =
        List.of(
            Pair.of(TaskCustomIntField.CUSTOM_INT_1, 1),
            Pair.of(TaskCustomIntField.CUSTOM_INT_2, 2),
            Pair.of(TaskCustomIntField.CUSTOM_INT_3, 3),
            Pair.of(TaskCustomIntField.CUSTOM_INT_4, 4),
            Pair.of(TaskCustomIntField.CUSTOM_INT_5, 5),
            Pair.of(TaskCustomIntField.CUSTOM_INT_6, 6),
            Pair.of(TaskCustomIntField.CUSTOM_INT_7, 7),
            Pair.of(TaskCustomIntField.CUSTOM_INT_8, 8));

    ThrowingConsumer<Pair<TaskCustomIntField, Integer>> test =
        p -> {
          WorkbasketPriorityReport report =
              MONITOR_SERVICE
                  .createWorkbasketPriorityReportBuilder()
                  .withColumnHeaders(DEFAULT_TEST_HEADERS)
                  .customIntAttributeIn(p.getLeft(), p.getRight())
                  .inWorkingDays()
                  .buildReport();
          assertThat(report).isNotNull();
          assertThat(report.rowSize()).isEqualTo(5);
          assertThat(report.getRow("USER-1-1").getCells()).isEqualTo(new int[] {20, 0, 0});
        };

    return DynamicTest.stream(testCases.iterator(), p -> p.getLeft().name(), test);
  }

  @WithAccessId(user = "monitor")
  @TestFactory
  Stream<DynamicTest> should_ApplyFilter_When_QueryingForCustomIntNotIn() {
    List<Pair<TaskCustomIntField, Integer>> testCases =
        List.of(
            Pair.of(TaskCustomIntField.CUSTOM_INT_1, 1),
            Pair.of(TaskCustomIntField.CUSTOM_INT_2, 2),
            Pair.of(TaskCustomIntField.CUSTOM_INT_3, 3),
            Pair.of(TaskCustomIntField.CUSTOM_INT_4, 4),
            Pair.of(TaskCustomIntField.CUSTOM_INT_5, 5),
            Pair.of(TaskCustomIntField.CUSTOM_INT_6, 6),
            Pair.of(TaskCustomIntField.CUSTOM_INT_7, 7),
            Pair.of(TaskCustomIntField.CUSTOM_INT_8, 8));

    ThrowingConsumer<Pair<TaskCustomIntField, Integer>> test =
        p -> {
          WorkbasketPriorityReport report =
              MONITOR_SERVICE
                  .createWorkbasketPriorityReportBuilder()
                  .withColumnHeaders(DEFAULT_TEST_HEADERS)
                  .customIntAttributeNotIn(p.getLeft(), p.getRight())
                  .inWorkingDays()
                  .buildReport();
          assertThat(report).isNotNull();
          assertThat(report.rowSize()).isZero();
        };

    return DynamicTest.stream(testCases.iterator(), p -> p.getLeft().name(), test);
  }

  @WithAccessId(user = "monitor")
  @TestFactory
  Stream<DynamicTest> should_ApplyFilter_When_QueryingForCustomIntWithin() {
    List<Pair<TaskCustomIntField, IntInterval>> testCases =
        List.of(
            Pair.of(TaskCustomIntField.CUSTOM_INT_1, new IntInterval(1, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_2, new IntInterval(2, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_3, new IntInterval(3, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_4, new IntInterval(4, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_5, new IntInterval(5, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_6, new IntInterval(6, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_7, new IntInterval(7, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_8, new IntInterval(8, null)));

    ThrowingConsumer<Pair<TaskCustomIntField, IntInterval>> test =
        p -> {
          WorkbasketPriorityReport report =
              MONITOR_SERVICE
                  .createWorkbasketPriorityReportBuilder()
                  .withColumnHeaders(DEFAULT_TEST_HEADERS)
                  .customIntAttributeWithin(p.getLeft(), p.getRight())
                  .inWorkingDays()
                  .buildReport();
          assertThat(report).isNotNull();
          assertThat(report.rowSize()).isEqualTo(5);
          assertThat(report.getRow("USER-1-1").getCells()).isEqualTo(new int[] {20, 0, 0});
        };

    return DynamicTest.stream(testCases.iterator(), p -> p.getLeft().name(), test);
  }

  @WithAccessId(user = "monitor")
  @TestFactory
  Stream<DynamicTest> should_ApplyFilter_When_QueryingForCustomIntNotWithin() {
    List<Pair<TaskCustomIntField, IntInterval>> testCases =
        List.of(
            Pair.of(TaskCustomIntField.CUSTOM_INT_1, new IntInterval(3, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_2, new IntInterval(4, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_3, new IntInterval(5, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_4, new IntInterval(6, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_5, new IntInterval(7, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_6, new IntInterval(8, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_7, new IntInterval(9, null)),
            Pair.of(TaskCustomIntField.CUSTOM_INT_8, new IntInterval(10, null)));

    ThrowingConsumer<Pair<TaskCustomIntField, IntInterval>> test =
        p -> {
          WorkbasketPriorityReport report =
              MONITOR_SERVICE
                  .createWorkbasketPriorityReportBuilder()
                  .withColumnHeaders(DEFAULT_TEST_HEADERS)
                  .customIntAttributeNotWithin(p.getLeft(), p.getRight())
                  .inWorkingDays()
                  .buildReport();
          assertThat(report).isNotNull();
          assertThat(report.rowSize()).isEqualTo(5);
          assertThat(report.getRow("USER-1-1").getCells()).isEqualTo(new int[] {20, 0, 0});
        };

    return DynamicTest.stream(testCases.iterator(), p -> p.getLeft().name(), test);
  }

  @WithAccessId(user = "monitor")
  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_FilteringByCustomIntWithinWithInvalidIntervals() {
    List<Pair<TaskCustomIntField, IntInterval[]>> testCases =
        List.of(
            Pair.of(TaskCustomIntField.CUSTOM_INT_1, new IntInterval[] {new IntInterval(4, 1)}),
            // Only first interval invalid
            Pair.of(
                TaskCustomIntField.CUSTOM_INT_2,
                new IntInterval[] {new IntInterval(null, null), new IntInterval(0, null)}),
            // Only second interval invalid
            Pair.of(
                TaskCustomIntField.CUSTOM_INT_3,
                new IntInterval[] {new IntInterval(-1, 5), new IntInterval(null, null)}),
            // Both intervals invalid
            Pair.of(
                TaskCustomIntField.CUSTOM_INT_4,
                new IntInterval[] {new IntInterval(0, -5), new IntInterval(-2, -10)}),
            // One interval invalid
            Pair.of(
                TaskCustomIntField.CUSTOM_INT_5, new IntInterval[] {new IntInterval(null, null)}),
            Pair.of(TaskCustomIntField.CUSTOM_INT_6, new IntInterval[] {new IntInterval(0, -5)}),
            Pair.of(
                TaskCustomIntField.CUSTOM_INT_7,
                new IntInterval[] {new IntInterval(null, null), new IntInterval(null, null)}),
            Pair.of(
                TaskCustomIntField.CUSTOM_INT_8, new IntInterval[] {new IntInterval(123, 122)}));

    ThrowingConsumer<Pair<TaskCustomIntField, IntInterval[]>> test =
        p -> {
          ThrowingCallable result =
              () ->
                  MONITOR_SERVICE
                      .createWorkbasketPriorityReportBuilder()
                      .withColumnHeaders(DEFAULT_TEST_HEADERS)
                      .customIntAttributeWithin(p.getLeft(), p.getRight())
                      .inWorkingDays()
                      .buildReport();
          assertThatThrownBy(result)
              .isInstanceOf(IllegalArgumentException.class)
              .hasMessageContaining("IntInterval");
        };

    return DynamicTest.stream(testCases.iterator(), p -> p.getLeft().name(), test);
  }
}
