package pro.taskana.impl;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.Classification;
import pro.taskana.JobService;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.jobs.ScheduledJob;
import pro.taskana.mappings.ClassificationMapper;
import pro.taskana.mappings.JobMapper;

/**
 * Unit Test for ClassificationServiceImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationServiceImplTest {

    private final String todaysDate = LocalDate.now().toString().substring(0, 10);

    @Spy
    @InjectMocks
    private ClassificationServiceImpl cutSpy;

    @Mock
    private ClassificationMapper classificationMapperMock;

    @Mock
    private TaskanaEngineImpl taskanaEngineImplMock;

    @Mock
    private ClassificationQueryImpl classificationQueryImplMock;

    @Mock
    private SqlSession sqlSessionMock;

    @Mock
    private JobService jobServiceMock;

    @Before
    public void setup() {
        doNothing().when(taskanaEngineImplMock).openConnection();
        doNothing().when(taskanaEngineImplMock).returnConnection();
    }

    @Test(expected = ClassificationAlreadyExistException.class)
    public void testCreateClassificationAlreadyExisting()
        throws ClassificationAlreadyExistException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        Classification classification = createDummyClassification();
        doReturn(classification).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        doReturn(true).when(taskanaEngineImplMock).domainExists(any());

        try {
            cutSpy.createClassification(classification);
        } catch (ClassificationAlreadyExistException e) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
                classification.getDomain());
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
            verify(taskanaEngineImplMock, times(1)).domainExists(any());
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = InvalidArgumentException.class)
    public void testCreateClassificationParentIdNotExisting()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        Classification classification = createDummyClassification(null);
        classification.setParentId("NOT EXISTING ID");
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        doReturn(null).when(classificationMapperMock).findById(classification.getParentId());
        doReturn(true).when(taskanaEngineImplMock).domainExists(any());

        try {
            cutSpy.createClassification(classification);
        } catch (InvalidArgumentException e) {
            verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
            verify(taskanaEngineImplMock, times(2)).openConnection();
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
                classification.getDomain());
            verify(cutSpy, times(1)).getClassification(classification.getParentId());
            verify(classificationMapperMock, times(1)).findById(classification.getParentId());
            verify(taskanaEngineImplMock, times(2)).returnConnection();
            verify(taskanaEngineImplMock, times(1)).domainExists(any());
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = InvalidArgumentException.class)
    public void testCreateClassificationParentKeyNotExisting()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        Classification classification = createDummyClassification(null);
        classification.setParentKey("NOT EXISTING KEY");
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getParentKey(),
            classification.getDomain());
        doReturn(true).when(taskanaEngineImplMock).domainExists(any());

        try {
            cutSpy.createClassification(classification);
        } catch (InvalidArgumentException e) {
            verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
            verify(taskanaEngineImplMock, times(2)).openConnection();
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
                classification.getDomain());
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getParentKey(),
                classification.getDomain());
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getParentKey(), "");
            verify(cutSpy, times(1)).getClassification(classification.getParentKey(), classification.getDomain());
            verify(taskanaEngineImplMock, times(2)).returnConnection();
            verify(taskanaEngineImplMock, times(1)).domainExists(any());
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testCreateClassificationInOwnDomainButExistingInMaster()
        throws ClassificationAlreadyExistException, InterruptedException,
        NotAuthorizedException, DomainNotFoundException, InvalidArgumentException {
        Instant beforeTimestamp = Instant.now();
        Thread.sleep(10L);
        Classification classification = createDummyClassification(null);
        String domain = classification.getDomain();
        String key = classification.getKey();
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        doReturn(classification).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(), "");
        doReturn(true).when(taskanaEngineImplMock).domainExists(any());

        classification = cutSpy.createClassification(classification);

        Thread.sleep(10L);
        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(key, domain);
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(key, "");
        verify(classificationMapperMock, times(1)).insert(any());
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(1)).domainExists(any());
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        Thread.sleep(15);
        assertThat(classification.getCreated().toString().substring(0, 10), equalTo(todaysDate));
        assertFalse(classification.getCreated().isAfter(Instant.now()));
        assertTrue(classification.getCreated().isAfter(beforeTimestamp));
        assertThat(classification.getDomain(), equalTo(domain));
        assertThat(classification.getKey(), equalTo(key));
    }

    @Test
    public void testCreateClassificationInOwnDomainAndCopyInMasterDomain()
        throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
        InvalidArgumentException {
        Classification classification = createDummyClassification("");
        String domain = classification.getDomain();
        String key = classification.getKey();
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(), "");
        doReturn(true).when(taskanaEngineImplMock).domainExists(any());

        classification = cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(key, domain);
        verify(classificationMapperMock, times(2)).findByKeyAndDomain(key, "");
        verify(classificationMapperMock, times(2)).insert(any());
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(1)).domainExists(any());
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated().toString().substring(0, 10), equalTo(todaysDate));
        assertThat(classification.getDomain(), equalTo(domain));
        assertThat(classification.getKey(), equalTo(key));
    }

    @Test
    public void testCreateChildClassificationInOwnDomainAndCopyInMasterDomain()
        throws ClassificationAlreadyExistException, NotAuthorizedException, DomainNotFoundException,
        InvalidArgumentException {
        Classification classification = createDummyClassification("");
        classification.setParentId("parentId");
        classification.setParentKey("ParentKey");
        ClassificationImpl parentDomainClassification = (ClassificationImpl) createDummyClassification("ParentId");
        parentDomainClassification.setKey("ParentKey");
        ClassificationImpl parentMasterClassification = (ClassificationImpl) createDummyClassification(
            "ParentIdMaster");
        parentMasterClassification.setKey("ParentKey");
        parentMasterClassification.setDomain("");
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(), "");
        doReturn(parentMasterClassification).when(classificationMapperMock).findByKeyAndDomain("ParentKey", "");

        doReturn(parentDomainClassification).when(classificationMapperMock).findById("parentId");
        doReturn(true).when(taskanaEngineImplMock).domainExists(any());

        cutSpy.createClassification(classification);

        verify(classificationMapperMock, times(2)).insert(any());
    }

    @Test
    public void testCreateClassificationIntoRootDomain()
        throws ClassificationAlreadyExistException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        ClassificationImpl classification = (ClassificationImpl) createDummyClassification(null);
        classification.setDomain("");
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());

        classification = (ClassificationImpl) cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        verify(classificationMapperMock, times(1)).insert(classification);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(1)).domainExists(any());
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated().toString().substring(0, 10), equalTo(todaysDate));
    }

    @Test
    public void testUpdateExistingClassification()
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException, InvalidArgumentException {
        Instant now = Instant.now();
        Classification classification = createDummyClassification();
        ((ClassificationImpl) classification).setModified(now);
        ClassificationImpl oldClassification = (ClassificationImpl) createDummyClassification();
        oldClassification.setModified(now);
        doReturn(oldClassification).when(cutSpy).getClassification(classification.getKey(), classification.getDomain());

        classification = cutSpy.updateClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getClassification(classification.getKey(), classification.getDomain());
        verify(classificationMapperMock, times(1)).update(any());
        verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
    }

    @Test
    public void testUpdateClassificationWithEmptyServiceLevel()
        throws ClassificationNotFoundException, NotAuthorizedException, ConcurrencyException, InvalidArgumentException {
        Instant now = Instant.now();
        Classification classification = createDummyClassification();
        ((ClassificationImpl) classification).setModified(now);
        classification.setServiceLevel("");
        ClassificationImpl oldClassification = (ClassificationImpl) createDummyClassification();
        oldClassification.setModified(now);
        doReturn(oldClassification).when(cutSpy).getClassification(classification.getKey(), classification.getDomain());
        doReturn(jobServiceMock).when(taskanaEngineImplMock).getJobService();

        cutSpy.updateClassification(classification);

        verify(classificationMapperMock, times(1)).update(any());
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testUpdateClassificationParentIdNotExisting()
        throws ClassificationNotFoundException, NotAuthorizedException,
        ConcurrencyException, InvalidArgumentException {
        Instant now = Instant.now();
        ClassificationImpl oldClassification = (ClassificationImpl) createDummyClassification();
        oldClassification.setParentId("SOME ID");
        oldClassification.setCreated(now);
        oldClassification.setModified(now);
        Classification classification = createDummyClassification();
        classification.setParentId("DIFFERENT ID - FOR CHECKING PARENT");
        ((ClassificationImpl) classification).setCreated(oldClassification.getCreated());
        ((ClassificationImpl) classification).setModified(oldClassification.getModified());
        doReturn(oldClassification).when(cutSpy).getClassification(classification.getKey(), classification.getDomain());
        doReturn(null).when(classificationMapperMock).findById(classification.getParentId());

        try {
            cutSpy.updateClassification(classification);
        } catch (ClassificationNotFoundException e) {
            verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
            verify(taskanaEngineImplMock, times(2)).openConnection();
            verify(cutSpy, times(1)).getClassification(classification.getKey(),
                classification.getDomain());
            verify(cutSpy, times(1)).getClassification(classification.getParentId());
            verify(classificationMapperMock, times(1)).findById(classification.getParentId());
            verify(taskanaEngineImplMock, times(2)).returnConnection();
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testUpdateClassificationParentKeyNotExisting()
        throws ClassificationNotFoundException, NotAuthorizedException,
        ConcurrencyException, InvalidArgumentException {
        Instant now = Instant.now();
        ClassificationImpl oldClassification = (ClassificationImpl) createDummyClassification();
        oldClassification.setParentKey("SOME KEY");
        oldClassification.setCreated(now);
        oldClassification.setModified(now);
        Classification classification = createDummyClassification();
        classification.setParentKey("DIFFERENT KEY - FOR CHECKING PARENT");
        ((ClassificationImpl) classification).setCreated(oldClassification.getCreated());
        ((ClassificationImpl) classification).setModified(oldClassification.getModified());
        doReturn(oldClassification).when(cutSpy).getClassification(classification.getKey(), classification.getDomain());
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getParentKey(),
            classification.getDomain());

        try {
            cutSpy.updateClassification(classification);
        } catch (ClassificationNotFoundException e) {
            verify(taskanaEngineImplMock, times(1)).checkRoleMembership(any());
            verify(taskanaEngineImplMock, times(2)).openConnection();
            verify(cutSpy, times(1)).getClassification(classification.getKey(),
                classification.getDomain());
            verify(cutSpy, times(1)).getClassification(classification.getParentKey(),
                classification.getDomain());
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getParentKey(),
                classification.getDomain());
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getParentKey(), "");
            verify(taskanaEngineImplMock, times(2)).returnConnection();
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testGetClassificationFromDomain() throws ClassificationNotFoundException {
        Classification expectedClassification = createDummyClassification();
        doReturn(expectedClassification).when(classificationMapperMock)
            .findByKeyAndDomain(expectedClassification.getKey(), expectedClassification.getDomain());

        Classification actualClassification = cutSpy.getClassification(expectedClassification.getKey(),
            expectedClassification.getDomain());

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(expectedClassification.getKey(),
            expectedClassification.getDomain());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(actualClassification, equalTo(expectedClassification));
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testGetClassificationThrowingNotFoundException() throws ClassificationNotFoundException {
        Classification classification = createDummyClassification();
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(), "");

        try {
            cutSpy.getClassification(classification.getKey(), classification.getDomain());
        } catch (ClassificationNotFoundException e) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
                classification.getDomain());
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(), "");
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testGetClassificationWithInvalidNullKey() throws ClassificationNotFoundException {
        try {
            cutSpy.getClassification(null, "domain");
        } catch (ClassificationNotFoundException e) {
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testCreateClassificationQuery() {
        cutSpy.createClassificationQuery();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testThrowExceptionIdIfClassificationIsCreatedWithAnExplicitId()
        throws DomainNotFoundException, InvalidArgumentException,
        NotAuthorizedException, ClassificationAlreadyExistException {
        try {
            Classification classification = createDummyClassification();
            doReturn(true).when(taskanaEngineImplMock).domainExists(any());
            cutSpy.createClassification(classification);
        } catch (InvalidArgumentException e) {
            assertEquals(e.getMessage(), "ClassificationId should be null on creation");
            throw e;
        }

    }

    private Classification createDummyClassification() {
        return this.createDummyClassification("ID: 1");
    }

    private Classification createDummyClassification(String id) {

        ClassificationImpl classificationImpl = new ClassificationImpl();
        classificationImpl.setDescription("A DUMMY FOR TESTING A SERVICE");
        classificationImpl.setName("SERVICE-DUMMY");
        classificationImpl.setDomain("DOMAIN_A");
        classificationImpl.setServiceLevel("P2D");
        classificationImpl.setId(id);
        classificationImpl.setKey("ABC111");
        classificationImpl.setParentId("");
        classificationImpl.setParentKey("");
        return classificationImpl;
    }

    /**
     * This is the mock of a jobRunner.
     */
    private class JobRunnerMock implements JobMapper {

        @Override
        public void insertJob(ScheduledJob job) {

        }

        @Override
        public List<ScheduledJob> findJobsToRun() {
            return null;
        }

        @Override
        public void update(ScheduledJob job) {

        }

        @Override
        public void delete(ScheduledJob job) {

        }
    }
}
