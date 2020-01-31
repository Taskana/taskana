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

import pro.taskana.common.api.CustomField;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.report.api.TaskMonitorService;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "classification report" scenarios. */
@ExtendWith(JaasExtension.class)
class GetCustomAttributeValuesForReportAccTest extends AbstractReportAccTest {

  @Test
  void testRoleCheck() {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    Assertions.assertThrows(
        NotAuthorizedException.class,
        () ->
            taskMonitorService
                .createWorkbasketReportBuilder()
                .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_2));
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetCustomAttributeValuesForOneWorkbasket() throws NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<String> values =
        taskMonitorService
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
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<String> values =
        taskMonitorService
            .createWorkbasketReportBuilder()
            .domainIn(Collections.singletonList("DOMAIN_A"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_16);
    assertNotNull(values);
    assertEquals(26, values.size());
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetCustomAttributeValuesForCustomAttribute() throws NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_2, "Vollkasko");
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");

    List<String> values =
        taskMonitorService
            .createCategoryReportBuilder()
            .customAttributeFilterIn(customAttributeFilter)
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_16);

    assertNotNull(values);
    assertEquals(12, values.size());
  }

  @WithAccessId(userName = "monitor")
  @Test
  void testGetCustomAttributeValuesForExcludedClassifications() throws NotAuthorizedException {
    TaskMonitorService taskMonitorService = taskanaEngine.getTaskMonitorService();

    List<String> domains = new ArrayList<>();
    domains.add("DOMAIN_A");
    domains.add("DOMAIN_B");
    domains.add("DOMAIN_C");

    List<String> values =
        taskMonitorService
            .createCategoryReportBuilder()
            .domainIn(domains)
            .excludedClassificationIdIn(
                Collections.singletonList("CLI:000000000000000000000000000000000003"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_16);

    assertNotNull(values);
    assertEquals(43, values.size());
  }
}
