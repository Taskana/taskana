package acceptance.report;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.TimestampQueryItem;
import pro.taskana.monitor.api.reports.row.SingleRow;
import pro.taskana.monitor.api.reports.row.TimestampRow;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Test class for {@link TimestampReport}. */
@ExtendWith(JaasExtension.class)
class ProvideTimestampReportAccTest extends AbstractReportAccTest {

  /**
   * This test covers every insert operation of the TimestampReport. We have two definitions for org
   * level 1: 'org1' and 'N/A'. All other org levels only contain 'N/A'. Thus this test only tests
   * the separation for org level1. Since every OrgLevelRow is a FoldableRow this is sufficient to
   * prove that the separation/grouping by detail mechanism works.
   *
   * @throws Exception if any error occurs during the test.
   */
  @WithAccessId(userName = "monitor")
  @Test
  void testProperInsertionOfQueryItems() throws Exception {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    // last 14 days. Today excluded.
    List<TimeIntervalColumnHeader> headers =
        IntStream.range(-14, 0)
            .mapToObj(TimeIntervalColumnHeader.Date::new)
            .collect(Collectors.toList());
    TimestampReport timestampReport =
        monitorService.createTimestampReportBuilder().withColumnHeaders(headers).buildReport();
    final HashSet<String> org1Set = new HashSet<>(Arrays.asList("N/A", "org1"));
    final HashSet<String> allOtherOrgLevelSet = new HashSet<>(Collections.singletonList("N/A"));

    assertEquals(2, timestampReport.getRows().size());
    assertEquals(
        new HashSet<>(Arrays.asList("CREATED", "COMPLETED")), timestampReport.getRows().keySet());

    // * * * * * * * * * * * * * * * * * *  * *  * TEST THE CREATED ROW * * * * * * * * * * * * * *
    // * * * * * * *

    TimestampRow statusRow = timestampReport.getRow("CREATED");
    assertEquals(2, statusRow.getFoldableRowCount());
    assertEquals(org1Set, statusRow.getFoldableRowKeySet());
    // 2 Entries with -8 days and one with -9 days.
    assertArrayEquals(new int[] {0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0, 0}, statusRow.getCells());
    assertEquals(3, statusRow.getTotalValue());

    // 'CREATED' -> 'org1'
    TimestampRow.OrgLevel1Row org1Row = statusRow.getFoldableRow("org1");
    assertEquals(1, org1Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org1Row.getFoldableRowKeySet());
    // only task TKI:000000000000000000000000000000000029 in 'org1'.
    assertArrayEquals(new int[] {0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}, org1Row.getCells());
    assertEquals(1, org1Row.getTotalValue());

    // 'CREATED' -> 'org1'/'N/A'
    TimestampRow.OrgLevel2Row org2Row = org1Row.getFoldableRow("N/A");
    assertEquals(1, org2Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org2Row.getFoldableRowKeySet());
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org1Row.getCells(), org2Row.getCells());
    assertEquals(org1Row.getTotalValue(), org2Row.getTotalValue());

