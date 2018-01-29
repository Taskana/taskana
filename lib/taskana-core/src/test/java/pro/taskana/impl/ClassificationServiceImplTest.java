package pro.taskana.impl;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.Classification;
import pro.taskana.ClassificationSummary;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.mappings.ClassificationMapper;

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

    @Before
    public void setup() {
        doNothing().when(taskanaEngineImplMock).openConnection();
        doNothing().when(taskanaEngineImplMock).returnConnection();
    }

    @Test
    public void testGetClassificationTree() throws NotAuthorizedException, InvalidArgumentException {
        List<Classification> classifications = new ArrayList<>();

        doReturn(classificationQueryImplMock).when(cutSpy).createClassificationQuery();
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).parentClassificationKey("");
        doReturn(classifications).when(classificationQueryImplMock).list();

        List<ClassificationSummary> actaulResults = cutSpy.getClassificationTree();

        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(cutSpy, times(1)).createClassificationQuery();
        verify(classificationQueryImplMock, times(1)).parentClassificationKey("");
        verify(classificationQueryImplMock, times(1)).list();
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        assertThat(actaulResults, equalTo(classifications));
    }

    @Test(expected = ClassificationAlreadyExistException.class)
    public void testCreateClassificationAlreadyExisting()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException {
        Classification classification = createDummyClassification();
        doReturn(classification).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());

        try {
            cutSpy.createClassification(classification);
        } catch (ClassificationAlreadyExistException e) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
                classification.getDomain());
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testCreateClassificationInOwnDomainButExistingInRoot()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException, InterruptedException {
        Instant beforeTimestamp = Instant.now();
        Thread.sleep(10L);
        Classification classification = createDummyClassification();
        String domain = classification.getDomain();
        String key = classification.getKey();
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        doReturn(classification).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(), "");

        classification = cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(key, domain);
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(key, "");
        verify(classificationMapperMock, times(1)).insert(any());
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated().toString().substring(0, 10), equalTo(todaysDate));
        assertFalse(classification.getCreated().isAfter(Instant.now()));
        assertTrue(classification.getCreated().isAfter(beforeTimestamp));
        assertThat(classification.getDomain(), equalTo(domain));
        assertThat(classification.getKey(), equalTo(key));
    }

    @Test
    public void testCreateClassificationInOwnDomainAndCopyInRootDomain()
        throws ClassificationAlreadyExistException {
        Classification classification = createDummyClassification();
        String domain = classification.getDomain();
        String key = classification.getKey();
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(), "");

        classification = cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(key, domain);
        verify(classificationMapperMock, times(2)).findByKeyAndDomain(key, "");
        verify(classificationMapperMock, times(2)).insert(any());
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated().toString().substring(0, 10), equalTo(todaysDate));
        assertThat(classification.getDomain(), equalTo(domain));
        assertThat(classification.getKey(), equalTo(key));
    }

    @Test
    public void testCreateClassificationIntoRootDomain()
        throws ClassificationAlreadyExistException {
        ClassificationImpl classification = (ClassificationImpl) createDummyClassification();
        classification.setDomain("");
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());

        classification = (ClassificationImpl) cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
            classification.getDomain());
        verify(classificationMapperMock, times(1)).insert(classification);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated().toString().substring(0, 10), equalTo(todaysDate));
    }

    @Test
    public void testUpdateExistingClassification() throws ClassificationNotFoundException {
        Classification classification = createDummyClassification();
        ClassificationImpl oldClassification = (ClassificationImpl) createDummyClassification();
        doReturn(oldClassification).when(cutSpy).getClassification(classification.getKey(), classification.getDomain());

        classification = cutSpy.updateClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(cutSpy, times(2)).getClassification(classification.getKey(), classification.getDomain());
        verify(classificationMapperMock, times(1)).update(any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
    }

    @Test
    public void testGetAllClassificationWithId() {
        Classification dummyClassification = createDummyClassification();
        List<ClassificationSummary> classificationImpls = Arrays.asList(new ClassificationSummaryImpl(),
            new ClassificationSummaryImpl());
        doReturn(classificationImpls).when(classificationMapperMock)
            .getAllClassificationsWithKey(dummyClassification.getKey(), dummyClassification.getDomain());

        List<ClassificationSummary> actualResults = cutSpy.getAllClassifications(dummyClassification.getKey(),
            dummyClassification.getDomain());

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(classificationMapperMock, times(1)).getAllClassificationsWithKey(dummyClassification.getKey(),
            dummyClassification.getDomain());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(actualResults.size(), equalTo(classificationImpls.size()));
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

    private Classification createDummyClassification() {
        ClassificationImpl classificationImpl = new ClassificationImpl();
        classificationImpl.setDescription("A DUMMY FOR TESTING A SERVICE");
        classificationImpl.setName("SERVICE-DUMMY");
        classificationImpl.setDomain("test-domain");
        classificationImpl.setServiceLevel("P2D");
        classificationImpl.setId("ID: 1");
        classificationImpl.setKey("ABC111");
        classificationImpl.setParentClassificationKey("");
        return classificationImpl;
    }
}
