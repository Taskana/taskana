package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
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

/** Acceptance test for all "get task ids of classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetTaskIdsOfClassificationReportAccTest extends AbstractReportAccTest {

  @Test
  void testRoleCheck() {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("L10000");
    s1.setLowerAgeLimit(0);
    s1.setUpperAgeLimit(0);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("L10000");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("L30000");
    s3.setLowerAgeLimit(Integer.MIN_VALUE);
    s3.setUpperAgeLimit(-11);
    selectedItems.add(s3);

    ThrowingCallable call =
        () -> {
          monitorService
              .createClassificationReportBuilder()
              .listTaskIdsForSelectedItems(selectedItems);
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfClassificationReport()
      throws InvalidArgumentException, NotAuthorizedException {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("L10000");
    s1.setLowerAgeLimit(0);
    s1.setUpperAgeLimit(0);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("L10000");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("L30000");
    s3.setLowerAgeLimit(Integer.MIN_VALUE);
    s3.setUpperAgeLimit(-11);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids).hasSize(6);
    assertThat(ids.contains("TKI:000000000000000000000000000000000001")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000004")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000007")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000010")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000033")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000006")).isTrue();
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfClassificationReportWithAttachments()
      throws InvalidArgumentException, NotAuthorizedException {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("L10000");
    s1.setSubKey("L11000");
    s1.setLowerAgeLimit(0);
    s1.setUpperAgeLimit(0);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("L10000");
    s2.setSubKey("L11000");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("L30000");
    s3.setLowerAgeLimit(Integer.MIN_VALUE);
    s3.setUpperAgeLimit(-11);
    selectedItems.add(s3);

    List<String> ids =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids).hasSize(2);
    assertThat(ids.contains("TKI:000000000000000000000000000000000001")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000033")).isTrue();
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetTaskIdsOfClassificationReportWithDomainFilter()
      throws InvalidArgumentException, NotAuthorizedException {
    final MonitorService monitorService = taskanaEngine.getMonitorService();

    final List<TimeIntervalColumnHeader> columnHeaders = getListOfColumnHeaders();

    final List<SelectedItem> selectedItems = new ArrayList<>();

    SelectedItem s1 = new SelectedItem();
    s1.setKey("L10000");
    s1.setLowerAgeLimit(0);
    s1.setUpperAgeLimit(0);
    selectedItems.add(s1);

    SelectedItem s2 = new SelectedItem();
    s2.setKey("L10000");
    s2.setLowerAgeLimit(Integer.MIN_VALUE);
    s2.setUpperAgeLimit(-11);
    selectedItems.add(s2);

    SelectedItem s3 = new SelectedItem();
    s3.setKey("L30000");
    s3.setLowerAgeLimit(Integer.MIN_VALUE);
    s3.setUpperAgeLimit(-11);
    selectedItems.add(s3);

    List<String> domains = new ArrayList<>();
    domains.add("DOMAIN_B");
    domains.add("DOMAIN_C");

    List<String> ids =
        monitorService
            .createClassificationReportBuilder()
            .withColumnHeaders(columnHeaders)
            .inWorkingDays()
            .domainIn(domains)
            .listTaskIdsForSelectedItems(selectedItems);

    assertThat(ids).hasSize(3);
    assertThat(ids.contains("TKI:000000000000000000000000000000000001")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000004")).isTrue();
    assertThat(ids.contains("TKI:000000000000000000000000000000000006")).isTrue();
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
