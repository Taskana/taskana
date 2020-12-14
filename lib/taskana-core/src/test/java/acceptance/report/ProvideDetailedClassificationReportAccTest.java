package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
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
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.ClassificationReport.Builder;
import pro.taskana.monitor.api.reports.ClassificationReport.DetailedClassificationReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.DetailedMonitorQueryItem;
import pro.taskana.monitor.api.reports.row.DetailedClassificationRow;
import pro.taskana.monitor.api.reports.row.FoldableRow;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "detailed classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideDetailedClassificationReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = taskanaEngine.getMonitorService();

  @Test
  void testRoleCheck() {
    ThrowingCallable call =
        () -> MONITOR_SERVICE.createClassificationReportBuilder().buildDetailedReport();
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_FilterTasksAccordingToClassificationId_When_ClassificationIdFilterIsApplied()
      throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .classificationIdIn(List.of("CLI:000000000000000000000000000000000001"))
            .buildDetailedReport();
    assertThat(report).isNotNull();

    assertThat(report.rowSize()).isOne();
    DetailedClassificationRow row = report.getRow("L10000");
    assertThat(row.getCells()).isEqualTo(new int[] {7, 2, 0, 0, 1, 0, 0, 0, 0});
    assertThat(row.getFoldableRow("L11000").getCells())
        .isEqualTo(new int[] {2, 0, 0, 0, 1, 0, 0, 0, 0});
    assertThat(row.getFoldableRow("N/A").getCells())
        .isEqualTo(new int[] {5, 2, 0, 0, 0, 0, 0, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_AugmentDisplayNames_When_ReportIsBuild() throws Exception {
    DetailedClassificationReport report =
        MONITOR_SERVICE.createClassificationReportBuilder().buildDetailedReport();

    assertThat(report.getRows()).hasSize(5);

    DetailedClassificationRow row = report.getRow("L10000");
    assertThat(row.getDisplayName()).isEqualTo("OLD-Leistungsfall");
    assertThat(row.getFoldableRowCount()).isEqualTo(2);
    assertThat(row.getFoldableRow("L11000").getDisplayName()).isEqualTo("Anhang 1");
    assertThat(row.getFoldableRow("N/A").getDisplayName()).isEqualTo("N/A");

    row = report.getRow("L20000");
    assertThat(row.getDisplayName()).isEqualTo("Beratungsprotokoll");
    assertThat(row.getFoldableRowCount()).isEqualTo(2);
    assertThat(row.getFoldableRow("L22000").getDisplayName()).isEqualTo("Anhang 2");
    assertThat(row.getFoldableRow("N/A").getDisplayName()).isEqualTo("N/A");

    row = report.getRow("L30000");
    assertThat(row.getDisplayName()).isEqualTo("Widerruf");
    assertThat(row.getFoldableRowCount()).isEqualTo(3);
    assertThat(row.getFoldableRow("L33000").getDisplayName()).isEqualTo("Anhang 3");
    assertThat(row.getFoldableRow("L99000").getDisplayName()).isEqualTo("Anhang 9");
    assertThat(row.getFoldableRow("N/A").getDisplayName()).isEqualTo("N/A");

    row = report.getRow("L40000");
    assertThat(row.getDisplayName()).isEqualTo("Dynamikaenderung");
    assertThat(row.getFoldableRowCount()).isOne();
    assertThat(row.getFoldableRow("N/A").getDisplayName()).isEqualTo("N/A");

    row = report.getRow("L50000");
    assertThat(row.getDisplayName()).isEqualTo("Dynamik-Ablehnung");
    assertThat(row.getFoldableRowCount()).isOne();
    assertThat(row.getFoldableRow("N/A").getDisplayName()).isEqualTo("N/A");
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_NotThrowSqlExceptionDuringAugmentation_When_DetailedReportContainsNoRows() {
    Builder builder =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .classificationIdIn(List.of("DOES NOT EXIST"));
    ThrowingCallable test =
        () -> {
          DetailedClassificationReport report = builder.buildDetailedReport();
          assertThat(report).isNotNull();
          assertThat(report.rowSize()).isZero();
        };
    assertThatCode(test).doesNotThrowAnyException();
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfDetailedClassificationReport() throws Exception {
    DetailedClassificationReport report =
        MONITOR_SERVICE.createClassificationReportBuilder().buildDetailedReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    FoldableRow<DetailedMonitorQueryItem> row1 = report.getRow("L10000");
    assertThat(row1.getTotalValue()).isEqualTo(10);
    assertThat(row1.getFoldableRow("L11000").getTotalValue()).isEqualTo(3);
    assertThat(row1.getFoldableRow("N/A").getTotalValue()).isEqualTo(7);
    assertThat(row1.getCells().length).isEqualTo(0);
    assertThat(row1.getFoldableRowCount()).isEqualTo(2);

    FoldableRow<DetailedMonitorQueryItem> row2 = report.getRow("L20000");
    assertThat(row2.getTotalValue()).isEqualTo(10);
    assertThat(row2.getFoldableRow("L22000").getTotalValue()).isEqualTo(4);
    assertThat(row2.getFoldableRow("N/A").getTotalValue()).isEqualTo(6);
    assertThat(row2.getCells().length).isEqualTo(0);
    assertThat(row2.getFoldableRowCount()).isEqualTo(2);

    FoldableRow<DetailedMonitorQueryItem> row3 = report.getRow("L30000");
    assertThat(row3.getTotalValue()).isEqualTo(7);
    assertThat(row3.getFoldableRow("L33000").getTotalValue()).isEqualTo(3);
    assertThat(row3.getFoldableRow("L99000").getTotalValue()).isEqualTo(1);
    assertThat(row3.getFoldableRow("N/A").getTotalValue()).isEqualTo(3);
    assertThat(row3.getCells().length).isEqualTo(0);
    assertThat(row3.getFoldableRowCount()).isEqualTo(3);

    FoldableRow<DetailedMonitorQueryItem> row4 = report.getRow("L40000");
    assertThat(row4.getTotalValue()).isEqualTo(10);
    assertThat(row4.getFoldableRow("N/A").getTotalValue()).isEqualTo(10);
    assertThat(row4.getCells().length).isEqualTo(0);
    assertThat(row4.getFoldableRowCount()).isEqualTo(1);

    FoldableRow<DetailedMonitorQueryItem> row5 = report.getRow("L50000");
    assertThat(row5.getTotalValue()).isEqualTo(13);
    assertThat(row5.getFoldableRow("N/A").getTotalValue()).isEqualTo(13);
    assertThat(row5.getCells().length).isEqualTo(0);
    assertThat(row5.getFoldableRowCount()).isEqualTo(1);

    assertThat(report.getSumRow().getTotalValue()).isEqualTo(50);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetDetailedClassificationReportWithReportLineItemDefinitions() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    assertThat(report.getRow("L10000").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("L20000").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("L30000").getTotalValue()).isEqualTo(7);
    assertThat(report.getRow("L40000").getTotalValue()).isEqualTo(10);
    assertThat(report.getRow("L50000").getTotalValue()).isEqualTo(13);

    int[] sumRow = report.getSumRow().getCells();
    assertThat(sumRow).isEqualTo(new int[] {10, 9, 11, 0, 4, 0, 7, 4, 5});
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
                      .createClassificationReportBuilder()
                      .buildDetailedReport(timestamp);
          assertThatCode(callable).doesNotThrowAnyException();
        };
    return DynamicTest.stream(iterator, t -> "for TaskState " + t, test);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfDetailedClassificationReport() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    FoldableRow<DetailedMonitorQueryItem> line1 = report.getRow("L10000");
    assertThat(line1.getCells()).isEqualTo(new int[] {7, 2, 1, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine1 = line1.getFoldableRow("L11000");
    assertThat(detailedLine1.getCells()).isEqualTo(new int[] {2, 0, 1, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment1.getCells()).isEqualTo(new int[] {5, 2, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line2 = report.getRow("L20000");
    assertThat(line2.getCells()).isEqualTo(new int[] {5, 3, 1, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLine2 = line2.getFoldableRow("L22000");
    assertThat(detailedLine2.getCells()).isEqualTo(new int[] {1, 1, 1, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment2.getCells()).isEqualTo(new int[] {4, 2, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line3 = report.getRow("L30000");
    assertThat(line3.getCells()).isEqualTo(new int[] {2, 1, 0, 1, 3});

    Row<DetailedMonitorQueryItem> detailedLine3a = line3.getFoldableRow("L33000");
    assertThat(detailedLine3a.getCells()).isEqualTo(new int[] {0, 1, 0, 1, 1});

    Row<DetailedMonitorQueryItem> detailedLine3b = line3.getFoldableRow("L99000");
    assertThat(detailedLine3b.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 1});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment3.getCells()).isEqualTo(new int[] {2, 0, 0, 0, 1});

    FoldableRow<DetailedMonitorQueryItem> line4 = report.getRow("L40000");
    assertThat(line4.getCells()).isEqualTo(new int[] {2, 2, 2, 0, 4});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment4.getCells()).isEqualTo(new int[] {2, 2, 2, 0, 4});

    FoldableRow<DetailedMonitorQueryItem> line5 = report.getRow("L50000");
    assertThat(line5.getCells()).isEqualTo(new int[] {3, 3, 0, 5, 2});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment5.getCells()).isEqualTo(new int[] {3, 3, 0, 5, 2});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ComputeNumbersAccordingToPlannedDate_When_BuildReportForPlanned() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport(TaskTimestamp.PLANNED);

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    FoldableRow<DetailedMonitorQueryItem> line1 = report.getRow("L10000");
    assertThat(line1.getCells()).isEqualTo(new int[] {0, 2, 8, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine1 = line1.getFoldableRow("L11000");
    assertThat(detailedLine1.getCells()).isEqualTo(new int[] {0, 1, 2, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment1.getCells()).isEqualTo(new int[] {0, 1, 6, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line2 = report.getRow("L20000");
    assertThat(line2.getCells()).isEqualTo(new int[] {0, 1, 9, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine2 = line2.getFoldableRow("L22000");
    assertThat(detailedLine2.getCells()).isEqualTo(new int[] {0, 0, 4, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment2.getCells()).isEqualTo(new int[] {0, 1, 5, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line3 = report.getRow("L30000");
    assertThat(line3.getCells()).isEqualTo(new int[] {0, 0, 7, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine3a = line3.getFoldableRow("L33000");
    assertThat(detailedLine3a.getCells()).isEqualTo(new int[] {0, 0, 3, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine3b = line3.getFoldableRow("L99000");
    assertThat(detailedLine3b.getCells()).isEqualTo(new int[] {0, 0, 1, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment3.getCells()).isEqualTo(new int[] {0, 0, 3, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line4 = report.getRow("L40000");
    assertThat(line4.getCells()).isEqualTo(new int[] {0, 0, 10, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment4.getCells()).isEqualTo(new int[] {0, 0, 10, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line5 = report.getRow("L50000");
    assertThat(line5.getCells()).isEqualTo(new int[] {0, 0, 13, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment5.getCells()).isEqualTo(new int[] {0, 0, 13, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfDetailedClassificationReportWithWorkbasketFilter() throws Exception {
    List<String> workbasketIds = List.of("WBI:000000000000000000000000000000000001");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    FoldableRow<DetailedMonitorQueryItem> line1 = report.getRow("L10000");
    assertThat(line1.getCells()).isEqualTo(new int[] {6, 0, 0, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine1 = line1.getFoldableRow("L11000");
    assertThat(detailedLine1.getCells()).isEqualTo(new int[] {2, 0, 0, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment1.getCells()).isEqualTo(new int[] {4, 0, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line2 = report.getRow("L20000");
    assertThat(line2.getCells()).isEqualTo(new int[] {2, 0, 0, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine2 = line2.getFoldableRow("L22000");
    assertThat(detailedLine2.getCells()).isEqualTo(new int[] {1, 0, 0, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment2.getCells()).isEqualTo(new int[] {1, 0, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line3 = report.getRow("L30000");
    assertThat(line3.getCells()).isEqualTo(new int[] {2, 1, 0, 1, 1});

    Row<DetailedMonitorQueryItem> detailedLine3a = line3.getFoldableRow("L33000");
    assertThat(detailedLine3a.getCells()).isEqualTo(new int[] {0, 1, 0, 1, 1});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment3.getCells()).isEqualTo(new int[] {2, 0, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line4 = report.getRow("L40000");
    assertThat(line4.getCells()).isEqualTo(new int[] {1, 0, 1, 0, 1});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment4.getCells()).isEqualTo(new int[] {1, 0, 1, 0, 1});

    FoldableRow<DetailedMonitorQueryItem> line5 = report.getRow("L50000");
    assertThat(line5.getCells()).isEqualTo(new int[] {2, 2, 0, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment5.getCells()).isEqualTo(new int[] {2, 2, 0, 0, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfDetailedClassificationReportWithStateFilter() throws Exception {
    List<TaskState> states = List.of(TaskState.READY);
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .stateIn(states)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    FoldableRow<DetailedMonitorQueryItem> line1 = report.getRow("L10000");
    assertThat(line1.getCells()).isEqualTo(new int[] {7, 2, 1, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine1 = line1.getFoldableRow("L11000");
    assertThat(detailedLine1.getCells()).isEqualTo(new int[] {2, 0, 1, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment1.getCells()).isEqualTo(new int[] {5, 2, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line2 = report.getRow("L20000");
    assertThat(line2.getCells()).isEqualTo(new int[] {5, 3, 1, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLine2 = line2.getFoldableRow("L22000");
    assertThat(detailedLine2.getCells()).isEqualTo(new int[] {1, 1, 1, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment2.getCells()).isEqualTo(new int[] {4, 2, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line3 = report.getRow("L30000");
    assertThat(line3.getCells()).isEqualTo(new int[] {2, 1, 0, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLine3a = line3.getFoldableRow("L33000");
    assertThat(detailedLine3a.getCells()).isEqualTo(new int[] {0, 1, 0, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment3.getCells()).isEqualTo(new int[] {2, 0, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line4 = report.getRow("L40000");
    assertThat(line4.getCells()).isEqualTo(new int[] {2, 2, 2, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment4.getCells()).isEqualTo(new int[] {2, 2, 2, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line5 = report.getRow("L50000");
    assertThat(line5.getCells()).isEqualTo(new int[] {3, 3, 0, 5, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment5.getCells()).isEqualTo(new int[] {3, 3, 0, 5, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfDetailedClassificationReportNotInWorkingDays() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    FoldableRow<DetailedMonitorQueryItem> line1 = report.getRow("L10000");
    assertThat(line1.getCells()).isEqualTo(new int[] {9, 0, 1, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine1 = line1.getFoldableRow("L11000");
    assertThat(detailedLine1.getCells()).isEqualTo(new int[] {2, 0, 1, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment1.getCells()).isEqualTo(new int[] {7, 0, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line2 = report.getRow("L20000");
    assertThat(line2.getCells()).isEqualTo(new int[] {8, 0, 1, 0, 1});

    Row<DetailedMonitorQueryItem> detailedLine2 = line2.getFoldableRow("L22000");
    assertThat(detailedLine2.getCells()).isEqualTo(new int[] {2, 0, 1, 0, 1});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment2.getCells()).isEqualTo(new int[] {6, 0, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line3 = report.getRow("L30000");
    assertThat(line3.getCells()).isEqualTo(new int[] {3, 0, 0, 0, 4});

    Row<DetailedMonitorQueryItem> detailedLine3a = line3.getFoldableRow("L33000");
    assertThat(detailedLine3a.getCells()).isEqualTo(new int[] {1, 0, 0, 0, 2});

    Row<DetailedMonitorQueryItem> detailedLine3b = line3.getFoldableRow("L99000");
    assertThat(detailedLine3b.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 1});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment3.getCells()).isEqualTo(new int[] {2, 0, 0, 0, 1});

    FoldableRow<DetailedMonitorQueryItem> line4 = report.getRow("L40000");
    assertThat(line4.getCells()).isEqualTo(new int[] {4, 0, 2, 0, 4});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment4.getCells()).isEqualTo(new int[] {4, 0, 2, 0, 4});

    FoldableRow<DetailedMonitorQueryItem> line5 = report.getRow("L50000");
    assertThat(line5.getCells()).isEqualTo(new int[] {6, 0, 0, 0, 7});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment5.getCells()).isEqualTo(new int[] {6, 0, 0, 0, 7});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfDetailedClassificationReportWithCategoryFilter() throws Exception {
    List<String> categories = List.of("AUTOMATIC", "MANUAL");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .classificationCategoryIn(categories)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(2);

    FoldableRow<DetailedMonitorQueryItem> line1 = report.getRow("L30000");
    assertThat(line1.getCells()).isEqualTo(new int[] {2, 1, 0, 1, 3});

    Row<DetailedMonitorQueryItem> detailedLine1a = line1.getFoldableRow("L33000");
    assertThat(detailedLine1a.getCells()).isEqualTo(new int[] {0, 1, 0, 1, 1});

    Row<DetailedMonitorQueryItem> detailedLine1b = line1.getFoldableRow("L99000");
    assertThat(detailedLine1b.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 1});

    Row<DetailedMonitorQueryItem> detailedLine1WithoutAttachment = line1.getFoldableRow("N/A");
    assertThat(detailedLine1WithoutAttachment.getCells()).isEqualTo(new int[] {2, 0, 0, 0, 1});

    FoldableRow<DetailedMonitorQueryItem> line2 = report.getRow("L40000");
    assertThat(line2.getCells()).isEqualTo(new int[] {2, 2, 2, 0, 4});

    Row<DetailedMonitorQueryItem> detailedLine2WithoutAttachment = line2.getFoldableRow("N/A");
    assertThat(detailedLine2WithoutAttachment.getCells()).isEqualTo(new int[] {2, 2, 2, 0, 4});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfDetailedClassificationReportWithDomainFilter() throws Exception {
    List<String> domains = List.of("DOMAIN_A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .buildDetailedReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    FoldableRow<DetailedMonitorQueryItem> line1 = report.getRow("L10000");
    assertThat(line1.getCells()).isEqualTo(new int[] {5, 2, 1, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine1 = line1.getFoldableRow("L11000");
    assertThat(detailedLine1.getCells()).isEqualTo(new int[] {1, 0, 1, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment1.getCells()).isEqualTo(new int[] {4, 2, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line2 = report.getRow("L20000");
    assertThat(line2.getCells()).isEqualTo(new int[] {3, 1, 1, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLine2 = line2.getFoldableRow("L22000");
    assertThat(detailedLine2.getCells()).isEqualTo(new int[] {1, 0, 1, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment2.getCells()).isEqualTo(new int[] {2, 1, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line3 = report.getRow("L30000");
    assertThat(line3.getCells()).isEqualTo(new int[] {1, 0, 0, 1, 1});

    Row<DetailedMonitorQueryItem> detailedLine3 = line3.getFoldableRow("L33000");
    assertThat(detailedLine3.getCells()).isEqualTo(new int[] {0, 0, 0, 1, 1});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment3.getCells()).isEqualTo(new int[] {1, 0, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line4 = report.getRow("L40000");
    assertThat(line4.getCells()).isEqualTo(new int[] {2, 0, 0, 0, 3});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment4.getCells()).isEqualTo(new int[] {2, 0, 0, 0, 3});

    FoldableRow<DetailedMonitorQueryItem> line5 = report.getRow("L50000");
    assertThat(line5.getCells()).isEqualTo(new int[] {0, 1, 0, 3, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment5.getCells()).isEqualTo(new int[] {0, 1, 0, 3, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void testEachItemOfDetailedClassificationReportWithCustomFieldValueFilter() throws Exception {
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    assertThat(report).isNotNull();
    assertThat(report.rowSize()).isEqualTo(5);

    FoldableRow<DetailedMonitorQueryItem> line1 = report.getRow("L10000");
    assertThat(line1.getCells()).isEqualTo(new int[] {4, 0, 0, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLine1 = line1.getFoldableRow("L11000");
    assertThat(detailedLine1.getCells()).isEqualTo(new int[] {1, 0, 0, 0, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment1 = line1.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment1.getCells()).isEqualTo(new int[] {3, 0, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line2 = report.getRow("L20000");
    assertThat(line2.getCells()).isEqualTo(new int[] {4, 1, 1, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLine2 = line2.getFoldableRow("L22000");
    assertThat(detailedLine2.getCells()).isEqualTo(new int[] {1, 1, 1, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment2 = line2.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment2.getCells()).isEqualTo(new int[] {3, 0, 0, 0, 0});

    FoldableRow<DetailedMonitorQueryItem> line3 = report.getRow("L30000");
    assertThat(line3.getCells()).isEqualTo(new int[] {1, 0, 0, 1, 1});

    Row<DetailedMonitorQueryItem> detailedLine3a = line3.getFoldableRow("L33000");
    assertThat(detailedLine3a.getCells()).isEqualTo(new int[] {0, 0, 0, 1, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment3 = line3.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment3.getCells()).isEqualTo(new int[] {1, 0, 0, 0, 1});

    FoldableRow<DetailedMonitorQueryItem> line4 = report.getRow("L40000");
    assertThat(line4.getCells()).isEqualTo(new int[] {1, 1, 2, 0, 2});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment4 = line4.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment4.getCells()).isEqualTo(new int[] {1, 1, 2, 0, 2});

    FoldableRow<DetailedMonitorQueryItem> line5 = report.getRow("L50000");
    assertThat(line5.getCells()).isEqualTo(new int[] {1, 2, 0, 2, 0});

    Row<DetailedMonitorQueryItem> detailedLineNoAttachment5 = line5.getFoldableRow("N/A");
    assertThat(detailedLineNoAttachment5.getCells()).isEqualTo(new int[] {1, 2, 0, 2, 0});
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
