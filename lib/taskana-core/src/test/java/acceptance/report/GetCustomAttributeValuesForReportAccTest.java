package acceptance.report;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.task.api.TaskCustomField;

/** Acceptance test for all "classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetCustomAttributeValuesForReportAccTest extends AbstractReportAccTest {

  private static final MonitorService MONITOR_SERVICE = taskanaEngine.getMonitorService();

  @Test
  void testRoleCheck() {
    ThrowingCallable call =
        () ->
            MONITOR_SERVICE
                .createWorkbasketReportBuilder()
                .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_2);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForOneWorkbasket() throws Exception {
    List<String> values =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .workbasketIdIn(Collections.singletonList("WBI:000000000000000000000000000000000001"))
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_2);

    assertThat(values).containsExactlyInAnyOrder("Vollkasko", "Teilkasko");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForOneDomain() throws Exception {
    List<String> values =
        MONITOR_SERVICE
            .createWorkbasketReportBuilder()
            .domainIn(Collections.singletonList("DOMAIN_A"))
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_16);
    assertThat(values).hasSize(26);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForCustomAttribute() throws Exception {
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_2, "Vollkasko");
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");

    List<String> values =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .customAttributeFilterIn(customAttributeFilter)
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_16);

    assertThat(values).hasSize(12);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForExcludedClassifications() throws Exception {
    List<String> domains = Arrays.asList("DOMAIN_A", "DOMAIN_B", "DOMAIN_C");

    List<String> values =
        MONITOR_SERVICE
            .createClassificationCategoryReportBuilder()
            .domainIn(domains)
            .excludedClassificationIdIn(
                Collections.singletonList("CLI:000000000000000000000000000000000003"))
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_16);

    assertThat(values).hasSize(43);
  }
}
