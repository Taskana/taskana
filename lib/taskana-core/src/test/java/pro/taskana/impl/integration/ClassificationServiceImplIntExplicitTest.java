package pro.taskana.impl.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.h2.store.fs.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.DBCleaner;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;

/**
 * Integration Test for ClassificationServiceImpl with connection management mode EXPLICIT.
 *
 * @author BBR
 */

public class ClassificationServiceImplIntExplicitTest {

    static int counter = 0;
    private DataSource dataSource;
    private ClassificationService classificationService;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;
    private static final String ID_PREFIX_CLASSIFICATION = "CLI";

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
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @Test
    public void testInsertClassification()
        throws SQLException, ClassificationNotFoundException, ClassificationAlreadyExistException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        final String domain = "test-domain";
        final String key = "dummy-key";
        ClassificationImpl expectedClassification;
        Classification actualClassification;
        Classification actualClassification2;

        // empty classification (root)
        expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey("", "t1");
        expectedClassification = (ClassificationImpl) classificationService
            .createClassification(expectedClassification);
        connection.commit();
        actualClassification = classificationService.getClassification(expectedClassification.getKey(),
            expectedClassification.getDomain());
        assertThat(actualClassification, not(equalTo(null)));
        assertThat(actualClassification.getCreated(), not(equalTo(null)));
        assertThat(actualClassification.getId(), not(equalTo(null)));
        assertThat(actualClassification.getId(), startsWith(ID_PREFIX_CLASSIFICATION));

        // specific to domain + root
        expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey(domain, "t1");
        expectedClassification.setKey(key);
        expectedClassification = (ClassificationImpl) classificationService
            .createClassification(expectedClassification);
        connection.commit();
        actualClassification = classificationService.getClassification(expectedClassification.getKey(),
            expectedClassification.getDomain());
        actualClassification2 = classificationService.getClassification(expectedClassification.getKey(), "");
        assertThat(actualClassification, not(equalTo(null)));
        assertThat(actualClassification.getCreated(), not(equalTo(null)));
        assertThat(actualClassification.getId(), not(equalTo(null)));
        assertThat(actualClassification.getKey(), equalTo(key));
        assertThat(actualClassification.getDomain(), equalTo(domain));
        assertThat(actualClassification.getId(), startsWith(ID_PREFIX_CLASSIFICATION));
        assertThat(actualClassification2, not(equalTo(null)));
        assertThat(actualClassification2.getCreated(), not(equalTo(null)));
        assertThat(actualClassification2.getId(), not(equalTo(null)));
        assertThat(actualClassification2.getId(), not(equalTo(actualClassification.getId())));
        assertThat(actualClassification2.getKey(), equalTo(key));
        assertThat(actualClassification2.getDomain(), equalTo(""));
        assertThat(actualClassification2.getId(), startsWith(ID_PREFIX_CLASSIFICATION));

        // does exist already
        try {
            expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey(domain, "t1");
            expectedClassification.setKey(key);
            classificationService.createClassification(expectedClassification);
            connection.commit();
            fail("Should have thrown 'ClassificationAlreadyExistException' here.");
        } catch (ClassificationAlreadyExistException e) {
        }

        // new classification but root existing
        expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey("", "t1");
        expectedClassification.setKey(key);
        expectedClassification.setDomain(domain + "_2");
        classificationService.createClassification(expectedClassification);
        connection.commit();
        actualClassification = classificationService.getClassification(key, domain + "_2");
        assertThat(actualClassification, not(equalTo(null)));
        assertThat(actualClassification.getCreated(), not(equalTo(null)));
        assertThat(actualClassification.getId(), not(equalTo(null)));
        assertThat(actualClassification.getKey(), equalTo(key));
        assertThat(actualClassification.getDomain(), equalTo(domain + "_2"));
        assertThat(actualClassification.getId(), startsWith(ID_PREFIX_CLASSIFICATION));
        List<ClassificationSummary> rootResults = classificationService.getAllClassifications(key, "");
        assertThat(rootResults.size(), equalTo(1));

