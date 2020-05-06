package pro.taskana.monitor.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.task.api.CustomField;
import pro.taskana.task.api.TaskState;

/** Unit Test for WorkbasketReportBuilderImpl. */
@ExtendWith(MockitoExtension.class)
class WorkbasketReportBuilderImplTest {

  @InjectMocks private MonitorServiceImpl cut;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfiguration;

  @Mock private MonitorMapper monitorMapperMock;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaEngineConfiguration);
    when(taskanaEngineConfiguration.isGermanPublicHolidaysEnabled()).thenReturn(true);
    when(taskanaEngineConfiguration.getCustomHolidays()).thenReturn(null);
  }

  @Test
  void testGetTotalNumbersOfWorkbasketReportBasedOnDueDate()
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
    final List<CombinedClassificationFilter> combinedClassificationFilter =
        Collections.singletonList(
            new CombinedClassificationFilter(
                "CLI:000000000000000000000000000000000003",
                "CLI:000000000000000000000000000000000008"));

    List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfWorkbaskets(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter,
            combinedClassificationFilter))
        .thenReturn(expectedResult);

    final WorkbasketReport actualResult =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .combinedClassificationFilterIn(combinedClassificationFilter)
            .buildReport();

    verify(internalTaskanaEngineMock, times(1)).openConnection();
    verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
    verify(taskanaEngineMock, times(2)).getConfiguration();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
    verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
    verify(monitorMapperMock, times(1))
        .getTaskCountOfWorkbaskets(any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock, times(1)).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfiguration);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("WBI:000000000000000000000000000000000001").getTotalValue())
        .isEqualTo(1);
    assertThat(actualResult.getSumRow().getTotalValue()).isEqualTo(1);
  }

  @Test
  void testGetWorkbasketReportWithReportLineItemDefinitions()
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
    final List<CombinedClassificationFilter> combinedClassificationFilter =
        Collections.singletonList(
            new CombinedClassificationFilter(
                "CLI:000000000000000000000000000000000003",
                "CLI:000000000000000000000000000000000008"));
    final List<TimeIntervalColumnHeader> columnHeaders =
        Collections.singletonList(new TimeIntervalColumnHeader(0, 0));

    final List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
    monitorQueryItem.setAgeInDays(0);
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfWorkbaskets(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter,
            combinedClassificationFilter))
        .thenReturn(expectedResult);

    final WorkbasketReport actualResult =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .combinedClassificationFilterIn(combinedClassificationFilter)
            .withColumnHeaders(columnHeaders)
            .buildReport();

    verify(internalTaskanaEngineMock, times(1)).openConnection();
    verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
    verify(taskanaEngineMock, times(2)).getConfiguration();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
    verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
    verify(monitorMapperMock, times(1))
        .getTaskCountOfWorkbaskets(any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock, times(1)).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfiguration);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("WBI:000000000000000000000000000000000001").getTotalValue())
        .isEqualTo(1);
    assertThat(actualResult.getRow("WBI:000000000000000000000000000000000001").getCells()[0])
        .isEqualTo(1);
    assertThat(actualResult.getSumRow().getTotalValue()).isEqualTo(1);
  }

  @Test
  void testGetTaskIdsOfCategoryReportForSelectedItems()
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
            "WORKBASKET_KEY",
            selectedItems,
            false))
        .thenReturn(expectedResult);

    final List<String> actualResult =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listTaskIdsForSelectedItems(selectedItems);

    verify(internalTaskanaEngineMock, times(1)).openConnection();
    verify(taskanaEngineMock, times(1)).checkRoleMembership(any());
    verify(taskanaEngineMock, times(2)).getConfiguration();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
    verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
    verify(monitorMapperMock, times(1))
        .getTaskIdsForSelectedItems(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), eq(false));
    verify(internalTaskanaEngineMock, times(1)).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfiguration);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  void testListTaskIdsForSelectedItemsIsEmptyResult() {
    List<SelectedItem> selectedItems = new ArrayList<>();
    ThrowingCallable call =
        () -> {
          List<String> result =
              cut.createWorkbasketReportBuilder()
                  .workbasketIdIn(Arrays.asList("DieGibtsGarantiertNed"))
                  .listTaskIdsForSelectedItems(selectedItems);
          assertThat(result).isNotNull();
        };
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
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
        cut.createWorkbasketReportBuilder()
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
    verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
    verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
    verify(monitorMapperMock, times(1))
        .getCustomAttributeValuesForReport(any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock, times(1)).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfiguration);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  void testListCustomAttributeValuesForCustomAttributeNameIsEmptyResult()
      throws NotAuthorizedException {
    List<String> result =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(Arrays.asList("GibtsSicherNed"))
            .listCustomAttributeValuesForCustomAttributeName(CustomField.CUSTOM_14);
    assertThat(result).isNotNull();
  }

  @Test
  void testGetTotalNumbersOfWorkbasketReportBasedOnCreatedDate()
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
    final List<CombinedClassificationFilter> combinedClassificationFilter =
        Arrays.asList(
            new CombinedClassificationFilter(
                "CLI:000000000000000000000000000000000003",
                "CLI:000000000000000000000000000000000008"));

    List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfWorkbasketsBasedOnPlannedDate(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter,
            combinedClassificationFilter))
        .thenReturn(expectedResult);

    final WorkbasketReport actualResult =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .categoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .combinedClassificationFilterIn(combinedClassificationFilter)
            .buildPlannedDateBasedReport();

    verify(internalTaskanaEngineMock, times(1)).openConnection();
    verify(taskanaEngineMock, times(1)).checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    verify(taskanaEngineMock, times(2)).getConfiguration();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(taskanaEngineConfiguration, times(1)).isGermanPublicHolidaysEnabled();
    verify(taskanaEngineConfiguration, times(1)).getCustomHolidays();
    verify(monitorMapperMock, times(1))
        .getTaskCountOfWorkbasketsBasedOnPlannedDate(
            workbasketIds,
            states,
            categories,
            domains,
            classificationIds,
            excludedClassificationIds,
            customAttributeFilter,
            combinedClassificationFilter);
    verify(internalTaskanaEngineMock, times(1)).returnConnection();
    verifyNoMoreInteractions(
        internalTaskanaEngineMock,
        taskanaEngineMock,
        monitorMapperMock,
        taskanaEngineConfiguration);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("WBI:000000000000000000000000000000000001").getTotalValue())
        .isEqualTo(1);
    assertThat(actualResult.getSumRow().getTotalValue()).isEqualTo(1);
  }
}
