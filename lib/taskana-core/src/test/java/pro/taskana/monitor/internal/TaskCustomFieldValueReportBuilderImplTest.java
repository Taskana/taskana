package pro.taskana.monitor.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.TaskCustomFieldValueReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** Unit Test for CustomFieldValueReportBuilderImpl. */
@ExtendWith(MockitoExtension.class)
class TaskCustomFieldValueReportBuilderImplTest {

  @InjectMocks private MonitorServiceImpl cut;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

  @Mock private MonitorMapper monitorMapperMock;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
  }

  @Test
  void testGetTotalNumbersOfCustomFieldValueReport() throws Exception {
    final List<String> workbasketIds = List.of("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = List.of(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = List.of("EXTERN");
    final List<String> domains = List.of("DOMAIN_A");
    final List<String> classificationIds = List.of("L10000");
    final List<String> excludedClassificationIds = List.of("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");

    final List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("Geschaeftsstelle A");
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfTaskCustomFieldValues(
            TaskCustomField.CUSTOM_1,
            workbasketIds,
            states,
            categories,
            domains,
            TaskTimestamp.DUE,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);

    final TaskCustomFieldValueReport actualResult =
        cut.createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .buildReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(internalTaskanaEngineMock, times(2)).getEngine();
    verify(monitorMapperMock)
        .getTaskCountOfTaskCustomFieldValues(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfigurationMock);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("Geschaeftsstelle A").getTotalValue()).isOne();
    assertThat(actualResult.getSumRow().getTotalValue()).isOne();
  }

  @Test
  void testGetCustomFieldValueReportWithReportLineItemDefinitions() throws Exception {
    final List<String> workbasketIds = List.of("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = List.of(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = List.of("EXTERN");
    final List<String> domains = List.of("DOMAIN_A");
    final List<String> classificationIds = List.of("L10000");
    final List<String> excludedClassificationIds = List.of("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders =
        List.of(new TimeIntervalColumnHeader(0, 0));

    final List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("Geschaeftsstelle A");
    monitorQueryItem.setAgeInDays(0);
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfTaskCustomFieldValues(
            TaskCustomField.CUSTOM_1,
            workbasketIds,
            states,
            categories,
            domains,
            TaskTimestamp.DUE,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);

    final TaskCustomFieldValueReport actualResult =
        cut.createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .buildReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(internalTaskanaEngineMock, times(2)).getEngine();
    verify(monitorMapperMock)
        .getTaskCountOfTaskCustomFieldValues(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfigurationMock);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("Geschaeftsstelle A").getTotalValue()).isOne();
    assertThat(actualResult.getRow("Geschaeftsstelle A").getCells()[0]).isOne();
    assertThat(actualResult.getSumRow().getTotalValue()).isOne();
  }

  @Test
  void testListCustomAttributeValuesForCustomAttributeName() throws Exception {
    final List<String> workbasketIds = List.of("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = List.of(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = List.of("EXTERN");
    final List<String> domains = List.of("DOMAIN_A");
    final List<String> classificationIds = List.of("L10000");
    final List<String> excludedClassificationIds = List.of("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders =
        List.of(new TimeIntervalColumnHeader(0, 0));

    List<String> expectedResult = List.of("Geschaeftsstelle A");
    when(monitorMapperMock.getCustomAttributeValuesForReport(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter,
            null,
            TaskCustomField.CUSTOM_1))
        .thenReturn(expectedResult);

    final List<String> actualResult =
        cut.createTaskCustomFieldValueReportBuilder(TaskCustomField.CUSTOM_1)
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_1);

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(internalTaskanaEngineMock, times(2)).getEngine();
    verify(monitorMapperMock)
        .getCustomAttributeValuesForReport(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfigurationMock);

    assertThat(actualResult).isEqualTo(expectedResult);
  }
}