        // invalid serviceLevel
        try {
            expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey("", "t1");
            expectedClassification.setDomain(domain + "_3");
            expectedClassification.setKey("");
            expectedClassification.setServiceLevel("ASAP");
            classificationService.createClassification(expectedClassification);
            connection.commit();
            fail("Should have thrown IllegalArgumentException, because ServiceLevel is invalid.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testFindAllClassifications()
        throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
        ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification0 = this.createNewClassificationWithUniqueKey("", "t1");
        classificationService.createClassification(classification0);
        Classification classification1 = this.createNewClassificationWithUniqueKey("", "t1");
        classificationService.createClassification(classification1);
        Classification classification2 = this.createNewClassificationWithUniqueKey("", "t1");
        classification2.setParentClassificationKey(classification0.getKey());
        classificationService.createClassification(classification2);

        Assert.assertEquals(2 + 1, classificationService.getClassificationTree().size());
        connection.commit();
    }

    @Test
    public void testModifiedClassification()
        throws SQLException, ClassificationAlreadyExistException, ClassificationNotFoundException {

        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("novatec", "t1");
        connection.commit();
        classification = classificationService.createClassification(classification);

        String updatedDescription = "TEST SOMETHING";
        classification.setDescription(updatedDescription);
        classification = classificationService.updateClassification(classification);
        connection.commit();

        classification = classificationService.getClassification(classification.getKey(), classification.getDomain());
        assertThat(classification.getDescription(), equalTo(updatedDescription));
    }

    @Test
    public void testInsertAndClassificationQuery()
        throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
        ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("", "t1");
        classificationService.createClassification(classification);
        Instant today = Instant.now();
        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .validInDomain(Boolean.TRUE)
            .created(today)
            .list();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testUpdateAndClassificationQuery() throws NotAuthorizedException, SQLException,
        ClassificationAlreadyExistException, ClassificationNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("", "t1");
        classification.setDescription("");
        classification = classificationService.createClassification(classification);
        classification.setDescription("description");
        classification = classificationService.updateClassification(classification);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().validInDomain(true).list();
        Assert.assertEquals(1, list.size());
        classification = classificationService.getClassification(classification.getKey(), classification.getDomain());
        assertThat(classification.getDescription(), equalTo("description"));

        classification = classificationService.updateClassification(classification);
        list = classificationService.createClassificationQuery()
            .list();
        Assert.assertEquals(1, list.size());

        List<ClassificationSummary> allClassifications = classificationService.getClassificationTree();
        Assert.assertEquals(1, allClassifications.size());
        connection.commit();
    }

    @Test
    public void testFindWithClassificationMapperDomainAndCategory()
        throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
        ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification1 = this.createNewClassificationWithUniqueKey("domain1", "t1");
        classification1.setCategory("category1");
        classificationService.createClassification(classification1);
        Classification classification2 = this.createNewClassificationWithUniqueKey("domain2", "t1");
        classification2.setCategory("category1");
        classificationService.createClassification(classification2);

