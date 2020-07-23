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
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "get task ids of category report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskIdsOfClassificationCategoryReportAccTest extends AbstractReportAccTest {

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<SelectedItem> selectedItems = new ArrayList<>();
    ThrowingCallable call =
        () -> {
          monitorService
              .createClassificationCategoryReportBuilder()
              .listTaskIdsForSelectedItems(selectedItems);
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReport() throws Exception {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

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
        monitorService
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids)
        .containsOnly(
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000020",
            "TKI:000000000000000000000000000000000021",
            "TKI:000000000000000000000000000000000022",
            "TKI:000000000000000000000000000000000023",
            "TKI:000000000000000000000000000000000024",
            "TKI:000000000000000000000000000000000026",
            "TKI:000000000000000000000000000000000027",
            "TKI:000000000000000000000000000000000028",
            "TKI:000000000000000000000000000000000031",
            "TKI:000000000000000000000000000000000032");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithWorkbasketFilter() throws Exception {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

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
        monitorService
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .workbasketIdIn(workbasketIds)
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids)
        .containsOnly(
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000020",
            "TKI:000000000000000000000000000000000026",
            "TKI:000000000000000000000000000000000031");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithStateFilter() throws Exception {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

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
        monitorService
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .stateIn(states)
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids)
        .containsOnly(
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000020",
            "TKI:000000000000000000000000000000000021",
            "TKI:000000000000000000000000000000000022",
            "TKI:000000000000000000000000000000000023",
            "TKI:000000000000000000000000000000000024",
            "TKI:000000000000000000000000000000000026",
            "TKI:000000000000000000000000000000000027",
            "TKI:000000000000000000000000000000000028",
            "TKI:000000000000000000000000000000000031",
            "TKI:000000000000000000000000000000000032");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithCategoryFilter() throws Exception {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

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
        monitorService
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .classificationCategoryIn(categories)
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids)
        .containsOnly(
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000031",
            "TKI:000000000000000000000000000000000032");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithDomainFilter() throws Exception {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

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
        monitorService
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .domainIn(domains)
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids)
        .containsOnly(
            "TKI:000000000000000000000000000000000020",
            "TKI:000000000000000000000000000000000021",
            "TKI:000000000000000000000000000000000022",
            "TKI:000000000000000000000000000000000028");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCategoryReportWithCustomFieldValueFilter() throws Exception {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
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
        monitorService
            .createClassificationCategoryReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .customAttributeFilterIn(customAttributeFilter)
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids)
        .containsOnly(
            "TKI:000000000000000000000000000000000020",
            "TKI:000000000000000000000000000000000024",
            "TKI:000000000000000000000000000000000027",
            "TKI:000000000000000000000000000000000031",
            "TKI:000000000000000000000000000000000032");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testThrowsExceptionIfSubKeysAreUsed() {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("EXTERN");
    s1.setSubKey("INVALID");
    s1.setLowerAgeLimit(-5);
    s1.setUpperAgeLimit(-2);
    selectedItems.add(s1);

    ThrowingCallable call =
        () -> {
          monitorService
              .createClassificationCategoryReportBuilder()
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