    // 'CREATED' -> 'org1'/'N/A'/'N/A'
    TimestampRow.OrgLevel3Row org3Row = org2Row.getFoldableRow("N/A");
    assertEquals(1, org2Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org3Row.getFoldableRowKeySet());
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org2Row.getCells(), org3Row.getCells());
    assertEquals(org2Row.getTotalValue(), org3Row.getTotalValue());

    // 'CREATED' -> 'org1'/'N/A'/'N/A'/'N/A'
    SingleRow<TimestampQueryItem> org4Row = org3Row.getFoldableRow("N/A");
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org3Row.getCells(), org4Row.getCells());
    assertEquals(org3Row.getTotalValue(), org4Row.getTotalValue());

    // 'CREATED' -> 'N/A'
    org1Row = statusRow.getFoldableRow("N/A");
    assertEquals(1, org1Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org1Row.getFoldableRowKeySet());
    // task TKI:000000000000000000000000000000000030,
    //  and TKI:000000000000000000000000000000000036 in 'N/A'.
    assertArrayEquals(new int[] {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0}, org1Row.getCells());
    assertEquals(2, org1Row.getTotalValue());

    // 'CREATED' -> 'N/A'/'N/A'
    org2Row = org1Row.getFoldableRow("N/A");
    assertEquals(1, org2Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org2Row.getFoldableRowKeySet());
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org1Row.getCells(), org2Row.getCells());
    assertEquals(org1Row.getTotalValue(), org2Row.getTotalValue());

    // 'CREATED' -> 'N/A'/'N/A'/'N/A'
    org3Row = org2Row.getFoldableRow("N/A");
    assertEquals(1, org2Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org3Row.getFoldableRowKeySet());
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org2Row.getCells(), org3Row.getCells());
    assertEquals(org2Row.getTotalValue(), org3Row.getTotalValue());

    // 'CREATED' -> 'N/A'/'N/A'/'N/A'/'N/A'
    org4Row = org3Row.getFoldableRow("N/A");
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org3Row.getCells(), org4Row.getCells());
    assertEquals(org3Row.getTotalValue(), org4Row.getTotalValue());

    // * * * * * * * * * * * * * * * * * *  * *  * TEST THE COMPLETED ROW * * * * * * * * * * * * *
    // * * * * * * * *

    statusRow = timestampReport.getRow("COMPLETED");
    assertEquals(2, statusRow.getFoldableRowCount());
    assertEquals(org1Set, statusRow.getFoldableRowKeySet());
    // 2 Entries with -1 days, one with -2 days and one with -7 days.
    assertArrayEquals(new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 2}, statusRow.getCells());
    assertEquals(4, statusRow.getTotalValue());

    // 'COMPLETED' -> 'org1'
    org1Row = statusRow.getFoldableRow("org1");
    assertEquals(1, org1Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org1Row.getFoldableRowKeySet());
    // only task TKI:000000000000000000000000000000000029 in 'org1'.
    assertArrayEquals(new int[] {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0}, org1Row.getCells());
    assertEquals(1, org1Row.getTotalValue());

    // 'COMPLETED' -> 'org1'/'N/A'
    org2Row = org1Row.getFoldableRow("N/A");
    assertEquals(1, org2Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org2Row.getFoldableRowKeySet());
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org1Row.getCells(), org2Row.getCells());
    assertEquals(org1Row.getTotalValue(), org2Row.getTotalValue());

    // 'COMPLETED' -> 'org1'/'N/A'/'N/A'
    org3Row = org2Row.getFoldableRow("N/A");
    assertEquals(1, org2Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org3Row.getFoldableRowKeySet());
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org2Row.getCells(), org3Row.getCells());
    assertEquals(org2Row.getTotalValue(), org3Row.getTotalValue());

    // 'COMPLETED' -> 'org1'/'N/A'/'N/A'/'N/A'
    org4Row = org3Row.getFoldableRow("N/A");
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org3Row.getCells(), org4Row.getCells());
    assertEquals(org3Row.getTotalValue(), org4Row.getTotalValue());

    // 'COMPLETED' -> 'N/A'
    org1Row = statusRow.getFoldableRow("N/A");
    assertEquals(1, org1Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org1Row.getFoldableRowKeySet());
    // task TKI:000000000000000000000000000000000032,
    //      TKI:000000000000000000000000000000000034,
    //  and TKI:000000000000000000000000000000000037  in 'N/A'.
    assertArrayEquals(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2}, org1Row.getCells());
    assertEquals(3, org1Row.getTotalValue());

    // 'COMPLETED' -> 'N/A'/'N/A'
    org2Row = org1Row.getFoldableRow("N/A");
    assertEquals(1, org2Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org2Row.getFoldableRowKeySet());
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org1Row.getCells(), org2Row.getCells());
    assertEquals(org1Row.getTotalValue(), org2Row.getTotalValue());

    // 'COMPLETED' -> 'N/A'/'N/A'/'N/A'
    org3Row = org2Row.getFoldableRow("N/A");
    assertEquals(1, org2Row.getFoldableRowCount());
    assertEquals(allOtherOrgLevelSet, org3Row.getFoldableRowKeySet());
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org2Row.getCells(), org3Row.getCells());
    assertEquals(org2Row.getTotalValue(), org3Row.getTotalValue());

    // 'COMPLETED' -> 'N/A'/'N/A'/'N/A'/'N/A'
    org4Row = org3Row.getFoldableRow("N/A");
    // Since no further separation (in org level) they should be the same.
    assertArrayEquals(org3Row.getCells(), org4Row.getCells());
    assertEquals(org3Row.getTotalValue(), org4Row.getTotalValue());
  }
}
