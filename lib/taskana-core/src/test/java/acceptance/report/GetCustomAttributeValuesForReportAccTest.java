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
import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.task.api.CustomField;

/** Acceptance test for all "classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetCustomAttributeValuesForReportAccTest extends AbstractReportAccTest {

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getMonitorService();
    ThrowingCallable call =
        () -> {
          monitorService
              .createWorkbasketReportBuilder()
              .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_2);
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForOneWorkbasket() throws NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> values =
        monitorService
            .createWorkbasketReportBuilder()
            .workbasketIdIn(Collections.singletonList("WBI:000000000000000000000000000000000001"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_2);

    assertThat(values).containsOnly("Vollkasko", "Teilkasko");
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForOneDomain() throws NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> values =
        monitorService
            .createWorkbasketReportBuilder()
            .domainIn(Collections.singletonList("DOMAIN_A"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_16);
    assertThat(values).hasSize(26);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForCustomAttribute() throws NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_2, "Vollkasko");
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");

    List<String> values =
        monitorService
            .createCategoryReportBuilder()
            .customAttributeFilterIn(customAttributeFilter)
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_16);

    assertThat(values).hasSize(12);
  }

  @WithAccessId(user = "monitor")
  @Test
  void testGetCustomAttributeValuesForExcludedClassifications() throws NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getMonitorService();

    List<String> domains = Arrays.asList("DOMAIN_A", "DOMAIN_B", "DOMAIN_C");

    List<String> values =
        monitorService
            .createCategoryReportBuilder()
            .domainIn(domains)
            .excludedClassificationIdIn(
                Collections.singletonList("CLI:000000000000000000000000000000000003"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_16);

    assertThat(values).hasSize(43);
  }
}
