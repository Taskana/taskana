package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.TimestampQueryItem;
import pro.taskana.monitor.api.reports.row.SingleRow;
import pro.taskana.monitor.api.reports.row.TimestampRow;

@ExtendWith(JaasExtension.class)
class ProvideTimestampReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = taskanaEngine.getMonitorService();

  @WithAccessId(user = "monitor")
  @Test
  void should_AugmentDisplayNames_When_ReportIsBuild() throws Exception {
    TimestampReport report = MONITOR_SERVICE.createTimestampReportBuilder().buildReport();
    assertThat(report.getRows()).hasSize(2);
    assertThat(report.getRow("CREATED").getDisplayName()).isEqualTo("CREATED");
    assertThat(report.getRow("COMPLETED").getDisplayName()).isEqualTo("COMPLETED");
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_NotThrowSqlExceptionDuringAugmentation_When_ReportContainsNoRows() {
    TimestampReport.Builder builder =
        MONITOR_SERVICE.createTimestampReportBuilder().domainIn(List.of("DOES_NOT_EXIST"));
    ThrowingCallable test =
        () -> {
          TimestampReport report = builder.buildReport();
          assertThat(report).isNotNull();
          assertThat(report.rowSize()).isZero();
        };
    assertThatCode(test).doesNotThrowAnyException();
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_FilterTasksAccordingToDomain_When_DomainFilterIsApplied() throws Exception {
    List<TimeIntervalColumnHeader> headers =
        IntStream.rangeClosed(-14, 0)
            .mapToObj(TimeIntervalColumnHeader.Date::new)
            .collect(Collectors.toList());
    TimestampReport report =
        MONITOR_SERVICE
            .createTimestampReportBuilder()
            .withColumnHeaders(headers)
            .domainIn(List.of("DOMAIN_A"))
            .buildReport();
    assertThat(report).isNotNull();

    assertThat(report.rowSize()).isEqualTo(2);
    assertThat(report.getRow("CREATED").getCells())
        .isEqualTo(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27});
    assertThat(report.getRow("COMPLETED").getCells())
        .isEqualTo(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0});
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_FilterTasksAccordingToClassificationId_When_ClassificationIdFilterIsApplied()
      throws Exception {
    List<TimeIntervalColumnHeader> headers =
        IntStream.rangeClosed(-14, 0)
            .mapToObj(TimeIntervalColumnHeader.Date::new)
            .collect(Collectors.toList());
    TimestampReport report =
        MONITOR_SERVICE
            .createTimestampReportBuilder()
            .withColumnHeaders(headers)
            .classificationIdIn(List.of("CLI:000000000000000000000000000000000001"))
            .buildReport();
    assertThat(report).isNotNull();

    assertThat(report.rowSize()).isEqualTo(2);
    assertThat(report.getRow("CREATED").getCells())
        .isEqualTo(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10});
    assertThat(report.getRow("COMPLETED").getCells())
        .isEqualTo(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
  }

  /**
   * This test covers every insert operation of the TimestampReport. We have two definitions for org
   * level 1: 'org1' and 'N/A'. All other org levels only contain 'N/A'. Thus this test only tests
   * the separation for org level1. Since every OrgLevelRow is a FoldableRow this is sufficient to
   * prove that the separation/grouping by detail mechanism works.
   *
   * @throws Exception if any error occurs during the test
   */
  @WithAccessId(user = "monitor")
  @Test
  void testProperInsertionOfQueryItems() throws Exception {

    // last 14 days. Today excluded.
    List<TimeIntervalColumnHeader> headers =
        IntStream.range(-14, 0)
            .mapToObj(TimeIntervalColumnHeader.Date::new)
            .collect(Collectors.toList());
    TimestampReport timestampReport =
        MONITOR_SERVICE.createTimestampReportBuilder().withColumnHeaders(headers).buildReport();
    final Set<String> org1Set = Set.of("N/A", "org1");
    final Set<String> allOtherOrgLevelSet = Set.of("N/A");

    assertThat(timestampReport.getRows()).containsOnlyKeys("CREATED", "COMPLETED");

    // * * * * * * * * * * * * * * * * * * TEST THE CREATED ROW * * * * * * * * * * * * * * * * * *

    TimestampRow statusRow = timestampReport.getRow("CREATED");
    assertThat(statusRow.getFoldableRowCount()).isEqualTo(2);
    assertThat(statusRow.getFoldableRowKeySet()).isEqualTo(org1Set);
    // 2 Entries with -8 days and one with -9 days.
    assertThat(statusRow.getCells())
        .isEqualTo(new int[] {0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0});
    assertThat(statusRow.getTotalValue()).isEqualTo(3);

    // 'CREATED' -> 'org1'
    TimestampRow.OrgLevel1Row org1Row = statusRow.getFoldableRow("org1");
    assertThat(org1Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org1Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // only task TKI:000000000000000000000000000000000029 in 'org1'.
    assertThat(org1Row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0});
    assertThat(org1Row.getTotalValue()).isEqualTo(1);

    // 'CREATED' -> 'org1'/'N/A'
    TimestampRow.OrgLevel2Row org2Row = org1Row.getFoldableRow("N/A");
    assertThat(org2Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org2Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // Since no further separation (in org level) they should be the same.
    assertThat(org2Row.getCells()).isEqualTo(org1Row.getCells());
    assertThat(org2Row.getTotalValue()).isEqualTo(org1Row.getTotalValue());

    // 'CREATED' -> 'org1'/'N/A'/'N/A'
    TimestampRow.OrgLevel3Row org3Row = org2Row.getFoldableRow("N/A");
    assertThat(org2Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org3Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // Since no further separation (in org level) they should be the same.
    assertThat(org3Row.getCells()).isEqualTo(org2Row.getCells());
    assertThat(org3Row.getTotalValue()).isEqualTo(org2Row.getTotalValue());

    // 'CREATED' -> 'org1'/'N/A'/'N/A'/'N/A'
    SingleRow<TimestampQueryItem> org4Row = org3Row.getFoldableRow("N/A");
    // Since no further separation (in org level) they should be the same.
    assertThat(org4Row.getCells()).isEqualTo(org3Row.getCells());
    assertThat(org4Row.getTotalValue()).isEqualTo(org3Row.getTotalValue());

    // 'CREATED' -> 'N/A'
    org1Row = statusRow.getFoldableRow("N/A");
    assertThat(org1Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org1Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // task TKI:000000000000000000000000000000000030,
    //  and TKI:000000000000000000000000000000000036 in 'N/A'.
    assertThat(org1Row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0});
    assertThat(org1Row.getTotalValue()).isEqualTo(2);

    // 'CREATED' -> 'N/A'/'N/A'
    org2Row = org1Row.getFoldableRow("N/A");
    assertThat(org2Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org2Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // Since no further separation (in org level) they should be the same.
    assertThat(org2Row.getCells()).isEqualTo(org1Row.getCells());
    assertThat(org2Row.getTotalValue()).isEqualTo(org1Row.getTotalValue());

    // 'CREATED' -> 'N/A'/'N/A'/'N/A'
    org3Row = org2Row.getFoldableRow("N/A");
    assertThat(org2Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org3Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // Since no further separation (in org level) they should be the same.
    assertThat(org3Row.getCells()).isEqualTo(org2Row.getCells());
    assertThat(org3Row.getTotalValue()).isEqualTo(org2Row.getTotalValue());

    // 'CREATED' -> 'N/A'/'N/A'/'N/A'/'N/A'
    org4Row = org3Row.getFoldableRow("N/A");
    // Since no further separation (in org level) they should be the same.
    assertThat(org4Row.getCells()).isEqualTo(org3Row.getCells());
    assertThat(org4Row.getTotalValue()).isEqualTo(org3Row.getTotalValue());

    // * * * * * * * * * * * * * * * * * * TEST THE COMPLETED ROW * * * * * * * * * * * * * * * * *

    statusRow = timestampReport.getRow("COMPLETED");
    assertThat(statusRow.getFoldableRowCount()).isEqualTo(2);
    assertThat(statusRow.getFoldableRowKeySet()).isEqualTo(org1Set);
    // 2 Entries with -1 days, one with -2 days and one with -7 days.
    assertThat(statusRow.getCells())
        .isEqualTo(new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 2});
    assertThat(statusRow.getTotalValue()).isEqualTo(4);

    // 'COMPLETED' -> 'org1'
    org1Row = statusRow.getFoldableRow("org1");
    assertThat(org1Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org1Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // only task TKI:000000000000000000000000000000000029 in 'org1'.
    assertThat(org1Row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0});
    assertThat(org1Row.getTotalValue()).isEqualTo(1);

    // 'COMPLETED' -> 'org1'/'N/A'
    org2Row = org1Row.getFoldableRow("N/A");
    assertThat(org2Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org2Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // Since no further separation (in org level) they should be the same.
    assertThat(org2Row.getCells()).isEqualTo(org1Row.getCells());
    assertThat(org2Row.getTotalValue()).isEqualTo(org1Row.getTotalValue());

    // 'COMPLETED' -> 'org1'/'N/A'/'N/A'
    org3Row = org2Row.getFoldableRow("N/A");
    assertThat(org2Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org3Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // Since no further separation (in org level) they should be the same.
    assertThat(org3Row.getCells()).isEqualTo(org2Row.getCells());
    assertThat(org3Row.getTotalValue()).isEqualTo(org2Row.getTotalValue());

    // 'COMPLETED' -> 'org1'/'N/A'/'N/A'/'N/A'
    org4Row = org3Row.getFoldableRow("N/A");
    // Since no further separation (in org level) they should be the same.
    assertThat(org4Row.getCells()).isEqualTo(org3Row.getCells());
    assertThat(org4Row.getTotalValue()).isEqualTo(org3Row.getTotalValue());

    // 'COMPLETED' -> 'N/A'
    org1Row = statusRow.getFoldableRow("N/A");
    assertThat(org1Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org1Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // task TKI:000000000000000000000000000000000032,
    //      TKI:000000000000000000000000000000000034,
    //  and TKI:000000000000000000000000000000000037  in 'N/A'.
    assertThat(org1Row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2});
    assertThat(org1Row.getTotalValue()).isEqualTo(3);

    // 'COMPLETED' -> 'N/A'/'N/A'
    org2Row = org1Row.getFoldableRow("N/A");
    assertThat(org2Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org2Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // Since no further separation (in org level) they should be the same.
    assertThat(org2Row.getCells()).isEqualTo(org1Row.getCells());
    assertThat(org2Row.getTotalValue()).isEqualTo(org1Row.getTotalValue());

    // 'COMPLETED' -> 'N/A'/'N/A'/'N/A'
    org3Row = org2Row.getFoldableRow("N/A");
    assertThat(org2Row.getFoldableRowCount()).isEqualTo(1);
    assertThat(org3Row.getFoldableRowKeySet()).isEqualTo(allOtherOrgLevelSet);
    // Since no further separation (in org level) they should be the same.
    assertThat(org3Row.getCells()).isEqualTo(org2Row.getCells());
    assertThat(org3Row.getTotalValue()).isEqualTo(org2Row.getTotalValue());

    // 'COMPLETED' -> 'N/A'/'N/A'/'N/A'/'N/A'
    org4Row = org3Row.getFoldableRow("N/A");
    // Since no further separation (in org level) they should be the same.
    assertThat(org4Row.getCells()).isEqualTo(org3Row.getCells());
    assertThat(org4Row.getTotalValue()).isEqualTo(org3Row.getTotalValue());
  }
}
