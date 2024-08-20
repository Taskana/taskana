package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.monitor.api.CombinedClassificationFilter;
import io.kadai.monitor.api.MonitorService;
import io.kadai.monitor.api.SelectedItem;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

/** Acceptance test for all "get task ids of workbasket report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskIdsOfWorkbasketReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = kadaiEngine.getMonitorService();

  private static final SelectedItem S_1 = new SelectedItem("USER-1-1", null, 0, 0);
  private static final SelectedItem S_2 =
      new SelectedItem("USER-1-1", null, Integer.MIN_VALUE, -11);
  private static final SelectedItem S_3 =
      new SelectedItem("USER-1-2", null, 1000, Integer.MAX_VALUE);

  @Test
  void testRoleCheck() {
    ThrowingCallable call =
        () ->
            MONITOR_SERVICE
                .createWorkbasketReportBuilder()
                .listTaskIdsForSelectedItems(List.of(), TaskTimestamp.DUE);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @TestFactory
  Stream<DynamicTest> should_NotThrowError_When_BuildReportForTaskState() {
    Iterator<TaskTimestamp> iterator = Arrays.stream(TaskTimestamp.values()).iterator();

    ThrowingConsumer<TaskTimestamp> test =
        timestamp -> {
          ThrowingCallable callable =
              () ->
                  MONITOR_SERVICE
                      .createWorkbasketReportBuilder()
                      .listTaskIdsForSelectedItems(List.of(S_1), timestamp);
          assertThatCode(callable).doesNotThrowAnyException();
        };

    return DynamicTest.stream(iterator, t -> "for " + t, test);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_SelectCompletedItems_When_CompletedTimeStampIsRequested() throws Exception {
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    final List<SelectedItem> selectedItems = List.of(S_1, S_2, S_3);

    List<String> ids =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.COMPLETED);

    assertThat(ids).containsExactlyInAnyOrder("TKI:000000000000000000000000000000000001");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfWorkbasketReport() throws Exception {
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    final List<SelectedItem> selectedItems = List.of(S_1, S_2, S_3);

    List<String> ids =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000001",
            "TKI:000000000000000000000000000000000004",
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000010",
            "TKI:000000000000000000000000000000000031",
            "TKI:000000000000000000000000000000000050",
            "TKI:100000000000000000000000000000000050",
            "TKI:200000000000000000000000000000000050");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfWorkbasketReportWithExcludedClassifications() throws Exception {
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    final List<SelectedItem> selectedItems = List.of(S_1, S_2, S_3);

    List<String> ids =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .excludedClassificationIdIn(List.of("CLI:000000000000000000000000000000000001"))
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000006",
            "TKI:000000000000000000000000000000000009",
            "TKI:000000000000000000000000000000000031",
            "TKI:000000000000000000000000000000000050",
            "TKI:100000000000000000000000000000000050",
            "TKI:200000000000000000000000000000000050");
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnTaskIdsOfWorkbasketReport_When_CombinedClassificationFilterIsUsed()
      throws Exception {
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    final List<SelectedItem> selectedItems =
        List.of(new SelectedItem("USER-1-1", null, Integer.MIN_VALUE, Integer.MAX_VALUE));
    final List<CombinedClassificationFilter> combinedClassificationFilters =
        List.of(
            new CombinedClassificationFilter(
                "CLI:000000000000000000000000000000000003",
                "CLI:000000000000000000000000000000000008"),
            new CombinedClassificationFilter(
                "CLI:000000000000000000000000000000000001",
                "CLI:000000000000000000000000000000000006"));

    List<String> ids =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .combinedClassificationFilterIn(combinedClassificationFilters)
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000001", // from second filter
            "TKI:000000000000000000000000000000000013", // from second filter
            "TKI:000000000000000000000000000000000025", // from first filter
            "TKI:000000000000000000000000000000000036", // from first filter
            "TKI:000000000000000000000000000000000044"); // from first filter
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
