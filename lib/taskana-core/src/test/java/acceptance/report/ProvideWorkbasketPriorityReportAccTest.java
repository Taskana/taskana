package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.WorkbasketPriorityReport;
import pro.taskana.monitor.api.reports.header.PriorityColumnHeader;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.workbasket.api.WorkbasketType;

/** Acceptance test for all "workbasket priority report" scenarios. */
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
        .isInstanceOf(MismatchedRoleException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_BuildReport_When_UserIsAuthorized() throws Exception {
    WorkbasketPriorityReport priorityReport =
        MONITOR_SERVICE
            .createWorkbasketPriorityReportBuilder()
            .withColumnHeaders(LOW_TEST_HEADERS)
            .buildReport();
    int[] expectedCells = {45, 9};
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
}
