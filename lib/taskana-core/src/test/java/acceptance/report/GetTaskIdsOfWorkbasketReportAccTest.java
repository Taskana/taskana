package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;

/** Acceptance test for all "get task ids of workbasket report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskIdsOfWorkbasketReportAccTest extends AbstractReportAccTest {

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<SelectedItem> selectedItems = new ArrayList<>();

    ThrowingCallable call =
        () -> {
          monitorService.createWorkbasketReportBuilder().listTaskIdsForSelectedItems(selectedItems);
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfWorkbasketReport() throws Exception {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("USER-1-1");
    s1.setLowerAgeLimit(0);
    s1.setUpperAgeLimit(0);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("USER-1-1");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("USER-1-2");
    s3.setLowerAgeLimit(1000);
    s3.setUpperAgeLimit(Integer.MAX_VALUE);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids)
        .containsOnly(
            "TKI:000000000000000000000000000000000001",
            "TKI:000000000000000000000000000000000004",
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000010",
            "TKI:000000000000000000000000000000000031",
            "TKI:000000000000000000000000000000000050");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfWorkbasketReportWithExcludedClassifications() throws Exception {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("USER-1-1");
    s1.setLowerAgeLimit(0);
    s1.setUpperAgeLimit(0);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("USER-1-1");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("USER-1-2");
    s3.setLowerAgeLimit(1000);
    s3.setUpperAgeLimit(Integer.MAX_VALUE);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .excludedClassificationIdIn(
                Collections.singletonList("CLI:000000000000000000000000000000000001"))
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids).hasSize(4);
    assertThat(ids)
        .containsOnly(
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000031",
            "TKI:000000000000000000000000000000000050");
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
}
