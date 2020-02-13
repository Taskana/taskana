package pro.taskana.task.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.MonitorServiceImpl;
import pro.taskana.task.api.TaskState;

/** Unit Test for TaskStatusReportBuilderImpl. */
@ExtendWith(MockitoExtension.class)
class TaskStatusReportBuilderImplTest {

  @InjectMocks private MonitorServiceImpl cut;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngine taskanaEngineMock;

  @Mock private MonitorMapper monitorMapperMock;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
  }

  @Test
  void testGetTaskStateReportWithoutFilters()
      throws NotAuthorizedException, InvalidArgumentException {
    // given
    TaskQueryItem queryItem1 = new TaskQueryItem();
    queryItem1.setCount(50);
    queryItem1.setState(TaskState.READY);
    queryItem1.setDomain("DOMAIN_X");
    TaskQueryItem queryItem2 = new TaskQueryItem();
    queryItem2.setCount(30);
    queryItem2.setState(TaskState.COMPLETED);
    queryItem2.setDomain("DOMAIN_X");
    List<TaskQueryItem> queryItems = Arrays.asList(queryItem1, queryItem2);
    when(monitorMapperMock.getTasksCountByState(null, null)).thenReturn(queryItems);

    // when
    final TaskStatusReport report = cut.createTaskStatusReportBuilder().buildReport();

    // then
    InOrder inOrder = inOrder(taskanaEngineMock, internalTaskanaEngineMock, monitorMapperMock);
    inOrder.verify(internalTaskanaEngineMock).getEngine();
    inOrder.verify(taskanaEngineMock).checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    inOrder.verify(internalTaskanaEngineMock).openConnection();
    inOrder.verify(monitorMapperMock).getTasksCountByState(eq(null), eq(null));
    inOrder.verify(internalTaskanaEngineMock).returnConnection();
    inOrder.verifyNoMoreInteractions();
    verifyNoMoreInteractions(taskanaEngineMock, internalTaskanaEngineMock, monitorMapperMock);

    assertNotNull(report);
    assertEquals(1, report.rowSize());
    assertArrayEquals(new int[] {50, 0, 30}, report.getRow("DOMAIN_X").getCells());
    assertArrayEquals(new int[] {50, 0, 30}, report.getSumRow().getCells());
    assertEquals(80, report.getRow("DOMAIN_X").getTotalValue());
    assertEquals(80, report.getSumRow().getTotalValue());
  }

  @Test
  void testGetTotalNumberOfTaskStateReport()
      throws NotAuthorizedException, InvalidArgumentException {
    // given
    TaskQueryItem queryItem1 = new TaskQueryItem();
    queryItem1.setCount(50);
    queryItem1.setState(TaskState.READY);
    queryItem1.setDomain("DOMAIN_X");
    TaskQueryItem queryItem2 = new TaskQueryItem();
    queryItem2.setCount(30);
    queryItem2.setState(TaskState.COMPLETED);
    queryItem2.setDomain("DOMAIN_X");
    List<TaskQueryItem> queryItems = Arrays.asList(queryItem1, queryItem2);
    when(monitorMapperMock.getTasksCountByState(eq(null), eq(Collections.emptyList())))
        .thenReturn(queryItems);

    // when
    final TaskStatusReport report =
        cut.createTaskStatusReportBuilder().stateIn(Collections.emptyList()).buildReport();

    // then
    InOrder inOrder = inOrder(taskanaEngineMock, monitorMapperMock, internalTaskanaEngineMock);
    inOrder.verify(internalTaskanaEngineMock).getEngine();
    inOrder.verify(taskanaEngineMock).checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    inOrder.verify(internalTaskanaEngineMock).openConnection();
    inOrder.verify(monitorMapperMock).getTasksCountByState(eq(null), eq(Collections.emptyList()));
    inOrder.verify(internalTaskanaEngineMock).returnConnection();
    inOrder.verifyNoMoreInteractions();
    verifyNoMoreInteractions(taskanaEngineMock, monitorMapperMock, internalTaskanaEngineMock);

    assertNotNull(report);
    assertEquals(1, report.rowSize());
    assertArrayEquals(new int[0], report.getRow("DOMAIN_X").getCells());
    assertArrayEquals(new int[0], report.getSumRow().getCells());
    assertEquals(80, report.getRow("DOMAIN_X").getTotalValue());
    assertEquals(80, report.getSumRow().getTotalValue());
  }
}
