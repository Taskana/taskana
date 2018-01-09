package pro.taskana.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.sql.Date;
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

    private final Date today = Date.valueOf(LocalDate.now());

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
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock)
            .validUntil(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        doReturn(classifications).when(classificationQueryImplMock).list();

        List<Classification> actaulResults = cutSpy.getClassificationTree();

        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(cutSpy, times(1)).createClassificationQuery();
        verify(classificationQueryImplMock, times(1)).parentClassificationKey("");
        verify(classificationQueryImplMock, times(1))
            .validUntil(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        verify(classificationQueryImplMock, times(1)).list();
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        assertThat(actaulResults, equalTo(classifications));
    }

    @Test(expected = ClassificationAlreadyExistException.class)
    public void testCreateClassificationAlreadyExisting()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException {
        Classification classification = createDummyClassification();
        doReturn(classification).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain(), ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);

        try {
            cutSpy.createClassification(classification);
        } catch (ClassificationAlreadyExistException e) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
                classification.getDomain(), ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testCreateClassificationInOwnDomainButExistingInRoot()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException {
        Classification classification = createDummyClassification();
        String domain = classification.getDomain();
        String key = classification.getKey();
        Date validUntil = ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL;
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain(), ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        doReturn(classification).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            "", ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);

        cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(key, domain, validUntil);
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(key, "", validUntil);
        verify(classificationMapperMock, times(1)).insert(any());
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated(), equalTo(today));
        assertThat(classification.getDomain(), equalTo(domain));
        assertThat(classification.getKey(), equalTo(key));
        assertThat(classification.getValidFrom(), equalTo(today));
        assertThat(classification.getValidUntil(),
            equalTo(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL));
    }

    @Test
    public void testCreateClassificationInOwnDomainAndInRoot()
        throws ClassificationAlreadyExistException {
        Classification classification = createDummyClassification();
        String domain = classification.getDomain();
        String key = classification.getKey();
        Date validUntil = ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL;
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain(), ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            "", ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);

        cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(key, domain, validUntil);
        verify(classificationMapperMock, times(2)).findByKeyAndDomain(key, "", validUntil);
        verify(classificationMapperMock, times(2)).insert(any());
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated(), equalTo(today));
        assertThat(classification.getDomain(), equalTo(domain));
        assertThat(classification.getKey(), equalTo(key));
        assertThat(classification.getValidFrom(), equalTo(today));
        assertThat(classification.getValidUntil(),
            equalTo(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL));
    }

    @Test
    public void testCreateClassificationIntoRootDomain()
        throws ClassificationAlreadyExistException {
        Classification classification = createDummyClassification();
        classification.setDomain("");
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain(), ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);

        cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
            classification.getDomain(), ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        verify(classificationMapperMock, times(1)).insert((ClassificationImpl) classification);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated(), equalTo(today));
        assertThat(classification.getValidFrom(), equalTo(today));
        assertThat(classification.getValidUntil(),
            equalTo(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL));
    }

    @Test
    public void testUpdateClassificationAtNewDomain() throws ClassificationNotFoundException {
        Classification classification = createDummyClassification();
        Classification oldClassification = createDummyClassification();
        oldClassification.setDomain("");
        doReturn(oldClassification).when(cutSpy).getClassification(classification.getKey(), classification.getDomain());

        cutSpy.updateClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getClassification(classification.getKey(), classification.getDomain());
        verify(classificationMapperMock, times(1)).insert((ClassificationImpl) classification);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated(), equalTo(today));
        assertThat(classification.getValidFrom(), equalTo(today));
        assertThat(classification.getValidUntil(),
            equalTo(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL));
    }

    @Test
    public void testUpdateClassificationAtSameDomain() throws ClassificationNotFoundException {
        Classification classification = createDummyClassification();
        ClassificationImpl oldClassification = (ClassificationImpl) createDummyClassification();
        oldClassification.setValidUntil(Date.valueOf(LocalDate.now()));
        Date yesterday = Date.valueOf(LocalDate.now().minusDays(1));
        doReturn(oldClassification).when(cutSpy).getClassification(classification.getKey(), classification.getDomain());

        cutSpy.updateClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getClassification(classification.getKey(), classification.getDomain());
        verify(classificationMapperMock, times(1)).update(any());
        verify(classificationMapperMock, times(1)).insert(any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(oldClassification.getValidUntil(), equalTo(yesterday));
    }

    @Test
    public void testGetAllClassificationWithId() {
        Classification dummyClassification = createDummyClassification();
        List<ClassificationImpl> classificationImpls = Arrays.asList(createDummyCLassificationImpl(),
            createDummyCLassificationImpl());
        doReturn(classificationImpls).when(classificationMapperMock)
            .getAllClassificationsWithKey(dummyClassification.getKey(), dummyClassification.getDomain());

        List<Classification> actualResults = cutSpy.getAllClassificationsWithKey(dummyClassification.getKey(),
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
            .findByKeyAndDomain(expectedClassification.getKey(), expectedClassification.getDomain(),
                ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);

        Classification actualClassification = cutSpy.getClassification(expectedClassification.getKey(),
            expectedClassification.getDomain());

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(classificationMapperMock, times(1)).findByKeyAndDomain(expectedClassification.getKey(),
            expectedClassification.getDomain(), ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(actualClassification, equalTo(expectedClassification));
    }

    @Test(expected = ClassificationNotFoundException.class)
    public void testGetClassificationThrowingNotFoundException() throws ClassificationNotFoundException {
        Classification classification = createDummyClassification();
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            classification.getDomain(),
            ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        doReturn(null).when(classificationMapperMock).findByKeyAndDomain(classification.getKey(),
            "",
            ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);

        try {
            cutSpy.getClassification(classification.getKey(), classification.getDomain());
        } catch (ClassificationNotFoundException e) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
                classification.getDomain(),
                ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
            verify(classificationMapperMock, times(1)).findByKeyAndDomain(classification.getKey(),
                "",
                ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
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

    private ClassificationImpl createDummyCLassificationImpl() {
        return (ClassificationImpl) createDummyClassification();
    }
}
