package org.taskana.impl.integration;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.taskana.ClassificationService;
import org.taskana.TaskanaEngine;
import org.taskana.configuration.TaskanaEngineConfiguration;
import org.taskana.model.Classification;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Integration Test for ClassificationServiceImpl.
 * @author EH
 */
public class ClassificationServiceImplIntTest {
    static int counter = 0;
    private ClassificationService classificationService;

    @Before
    public void setup() throws FileNotFoundException, SQLException, LoginException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:test-db-classification" + counter++);
        ds.setPassword("sa");
        ds.setUser("sa");
        TaskanaEngineConfiguration taskEngineConfiguration = new TaskanaEngineConfiguration(ds, false);

        TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();
        classificationService = te.getClassificationService();
    }

    @Test
    public void testInsertClassification() {
        Classification classification = new Classification();
        classification.setId("0");
        classificationService.insertClassification(classification);

        Assert.assertNotNull(classificationService.selectClassificationById(classification.getId()));
    }

    @Test
    public void testFindAllClassifications() {
        Classification classification0 = new Classification();
        classification0.setId("0");
        classification0.setParentClassificationId("");
        classificationService.insertClassification(classification0);
        Classification classification1 = new Classification();
        classification1.setId("1");
        classification1.setParentClassificationId("");
        classificationService.insertClassification(classification1);

        Assert.assertEquals(2, classificationService.selectClassifications().size());
    }

    @Test
    public void testFindByParentClassification() {
        Classification classification0 = new Classification();
        classification0.setId("0");
        classification0.setParentClassificationId("0");
        classificationService.insertClassification(classification0);
        Classification classification1 = new Classification();
        classification1.setId("1");
        classification1.setParentClassificationId("0");
        classificationService.insertClassification(classification1);

        Assert.assertEquals(2, classificationService.selectClassificationsByParentId("0").size());
    }

    @Test
    public void testModifiedClassification() {
        Classification classification = new Classification();
        classificationService.insertClassification(classification);
        classification.setDescription("TEST EVERYTHING");
        classificationService.updateClassification(classification);

        Assert.assertEquals(classification.getModified().toString(), LocalDate.now().toString());
    }

    @Test
    public void testFindByDomainAndCategory() {
        Classification classification1 = new Classification();
        classification1.setDomain("domain1");
        classification1.setCategory("category1");
        classificationService.insertClassification(classification1);
        Classification classification2 = new Classification();
        classification2.setDomain("domain2");
        classification2.setCategory("category1");
        classificationService.insertClassification(classification2);
        Classification classification3 = new Classification();
        classification3.setDomain("domain1");
        classification3.setCategory("category2");
        classificationService.insertClassification(classification3);

        int sizeOfReturn = classificationService.selectClassificationByDomainAndCategory("domain1", "category1").size();
        Assert.assertEquals(sizeOfReturn, 1);
    }

}
