package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** Acceptance test for all "get task ids of category report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskIdsOfTaskCustomFieldValueReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = taskanaEngine.getMonitorService();

  private static final SelectedItem GESCHAEFTSSTELLE_A =
      new SelectedItem("Geschaeftsstelle A", null, -5, -2);
  private static final SelectedItem GESCHAEFTSSTELLE_B =
      new SelectedItem("Geschaeftsstelle B", null, Integer.MIN_VALUE, -11);
  private static final SelectedItem GESCHAEFTSSTELLE_C =
      new SelectedItem("Geschaeftsstelle C", null, 0, 0);

  @Test
  void testRoleCheck() {
    ThrowingCallable call =
        () ->
            MONITOR_SERVICE
                .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
                .listTaskIdsForSelectedItems(Collections.emptyList(), TaskTimestamp.DUE);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @TestFactory
  Stream<DynamicTest> should_NotThrowError_When_buildReportForTaskState() {
    Iterator<TaskTimestamp> iterator = Arrays.stream(TaskTimestamp.values()).iterator();

    ThrowingConsumer<TaskTimestamp> test =
        timestamp -> {
          ThrowingCallable callable =
              () ->
                  MONITOR_SERVICE
                      .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
                      .listTaskIdsForSelectedItems(
                          Collections.singletonList(GESCHAEFTSSTELLE_A), timestamp);
          assertThatCode(callable).doesNotThrowAnyException();
        };

    return DynamicTest.stream(iterator, t -> "for " + t, test);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_selectCompletedItems_When_CompletedTimeStampIsRequested() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems =
        Collections
            .singletonList(new SelectedItem("Geschaeftsstelle A", null, Integer.MIN_VALUE, -5));

    List<String> ids =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.COMPLETED);

    assertThat(ids).containsExactly("TKI:000000000000000000000000000000000029");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReport() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems =
        Arrays.asList(GESCHAEFTSSTELLE_A, GESCHAEFTSSTELLE_B, GESCHAEFTSSTELLE_C);

    List<String> ids =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000002",
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000020",
            "TKI:000000000000000000000000000000000024",
            "TKI:000000000000000000000000000000000027",
            "TKI:000000000000000000000000000000000029",
            "TKI:000000000000000000000000000000000033");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithWorkbasketFilter() throws Exception {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems =
        Arrays.asList(GESCHAEFTSSTELLE_A, GESCHAEFTSSTELLE_B, GESCHAEFTSSTELLE_C);

    List<String> ids =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .workbasketIdIn(workbasketIds)
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000020");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithStateFilter() throws Exception {
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems =
        Arrays.asList(GESCHAEFTSSTELLE_A, GESCHAEFTSSTELLE_B, GESCHAEFTSSTELLE_C);

    List<String> ids =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .stateIn(Collections.singletonList(TaskState.READY))
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

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

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithCategoryFilter() throws Exception {
    final List<String> categories = Arrays.asList("AUTOMATIC", "MANUAL");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems =
        Arrays.asList(GESCHAEFTSSTELLE_A, GESCHAEFTSSTELLE_B, GESCHAEFTSSTELLE_C);

    List<String> ids =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .classificationCategoryIn(categories)
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids).hasSize(3);
    assertThat(ids.contains("TKI:000000000000000000000000000000000006")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000009")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000029")).isTrue();
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithDomainFilter() throws Exception {
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems =
        Arrays.asList(GESCHAEFTSSTELLE_A, GESCHAEFTSSTELLE_B, GESCHAEFTSSTELLE_C);

    List<String> ids =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .domainIn(Collections.singletonList("DOMAIN_A"))
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids).hasSize(3);
    assertThat(ids.contains("TKI:000000000000000000000000000000000009")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000020")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000033")).isTrue();
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfCustomFieldValueReportWithCustomFieldValueFilter() throws Exception {
    final Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems =
        Arrays.asList(GESCHAEFTSSTELLE_A, GESCHAEFTSSTELLE_B, GESCHAEFTSSTELLE_C);

    List<String> ids =
        MONITOR_SERVICE
            .createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .customAttributeFilterIn(customAttributeFilter)
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids).hasSize(4);
    assertThat(ids.contains("TKI:000000000000000000000000000000000020")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000024")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000027")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000029")).isTrue();
  }

  @WithAccessId(user = "monitor")
  @Test
  void testThrowsExceptionIfSubKeysAreUsed() {
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    final List<SelectedItem> selectedItems =
        Collections.singletonList(new SelectedItem("Geschaeftsstelle A", "INVALID", -5, -2));

    ThrowingCallable call =
        () ->
            MONITOR_SERVICE
                .createClassificationCategoryReportBuilder()
                .withColumnHeaders(columnHeaders)
                .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);
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
