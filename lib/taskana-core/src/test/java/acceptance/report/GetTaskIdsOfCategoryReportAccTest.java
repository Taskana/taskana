package acceptance.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.SelectedItem;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "get task ids of category report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskIdsOfCategoryReportAccTest extends AbstractReportAccTest {

  @Test
  void testRoleCheck() {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<SelectedItem> selectedItems = new ArrayList<>();

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            taskMonitorService
                .createCategoryReportBuilder()
                .listTaskIdsForSelectedItems(selectedItems));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReport() throws InvalidArgumentException, NotAuthorizedException {
    final TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("EXTERN");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("AUTOMATIC");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("MANUAL");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        taskMonitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

    assertEquals(11, ids.size());
    assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000021"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000022"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000023"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000024"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000026"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000027"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000028"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000032"));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithWorkbasketFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("EXTERN");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("AUTOMATIC");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("MANUAL");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        taskMonitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .workbasketIdIn(workbasketIds)
            .listTaskIdsForSelectedItems(selectedItems);

    assertEquals(4, ids.size());
    assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000026"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithStateFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    final List<TaskState> states = Collections.singletonList(TaskState.READY);
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("EXTERN");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("AUTOMATIC");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("MANUAL");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        taskMonitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .stateIn(states)
            .listTaskIdsForSelectedItems(selectedItems);

    assertEquals(11, ids.size());
    assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000021"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000022"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000023"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000024"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000026"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000027"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000028"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000032"));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithCategoryFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    final List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("AUTOMATIC");
    s1.setLowerAgeLimit(Integer.MIN_VALUE);
    s1.setUpperAgeLimit(-11);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("MANUAL");
    s2.setLowerAgeLimit(0);
    s2.setUpperAgeLimit(0);
    selectedItems.add(s2);

    List<String> ids =
        taskMonitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .categoryIn(categories)
            .listTaskIdsForSelectedItems(selectedItems);

    assertEquals(3, ids.size());
    assertTrue(ids.contains("TKI:000000000000000000000000000000000006"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000032"));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithDomainFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("EXTERN");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("AUTOMATIC");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("MANUAL");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        taskMonitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .domainIn(domains)
            .listTaskIdsForSelectedItems(selectedItems);

    assertEquals(4, ids.size());
    assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000021"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000022"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000028"));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithCustomFieldValueFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("EXTERN");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("AUTOMATIC");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("MANUAL");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        taskMonitorService
            .createCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .customAttributeFilterIn(customAttributeFilter)
            .listTaskIdsForSelectedItems(selectedItems);

    assertEquals(5, ids.size());
    assertTrue(ids.contains("TKI:000000000000000000000000000000000020"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000024"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000027"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000031"));
    assertTrue(ids.contains("TKI:000000000000000000000000000000000032"));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testThrowsExceptionIfSubKeysAreUsed() {
    final TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("EXTERN");
    s1.setSubKey("INVALID");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    Assertions.assertThrows(
        InvalidArgumentException.class,
        () ->
            taskMonitorService
                .createCategoryReportBuilder()
                .withColumnHeaders(columnHeaders)
                .listTaskIdsForSelectedItems(selectedItems));
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
