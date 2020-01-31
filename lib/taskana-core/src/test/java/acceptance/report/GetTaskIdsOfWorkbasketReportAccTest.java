package acceptance.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.report.api.TaskMonitorService;
import pro.taskana.report.internal.SelectedItem;
import pro.taskana.report.internal.header.TimeIntervalColumnHeader;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "get task ids of workbasket report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskIdsOfWorkbasketReportAccTest extends AbstractReportAccTest {

  @Test
  void testRoleCheck() {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<SelectedItem> selectedItems = new ArrayList<>();

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            taskMonitorService
                .createWorkbasketReportBuilder()
                .listTaskIdsForSelectedItems(selectedItems));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfWorkbasketReport() throws InvalidArgumentException, NotAuthorizedException {
    final TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("USER_1_1");
    s1.setLowerAgeLimit(0);
    s1.setUpperAgeLimit(0);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("USER_1_1");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("USER_1_2");
    s3.setLowerAgeLimit(1000);
    s3.setUpperAgeLimit(Integer.MAX_VALUE);
    selectedItems.add(s3);

    List<String> ids =
        taskMonitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

    assertEquals(7, ids.size());
    assertTrue(ids.contains("TKI:000000000000000000000000000000000001"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000004"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000009"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000010"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000050"));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfWorkbasketReportWithExcludedClassifications()
      throws InvalidArgumentException, NotAuthorizedException {
    final TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("USER_1_1");
    s1.setLowerAgeLimit(0);
    s1.setUpperAgeLimit(0);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("USER_1_1");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("USER_1_2");
    s3.setLowerAgeLimit(1000);
    s3.setUpperAgeLimit(Integer.MAX_VALUE);
    selectedItems.add(s3);

    List<String> ids =
        taskMonitorService
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .excludedClassificationIdIn(
                Collections.singletonList("CLI:000000000000000000000000000000000001"))
            .listTaskIdsForSelectedItems(selectedItems);

    assertEquals(4, ids.size());
    assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000009"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000050"));
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