        Classification classification3 = this.createNewClassificationWithUniqueKey("domain1", "t1");
        classification3.setCategory("category2");
        classificationService.createClassification(classification3);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .category("category1")
            .domain("domain1")
            .list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().domain("domain1", "domain3").list();
        Assert.assertEquals(2, list.size());
        connection.commit();
    }

    @Test
    public void testFindWithClassificationMapperCustomAndCategory()
        throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
        ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification1 = this.createNewClassificationWithUniqueKey("", "t1");
        classification1.setDescription("DESC1");
        classification1.setCategory("category1");
        classificationService.createClassification(classification1);
        Classification classification2 = this.createNewClassificationWithUniqueKey("", "t1");
        classification2.setDescription("DESC1");
        classification2.setCustom1("custom1");
        classification2.setCategory("category1");
        classificationService.createClassification(classification2);
        Classification classification3 = this.createNewClassificationWithUniqueKey("", "t1");
        classification3.setCustom1("custom2");
        classification3.setCustom2("custom1");
        classification3.setCategory("category2");
        classificationService.createClassification(classification3);
        Classification classification4 = this.createNewClassificationWithUniqueKey("", "t1");
        classification4.setDescription("description2");
        classification4.setCustom8("custom2");
        classification4.setCategory("category1");
        classificationService.createClassification(classification4);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .descriptionLike("DESC1")
            .customFields("custom1")
            .list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().customFields("custom2").list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().descriptionLike("DESC1").category("category1").list();
        Assert.assertEquals(2, list.size());
        connection.commit();
    }

    @Test
    public void testFindWithClassificationMapperPriorityTypeAndParent()
        throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
        ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("", "type1");
        classification.setPriority(Integer.decode("5"));
        classificationService.createClassification(classification);
        Classification classification1 = this.createNewClassificationWithUniqueKey("", "type1");
        classification1.setPriority(Integer.decode("3"));
        classification1.setParentClassificationKey(classification.getKey());
        classificationService.createClassification(classification1);
        Classification classification2 = this.createNewClassificationWithUniqueKey("", "type2");
        classification2.setPriority(Integer.decode("5"));
        classification2.setParentClassificationKey(classification.getKey());
        classificationService.createClassification(classification2);
        Classification classification3 = this.createNewClassificationWithUniqueKey("", "type1");
        classification3.setPriority(Integer.decode("5"));
        classification3.setParentClassificationKey(classification1.getKey());
        classificationService.createClassification(classification3);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .parentClassificationKey(classification.getKey())
            .list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().type("type1").priority(Integer.decode("5")).list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery()
            .priority(Integer.decode("5"))
            .type("type1")
            .parentClassificationKey(classification1.getKey())
            .list();
        Assert.assertEquals(1, list.size());
        connection.commit();
    }

    @Test
    public void testFindWithClassificationMapperServiceLevelNameAndDescription()
        throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
        ClassificationNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        int all = 0;
        Classification classification = this.createNewClassificationWithUniqueKey("", "type1");
        classification.setServiceLevel("P1D");
        classification.setName("name1");
        classification.setDescription("desc");
        classificationService.createClassification(classification);
        all++;
        Classification classification1 = this.createNewClassificationWithUniqueKey("", "type1");
        classification1.setServiceLevel("P1DT1H");
        classification1.setName("name1");
        classification1.setDescription("desc");
        classificationService.createClassification(classification1);
        all++;
        Classification classification2 = this.createNewClassificationWithUniqueKey("", "type1");
        classification2.setServiceLevel("P1D");
        classification2.setName("name");
        classification2.setDescription("desc");
        classificationService.createClassification(classification2);
        all++;
        Classification classification3 = this.createNewClassificationWithUniqueKey("", "type1");
        classification3.setName("name1");
        classification3.setDescription("description");
        classificationService.createClassification(classification3);
        all++;

        List<ClassificationSummary> list = classificationService.createClassificationQuery().name("name").list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().serviceLevel("P1D").descriptionLike("desc").list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().serviceLevel("P1DT1H").name("name").list();
        Assert.assertEquals(0, list.size());
        list = classificationService.createClassificationQuery().descriptionLike("desc%").list();
        Assert.assertEquals(all, list.size());
        connection.commit();
    }

    @Test
    public void testDefaultSettingsWithClassificationQuery() throws NotAuthorizedException, SQLException,
        ClassificationAlreadyExistException, ClassificationNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("", "type1");
        classification = classificationService.createClassification(classification);

        Classification classification1 = this.createNewClassificationWithUniqueKey("", "type1");
        classification1 = classificationService.createClassification(classification1);
        classification1.setParentClassificationKey(classification.getKey());
        classification1 = classificationService.updateClassification(classification1);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .parentClassificationKey("")
            .list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery()
            .list();
        Assert.assertEquals(2, list.size());
        connection.commit();

        list = classificationService.createClassificationQuery().validInDomain(true).list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().created(Instant.now()).list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().domain("domain1").validInDomain(false).list();
        Assert.assertEquals(0, list.size());
        list = classificationService.createClassificationQuery()
            .key(classification1.getKey())
            .list();
        Assert.assertEquals(1, list.size());

        list = classificationService.createClassificationQuery()
            .parentClassificationKey(classification.getKey())
            .list();
        Assert.assertEquals(1, list.size());
        assertThat(list.get(0).getKey(), equalTo(classification1.getKey()));
        connection.commit();
    }

    @After
    public void cleanUp() {
        taskanaEngineImpl.setConnection(null);
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/data", true);
    }

    private Classification createNewClassificationWithUniqueKey(String domain, String type) {
        Classification classification = classificationService.newClassification(domain, "TEST" + counter, type);
        counter++;
        return classification;
    }
}
