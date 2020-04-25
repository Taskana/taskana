package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.ClassificationReport.DetailedClassificationReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.DetailedMonitorQueryItem;
import pro.taskana.monitor.api.reports.row.FoldableRow;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.CustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "detailed classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class ProvideDetailedClassificationReportAccTest extends AbstractReportAccTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(ProvideDetailedClassificationReportAccTest.class);

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    ThrowingCallable call =
        () -> {
          monitorService.createClassificationReportBuilder().buildDetailedReport();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTotalNumbersOfTasksOfDetailedClassificationReport()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    DetailedClassificationReport report =
        monitorService.createClassificationReportBuilder().buildDetailedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report));
    }

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
  void testGetDetailedClassificationReportWithReportLineItemDefinitions()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    DetailedClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

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
  @Test
  void testEachItemOfDetailedClassificationReport()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

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
  void testEachItemOfDetailedClassificationReportWithWorkbasketFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .workbasketIdIn(workbasketIds)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

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
  void testEachItemOfDetailedClassificationReportWithStateFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TaskState> states = Collections.singletonList(TaskState.READY);
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .stateIn(states)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

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
  void testEachItemOfDetailedClassificationReportNotInWorkingDays()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

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
  void testEachItemOfDetailedClassificationReportWithCategoryFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .categoryIn(categories)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

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
  void testEachItemOfDetailedClassificationReportWithDomainFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> domains = Collections.singletonList("DOMAIN_A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .domainIn(domains)
            .buildDetailedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

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
  void testEachItemOfDetailedClassificationReportWithCustomFieldValueFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    List<TimeIntervalColumnHeader> columnHeaders = getShortListOfColumnHeaders();

    DetailedClassificationReport report =
        monitorService
            .createClassificationReportBuilder()
            .customAttributeFilterIn(customAttributeFilter)
            .inWorkingDays()
            .withColumnHeaders(columnHeaders)
            .buildDetailedReport();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(reportToString(report, columnHeaders));
    }

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

  private String reportToString(DetailedClassificationReport report) {
    return reportToString(report, null);
  }

  private String reportToString(
      DetailedClassificationReport report, List<TimeIntervalColumnHeader> columnHeaders) {
    String formatColumWidth = "| %-7s ";
    String formatFirstColumn = "| %-36s  %-4s ";
    final String formatFirstColumnFirstLine = "| %-29s %12s ";
    final String formatFirstColumnDetailLines = "| + %-34s  %-4s ";
    final String formatFirstColumnSumLine = "| %-36s  %-5s";
    int reportWidth = columnHeaders == null ? 46 : columnHeaders.size() * 10 + 46;

    StringBuilder builder = new StringBuilder();
    builder.append("\n");
    for (int i = 0; i < reportWidth; i++) {
      builder.append("-");
    }
    builder.append("\n");
    builder.append(
        String.format(formatFirstColumnFirstLine, "Classifications + Attachments", "Total"));
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
      for (String detaileLine : report.getRow(rl).getFoldableRowKeySet()) {
        Row<DetailedMonitorQueryItem> reportLine = report.getRow(rl).getFoldableRow(detaileLine);
        builder.append(
            String.format(formatFirstColumnDetailLines, detaileLine, reportLine.getTotalValue()));
        for (int cell : reportLine.getCells()) {
          builder.append(String.format(formatColumWidth, cell));
        }
        builder.append("|\n");
      }

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
