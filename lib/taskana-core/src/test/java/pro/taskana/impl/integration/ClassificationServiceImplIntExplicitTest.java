package pro.taskana.impl.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import javax.sql.DataSource;

import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.taskana.Classification;
import pro.taskana.ClassificationService;
import pro.taskana.ClassificationSummary;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.TimeInterval;
import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.TaskanaEngineImpl;
import pro.taskana.impl.configuration.TaskanaEngineTestConfiguration;
import pro.taskana.sampledata.SampleDataGenerator;

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
    private pro.taskana.configuration.TaskanaEngineConfiguration taskanaEngineConfiguration;
    private TaskanaEngine taskanaEngine;
    private TaskanaEngineImpl taskanaEngineImpl;

    @BeforeAll
    public static void resetDb() throws SQLException {
        DataSource ds = TaskanaEngineTestConfiguration.getDataSource();
        String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
        new SampleDataGenerator(ds, schemaName).dropDb();
    }

    @BeforeEach
    public void setup() throws SQLException {
        dataSource = TaskanaEngineTestConfiguration.getDataSource();
        String schemaName = TaskanaEngineTestConfiguration.getSchemaName();
        taskanaEngineConfiguration = new pro.taskana.configuration.TaskanaEngineConfiguration(dataSource, false, false,
            schemaName);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        classificationService = taskanaEngine.getClassificationService();
        taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        taskanaEngineImpl.setConnectionManagementMode(ConnectionManagementMode.EXPLICIT);
        SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
        sampleDataGenerator.clearDb();
    }

    @AfterEach
    public void cleanUp() throws SQLException {
        taskanaEngineImpl.setConnection(null);
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

        // new classification but master existing
        expectedClassification = (ClassificationImpl) this.createNewClassificationWithUniqueKey("", "TASK");
        expectedClassification.setKey(key);
        expectedClassification.setDomain("DOMAIN_B");
        classificationService.createClassification(expectedClassification);
        connection.commit();
        actualClassification = classificationService.getClassification(key, "DOMAIN_B");
        assertThat(actualClassification, not(IsEqual.equalTo(null)));
        assertThat(actualClassification.getCreated(), not(IsEqual.equalTo(null)));
        assertThat(actualClassification.getId(), not(IsEqual.equalTo(null)));
        assertThat(actualClassification.getKey(), IsEqual.equalTo(key));
        assertThat(actualClassification.getDomain(), IsEqual.equalTo("DOMAIN_B"));
        assertThat(actualClassification.getId(), startsWith(ID_PREFIX_CLASSIFICATION));
        Classification masterResult = classificationService.getClassification(key, "");
        assertThat(masterResult, not(IsEqual.equalTo(null)));

        // invalid serviceLevel
        ClassificationImpl expectedClassificationCreated = (ClassificationImpl) this.createNewClassificationWithUniqueKey(
            "", "TASK");
        expectedClassificationCreated.setDomain(domain);
        expectedClassificationCreated.setKey("");
        expectedClassificationCreated.setServiceLevel("ASAP");

        Assertions.assertThrows(InvalidArgumentException.class, () -> {
                classificationService.createClassification(expectedClassificationCreated);
            },
            "Should have thrown IllegalArgumentException, because ServiceLevel is invalid.");

        connection.commit();
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

        assertEquals(2 + 1, classificationService.createClassificationQuery().list().size());
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
        assertThat(classification.getDescription(), IsEqual.equalTo(updatedDescription));
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
        assertEquals(1, list.size());
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
        assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().validInDomainEquals(true).list();
        assertEquals(1, list.size());
        classification = classificationService.getClassification(classification.getKey(), classification.getDomain());
        assertThat(classification.getDescription(), IsEqual.equalTo("description"));

        classification = classificationService.updateClassification(classification);
        list = classificationService.createClassificationQuery()
            .list();
        assertEquals(2, list.size());

        List<ClassificationSummary> allClassifications = classificationService.createClassificationQuery().list();
        assertEquals(2, allClassifications.size());
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
        assertEquals(3, list.size());
        list = classificationService.createClassificationQuery()
            .list();
        assertEquals(4, list.size());
        connection.commit();

        list = classificationService.createClassificationQuery().validInDomainEquals(true).list();
        assertEquals(2, list.size());
        list = classificationService.createClassificationQuery().createdWithin(today()).list();
        assertEquals(4, list.size());
        list = classificationService.createClassificationQuery().domainIn("DOMAIN_C").validInDomainEquals(false).list();
        assertEquals(0, list.size());
        list = classificationService.createClassificationQuery()
            .keyIn(classification1.getKey())
            .list();
        assertEquals(2, list.size());

        list = classificationService.createClassificationQuery()
            .parentIdIn(classification.getId())
            .list();
        assertEquals(1, list.size());
        assertThat(list.get(0).getKey(), IsEqual.equalTo(classification1.getKey()));
        connection.commit();
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
