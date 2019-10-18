package pro.taskana.impl;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.Classification;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.mappings.ClassificationMapper;

/**
 * Unit Test for ClassificationServiceImpl.
 *
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationServiceImplTest {

    @Spy
    @InjectMocks
    private ClassificationServiceImpl cutSpy;
    @Mock
    private ClassificationMapper classificationMapperMock;
    @Mock
    private TaskanaEngine taskanaEngineMock;
    @Mock
    private InternalTaskanaEngine internalTaskanaEngineMock;
    @Mock
    private ClassificationQueryImpl classificationQueryImplMock;

    @Before
    public void setup() {
        when(internalTaskanaEngineMock.getEngine()).thenReturn(taskanaEngineMock);
    }

    @Test
    public void testCreateClassificationQuery() {
        cutSpy.createClassificationQuery();
        verifyNoMoreInteractions(classificationMapperMock, internalTaskanaEngineMock, taskanaEngineMock,
            classificationQueryImplMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void testThrowExceptionIdIfClassificationIsCreatedWithAnExplicitId()
        throws DomainNotFoundException, InvalidArgumentException,
        NotAuthorizedException, ClassificationAlreadyExistException {
        try {
            Classification classification = createDummyClassification();
            when(internalTaskanaEngineMock.domainExists(any())).thenReturn(true);
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

}
