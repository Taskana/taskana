package acceptance;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;

import pro.taskana.Attachment;
import pro.taskana.ObjectReference;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.TimeInterval;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.database.TestDataGenerator;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.impl.configuration.TaskanaEngineConfigurationTest;
import pro.taskana.sampledata.DBCleaner;

/**
 * Base class for all acceptance tests.
 */
public abstract class AbstractAccTest {

    protected static TaskanaEngineConfiguration taskanaEngineConfiguration;
    protected static TaskanaEngine taskanaEngine;
    protected static TestDataGenerator testDataGenerator = new TestDataGenerator();

    @BeforeAll
    @BeforeClass
    public static void setupTest() throws Exception {
        resetDb(false);
    }

    public static void resetDb(boolean dropTables) throws SQLException, IOException {
        DataSource dataSource = TaskanaEngineConfigurationTest.getDataSource();
        String schemaName = TaskanaEngineConfigurationTest.getSchemaName();
        DBCleaner dbCleaner = new DBCleaner();
        if (dropTables) {
            dbCleaner.dropDb(dataSource, schemaName);
        }
        dataSource = TaskanaEngineConfigurationTest.getDataSource();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false,
            schemaName);
        taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
        dbCleaner.clearDb(dataSource, schemaName);
        testDataGenerator.generateTestData(dataSource, schemaName);
    }

    protected ObjectReference createObjectReference(String company, String system, String systemInstance, String type,
        String value) {
        ObjectReference objectReference = new ObjectReference();
        objectReference.setCompany(company);
        objectReference.setSystem(system);
        objectReference.setSystemInstance(systemInstance);
        objectReference.setType(type);
        objectReference.setValue(value);
        return objectReference;
    }

    protected Map<String, String> createSimpleCustomProperties(int propertiesCount) {
        HashMap<String, String> properties = new HashMap<>();
        for (int i = 1; i <= propertiesCount; i++) {
            properties.put("Property_" + i, "Property Value of Property_" + i);
        }
        return properties;
    }

    protected Attachment createAttachment(String classificationKey, ObjectReference objRef,
        String channel, String receivedDate, Map<String, String> customAttributes)
        throws ClassificationNotFoundException {
        Attachment attachment = taskanaEngine.getTaskService().newAttachment();

        attachment.setClassificationSummary(
            taskanaEngine.getClassificationService().getClassification(classificationKey, "DOMAIN_A").asSummary());
        attachment.setObjectReference(objRef);
        attachment.setChannel(channel);
        Instant receivedTimestamp = null;
        if (receivedDate != null && receivedDate.length() < 11) {
            // contains only the date, not the time
            LocalDate date = LocalDate.parse(receivedDate);
            receivedTimestamp = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        } else {
            receivedTimestamp = Instant.parse(receivedDate);
        }
        attachment.setReceived(receivedTimestamp);
        if (customAttributes != null) {
            attachment.setCustomAttributes(customAttributes);
        }

        return attachment;
    }

    protected TimeInterval todaysInterval() {
        Instant begin = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant();
        Instant end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();
        return new TimeInterval(begin, end);
    }

    protected Instant getInstant(String datetime) {
        return LocalDateTime.parse(datetime).atZone(ZoneId.systemDefault()).toInstant();
    }
}
