package pro.taskana.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.CustomFieldValueReport;

/** Unit Test for CustomFieldValueReportBuilderImpl. */
@ExtendWith(MockitoExtension.class)
class CustomFieldValueReportBuilderImplTest {

  @InjectMocks private TaskMonitorServiceImpl cut;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

  @Mock private TaskMonitorMapper taskMonitorMapperMock;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfigurationMock);
    when(taskanaEngineConfigurationMock.isGermanPublicHolidaysEnabled()).thenReturn(true);
    when(taskanaEngineConfigurationMock.getCustomHolidays()).thenReturn(null);
  }

  @Test
  void testGetTotalNumbersOfCustomFieldValueReport()
      throws InvalidArgumentException, NotAuthorizedException {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");

    final List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("Geschaeftsstelle A");
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(taskMonitorMapperMock.getTaskCountOfCustomFieldValues(
            CustomField.CUSTOM_1,
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);

    final CustomFieldValueReport actualResult =
        cut.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .buildReport();

    verify(internalTaskanaEngineMock, times(1)).openConnection();
    verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
    verify(taskanaEngineMock, times(2)).getConfiguration();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(taskanaEngineConfigurationMock, times(1)).isGermanPublicHolidaysEnabled();
    verify(taskanaEngineConfigurationMock, times(1)).getCustomHolidays();
    verify(taskMonitorMapperMock, times(1))
        .getTaskCountOfCustomFieldValues(any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock, times(1)).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        taskMonitorMapperMock,
        taskanaEngineConfigurationMock);

    assertNotNull(actualResult);
    assertEquals(actualResult.getRow("Geschaeftsstelle A").getTotalValue(), 1);
    assertEquals(actualResult.getSumRow().getTotalValue(), 1);
  }

  @Test
  void testGetCustomFieldValueReportWithReportLineItemDefinitions()
      throws InvalidArgumentException, NotAuthorizedException {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders =
        Collections.singletonList(new TimeIntervalColumnHeader(0, 0));

    final List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("Geschaeftsstelle A");
    monitorQueryItem.setAgeInDays(0);
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(taskMonitorMapperMock.getTaskCountOfCustomFieldValues(
            CustomField.CUSTOM_1,
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);

    final CustomFieldValueReport actualResult =
        cut.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .buildReport();

    verify(internalTaskanaEngineMock, times(1)).openConnection();
    verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
    verify(taskanaEngineMock, times(2)).getConfiguration();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(taskanaEngineConfigurationMock, times(1)).isGermanPublicHolidaysEnabled();
    verify(taskanaEngineConfigurationMock, times(1)).getCustomHolidays();
    verify(taskMonitorMapperMock, times(1))
        .getTaskCountOfCustomFieldValues(any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock, times(1)).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        taskMonitorMapperMock,
        taskanaEngineConfigurationMock);

    assertNotNull(actualResult);
    assertEquals(actualResult.getRow("Geschaeftsstelle A").getTotalValue(), 1);
    assertEquals(actualResult.getRow("Geschaeftsstelle A").getCells()[0], 1);
    assertEquals(actualResult.getSumRow().getTotalValue(), 1);
  }

  @Test
  void testListCustomAttributeValuesForCustomAttributeName() throws NotAuthorizedException {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders =
        Collections.singletonList(new TimeIntervalColumnHeader(0, 0));

    SelectedItem selectedItem = new SelectedItem();
    selectedItem.setKey("EXTERN");
    selectedItem.setLowerAgeLimit(1);
    selectedItem.setUpperAgeLimit(5);

    List<String> expectedResult = Collections.singletonList("Geschaeftsstelle A");
    when(taskMonitorMapperMock.getCustomAttributeValuesForReport(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter,
            CustomField.CUSTOM_1))
        .thenReturn(expectedResult);

    final List<String> actualResult =
        cut.createCustomFieldValueReportBuilder(CustomField.CUSTOM_1)
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_1);

    verify(internalTaskanaEngineMock, times(1)).openConnection();
    verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
    verify(taskanaEngineMock, times(2)).getConfiguration();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(taskanaEngineConfigurationMock, times(1)).isGermanPublicHolidaysEnabled();
    verify(taskanaEngineConfigurationMock, times(1)).getCustomHolidays();
    verify(taskMonitorMapperMock, times(1))
        .getCustomAttributeValuesForReport(any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock, times(1)).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        taskMonitorMapperMock,
        taskanaEngineConfigurationMock);

    assertNotNull(actualResult);
    assertEquals(expectedResult, actualResult);
  }
}
