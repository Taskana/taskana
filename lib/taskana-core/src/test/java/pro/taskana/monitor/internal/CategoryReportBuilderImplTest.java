package pro.taskana.monitor.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.reports.CategoryReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.task.api.CustomField;
import pro.taskana.task.api.TaskState;

/** Unit Test for CategoryBuilderImpl. */
@ExtendWith(MockitoExtension.class)
class CategoryReportBuilderImplTest {

  @InjectMocks private MonitorServiceImpl cut;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfiguration;

  @Mock private MonitorMapper monitorMapperMock;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
  }

  @Test
  void testGetTotalNumbersOfCatgoryReport() throws Exception {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");

    List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("EXTERN");
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfCategories(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);

    final CategoryReport actualResult =
        cut.createCategoryReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .buildReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(internalTaskanaEngineMock, times(2)).getEngine();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(monitorMapperMock)
        .getTaskCountOfCategories(any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfiguration);

    assertThat(actualResult).isNotNull();
    assertThat(1).isEqualTo(actualResult.getRow("EXTERN").getTotalValue());
    assertThat(1).isEqualTo(actualResult.getSumRow().getTotalValue());
  }

  @Test
  void testGetCategoryReportWithReportLineItemDefinitions() throws Exception {
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
    monitorQueryItem.setKey("EXTERN");
    monitorQueryItem.setAgeInDays(0);
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfCategories(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter))
        .thenReturn(expectedResult);

    final CategoryReport actualResult =
        cut.createCategoryReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .buildReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(internalTaskanaEngineMock, times(2)).getEngine();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(monitorMapperMock)
        .getTaskCountOfCategories(any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfiguration);

    assertThat(actualResult).isNotNull();
    assertThat(1).isEqualTo(actualResult.getRow("EXTERN").getTotalValue());
    assertThat(1).isEqualTo(actualResult.getRow("EXTERN").getCells()[0]);
    assertThat(1).isEqualTo(actualResult.getSumRow().getTotalValue());
  }

  @Test
  void testListTaskIdsOfCategoryReportForSelectedItems() throws Exception {
    final List<String> workbasketIds =
        Collections.singletonList("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = Arrays.asList(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = Collections.singletonList("EXTERN");
    final List<String> domains = Collections.singletonList("DOMAIN_A");
    final List<String> classificationIds = Collections.singletonList("L10000");
    final List<String> excludedClassificationIds = Collections.singletonList("L20000");
    final Map<CustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(CustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<TimeIntervalColumnHeader> columnHeaders =
        Collections.singletonList(new TimeIntervalColumnHeader(0, 0));

    SelectedItem selectedItem = new SelectedItem();
    selectedItem.setKey("EXTERN");
    selectedItem.setLowerAgeLimit(1);
    selectedItem.setUpperAgeLimit(5);
    List<SelectedItem> selectedItems = Collections.singletonList(selectedItem);

    List<String> expectedResult =
        Collections.singletonList("TKI:000000000000000000000000000000000001");
    when(monitorMapperMock.getTaskIdsForSelectedItems(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter,
            "CLASSIFICATION_CATEGORY",
            selectedItems,
            false))
        .thenReturn(expectedResult);

    final List<String> actualResult =
        cut.createCategoryReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listTaskIdsForSelectedItems(selectedItems);

    verify(internalTaskanaEngineMock).openConnection();
    verify(internalTaskanaEngineMock, times(2)).getEngine();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(monitorMapperMock)
        .getTaskIdsForSelectedItems(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), eq(false));
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfiguration);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  void testListTaskIdsForSelectedItemsIsEmptyResult() throws Exception {
    SelectedItem selectedItem = new SelectedItem();
    List<SelectedItem> selectedItems = Collections.singletonList(selectedItem);
    List<String> result =
        cut.createCategoryReportBuilder().listTaskIdsForSelectedItems(selectedItems);
    assertThat(result).isNotNull();
  }

  @Test
  void testListCustomAttributeValuesForCustomAttributeName() throws Exception {
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
    when(monitorMapperMock.getCustomAttributeValuesForReport(
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
        cut.createCategoryReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_1);

    verify(internalTaskanaEngineMock).openConnection();
    verify(internalTaskanaEngineMock, times(2)).getEngine();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(monitorMapperMock)
        .getCustomAttributeValuesForReport(any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfiguration);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  void testListCustomAttributeValuesForCustomAttributeNameIsEmptyResult() throws Exception {
    List<String> result =
        cut.createCategoryReportBuilder()
            .workbasketIdIn(Collections.singletonList("DieGibtsSicherNed"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_1);
    assertThat(result).isNotNull();
  }
}
