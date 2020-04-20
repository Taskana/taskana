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

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.CustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "get task ids of category report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskIdsOfCustomFieldValueReportAccTest extends AbstractReportAccTest {

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<SelectedItem> selectedItems = new ArrayList<>();

    ThrowingCallable call =
        () -> {
          monitorService
              .createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
              .listTaskIdsForSelectedItems(selectedItems);
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReport()
      throws InvalidArgumentException, NotAuthorizedException {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("Geschaeftsstelle A");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("Geschaeftsstelle B");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("Geschaeftsstelle C");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids)
        .containsOnly(
            "TKI:000000000000000000000000000000000002",
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000020",
            "TKI:000000000000000000000000000000000024",
            "TKI:000000000000000000000000000000000027",
            "TKI:000000000000000000000000000000000029",
            "TKI:000000000000000000000000000000000033");
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithWorkbasketFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("Geschaeftsstelle A");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("Geschaeftsstelle B");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("Geschaeftsstelle C");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .workbasketIdIn(workbasketIds)
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids).hasSize(3);
    assertThat(ids.contains("TKI:000000000000000000000000000000000006")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000009")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000020")).isTrue();
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithStateFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("Geschaeftsstelle A");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("Geschaeftsstelle B");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("Geschaeftsstelle C");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .stateIn(Collections.singletonList(TaskState.READY))
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids).hasSize(8);
    assertThat(ids.contains("TKI:000000000000000000000000000000000002")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000006")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000009")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000020")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000024")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000027")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000029")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000033")).isTrue();
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithCategoryFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("Geschaeftsstelle A");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("Geschaeftsstelle B");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("Geschaeftsstelle C");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .categoryIn(categories)
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids).hasSize(3);
    assertThat(ids.contains("TKI:000000000000000000000000000000000006")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000009")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000029")).isTrue();
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithDomainFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("Geschaeftsstelle A");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("Geschaeftsstelle B");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("Geschaeftsstelle C");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .domainIn(Collections.singletonList("DOMAIN_A"))
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids).hasSize(3);
    assertThat(ids.contains("TKI:000000000000000000000000000000000009")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000020")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000033")).isTrue();
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithCustomFieldValueFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("Geschaeftsstelle A");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("Geschaeftsstelle B");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("Geschaeftsstelle C");
    s3.setLowerAgeLimit(0);
    s3.setUpperAgeLimit(0);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .customAttributeFilterIn(customAttributeFilter)
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids).hasSize(4);
    assertThat(ids.contains("TKI:000000000000000000000000000000000020")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000024")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000027")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000029")).isTrue();
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testThrowsExceptionIfSubKeysAreUsed() {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("Geschaeftsstelle A");
    s1.setSubKey("INVALID");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    ThrowingCallable call =
        () -> {
          monitorService
              .createCategoryReportBuilder()
              .withColumnHeaders(columnHeaders)
              .listTaskIdsForSelectedItems(selectedItems);
        };
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
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
