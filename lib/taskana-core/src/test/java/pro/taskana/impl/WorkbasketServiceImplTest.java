package pro.taskana.impl;

import static org.mockito.Mockito.times;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.Workbasket;
import pro.taskana.model.WorkbasketAccessItem;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.mappings.DistributionTargetMapper;
import pro.taskana.model.mappings.WorkbasketAccessMapper;
import pro.taskana.model.mappings.WorkbasketMapper;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test for workbasketServiceImpl.
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkbasketServiceImplTest {

    private static final int THREE = 3;

    private static final int SLEEP_TIME = 100;

    @InjectMocks
    WorkbasketServiceImpl workbasketServiceImpl;

    @Mock
    WorkbasketMapper workbasketMapper;
    @Mock
    DistributionTargetMapper distributionTargetMapper;
    @Mock
    WorkbasketAccessMapper workbasketAccessMapper;
    @Mock
    TaskanaEngineImpl taskanaEngine;
    @Mock
    TaskanaEngineImpl taskanaEngineImpl;
    @Mock
    TaskanaEngineConfiguration taskanaEngineConfiguration;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void should_ReturnWorkbasket_when_WorkbasketIdExists() throws WorkbasketNotFoundException {
        when(workbasketMapper.findById(any())).thenReturn(new Workbasket());

        Workbasket workbasket = workbasketServiceImpl.getWorkbasket("fail");

        verify(workbasketMapper).findById(any());
        Assert.assertNotNull(workbasket);
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void should_ThrowWorkbasketNotFoundException_when_WorkbasketIdDoesNotExist()
            throws WorkbasketNotFoundException {
        workbasketServiceImpl.getWorkbasket("fail");
    }

    @Test
    public void should_ReturnListOfWorkbaskets_when_PermissionAndUserExists() {
        when(workbasketMapper.findByPermission(any(), any())).thenReturn(new ArrayList<Workbasket>());

        List<WorkbasketAuthorization> authorizations = new ArrayList<>();
        authorizations.add(WorkbasketAuthorization.OPEN);
        authorizations.add(WorkbasketAuthorization.APPEND);
        List<Workbasket> workbaskets = workbasketServiceImpl.getWorkbaskets(authorizations);

        verify(workbasketMapper).findByPermission(any(), any());
        Assert.assertNotNull(workbaskets);
    }

    @Test
    public void should_ReturnAllWorkbaskets_when_AllWorkbaskets() {
        when(workbasketMapper.findAll()).thenReturn(new ArrayList<Workbasket>());

        List<Workbasket> workbaskets = workbasketServiceImpl.getWorkbaskets();

        verify(workbasketMapper).findAll();
        Assert.assertNotNull(workbaskets);
    }

    @Test
    public void should_InitializeAndStoreWorkbasket_when_WorkbasketIsCreated() throws NotAuthorizedException {
        doNothing().when(workbasketMapper).insert(any());

        Workbasket workbasket = new Workbasket();
        workbasket.setId("1");
        workbasketServiceImpl.createWorkbasket(workbasket);

        Assert.assertEquals("1", workbasket.getId());
        Assert.assertEquals(workbasket.getModified(), workbasket.getCreated());

        verify(workbasketMapper).insert(any());
    }

    @SuppressWarnings("serial")
    @Test
    public void should_InitializeAndStoreWorkbasket_when_WorkbasketWithDistributionTargetsIsCreated()
            throws NotAuthorizedException {
        doNothing().when(workbasketMapper).insert(any());
        doNothing().when(distributionTargetMapper).insert(any(), any());

        Workbasket workbasket = new Workbasket();
        workbasket.setId("1");
        Workbasket workbasket1 = new Workbasket();
        workbasket1.setId("2");
        Workbasket workbasket2 = new Workbasket();
        workbasket2.setId("3");
        workbasket.setDistributionTargets(new ArrayList<Workbasket>() {
            {
                add(workbasket1);
                add(workbasket2);
            }
        });

        workbasketServiceImpl.createWorkbasket(workbasket);

        Assert.assertEquals("1", workbasket.getId());
        Assert.assertEquals(workbasket.getModified(), workbasket.getCreated());

        verify(workbasketMapper, times(THREE)).insert(any());
        verify(distributionTargetMapper, times(2)).insert(any(), any());
    }

    @Test
    public void should_ReturnUpdatedWorkbasket_when_ExistingWorkbasketDescriptionIsChanged()
            throws NotAuthorizedException {
        doNothing().when(workbasketMapper).insert(any());

        Workbasket workbasket = new Workbasket();
        workbasket.setId("0");
        workbasket.setDescription("TestDescription");
        workbasket.setName("Cool New WorkintheBasket");
        workbasket.setOwner("Arthur Dent");
        workbasketServiceImpl.createWorkbasket(workbasket);

        doNothing().when(workbasketMapper).update(any());
        workbasket.setDescription("42");
        workbasketServiceImpl.updateWorkbasket(workbasket);

        verify(workbasketMapper).update(any());
    }

    @SuppressWarnings("serial")
    @Test
    public void should_ReturnUpdatedWorkbasket_when_ExistingWorkbasketDistributionTargetIsChanged()
            throws NotAuthorizedException {
        doNothing().when(workbasketMapper).insert(any());

        Workbasket workbasket = new Workbasket();
        workbasket.setId("0");
        Workbasket workbasket1 = new Workbasket();
        workbasket1.setId("1");
        workbasket.setDistributionTargets(new ArrayList<Workbasket>() {
            {
                add(workbasket1);
            }
        });
        workbasketServiceImpl.createWorkbasket(workbasket);

        doNothing().when(workbasketMapper).update(any());
        when(workbasketMapper.findById(any())).thenReturn(workbasket);
        workbasket.getDistributionTargets().get(0).setDescription("Test123");
        Workbasket result = workbasketServiceImpl.updateWorkbasket(workbasket);

        verify(workbasketMapper).update(any());
        Assert.assertEquals("Test123", result.getDistributionTargets().get(0).getDescription());
    }

    @Test
    public void should_UpdateModifiedTimestamp_when_ExistingWorkbasketDistributionTargetIsChanged() throws Exception {
        doNothing().when(workbasketMapper).insert(any());

        Workbasket workbasket0 = new Workbasket();
        workbasket0.setId("0");
        Workbasket workbasket1 = new Workbasket();
        workbasket1.setId("1");
        Workbasket workbasket2 = new Workbasket();
        workbasket2.setId("2");
        workbasket2.getDistributionTargets().add(workbasket0);
        workbasket2.getDistributionTargets().add(workbasket1);
        workbasketServiceImpl.createWorkbasket(workbasket2);

        Workbasket workbasket3 = new Workbasket();
        workbasket3.setId("3");
        workbasket2.getDistributionTargets().clear();
        workbasket2.getDistributionTargets().add(workbasket3);
        Thread.sleep(SLEEP_TIME);

        doNothing().when(workbasketMapper).update(any());
        workbasketServiceImpl.updateWorkbasket(workbasket2);

        when(workbasketMapper.findById("2")).thenReturn(workbasket2);
        Workbasket foundBasket = workbasketServiceImpl.getWorkbasket(workbasket2.getId());

        when(workbasketMapper.findById("1")).thenReturn(workbasket1);
        when(workbasketMapper.findById("3")).thenReturn(workbasket1);

        List<Workbasket> distributionTargets = foundBasket.getDistributionTargets();
        Assert.assertEquals(1, distributionTargets.size());
        Assert.assertEquals("3", distributionTargets.get(0).getId());

        Assert.assertNotEquals(workbasketServiceImpl.getWorkbasket("2").getCreated(),
                workbasketServiceImpl.getWorkbasket("2").getModified());
        Assert.assertEquals(workbasketServiceImpl.getWorkbasket("1").getCreated(),
                workbasketServiceImpl.getWorkbasket("1").getModified());
        Assert.assertEquals(workbasketServiceImpl.getWorkbasket("3").getCreated(),
                workbasketServiceImpl.getWorkbasket("3").getModified());
    }

    @Test
    public void should_ReturnWorkbasketAuthorization_when_NewWorkbasketAccessItemIsCreated()
            throws NotAuthorizedException {
        doNothing().when(workbasketAccessMapper).insert(any());

        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setWorkbasketId("1");
        accessItem.setAccessId("Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        accessItem = workbasketServiceImpl.createWorkbasketAuthorization(accessItem);

        Assert.assertNotNull(accessItem.getId());
    }

    @Test
    public void should_ReturnWorkbasketAuthorization_when_WorkbasketAccessItemIsUpdated()
            throws NotAuthorizedException {
        doNothing().when(workbasketAccessMapper).insert(any());

        WorkbasketAccessItem accessItem = new WorkbasketAccessItem();
        accessItem.setWorkbasketId("1");
        accessItem.setAccessId("Arthur Dent");
        accessItem.setPermOpen(true);
        accessItem.setPermRead(true);
        accessItem = workbasketServiceImpl.createWorkbasketAuthorization(accessItem);

        Assert.assertNotNull(accessItem.getId());

        doNothing().when(workbasketAccessMapper).update(any());
        accessItem.setAccessId("Zaphod Beeblebrox");
        workbasketServiceImpl.updateWorkbasketAuthorization(accessItem);

        Assert.assertEquals("Zaphod Beeblebrox", accessItem.getAccessId());
    }

    @Test(expected = NotAuthorizedException.class)
    public void should_ThrowNotAuthorizedException_when_OperationIsNotAuthorized() throws NotAuthorizedException {
        when(taskanaEngine.getConfiguration()).thenReturn(taskanaEngineConfiguration);
        when(taskanaEngine.getConfiguration().isSecurityEnabled()).thenReturn(true);

        workbasketServiceImpl.checkAuthorization("1", WorkbasketAuthorization.READ);
    }

    @SuppressWarnings("serial")
    @Test
    public void should_Pass_when_OperationIsAuthorized() throws NotAuthorizedException {
        when(taskanaEngine.getConfiguration()).thenReturn(taskanaEngineConfiguration);
        when(taskanaEngine.getConfiguration().isSecurityEnabled()).thenReturn(true);

        when(workbasketAccessMapper.findByWorkbasketAndAccessIdAndAuthorizations(any(), any(), any()))
                .thenReturn(new ArrayList<WorkbasketAccessItem>() {
                    {
                        add(new WorkbasketAccessItem());
                    }
                });

        workbasketServiceImpl.checkAuthorization("1", WorkbasketAuthorization.READ);

        verify(workbasketAccessMapper, times(1)).findByWorkbasketAndAccessIdAndAuthorizations(any(), any(), any());
    }

    @Test
    public void should_Pass_when_SecurityIsDisabled() throws NotAuthorizedException {
        when(taskanaEngine.getConfiguration()).thenReturn(taskanaEngineConfiguration);
        when(taskanaEngine.getConfiguration().isSecurityEnabled()).thenReturn(false);

        workbasketServiceImpl.checkAuthorization("1", WorkbasketAuthorization.READ);
    }

}
