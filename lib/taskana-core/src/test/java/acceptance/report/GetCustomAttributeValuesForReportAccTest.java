package acceptance.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.CustomField;

/** Acceptance test for all "classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetCustomAttributeValuesForReportAccTest extends AbstractReportAccTest {

  @Test
  void testRoleCheck() {
    MonitorService monitorService = taskanaEngine.getTaskMonitorService();

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            monitorService
                .createWorkbasketReportBuilder()
                .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_2));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetCustomAttributeValuesForOneWorkbasket() throws NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getTaskMonitorService();

    List<String> values =
        monitorService
            .createWorkbasketReportBuilder()
            .workbasketIdIn(Collections.singletonList("WBI:000000000000000000000000000000000001"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_2);

    assertNotNull(values);
    assertEquals(2, values.size());
    assertTrue(values.contains("Vollkasko"));
    assertTrue(values.contains("Teilkasko"));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetCustomAttributeValuesForOneDomain() throws NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getTaskMonitorService();

    List<String> values =
        monitorService
            .createWorkbasketReportBuilder()
            .domainIn(Collections.singletonList("DOMAIN_A"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_16);
    assertNotNull(values);
    assertEquals(26, values.size());
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetCustomAttributeValuesForCustomAttribute() throws NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getTaskMonitorService();

    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_2, "Vollkasko");
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");

    List<String> values =
        monitorService
            .createCategoryReportBuilder()
            .customAttributeFilterIn(customAttributeFilter)
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_16);

    assertNotNull(values);
    assertEquals(12, values.size());
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetCustomAttributeValuesForExcludedClassifications() throws NotAuthorizedException {
    MonitorService monitorService = taskanaEngine.getTaskMonitorService();

    List<String> domains = new ArrayList<>();
    domains.add("DOMAIN_A");
    domains.add("DOMAIN_B");
    domains.add("DOMAIN_C");

    List<String> values =
        monitorService
            .createCategoryReportBuilder()
            .domainIn(domains)
            .excludedClassificationIdIn(
                Collections.singletonList("CLI:000000000000000000000000000000000003"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_16);

    assertNotNull(values);
    assertEquals(43, values.size());
  }
}
