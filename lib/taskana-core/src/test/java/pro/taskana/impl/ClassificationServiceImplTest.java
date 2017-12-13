package pro.taskana.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.startsWith;
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
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.ClassificationImpl;
import pro.taskana.model.mappings.ClassificationMapper;

/**
 * Unit Test for ClassificationServiceImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationServiceImplTest {

    private final Date today = Date.valueOf(LocalDate.now());

    private final String idPrefixClassification = "CLI";

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
    public void testGetClassificationTree() throws NotAuthorizedException {
        List<Classification> classifications = new ArrayList<>();

        doReturn(classificationQueryImplMock).when(cutSpy).createClassificationQuery();
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock).parentClassification("");
        doReturn(classificationQueryImplMock).when(classificationQueryImplMock)
            .validUntil(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        doReturn(classifications).when(classificationQueryImplMock).list();

        List<Classification> actaulResults = cutSpy.getClassificationTree();

        verify(taskanaEngineImplMock, times(2)).openConnection();
        verify(cutSpy, times(1)).createClassificationQuery();
        verify(classificationQueryImplMock, times(1)).parentClassification("");
        verify(classificationQueryImplMock, times(1))
            .validUntil(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        verify(classificationQueryImplMock, times(1)).list();
        verify(taskanaEngineImplMock, times(2)).returnConnection();
        assertThat(actaulResults, equalTo(classifications));
    }

    @Test(expected = ClassificationAlreadyExistException.class)
    public void testCreateClassificationAlreadyExisting()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException {
        Classification classification = createDummyCLassification();
        doReturn(classification).when(cutSpy).getClassification(classification.getId(), classification.getDomain());

        try {
            cutSpy.createClassification(classification);
        } catch (ClassificationAlreadyExistException e) {
            verify(taskanaEngineImplMock, times(1)).openConnection();
            verify(cutSpy, times(1)).getClassification(classification.getId(), classification.getDomain());
            verify(taskanaEngineImplMock, times(1)).returnConnection();
            verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
            throw e;
        }
    }

    @Test
    public void testCreateClassificationInOwnDomain()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException {
        Classification classification = createDummyCLassification();
        String domain = classification.getDomain();
        doReturn(null).when(cutSpy).getClassification(classification.getId(), classification.getDomain());

        cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getClassification(classification.getId(), domain);
        verify(classificationMapperMock, times(2)).insert(any());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated(), equalTo(today));
        assertThat(classification.getDomain(), equalTo(""));
        assertThat(classification.getValidFrom(), equalTo(today));
        assertThat(classification.getValidUntil(),
            equalTo(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL));
    }

    @Test
    public void testCreateClassificationAsRoot()
        throws ClassificationAlreadyExistException, ClassificationNotFoundException {
        Classification classification = createDummyCLassification();
        classification.setDomain("");
        doReturn(null).when(cutSpy).getClassification(classification.getId(), classification.getDomain());

        cutSpy.createClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getClassification(classification.getId(), classification.getDomain());
        verify(classificationMapperMock, times(1)).insert((ClassificationImpl) classification);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(classification.getCreated(), equalTo(today));
        assertThat(classification.getDomain(), equalTo(""));
        assertThat(classification.getValidFrom(), equalTo(today));
        assertThat(classification.getValidUntil(),
            equalTo(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL));
    }

    @Test
    public void testUpdateClassificationAtNewDomain() throws ClassificationNotFoundException {
        Classification classification = createDummyCLassification();
        Classification oldClassification = createDummyCLassification();
        oldClassification.setDomain("");
        doReturn(oldClassification).when(cutSpy).getClassification(classification.getId(), classification.getDomain());

        cutSpy.updateClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getClassification(classification.getId(), classification.getDomain());
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
        Classification classification = createDummyCLassification();
        ClassificationImpl oldClassification = (ClassificationImpl) createDummyCLassification();
        oldClassification.setValidUntil(Date.valueOf(LocalDate.now()));
        Date yesterday = Date.valueOf(LocalDate.now().minusDays(1));
        doReturn(oldClassification).when(cutSpy).getClassification(classification.getId(), classification.getDomain());

        cutSpy.updateClassification(classification);

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(cutSpy, times(1)).getClassification(classification.getId(), classification.getDomain());
        verify(classificationMapperMock, times(1)).update(oldClassification);
        verify(classificationMapperMock, times(1)).insert((ClassificationImpl) classification);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(oldClassification.getValidUntil(), equalTo(yesterday));
    }

    @Test
    public void testGetAllClassificationWithId() {
        Classification dummyClassification = createDummyCLassification();
        List<ClassificationImpl> classificationImpls = Arrays.asList(createDummyCLassificationImpl(),
            createDummyCLassificationImpl());
        doReturn(classificationImpls).when(classificationMapperMock)
            .getAllClassificationsWithId(dummyClassification.getId(), dummyClassification.getDomain());

        List<Classification> actualResults = cutSpy.getAllClassificationsWithId(dummyClassification.getId(),
            dummyClassification.getDomain());

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(classificationMapperMock, times(1)).getAllClassificationsWithId(dummyClassification.getId(),
            dummyClassification.getDomain());
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(actualResults.size(), equalTo(classificationImpls.size()));
    }

    @Test
    public void testGetClassificationFromChildDomain() throws ClassificationNotFoundException {
        Classification expectedClassification = createDummyCLassification();
        doReturn(expectedClassification).when(classificationMapperMock)
            .findByIdAndDomain(expectedClassification.getId(), expectedClassification.getDomain(),
                ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);

        Classification actualClassification = cutSpy.getClassification(expectedClassification.getId(),
            expectedClassification.getDomain());

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(classificationMapperMock, times(1)).findByIdAndDomain(expectedClassification.getId(),
            expectedClassification.getDomain(), ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(actualClassification, equalTo(expectedClassification));
    }

    @Test
    public void testGetClassificationFromRootDomain() throws ClassificationNotFoundException {
        Classification classification = createDummyCLassification();
        Classification expectedClassification = createDummyCLassification();
        expectedClassification.setDomain("");
        doReturn(null).when(classificationMapperMock)
            .findByIdAndDomain(classification.getId(), classification.getDomain(),
                ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        doReturn(expectedClassification).when(classificationMapperMock)
            .findByIdAndDomain(expectedClassification.getId(), expectedClassification.getDomain(),
                ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);

        Classification actualClassification = cutSpy.getClassification(classification.getId(),
            classification.getDomain());

        verify(taskanaEngineImplMock, times(1)).openConnection();
        verify(classificationMapperMock, times(1)).findByIdAndDomain(classification.getId(), classification.getDomain(),
            ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        verify(classificationMapperMock, times(1)).findByIdAndDomain(expectedClassification.getId(), "",
            ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL);
        verify(taskanaEngineImplMock, times(1)).returnConnection();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(actualClassification, equalTo(expectedClassification));
    }

    @Test
    public void testCreateClassificationQuery() {
        cutSpy.createClassificationQuery();
        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
    }

    @Test
    public void testNewClassification() {
        Classification actualResult = cutSpy.newClassification();

        verifyNoMoreInteractions(classificationMapperMock, taskanaEngineImplMock, classificationQueryImplMock);
        assertThat(actualResult.getId(), not(equalTo(null)));
        assertThat(actualResult.getId(), startsWith(idPrefixClassification));
        assertThat(actualResult.getCreated(), equalTo(today));
        assertThat(actualResult.getParentClassificationKey(), equalTo(""));
        assertThat(actualResult.getDomain(), equalTo(""));
        assertThat(actualResult.getValidFrom(), equalTo(today));
        assertThat(actualResult.getValidUntil(),
            equalTo(ClassificationServiceImpl.CURRENT_CLASSIFICATIONS_VALID_UNTIL));
    }

    private Classification createDummyCLassification() {
        ClassificationImpl classificationImpl = new ClassificationImpl();
        classificationImpl.setDescription("A DUMMY FOR TESTING A SERVICE");
        classificationImpl.setName("SERVICE-DUMMY");
        classificationImpl.setDomain("test-domain");
        classificationImpl.setServiceLevel("P2D");
        classificationImpl.setId("ID: 1");
        classificationImpl.setKey("ABC111");
        classificationImpl.setParentClassificationKey("");
        return (Classification) classificationImpl;
    }

    private ClassificationImpl createDummyCLassificationImpl() {
        return (ClassificationImpl) createDummyCLassification();
    }
}
