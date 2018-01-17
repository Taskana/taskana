package pro.taskana.impl;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.Workbasket;
import pro.taskana.WorkbasketSummary;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.WorkbasketNotFoundException;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.WorkbasketType;
import pro.taskana.model.mappings.DistributionTargetMapper;
import pro.taskana.model.mappings.WorkbasketAccessMapper;
import pro.taskana.model.mappings.WorkbasketMapper;

/**
 * Unit Test for workbasketServiceImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkbasketServiceImplTest {

    @Spy
    @InjectMocks
    private WorkbasketServiceImpl cutSpy;

    @Mock
    private WorkbasketMapper workbasketMapperMock;

    @Mock
    private DistributionTargetMapper distributionTargetMapperMock;

    @Mock
    private WorkbasketAccessMapper workbasketAccessMapperMock;

    @Mock
    private TaskanaEngineImpl taskanaEngineImplMock;

    @Mock
    private TaskanaEngineConfiguration taskanaEngineConfigurationMock;

    @Before
    public void setup() throws NotAuthorizedException {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NotAuthorizedException.class)
    public void testGetWorkbasketById_NonAuthorizedUser()
        throws WorkbasketNotFoundException, NotAuthorizedException {
        String wbId = "ID-1";
        Workbasket wb = createTestWorkbasket(wbId, "Key-1");
        WorkbasketAuthorization authorization = WorkbasketAuthorization.READ;
        doReturn(wb).when(workbasketMapperMock).findById(wbId);
        doThrow(NotAuthorizedException.class).when(cutSpy).checkAuthorization(wb.getKey(), authorization);

        try {
            cutSpy.getWorkbasket(wbId);
        } catch (NotAuthorizedException ex) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(workbasketMapperMock, times(1)).findById(wbId);
            verify(cutSpy, times(1)).checkAuthorization(wb.getKey(), authorization);
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            throw ex;
        }
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetWorkbasketById_AuthenticatedWithoutResult()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        String wbId = "ID-1";
        doReturn(null).when(workbasketMapperMock).findById(wbId);

        try {
            cutSpy.getWorkbasket(wbId);
        } catch (WorkbasketNotFoundException ex) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(workbasketMapperMock, times(1)).findById(wbId);
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            throw ex;
        }
    }

    @Test
    public void testGetWorkbasketById() throws NotAuthorizedException, WorkbasketNotFoundException {
        String wbId = "ID-1";
        Workbasket wb = createTestWorkbasket(wbId, "key-1");
        WorkbasketAuthorization authorization = WorkbasketAuthorization.READ;
        doReturn(wb).when(workbasketMapperMock).findById(wbId);
        doNothing().when(cutSpy).checkAuthorization(wb.getKey(), authorization);

        Workbasket actualWb = cutSpy.getWorkbasket(wbId);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(workbasketMapperMock, times(1)).findById(wbId);
        verify(cutSpy, times(1)).checkAuthorization(wb.getKey(), authorization);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
            taskanaEngineImplMock, taskanaEngineConfigurationMock);
        assertThat(actualWb, equalTo(wb));
    }

    @Test(expected = NotAuthorizedException.class)
    public void testGetWorkbasketByKey_NonAuthorizedUser()
        throws WorkbasketNotFoundException, NotAuthorizedException {
        String wbKey = "Key-1";
        Workbasket wb = createTestWorkbasket("ID", wbKey);
        WorkbasketAuthorization authorization = WorkbasketAuthorization.READ;
        doReturn(wb).when(workbasketMapperMock).findByKey(wbKey);
        doThrow(NotAuthorizedException.class).when(cutSpy).checkAuthorization(wbKey, authorization);

        try {
            cutSpy.getWorkbasketByKey(wbKey);
        } catch (NotAuthorizedException ex) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(workbasketMapperMock, times(1)).findByKey(wbKey);
            verify(cutSpy, times(1)).checkAuthorization(wbKey, authorization);
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            throw ex;
        }

    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testGetWorkbasketByKey_AuthenticatedWithoutResult()
        throws NotAuthorizedException, WorkbasketNotFoundException {
        String wbKey = "Key-1";
        doReturn(null).when(workbasketMapperMock).findByKey(wbKey);

        try {
            cutSpy.getWorkbasketByKey(wbKey);
        } catch (WorkbasketNotFoundException ex) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(workbasketMapperMock, times(1)).findByKey(wbKey);
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            throw ex;
        }
    }

    @Test
    public void testGetWorkbasketByKey() throws NotAuthorizedException, WorkbasketNotFoundException {
        String wbKey = "Key-1";
        Workbasket wb = createTestWorkbasket("ID-1", wbKey);
        WorkbasketAuthorization authorization = WorkbasketAuthorization.READ;
        doNothing().when(cutSpy).checkAuthorization(wbKey, authorization);
        doReturn(wb).when(workbasketMapperMock).findByKey(wbKey);

        Workbasket actualWb = cutSpy.getWorkbasketByKey(wbKey);
        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(cutSpy, times(1)).checkAuthorization(wbKey, authorization);
        verify(workbasketMapperMock, times(1)).findByKey(wbKey);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
            taskanaEngineImplMock, taskanaEngineConfigurationMock);
        assertThat(actualWb, equalTo(wb));
    }

    @Test
    public void testCreateWorkbasket_InvalidWorkbasketCases()
        throws WorkbasketNotFoundException, NotAuthorizedException {
        WorkbasketImpl wb = new WorkbasketImpl();
        int serviceCalls = 1;

        // KEY NULL
        try {
            wb.setId(null);
            wb.setKey(null);
            cutSpy.createWorkbasket(wb);
        } catch (InvalidWorkbasketException ex) {
            verify(taskanaEngineImplMock, times(serviceCalls)).openConnection();
            verify(taskanaEngineImplMock, times(serviceCalls)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            assertThat(wb.getId(), not(equalTo(null)));
            assertThat(wb.getCreated(), not(equalTo(null)));
            assertThat(wb.getModified(), not(equalTo(null)));
        }

        // KEY EMPTY
        serviceCalls++;
        try {
            wb.setKey("");
            cutSpy.createWorkbasket(wb);
        } catch (InvalidWorkbasketException ex) {
            verify(taskanaEngineImplMock, times(serviceCalls)).openConnection();
            verify(taskanaEngineImplMock, times(serviceCalls)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            assertThat(wb.getId(), not(equalTo(null)));
            assertThat(wb.getCreated(), not(equalTo(null)));
            assertThat(wb.getModified(), not(equalTo(null)));
        }

        // NAME NULL
        serviceCalls++;
        try {
            wb.setKey("KEY");
            wb.setName(null);
            cutSpy.createWorkbasket(wb);
        } catch (InvalidWorkbasketException ex) {
            verify(taskanaEngineImplMock, times(serviceCalls)).openConnection();
            verify(taskanaEngineImplMock, times(serviceCalls)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            assertThat(wb.getId(), not(equalTo(null)));
            assertThat(wb.getCreated(), not(equalTo(null)));
            assertThat(wb.getModified(), not(equalTo(null)));
        }

        // NAME EMPTY
        serviceCalls++;
        try {
            wb.setName("");
            cutSpy.createWorkbasket(wb);
        } catch (InvalidWorkbasketException ex) {
            verify(taskanaEngineImplMock, times(serviceCalls)).openConnection();
            verify(taskanaEngineImplMock, times(serviceCalls)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            assertThat(wb.getId(), not(equalTo(null)));
            assertThat(wb.getCreated(), not(equalTo(null)));
            assertThat(wb.getModified(), not(equalTo(null)));
        }

        // DOMAIN NULL
        serviceCalls++;
        try {
            wb.setName("Name");
            wb.setDomain(null);
            cutSpy.createWorkbasket(wb);
        } catch (InvalidWorkbasketException ex) {
            verify(taskanaEngineImplMock, times(serviceCalls)).openConnection();
            verify(taskanaEngineImplMock, times(serviceCalls)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            assertThat(wb.getId(), not(equalTo(null)));
            assertThat(wb.getCreated(), not(equalTo(null)));
            assertThat(wb.getModified(), not(equalTo(null)));
        }

        // TYPE NULL
        serviceCalls++;
        try {
            wb.setDomain("Domain");
            wb.setType(null);
            cutSpy.createWorkbasket(wb);
        } catch (InvalidWorkbasketException ex) {
            verify(taskanaEngineImplMock, times(serviceCalls)).openConnection();
            verify(taskanaEngineImplMock, times(serviceCalls)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            assertThat(wb.getId(), not(equalTo(null)));
            assertThat(wb.getCreated(), not(equalTo(null)));
            assertThat(wb.getModified(), not(equalTo(null)));
        }

    }

    @Test
    public void testCreateWorkbasket_WithoutDistibutionTargets()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
        WorkbasketImpl expectedWb = createTestWorkbasket(null, "Key-1");
        expectedWb.setDistributionTargets(null);
        doNothing().when(workbasketMapperMock).insert(expectedWb);
        doReturn(expectedWb).when(workbasketMapperMock).findById(any());

        Workbasket actualWb = cutSpy.createWorkbasket(expectedWb);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(workbasketMapperMock, times(1)).insert(expectedWb);
        verify(workbasketMapperMock, times(1)).findById(expectedWb.getId());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
            taskanaEngineImplMock, taskanaEngineConfigurationMock);
        assertThat(actualWb.getId(), not(equalTo(null)));
        assertThat(actualWb.getId(), startsWith("WBI"));
        assertThat(actualWb.getCreated(), not(equalTo(null)));
        assertThat(actualWb.getModified(), not(equalTo(null)));
    }

    @Test
    public void testCreateWorkbasket_WithDistibutionTargets()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
        final int distTargetAmount = 2;
        WorkbasketImpl expectedWb = createTestWorkbasket(null, "Key-1");
        expectedWb.setDistributionTargets(createTestDistributionTargets(distTargetAmount));
        doNothing().when(workbasketMapperMock).insert(expectedWb);
        doReturn(expectedWb).when(cutSpy).getWorkbasket(any());
        doReturn(expectedWb).when(workbasketMapperMock).findById(any());

        Workbasket actualWb = cutSpy.createWorkbasket(expectedWb);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(workbasketMapperMock, times(1)).insert(expectedWb);
        verify(cutSpy, times(distTargetAmount)).getWorkbasket(any());
        verify(distributionTargetMapperMock, times(distTargetAmount)).insert(any(), any());
        verify(workbasketMapperMock, times(1)).findById(expectedWb.getId());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
            taskanaEngineImplMock, taskanaEngineConfigurationMock);
        assertThat(actualWb.getId(), not(equalTo(null)));
        assertThat(actualWb.getId(), startsWith("WBI"));
        assertThat(actualWb.getCreated(), not(equalTo(null)));
        assertThat(actualWb.getModified(), not(equalTo(null)));
    }

    @Test(expected = WorkbasketNotFoundException.class)
    public void testCreateWorkbasket_DistibutionTargetNotExisting()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
        final int distTargetAmount = 5;
        WorkbasketImpl expectedWb = createTestWorkbasket("ID-1", "Key-1");
        expectedWb.setDistributionTargets(createTestDistributionTargets(distTargetAmount));
        doNothing().when(workbasketMapperMock).insert(expectedWb);
        doThrow(WorkbasketNotFoundException.class).when(cutSpy)
            .getWorkbasket(expectedWb.getDistributionTargets().get(0).getId());

        try {
            cutSpy.createWorkbasket(expectedWb);
        } catch (WorkbasketNotFoundException e) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(workbasketMapperMock, times(1)).insert(expectedWb);
            verify(cutSpy, times(1)).getWorkbasket(any());
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            throw e;
        }
    }

    // TODO Add stored-check. Not getWorkbasket() because permissions are not set with this action here.
    @Ignore
    @Test(expected = WorkbasketNotFoundException.class)
    public void testCreateWorkbasket_NotCreated()
        throws WorkbasketNotFoundException, NotAuthorizedException, InvalidWorkbasketException {
        WorkbasketImpl expectedWb = createTestWorkbasket(null, "Key-1");
        expectedWb.setDistributionTargets(null);
        doNothing().when(workbasketMapperMock).insert(expectedWb);
        doThrow(WorkbasketNotFoundException.class).when(workbasketMapperMock).findById(any());

        try {
            cutSpy.createWorkbasket(expectedWb);
        } catch (Exception e) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(workbasketMapperMock, times(1)).insert(expectedWb);
            verify(workbasketMapperMock, times(1)).findById(expectedWb.getId());
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(workbasketMapperMock, workbasketAccessMapperMock, distributionTargetMapperMock,
                taskanaEngineImplMock, taskanaEngineConfigurationMock);
            throw e;
        }
    }

    private WorkbasketImpl createTestWorkbasket(String id, String key) {
        WorkbasketImpl workbasket = new WorkbasketImpl();
        workbasket.setId(id);
        workbasket.setKey(key);
        workbasket.setName("Workbasket " + id);
        workbasket.setDescription("Description WB with Key " + key);
        workbasket.setType(WorkbasketType.PERSONAL);
        workbasket.setDomain("");
        return workbasket;
    }

    private List<WorkbasketSummary> createTestDistributionTargets(int amount) {
        List<WorkbasketSummary> distributionsTargets = new ArrayList<>();
        amount = (amount < 0) ? 0 : amount;
        for (int i = 0; i < amount; i++) {
            distributionsTargets.add(createTestWorkbasket("WB-ID-" + i, "WB-KEY-" + i).asSummary());
        }
        return distributionsTargets;
    }
}
