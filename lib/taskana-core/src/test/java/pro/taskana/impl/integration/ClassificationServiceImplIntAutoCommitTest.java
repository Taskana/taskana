package pro.taskana.impl.integration;

import java.io.FileNotFoundException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.model.ClassificationImpl;

/**
 * Integration Test for ClassificationServiceImpl with connection management mode AUTOCOMMIT.
 * @author EH
 */
public class ClassificationServiceImplIntAutoCommitTest {
    static int counter = 0;

    private DataSource dataSource;
    private ClassificationService classificationService;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;

    @BeforeClass
    public static void resetDb() throws SQLException {
        DataSource ds = TaskanaEngineConfigurationTest.getDataSource();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(ds, true);
    }

    @Before
    public void setup() throws FileNotFoundException, SQLException, LoginException {
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        classificationService = taskanaEngine.getClassificationService();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @Test
    public void testInsertClassification() throws ClassificationAlreadyExistException, ClassificationNotFoundException {
        ClassificationImpl classification = new ClassificationImpl();
        classificationService.createClassification(classification);

        Assert.assertNotNull(classificationService.getClassification(classification.getId(), ""));
    }

    @Test
    public void testFindAllClassifications() throws NotAuthorizedException, ClassificationAlreadyExistException {
        ClassificationImpl classification0 = new ClassificationImpl();
        classificationService.createClassification(classification0);
        ClassificationImpl classification1 = new ClassificationImpl();
        classificationService.createClassification(classification1);
        ClassificationImpl classification2 = new ClassificationImpl();
        classification2.setParentClassificationId(classification0.getId());
        classificationService.createClassification(classification2);

        Assert.assertEquals(2 + 1, classificationService.getClassificationTree().size());
    }

    @Test
    public void testModifiedClassification() throws ClassificationAlreadyExistException, ClassificationNotFoundException {

        ClassificationImpl classification = new ClassificationImpl();
        classificationService.createClassification(classification);
        classification.setDescription("TEST SOMETHING");
        classificationService.updateClassification(classification);

        Assert.assertEquals(classification.getValidFrom(), Date.valueOf(LocalDate.now()));
    }

    @Test
    public void testInsertAndClassificationMapper() throws NotAuthorizedException, ClassificationAlreadyExistException {
        ClassificationImpl classification = new ClassificationImpl();
        classificationService.createClassification(classification);
        Date today = Date.valueOf(LocalDate.now());
        List<Classification> list = classificationService.createClassificationQuery().validInDomain(Boolean.TRUE).created(today).validFrom(today).validUntil(Date.valueOf("9999-12-31")).list();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testUpdateAndClassificationMapper() throws NotAuthorizedException, ClassificationAlreadyExistException, ClassificationNotFoundException {
        ClassificationImpl classification = new ClassificationImpl();
        classificationService.createClassification(classification);
        System.out.println(classification.getId());
        classification.setDescription("description");
        classificationService.updateClassification(classification);

        List<Classification> list = classificationService.createClassificationQuery().validUntil(Date.valueOf("9999-12-31")).list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().validInDomain(true).list();
        Assert.assertEquals(2, list.size());

        classification.setDomain("domain");
        classificationService.updateClassification(classification);
        System.out.println(classification.getId());
        list = classificationService.createClassificationQuery().validUntil(Date.valueOf("9999-12-31")).list();
        Assert.assertEquals(2, list.size());

        System.out.println(classification.getParentClassificationId());

        List<Classification> temp = classificationService.getClassificationTree();
        List<ClassificationImpl> allClassifications = new ArrayList<>();
        temp.stream().forEach(c -> allClassifications.add((ClassificationImpl) c));
        Assert.assertEquals(2, allClassifications.size());
    }

    @Test
    public void testFindWithClassificationMapperDomainAndCategory() throws NotAuthorizedException, ClassificationAlreadyExistException {
        ClassificationImpl classification1 = new ClassificationImpl();
        classification1.setDomain("domain1");
        classification1.setCategory("category1");
        classificationService.createClassification(classification1);
        ClassificationImpl classification2 = new ClassificationImpl();
        classification2.setDomain("domain2");
        classification2.setCategory("category1");
        classificationService.createClassification(classification2);
        ClassificationImpl classification3 = new ClassificationImpl();
        classification3.setDomain("domain1");
        classification3.setCategory("category2");
        classificationService.createClassification(classification3);

        List<Classification> list = classificationService.createClassificationQuery().category("category1").domain("domain1").list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().domain("domain1", "domain3").list();
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testFindWithClassificationMapperCustomAndCategory() throws NotAuthorizedException, ClassificationAlreadyExistException {
        ClassificationImpl classification1 = new ClassificationImpl();
        classification1.setDescription("DESC1");
        classification1.setCategory("category1");
        classificationService.createClassification(classification1);
        ClassificationImpl classification2 = new ClassificationImpl();
        classification2.setDescription("DESC1");
        classification2.setCustom1("custom1");
        classification2.setCategory("category1");
        classificationService.createClassification(classification2);
        ClassificationImpl classification3 = new ClassificationImpl();
        classification3.setCustom1("custom2");
        classification3.setCustom2("custom1");
        classification3.setCategory("category2");
        classificationService.createClassification(classification3);
        ClassificationImpl classification4 = new ClassificationImpl();
        classification4.setDescription("description2");
        classification4.setCustom8("custom2");
        classification4.setCategory("category1");
        classificationService.createClassification(classification4);

        List<Classification> list = classificationService.createClassificationQuery().descriptionLike("DESC1").customFields("custom1").list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().customFields("custom2").list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().descriptionLike("DESC1").category("category1").list();
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testFindWithClassificationMapperPriorityTypeAndParent() throws NotAuthorizedException, ClassificationAlreadyExistException {
        ClassificationImpl classification = new ClassificationImpl();
        classification.setPriority(Integer.decode("5"));
        classification.setType("type1");
        classificationService.createClassification(classification);
        ClassificationImpl classification1 = new ClassificationImpl();
        classification1.setPriority(Integer.decode("3"));
        classification1.setType("type1");
        classification1.setParentClassificationId(classification.getId());
        classificationService.createClassification(classification1);
        ClassificationImpl classification2 = new ClassificationImpl();
        classification2.setPriority(Integer.decode("5"));
        classification2.setType("type2");
        classification2.setParentClassificationId(classification.getId());
        classificationService.createClassification(classification2);
        ClassificationImpl classification3 = new ClassificationImpl();
        classification3.setPriority(Integer.decode("5"));
        classification3.setType("type1");
        classification3.setParentClassificationId(classification1.getId());
        classificationService.createClassification(classification3);

        List<Classification> list = classificationService.createClassificationQuery().parentClassification(classification.getId()).list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().type("type1").priority(Integer.decode("5")).list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().priority(Integer.decode("5")).type("type1").parentClassification(classification1.getId()).list();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testFindWithClassificationMapperServiceLevelNameAndDescription() throws NotAuthorizedException, ClassificationAlreadyExistException {
        int all = 0;
        ClassificationImpl classification = new ClassificationImpl();
        classification.setServiceLevel("P1D");
        classification.setName("name1");
        classification.setDescription("desc");
        classificationService.createClassification(classification);
        all++;
        ClassificationImpl classification1 = new ClassificationImpl();
        classification1.setServiceLevel("P1DT1H");
        classification1.setName("name1");
        classification1.setDescription("desc");
        classificationService.createClassification(classification1);
        all++;
        ClassificationImpl classification2 = new ClassificationImpl();
        classification2.setServiceLevel("P1D");
        classification2.setName("name");
        classification2.setDescription("desc");
        classificationService.createClassification(classification2);
        all++;
        ClassificationImpl classification3 = new ClassificationImpl();
        classification3.setName("name1");
        classification3.setDescription("description");
        classificationService.createClassification(classification3);
        all++;

        List<Classification> list = classificationService.createClassificationQuery().name("name").list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().serviceLevel("P1D").descriptionLike("desc").list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().serviceLevel("P1DT1H").name("name").list();
        Assert.assertEquals(0, list.size());
        list = classificationService.createClassificationQuery().descriptionLike("desc%").list();
        Assert.assertEquals(all, list.size());
    }

    @Test
    public void testDefaultSettingsWithClassificationMapper() throws NotAuthorizedException, ClassificationAlreadyExistException, ClassificationNotFoundException {
        ClassificationImpl classification = new ClassificationImpl();
        ClassificationImpl classification1 = new ClassificationImpl();
        classificationService.createClassification(classification);
        classificationService.createClassification(classification1);
        classification1.setParentClassificationId(classification.getId());
        classificationService.updateClassification(classification1);

        List<Classification>
        list = classificationService.createClassificationQuery().parentClassification("").list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().validUntil(Date.valueOf("9999-12-31")).list();
        Assert.assertEquals(2, list.size());

        List<Classification> listAll = classificationService.createClassificationQuery().list();
        list = classificationService.createClassificationQuery().validFrom(Date.valueOf(LocalDate.now())).list();
        Assert.assertEquals(listAll.size(), list.size());
        list = classificationService.createClassificationQuery().validInDomain(true).list();
        Assert.assertEquals(listAll.size(), list.size());
        list = classificationService.createClassificationQuery().created(Date.valueOf(LocalDate.now())).list();
        Assert.assertEquals(listAll.size(), list.size());

        list = classificationService.createClassificationQuery().domain("domain1").validInDomain(false).list();
        Assert.assertEquals(0, list.size());
        list = classificationService.createClassificationQuery().validFrom(Date.valueOf((LocalDate.now()))).validUntil(Date.valueOf(LocalDate.now().minusDays(1))).list();
        Assert.assertEquals(1, list.size());
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

}
