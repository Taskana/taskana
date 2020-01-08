package pro.taskana.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import pro.taskana.TaskQuery;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
import pro.taskana.WorkbasketType;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.mappings.DistributionTargetMapper;
import pro.taskana.mappings.WorkbasketAccessMapper;
import pro.taskana.mappings.WorkbasketMapper;

/**
 * Unit Test for workbasketServiceImpl.
 *
 * @author EH
 */
@ExtendWith(MockitoExtension.class)
class WorkbasketServiceImplTest {

  @Spy @InjectMocks private WorkbasketServiceImpl workbasketServiceSpy;

  @Mock private WorkbasketMapper workbasketMapperMock;

  @Mock private DistributionTargetMapper distributionTargetMapperMock;

  @Mock private WorkbasketAccessMapper workbasketAccessMapperMock;

  @Mock private TaskService taskServiceMock;

  @Mock private TaskQuery taskQueryMock;

  @Mock private TaskanaEngine taskanaEngine;

  @Mock private InternalTaskanaEngine internalTaskanaEngineMock;

  @Mock private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

  @BeforeEach
  void setup() {
    when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngine);
  }

  @Test
  void testCreateWorkbasket_WithDistibutionTargets()
      throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException,
          WorkbasketAlreadyExistException, DomainNotFoundException {
    final int distTargetAmount = 2;
    WorkbasketImpl expectedWb = createTestWorkbasket(null, "Key-1");
    doReturn(expectedWb).when(workbasketServiceSpy).getWorkbasket(any());
    when(internalTaskanaEngineMock.domainExists(any())).thenReturn(true);

    final Workbasket actualWb = workbasketServiceSpy.createWorkbasket(expectedWb);
    workbasketServiceSpy.setDistributionTargets(
        expectedWb.getId(), createTestDistributionTargets(distTargetAmount));

    verify(internalTaskanaEngineMock, times(4)).openConnection();
    verify(workbasketMapperMock, times(3)).insert(any());
    verify(workbasketServiceSpy, times(distTargetAmount + 1)).getWorkbasket(any());
    verify(distributionTargetMapperMock, times(1)).deleteAllDistributionTargetsBySourceId(any());
    verify(distributionTargetMapperMock, times(distTargetAmount)).insert(any(), any());
    verify(workbasketMapperMock, times(3)).findByKeyAndDomain(any(), any());
    verify(workbasketMapperMock, times(1)).update(any());
    verify(internalTaskanaEngineMock, times(4)).returnConnection();
    verify(taskanaEngine, times(4)).checkRoleMembership(any());
    verify(internalTaskanaEngineMock, times(4)).getEngine();
    verify(internalTaskanaEngineMock, times(3)).domainExists(any());
    verifyNoMoreInteractions(
        taskQueryMock,
        taskServiceMock,
        workbasketMapperMock,
        workbasketAccessMapperMock,
        distributionTargetMapperMock,
        internalTaskanaEngineMock,
        taskanaEngine,
        taskanaEngineConfigurationMock);
    assertThat(actualWb.getId(), not(equalTo(null)));
    assertThat(actualWb.getId(), startsWith("WBI"));
    assertThat(actualWb.getCreated(), not(equalTo(null)));
    assertThat(actualWb.getModified(), not(equalTo(null)));
  }

  @Test
  void testCreateWorkbasket_DistibutionTargetNotExisting() throws Exception {
    WorkbasketImpl expectedWb = createTestWorkbasket("ID-1", "Key-1");
    when(internalTaskanaEngineMock.domainExists(any())).thenReturn(true);
    String otherWorkbasketId = "4711";
    List<String> destinations = Collections.singletonList(otherWorkbasketId);
    workbasketServiceSpy.createWorkbasket(expectedWb);
    doReturn(expectedWb).when(workbasketServiceSpy).getWorkbasket(eq(expectedWb.getId()));

    WorkbasketNotFoundException e =
        Assertions.assertThrows(
            WorkbasketNotFoundException.class,
            () -> workbasketServiceSpy.setDistributionTargets(expectedWb.getId(), destinations));

    Assertions.assertEquals(e.getId(), otherWorkbasketId);
    Assertions.assertNull(e.getKey());
    Assertions.assertNull(e.getDomain());

    verify(internalTaskanaEngineMock, times(3)).openConnection();
    verify(workbasketMapperMock, times(1)).insert(expectedWb);
    verify(workbasketMapperMock, times(1)).findById(any());
    verify(workbasketMapperMock, times(1)).findByKeyAndDomain(any(), any());
    verify(workbasketServiceSpy, times(2)).getWorkbasket(any());
    verify(internalTaskanaEngineMock, times(3)).returnConnection();
    verify(taskanaEngine, times(2)).checkRoleMembership(any());
    verify(internalTaskanaEngineMock, times(2)).getEngine();
    verify(internalTaskanaEngineMock, times(1)).domainExists(any());
    verify(distributionTargetMapperMock)
        .deleteAllDistributionTargetsBySourceId(eq(expectedWb.getId()));
    verify(workbasketMapperMock).update(eq(expectedWb));
    verifyNoMoreInteractions(
        taskQueryMock,
        taskServiceMock,
        workbasketMapperMock,
        workbasketAccessMapperMock,
        distributionTargetMapperMock,
        internalTaskanaEngineMock,
        taskanaEngine,
        taskanaEngineConfigurationMock);
  }

  @Test
  void testDeleteWorkbasketIsUsed() throws NotAuthorizedException, WorkbasketNotFoundException {
    Workbasket wb = createTestWorkbasket("WBI:0", "wb-key");
    List<TaskSummary> usages = Arrays.asList(new TaskSummaryImpl(), new TaskSummaryImpl());

    WorkbasketNotFoundException e =
        Assertions.assertThrows(
            WorkbasketNotFoundException.class,
            () -> workbasketServiceSpy.deleteWorkbasket(wb.getId()));

    verify(internalTaskanaEngineMock, times(2)).openConnection();
    verify(workbasketServiceSpy, times(1)).getWorkbasket(wb.getId());
    verify(taskanaEngine, times(0)).getTaskService();
    verify(taskServiceMock, times(0)).createTaskQuery();
    verify(taskQueryMock, times(0)).workbasketIdIn(wb.getId());
    verify(taskQueryMock, times(0)).count();
    verify(internalTaskanaEngineMock, times(2)).returnConnection();
    verifyNoMoreInteractions(
        taskQueryMock,
        taskServiceMock,
        workbasketAccessMapperMock,
        distributionTargetMapperMock,
        taskanaEngineConfigurationMock);
  }

  private WorkbasketImpl createTestWorkbasket(String id, String key) {
    WorkbasketImpl workbasket = new WorkbasketImpl();
    workbasket.setId(id);
    workbasket.setKey(key);
    workbasket.setName("Workbasket " + id);
    workbasket.setDescription("Description WB with Key " + key);
    workbasket.setType(WorkbasketType.PERSONAL);
    workbasket.setDomain("DOMAIN_A");
    return workbasket;
  }

  private List<String> createTestDistributionTargets(int amount)
      throws InvalidWorkbasketException, NotAuthorizedException, WorkbasketAlreadyExistException,
          DomainNotFoundException {
    List<String> distributionsTargets = new ArrayList<>();
    amount = Math.max(amount, 0);
    for (int i = 0; i < amount; i++) {
      WorkbasketImpl wb = createTestWorkbasket("WB-ID-" + i, "WB-KEY-" + i);
      workbasketServiceSpy.createWorkbasket(wb);
      distributionsTargets.add(wb.getId());
    }
    return distributionsTargets;
  }
}
