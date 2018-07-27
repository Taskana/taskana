package pro.taskana.impl.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.TimeInterval;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
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

    private static final String ID_PREFIX_CLASSIFICATION = "CLI";
    static int counter = 0;
    private DataSource dataSource;
    private ClassificationService classificationService;
    private TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;

    @BeforeClass
    public static void resetDb() {
        DataSource ds = TaskanaEngineConfigurationTest.getDataSource();
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(ds, true);
    }

    @Before
    public void setup() throws SQLException {
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false, false,
            TaskanaEngineConfigurationTest.getSchemaName());
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        classificationService = taskanaEngine.getClassificationService();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
        DBCleaner cleaner = new DBCleaner();
        cleaner.clearDb(dataSource, false);
    }

    @Test
    public void testInsertClassification()
        throws SQLException, ClassificationNotFoundException, ClassificationAlreadyExistException,
        NotAuthorizedException, DomainNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);

        final String domain = "DOMAIN_A";
        final String key = "dummy-key";
        ClassificationImpl expectedClassification;
        Classification actualClassification;
        Classification actualClassification2;

        // empty classification (master)
        expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey("", "TASK");
        expectedClassification = (ClassificationImpl) classificationService
            .createClassification(expectedClassification);
        connection.commit();
        actualClassification = classificationService.getClassification(expectedClassification.getKey(),
            expectedClassification.getDomain());
        assertThat(actualClassification, not(equalTo(null)));
        assertThat(actualClassification.getCreated(), not(equalTo(null)));
        assertThat(actualClassification.getId(), not(equalTo(null)));
        assertThat(actualClassification.getId(), startsWith(ID_PREFIX_CLASSIFICATION));

        // specific to domain + master
        expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey(domain, "TASK");
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
            expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey(domain, "TASK");
            expectedClassification.setKey(key);
            classificationService.createClassification(expectedClassification);
            connection.commit();
            fail("Should have thrown 'ClassificationAlreadyExistException' here.");
        } catch (ClassificationAlreadyExistException e) {
        }

        // new classification but master existing
        expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey("", "TASK");
        expectedClassification.setKey(key);
        expectedClassification.setDomain("DOMAIN_B");
        classificationService.createClassification(expectedClassification);
        connection.commit();
        actualClassification = classificationService.getClassification(key, "DOMAIN_B");
        assertThat(actualClassification, not(equalTo(null)));
        assertThat(actualClassification.getCreated(), not(equalTo(null)));
        assertThat(actualClassification.getId(), not(equalTo(null)));
        assertThat(actualClassification.getKey(), equalTo(key));
        assertThat(actualClassification.getDomain(), equalTo("DOMAIN_B"));
        assertThat(actualClassification.getId(), startsWith(ID_PREFIX_CLASSIFICATION));
        Classification masterResult = classificationService.getClassification(key, "");
        assertThat(masterResult, not(equalTo(null)));

        // invalid serviceLevel
        try {
            expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey("", "TASK");
            expectedClassification.setDomain(domain);
            expectedClassification.setKey("");
            expectedClassification.setServiceLevel("ASAP");
            classificationService.createClassification(expectedClassification);
            connection.commit();
            fail("Should have thrown IllegalArgumentException, because ServiceLevel is invalid.");
        } catch (InvalidArgumentException e) {
        }
    }

    @Test
    public void testFindAllClassifications()
        throws SQLException, ClassificationAlreadyExistException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification0 = this.createNewClassificationWithUniqueKey("", "TASK");
        classificationService.createClassification(classification0);
        Classification classification1 = this.createNewClassificationWithUniqueKey("", "TASK");
        classificationService.createClassification(classification1);
        Classification classification2 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification2.setParentId(classification0.getId());
        classificationService.createClassification(classification2);

        Assert.assertEquals(2 + 1, classificationService.createClassificationQuery().list().size());
        connection.commit();
    }

    @Test
    public void testModifiedClassification()
        throws SQLException, ClassificationAlreadyExistException, ClassificationNotFoundException,
        NotAuthorizedException, ConcurrencyException, DomainNotFoundException, InvalidArgumentException {

        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
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
        throws SQLException, ClassificationAlreadyExistException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
        classificationService.createClassification(classification);
        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .validInDomainEquals(Boolean.TRUE)
            .createdWithin(today())
            .list();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testUpdateAndClassificationQuery() throws NotAuthorizedException, SQLException,
        ClassificationAlreadyExistException, ClassificationNotFoundException, ConcurrencyException,
        DomainNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
        classification.setDescription("");
        classification = classificationService.createClassification(classification);
        classification.setDescription("description");
        classification = classificationService.updateClassification(classification);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().validInDomainEquals(true).list();
        Assert.assertEquals(1, list.size());
        classification = classificationService.getClassification(classification.getKey(), classification.getDomain());
        assertThat(classification.getDescription(), equalTo("description"));

        classification = classificationService.updateClassification(classification);
        list = classificationService.createClassificationQuery()
            .list();
        Assert.assertEquals(2, list.size());

        List<ClassificationSummary> allClassifications = classificationService.createClassificationQuery().list();
        Assert.assertEquals(2, allClassifications.size());
        connection.commit();
    }

    @Test
    public void testFindWithClassificationMapperDomainAndCategory()
        throws SQLException, ClassificationAlreadyExistException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification1 = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
        classification1.setCategory("EXTERNAL");
        classificationService.createClassification(classification1);
        Classification classification2 = this.createNewClassificationWithUniqueKey("DOMAIN_B", "TASK");
        classification2.setCategory("EXTERNAL");
        classificationService.createClassification(classification2);

        Classification classification3 = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
        classification3.setCategory("MANUAL");
        classificationService.createClassification(classification3);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .categoryIn("EXTERNAL")
            .domainIn("DOMAIN_A")
            .list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().domainIn("DOMAIN_A", "domain3").list();
        Assert.assertEquals(2, list.size());
        connection.commit();
    }

    @Test
    public void testFindWithClassificationMapperCustomAndCategory()
        throws SQLException, ClassificationAlreadyExistException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification1 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification1.setDescription("DESC1");
        classification1.setCategory("EXTERNAL");
        classificationService.createClassification(classification1);
        Classification classification2 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification2.setDescription("DESC1");
        classification2.setCustom1("custom1");
        classification2.setCategory("EXTERNAL");
        classificationService.createClassification(classification2);
        Classification classification3 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification3.setCustom1("custom2");
        classification3.setCustom2("custom1");
        classification3.setCategory("MANUAL");
        classificationService.createClassification(classification3);
        Classification classification4 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification4.setDescription("description2");
        classification4.setCustom8("custom2");
        classification4.setCategory("EXTERNAL");
        classificationService.createClassification(classification4);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .descriptionLike("DESC1")
            .customAttributeIn("1", "custom1")
            .list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().customAttributeIn("1", "custom2").list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery()
            .descriptionLike("DESC1")
            .categoryIn("EXTERNAL")
            .list();
        Assert.assertEquals(2, list.size());
        connection.commit();
    }

    @Test
    public void testFindWithClassificationMapperPriorityTypeAndParent()
        throws SQLException, ClassificationAlreadyExistException, NotAuthorizedException,
        DomainNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("", "TASK");
        classification.setPriority(Integer.decode("5"));
        classificationService.createClassification(classification);
        Classification classification1 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification1.setPriority(Integer.decode("3"));
        classification1.setParentId(classification.getId());
        classificationService.createClassification(classification1);
        Classification classification2 = this.createNewClassificationWithUniqueKey("", "DOCUMENT");
        classification2.setPriority(Integer.decode("5"));
        classification2.setParentId(classification.getId());
        classificationService.createClassification(classification2);
        Classification classification3 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification3.setPriority(Integer.decode("5"));
        classification3.setParentId(classification1.getId());
        classificationService.createClassification(classification3);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .parentIdIn(classification.getId())
            .list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().typeIn("TASK").priorityIn(Integer.decode("5")).list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery()
            .priorityIn(Integer.decode("5"))
            .typeIn("TASK")
            .parentIdIn(classification1.getId())
            .list();
        Assert.assertEquals(1, list.size());
        connection.commit();
    }

    @Test
    public void testFindWithClassificationMapperServiceLevelNameAndDescription()
        throws NotAuthorizedException, SQLException, ClassificationAlreadyExistException,
        DomainNotFoundException, InvalidArgumentException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        int all = 0;
        Classification classification = this.createNewClassificationWithUniqueKey("", "TASK");
        classification.setServiceLevel("P1D");
        classification.setName("name1");
        classification.setDescription("desc");
        classificationService.createClassification(classification);
        all++;
        Classification classification1 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification1.setServiceLevel("P1DT1H");
        classification1.setName("name1");
        classification1.setDescription("desc");
        classificationService.createClassification(classification1);
        all++;
        Classification classification2 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification2.setServiceLevel("P1D");
        classification2.setName("name");
        classification2.setDescription("desc");
        classificationService.createClassification(classification2);
        all++;
        Classification classification3 = this.createNewClassificationWithUniqueKey("", "TASK");
        classification3.setName("name1");
        classification3.setDescription("description");
        classificationService.createClassification(classification3);
        all++;

        List<ClassificationSummary> list = classificationService.createClassificationQuery().nameIn("name").list();
        Assert.assertEquals(1, list.size());
        list = classificationService.createClassificationQuery().serviceLevelIn("P1D").descriptionLike("desc").list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().serviceLevelIn("P1DT1H").nameIn("name").list();
        Assert.assertEquals(0, list.size());
        list = classificationService.createClassificationQuery().descriptionLike("desc%").list();
        Assert.assertEquals(all, list.size());
        connection.commit();
    }

    @Test
    public void testDefaultSettingsWithClassificationQuery() throws NotAuthorizedException, SQLException,
        ClassificationAlreadyExistException, ClassificationNotFoundException, InvalidArgumentException,
        ConcurrencyException, DomainNotFoundException {
        Connection connection = dataSource.getConnection();
        taskanaEngineImpl.setConnection(connection);
        Classification classification = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
        classification = classificationService.createClassification(classification);

        Classification classification1 = this.createNewClassificationWithUniqueKey("DOMAIN_A", "TASK");
        classification1 = classificationService.createClassification(classification1);
        classification1.setParentId(classification.getId());
        classification1 = classificationService.updateClassification(classification1);

        List<ClassificationSummary> list = classificationService.createClassificationQuery()
            .parentIdIn("")
            .list();
        Assert.assertEquals(3, list.size());
        list = classificationService.createClassificationQuery()
            .list();
        Assert.assertEquals(4, list.size());
        connection.commit();

        list = classificationService.createClassificationQuery().validInDomainEquals(true).list();
        Assert.assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().createdWithin(today()).list();
        Assert.assertEquals(4, list.size());
        list = classificationService.createClassificationQuery().domainIn("DOMAIN_C").validInDomainEquals(false).list();
        Assert.assertEquals(0, list.size());
        list = classificationService.createClassificationQuery()
            .keyIn(classification1.getKey())
            .list();
        Assert.assertEquals(2, list.size());

        list = classificationService.createClassificationQuery()
            .parentIdIn(classification.getId())
            .list();
        Assert.assertEquals(1, list.size());
        assertThat(list.get(0).getKey(), equalTo(classification1.getKey()));
        connection.commit();
    }

    @After
    public void cleanUp() throws SQLException {
        taskanaEngineImpl.setConnection(null);
    }

    private Classification createNewClassificationWithUniqueKey(String domain, String type) {
        Classification classification = classificationService.newClassification("TEST" + counter, domain, type);
        counter++;
        return classification;
    }

    private TimeInterval today() {
        Instant begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant();
        Instant end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
        return new TimeInterval(begin, end);
    }

}
