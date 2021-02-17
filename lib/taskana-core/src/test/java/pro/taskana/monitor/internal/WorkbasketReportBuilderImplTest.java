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

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** Unit Test for WorkbasketReportBuilderImpl. */
@ExtendWith(MockitoExtension.class)
class WorkbasketReportBuilderImplTest {

  @InjectMocks private MonitorServiceImpl cut;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private MonitorMapper monitorMapperMock;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
  }

  @Test
  void testGetTotalNumbersOfWorkbasketReportBasedOnDueDate() throws Exception {
    final List<String> workbasketIds = List.of("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = List.of(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = List.of("EXTERN");
    final List<String> domains = List.of("DOMAIN_A");
    final List<String> classificationIds = List.of("L10000");
    final List<String> excludedClassificationIds = List.of("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<CombinedClassificationFilter> combinedClassificationFilter =
        List.of(
            new CombinedClassificationFilter(
                "CLI:000000000000000000000000000000000003",
                "CLI:000000000000000000000000000000000008"));

    List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfWorkbaskets(
            any(),
            eq(workbasketIds),
            eq(states),
            eq(categories),
            eq(domains),
            eq(TaskTimestamp.DUE),
            eq(classificationIds),
            eq(excludedClassificationIds),
            eq(customAttributeFilter),
            eq(combinedClassificationFilter)))
        .thenReturn(expectedResult);
    when(internalTaskanaEngineMock.runAsAdmin(any())).thenReturn(Map.of());

    final WorkbasketReport actualResult =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .combinedClassificationFilterIn(combinedClassificationFilter)
            .buildReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(monitorMapperMock)
        .getTaskCountOfWorkbaskets(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verify(internalTaskanaEngineMock).runAsAdmin(any());
    verify(taskanaEngineMock).getWorkbasketService();
    verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, monitorMapperMock);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("WBI:000000000000000000000000000000000001").getTotalValue())
        .isOne();
    assertThat(actualResult.getSumRow().getTotalValue()).isOne();
  }

  @Test
  void testGetWorkbasketReportWithReportLineItemDefinitions() throws Exception {
    final List<String> workbasketIds = List.of("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = List.of(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = List.of("EXTERN");
    final List<String> domains = List.of("DOMAIN_A");
    final List<String> classificationIds = List.of("L10000");
    final List<String> excludedClassificationIds = List.of("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<CombinedClassificationFilter> combinedClassificationFilter =
        List.of(
            new CombinedClassificationFilter(
                "CLI:000000000000000000000000000000000003",
                "CLI:000000000000000000000000000000000008"));
    final List<TimeIntervalColumnHeader> columnHeaders =
        List.of(new TimeIntervalColumnHeader(0, 0));

    final List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
    monitorQueryItem.setAgeInDays(0);
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfWorkbaskets(
            any(),
            eq(workbasketIds),
            eq(states),
            eq(categories),
            eq(domains),
            eq(TaskTimestamp.DUE),
            eq(classificationIds),
            eq(excludedClassificationIds),
            eq(customAttributeFilter),
            eq(combinedClassificationFilter)))
        .thenReturn(expectedResult);
    when(internalTaskanaEngineMock.runAsAdmin(any())).thenReturn(Map.of());

    final WorkbasketReport actualResult =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .combinedClassificationFilterIn(combinedClassificationFilter)
            .withColumnHeaders(columnHeaders)
            .buildReport();

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(monitorMapperMock)
        .getTaskCountOfWorkbaskets(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verify(taskanaEngineMock).getWorkbasketService();
    verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, monitorMapperMock);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("WBI:000000000000000000000000000000000001").getTotalValue())
        .isEqualTo(1);
    assertThat(actualResult.getRow("WBI:000000000000000000000000000000000001").getCells()[0])
        .isEqualTo(1);
    assertThat(actualResult.getSumRow().getTotalValue()).isEqualTo(1);
  }

  @Test
  void testGetTaskIdsOfCategoryReportForSelectedItems() throws Exception {
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

    List<SelectedItem> selectedItems = List.of(new SelectedItem("EXTERN", null, 1, 5));

    List<String> expectedResult = List.of("TKI:000000000000000000000000000000000001");
    when(monitorMapperMock.getTaskIdsForSelectedItems(
            any(),
            eq(workbasketIds),
            eq(states),
            eq(categories),
            eq(domains),
            eq(classificationIds),
            eq(excludedClassificationIds),
            eq(customAttributeFilter),
            eq(null),
            eq("WORKBASKET_KEY"),
            eq(TaskTimestamp.DUE),
            eq(selectedItems),
            eq(false)))
        .thenReturn(expectedResult);

    final List<String> actualResult =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .withColumnHeaders(columnHeaders)
            .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(any());
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(monitorMapperMock)
        .getTaskIdsForSelectedItems(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            eq(TaskTimestamp.DUE),
            any(),
            eq(false));
    verify(internalTaskanaEngineMock).returnConnection();
    verify(taskanaEngineMock).getWorkbasketService();
    verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, monitorMapperMock);

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
                  .workbasketIdIn(List.of("DieGibtsGarantiertNed"))
                  .listTaskIdsForSelectedItems(selectedItems, TaskTimestamp.DUE);
          assertThat(result).isNotNull();
        };
    assertThatThrownBy(call).isInstanceOf(InvalidArgumentException.class);
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
        cut.createWorkbasketReportBuilder()
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
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(monitorMapperMock)
        .getCustomAttributeValuesForReport(
            any(), any(), any(), any(), any(), any(), any(), any(), any());
    verify(internalTaskanaEngineMock).returnConnection();
    verify(taskanaEngineMock).getWorkbasketService();
    verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, monitorMapperMock);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult).isEqualTo(expectedResult);
  }

  @Test
  void testListCustomAttributeValuesForCustomAttributeNameIsEmptyResult() throws Exception {
    List<String> result =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(List.of("GibtsSicherNed"))
            .listCustomAttributeValuesForCustomAttributeName(TaskCustomField.CUSTOM_14);
    assertThat(result).isNotNull();
  }

  @Test
  void testGetTotalNumbersOfWorkbasketReportBasedOnCreatedDate() throws Exception {
    final List<String> workbasketIds = List.of("WBI:000000000000000000000000000000000001");
    final List<TaskState> states = List.of(TaskState.CLAIMED, TaskState.READY);
    final List<String> categories = List.of("EXTERN");
    final List<String> domains = List.of("DOMAIN_A");
    final List<String> classificationIds = List.of("L10000");
    final List<String> excludedClassificationIds = List.of("L20000");
    Map<TaskCustomField, String> customAttributeFilter = new HashMap<>();
    customAttributeFilter.put(TaskCustomField.CUSTOM_1, "Geschaeftsstelle A");
    final List<CombinedClassificationFilter> combinedClassificationFilter =
        List.of(
            new CombinedClassificationFilter(
                "CLI:000000000000000000000000000000000003",
                "CLI:000000000000000000000000000000000008"));

    List<MonitorQueryItem> expectedResult = new ArrayList<>();
    MonitorQueryItem monitorQueryItem = new MonitorQueryItem();
    monitorQueryItem.setKey("WBI:000000000000000000000000000000000001");
    monitorQueryItem.setNumberOfTasks(1);
    expectedResult.add(monitorQueryItem);
    when(monitorMapperMock.getTaskCountOfWorkbaskets(
            any(),
            eq(workbasketIds),
            eq(states),
            eq(categories),
            eq(domains),
            eq(TaskTimestamp.PLANNED),
            eq(classificationIds),
            eq(excludedClassificationIds),
            eq(customAttributeFilter),
            eq(combinedClassificationFilter)))
        .thenReturn(expectedResult);

    when(internalTaskanaEngineMock.runAsAdmin(any())).thenReturn(Map.of());

    final WorkbasketReport actualResult =
        cut.createWorkbasketReportBuilder()
            .workbasketIdIn(workbasketIds)
            .stateIn(states)
            .classificationCategoryIn(categories)
            .domainIn(domains)
            .classificationIdIn(classificationIds)
            .excludedClassificationIdIn(excludedClassificationIds)
            .customAttributeFilterIn(customAttributeFilter)
            .combinedClassificationFilterIn(combinedClassificationFilter)
            .buildReport(TaskTimestamp.PLANNED);

    verify(internalTaskanaEngineMock).openConnection();
    verify(taskanaEngineMock).checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    verify(taskanaEngineMock).getWorkingDaysToDaysConverter();
    verify(internalTaskanaEngineMock, times(3)).getEngine();
    verify(monitorMapperMock)
        .getTaskCountOfWorkbaskets(
            any(),
            eq(workbasketIds),
            eq(states),
            eq(categories),
            eq(domains),
            eq(TaskTimestamp.PLANNED),
            eq(classificationIds),
            eq(excludedClassificationIds),
            eq(customAttributeFilter),
            eq(combinedClassificationFilter));
    verify(internalTaskanaEngineMock).returnConnection();
    verify(taskanaEngineMock).getWorkbasketService();
    verifyNoMoreInteractions(internalTaskanaEngineMock, taskanaEngineMock, monitorMapperMock);

    assertThat(actualResult).isNotNull();
    assertThat(actualResult.getRow("WBI:000000000000000000000000000000000001").getTotalValue())
        .isEqualTo(1);
    assertThat(actualResult.getSumRow().getTotalValue()).isEqualTo(1);
  }
}
