package org.taskana.impl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.taskana.model.Classification;
import org.taskana.model.mappings.ClassificationMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test for ClassificationServiceImpl.
 * @author EH
 */
@RunWith(MockitoJUnitRunner.class)
public class ClassificationServiceImplTest {

    @InjectMocks
    ClassificationServiceImpl classificationService;

    @Mock
    ClassificationMapper classificationMapper;

    @Test
    public void testInsertClassification() {
        doNothing().when(classificationMapper).insert(any());

        Classification classification = new Classification();
        classification.setId("0");
        classificationService.insertClassification(classification);

        when(classificationMapper.findByIdAndDomain(any(), any())).thenReturn(classification);

        Assert.assertNotNull(classificationService.selectClassificationById(classification.getId()));
    }

    @Test
    public void testFindAllClassifications() {
        doNothing().when(classificationMapper).insert(any());

        Classification classification0 = new Classification();
        classification0.setId("0");
        classification0.setParentClassificationId("");
        classificationService.insertClassification(classification0);
        Classification classification1 = new Classification();
        classification1.setId("1");
        classification1.setParentClassificationId("");
        classificationService.insertClassification(classification1);

        List<Classification> classifications = new ArrayList<>();
        classifications.add(classification0);
        when(classificationMapper.findByParentId("")).thenReturn(classifications);

        verify(classificationMapper, atLeast(2)).insert(any());
        Assert.assertEquals(1, classificationService.selectClassifications().size());
    }

    @Test
    public void testFindByParentClassification() {
        doNothing().when(classificationMapper).insert(any());

        Classification classification0 = new Classification();
        classification0.setId("0");
        classification0.setParentClassificationId("0");
        classificationService.insertClassification(classification0);
        Classification classification1 = new Classification();
        classification1.setId("1");
        classification1.setParentClassificationId("0");
        classificationService.insertClassification(classification1);

        List<Classification> classifications = new ArrayList<>();
        classifications.add(classification0);
        classifications.add(classification1);
        when(classificationMapper.findByParentId(any())).thenReturn(classifications);

        verify(classificationMapper, times(2)).insert(any());

        Assert.assertEquals(2, classificationService.selectClassificationsByParentId("0").size());
    }

    @Test
    public void testModifiedClassificationSameDay() {
        doNothing().when(classificationMapper).insert(any());
        doNothing().when(classificationMapper).update(any());

        Classification classification = new Classification();
        classificationService.insertClassification(classification);

        when(classificationMapper.findByIdAndDomain(any(), eq(""))).thenReturn(classification);
        when(classificationMapper.findByIdAndDomain(any(), eq("testDomain"))).thenReturn(null);

        //Same day, same domain
        Classification classification2 = classification;
        classification2.setDescription("TEST EVERYTHING");
        classificationService.updateClassification(classification2);

        //Same day, different domain
        Classification classification3 = classification;
        classification3.setDomain("testDomain");

        classificationService.updateClassification(classification3);

        verify(classificationMapper, times(1)).update(any());
        verify(classificationMapper, times(2)).insert(any()); // insert classification, insert classification 3

        Assert.assertEquals(classification.getValidFrom(), Date.valueOf(LocalDate.now()));
        Assert.assertEquals(classification.getValidUntil(), classification3.getValidUntil());
    }

    @Test
    public void testModifiedClassificationDifferentDates() {
        doNothing().when(classificationMapper).insert(any());
        doNothing().when(classificationMapper).update(any());

        int insert = 0;

        Classification classification = new Classification();
        classificationService.insertClassification(classification);
        insert++;
        classification.setValidFrom(Date.valueOf(LocalDate.now().minusDays(1)));

        when(classificationMapper.findByIdAndDomain(any(), eq(""))).thenReturn(classification);
        when(classificationMapper.findByIdAndDomain(any(), eq("testDomain"))).thenReturn(null);

        //Different day, same domain
        Classification classification2 = classification;
        classification2.setDescription("TEST EVERYTHING");
        classificationService.updateClassification(classification2);
        insert++;

        //Different day, different domain
        Classification classification3 = classification;
        classification3.setId(classification.getId());
        classification3.setDomain("testDomain");
        classificationService.updateClassification(classification3);
        insert++;

        verify(classificationMapper, times(1)).update(any()); // update when same domain
        verify(classificationMapper, times(insert)).insert(any()); // insert all classifications

        Assert.assertEquals(classification.getValidFrom(), Date.valueOf(LocalDate.now()));
        Assert.assertEquals(classification2.getValidUntil(), classification3.getValidUntil());
    }
}
