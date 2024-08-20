package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
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

/** Acceptance test for all "get task ids of classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskIdsOfClassificationReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = kadaiEngine.getMonitorService();

  private static final SelectedItem L_10000 = new SelectedItem("L10000", null, 0, 0);
  private static final SelectedItem L_10000_1 =
      new SelectedItem("L10000", null, Integer.MIN_VALUE, -11);
  private static final SelectedItem L_30000 =
      new SelectedItem("L30000", null, Integer.MIN_VALUE, -11);

  @Test
  void testRoleCheck() {
    List<SelectedItem> selectedItems = List.of(L_10000, L_10000_1, L_30000);

    ThrowingCallable call =
        () ->
            MONITOR_SERVICE
                .createClassificationReportBuilder()
                .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);
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
                      .createClassificationReportBuilder()
                      .listTaskIdsForSelectedItems(List.of(L_10000), timestamp);
          assertThatCode(callable).doesNotThrowAnyException();
        };

    return DynamicTest.stream(iterator, t -> "for " + t, test);
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_SelectCompletedItems_When_CompletedTimeStampIsRequested() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems = List.of(L_10000, L_10000_1, L_30000);

    List<String> ids =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.COMPLETED);

    assertThat(ids).containsExactlyInAnyOrder("TKI:000000000000000000000000000000000001");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfClassificationReport() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems = List.of(L_10000, L_10000_1, L_30000);

    List<String> ids =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000001",
            "TKI:000000000000000000000000000000000004",
            "TKI:000000000000000000000000000000000007",
            "TKI:000000000000000000000000000000000010",
            "TKI:000000000000000000000000000000000033",
            "TKI:000000000000000000000000000000000006");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfClassificationReportWithAttachments() throws Exception {
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems =
        List.of(
            new SelectedItem("L10000", "L11000", 0, 0),
            new SelectedItem("L10000", "L11000", Integer.MIN_VALUE, -11));

    List<String> ids =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000001", "TKI:000000000000000000000000000000000033");
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnTaskIdsOfDetailedClassificationReport_When_SomeSelectedItemsContainSubKey()
      throws Exception {
    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems =
        List.of(L_30000, new SelectedItem("L10000", "L11000", 0, 0));

    List<String> ids =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000006", "TKI:000000000000000000000000000000000033");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetTaskIdsOfClassificationReportWithDomainFilter() throws Exception {
    List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();
    List<SelectedItem> selectedItems = List.of(L_10000, L_10000_1, L_30000);
    List<String> domains = List.of("DOMAIN_B", "DOMAIN_C");

    List<String> ids =
        MONITOR_SERVICE
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .domainIn(domains)
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    assertThat(ids)
        .containsExactlyInAnyOrder(
            "TKI:000000000000000000000000000000000001",
            "TKI:000000000000000000000000000000000004",
            "TKI:000000000000000000000000000000000006");
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
