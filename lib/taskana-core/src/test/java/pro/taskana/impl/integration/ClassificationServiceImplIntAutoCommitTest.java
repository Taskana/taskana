package pro.taskana.impl.integration;

import org.h2.store.fs.FileUtils;
import org.junit.*;
import pro.taskana.ClassificationService;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.model.Classification;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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
    public void testInsertClassification() throws ClassificationNotFoundException {
        Classification classification = new Classification();
        classificationService.addClassification(classification);

        Assert.assertNotNull(classificationService.getClassification(classification.getId(), ""));
    }

    @Test
    public void testFindAllClassifications() throws NotAuthorizedException {
        Classification classification0 = new Classification();
        classificationService.addClassification(classification0);
        Classification classification1 = new Classification();
        classificationService.addClassification(classification1);
        Classification classification2 = new Classification();
        classification2.setParentClassificationId(classification0.getId());
        classificationService.addClassification(classification2);

        Assert.assertEquals(2 + 1, classificationService.getClassificationTree().size());
    }

    @Test
    public void testModifiedClassification() {

        Classification classification = new Classification();
        classificationService.addClassification(classification);
        classification.setDescription("TEST SOMETHING");
        classificationService.updateClassification(classification);

        Assert.assertEquals(classification.getValidFrom(), Date.valueOf(LocalDate.now()));
    }

    @Test
    public void testInsertAndClassificationMapper() throws NotAuthorizedException {
        Classification classification = new Classification();
        classificationService.addClassification(classification);
        Date today = Date.valueOf(LocalDate.now());
        List<Classification> list = classificationService.createClassificationQuery().validInDomain(Boolean.TRUE).created(today).validFrom(today).validUntil(Date.valueOf("9999-12-31")).list();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testUpdateAndClassificationMapper() throws NotAuthorizedException {
        Classification classification = new Classification();
        classificationService.addClassification(classification);
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

        List<Classification> allClassifications = classificationService.getClassificationTree();
        Assert.assertEquals(2, allClassifications.size());
    }

    @Test
    public void testFindWithClassificationMapperDomainAndCategory() throws NotAuthorizedException {
        Classification classification1 = new Classification();
        classification1.setDomain("domain1");
        classification1.setCategory("category1");
        classificationService.addClassification(classification1);
        Classification classification2 = new Classification();
        classification2.setDomain("domain2");
        classification2.setCategory("category1");
        classificationService.addClassification(classification2);
        Classification classification3 = new Classification();
        classification3.setDomain("domain1");
        classification3.setCategory("category2");
        classificationService.addClassification(classification3);

        List<Classification> list = classificationService.createClassificationQuery().category("category1").domain("domain1").list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().domain("domain1", "domain3").list();
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testFindWithClassificationMapperCustomAndCategory() throws NotAuthorizedException {
        Classification classification1 = new Classification();
        classification1.setDescription("DESC1");
        classification1.setCategory("category1");
        classificationService.addClassification(classification1);
        Classification classification2 = new Classification();
        classification2.setDescription("DESC1");
        classification2.setCustom1("custom1");
        classification2.setCategory("category1");
        classificationService.addClassification(classification2);
        Classification classification3 = new Classification();
        classification3.setCustom1("custom2");
        classification3.setCustom2("custom1");
        classification3.setCategory("category2");
        classificationService.addClassification(classification3);
        Classification classification4 = new Classification();
        classification4.setDescription("description2");
        classification4.setCustom8("custom2");
        classification4.setCategory("category1");
        classificationService.addClassification(classification4);

        List<Classification> list = classificationService.createClassificationQuery().descriptionLike("DESC1").customFields("custom1").list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().customFields("custom2").list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().descriptionLike("DESC1").category("category1").list();
        Assert.assertEquals(2, list.size());
    }

    @Test
    public void testFindWithClassificationMapperPriorityTypeAndParent() throws NotAuthorizedException {
        Classification classification = new Classification();
        classification.setPriority(Integer.decode("5"));
        classification.setType("type1");
        classificationService.addClassification(classification);
        Classification classification1 = new Classification();
        classification1.setPriority(Integer.decode("3"));
        classification1.setType("type1");
        classification1.setParentClassificationId(classification.getId());
        classificationService.addClassification(classification1);
        Classification classification2 = new Classification();
        classification2.setPriority(Integer.decode("5"));
        classification2.setType("type2");
        classification2.setParentClassificationId(classification.getId());
        classificationService.addClassification(classification2);
        Classification classification3 = new Classification();
        classification3.setPriority(Integer.decode("5"));
        classification3.setType("type1");
        classification3.setParentClassificationId(classification1.getId());
        classificationService.addClassification(classification3);

        List<Classification> list = classificationService.createClassificationQuery().parentClassification(classification.getId()).list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().type("type1").priority(Integer.decode("5")).list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().priority(Integer.decode("5")).type("type1").parentClassification(classification1.getId()).list();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testFindWithClassificationMapperServiceLevelNameAndDescription() throws NotAuthorizedException {
        int all = 0;
        Classification classification = new Classification();
        classification.setServiceLevel("P1D");
        classification.setName("name1");
        classification.setDescription("desc");
        classificationService.addClassification(classification);
        all++;
        Classification classification1 = new Classification();
        classification1.setServiceLevel("P1DT1H");
        classification1.setName("name1");
        classification1.setDescription("desc");
        classificationService.addClassification(classification1);
        all++;
        Classification classification2 = new Classification();
        classification2.setServiceLevel("P1D");
        classification2.setName("name");
        classification2.setDescription("desc");
        classificationService.addClassification(classification2);
        all++;
        Classification classification3 = new Classification();
        classification3.setName("name1");
        classification3.setDescription("description");
        classificationService.addClassification(classification3);
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
    public void testDefaultSettingsWithClassificationMapper() throws NotAuthorizedException {
        Classification classification = new Classification();
        Classification classification1 = new Classification();
        classificationService.addClassification(classification);
        classificationService.addClassification(classification1);
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
