package pro.taskana.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.Classification;
import pro.taskana.model.mappings.ClassificationMapper;

/**
 * Unit Test for ClassificationServiceImpl.
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationServiceImplTest {

    @Spy
    @InjectMocks
    ClassificationServiceImpl classificationService;

    @Mock
    ClassificationMapper classificationMapper;

    @Mock
    TaskanaEngineImpl taskanaEngineImpl;


    @Test
    public void testAddClassification() {
        doNothing().when(classificationMapper).insert(any());

        Classification classification = new Classification();
        classification.setId("0");
        classificationService.addClassification(classification);

        when(classificationMapper.findByIdAndDomain(any(), any(), any())).thenReturn(classification);

        Assert.assertNotNull(classificationService.getClassification(classification.getId(), ""));
    }

    @Test
    public void testModifiedClassification() {
        doNothing().when(classificationMapper).insert(any());
        doNothing().when(classificationMapper).update(any());
        doNothing().when(taskanaEngineImpl).openConnection();
        doNothing().when(taskanaEngineImpl).returnConnection();

        int insert = 0;

        Classification classification = new Classification();
        classification.setId("0");
        classificationService.addClassification(classification);
        insert++;

        when(classificationMapper.findByIdAndDomain(any(), any(), any())).thenReturn(classification);

        //same domain
        Classification classification2 = new Classification();
        classification2.setId(classification.getId());
        classification2.setDescription("TEST EVERYTHING");
        classificationService.updateClassification(classification2);
        insert++;

        //different domain
        Classification classification3 = new Classification();
        classification3.setId(classification.getId());
        classification3.setDomain("testDomain");
        classificationService.updateClassification(classification3);
        insert++;

        verify(classificationMapper, times(1)).update(any()); // update when same domain
        verify(classificationMapper, times(insert)).insert(any()); // insert all classifications

        Assert.assertEquals(classification.getValidUntil(), Date.valueOf(LocalDate.now().minusDays(1)));
        Assert.assertEquals(classification2.getValidUntil(), classification3.getValidUntil());
    }

    @Test
    public void testFindAllClassifications() throws NotAuthorizedException {
        doNothing().when(classificationMapper).insert(any());
        doNothing().when(taskanaEngineImpl).openConnection();
        doNothing().when(taskanaEngineImpl).returnConnection();

        // insert Classifications
        Classification classification0 = new Classification();
        classificationService.addClassification(classification0);
        Classification classification1 = new Classification();
        classificationService.addClassification(classification1);
        Classification classification2 = new Classification();
        classification2.setParentClassificationId(classification0.getId());
        classificationService.addClassification(classification2);

        //update Classification1
        Classification classification11 = new Classification();
        classification11.setId(classification1.getId());
        when(classificationMapper.findByIdAndDomain(any(), any(), any())).thenReturn(classification1);
        classificationService.updateClassification(classification11);

        List<Classification> classifications = new ArrayList<>();
        classifications.add(classification0);
        classifications.add(classification1);
        classifications.add(classification2);
        classifications.add(classification11);
        doReturn(new TestClassificationQuery(classifications)).when(classificationService).createClassificationQuery();

        List<Classification> classificationList = classificationService.getClassificationTree();

        verify(classificationMapper, atLeast(2)).insert(any());
        Assert.assertEquals(2 + 1, classificationList.size());
    }

    @Test
    public void testClassificationQuery() throws NotAuthorizedException {
        doNothing().when(classificationMapper).insert(any());
        doNothing().when(taskanaEngineImpl).openConnection();
        doNothing().when(taskanaEngineImpl).returnConnection();
        Classification classification = new Classification();
        classification.setDescription("DESC");
        classificationService.addClassification(classification);
        Classification classification1 = new Classification();
        classification1.setDescription("ABC");
        classificationService.addClassification(classification1);

        List<Classification> classifications = new ArrayList<>();
        classifications.add(classification);
        classifications.add(classification1);

        doReturn(new TestClassificationQuery(classifications)).when(classificationService).createClassificationQuery();

        List<Classification> classificationDESC = classificationService.createClassificationQuery().descriptionLike("DESC").list();
        Assert.assertEquals(1, classificationDESC.size());
    }
}
