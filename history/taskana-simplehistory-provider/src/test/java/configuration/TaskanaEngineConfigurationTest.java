package configuration;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.TaskanaEngineConfiguration;

/**
 * Unit Test for TaskanaEngineConfigurationTest.
 *
 * @author MMR
 */
public class TaskanaEngineConfigurationTest extends AbstractAccTest {

    @Test
    public void testCreateTaskanaEngine() throws SQLException {
        DataSource ds = getDataSource();
        TaskanaEngineConfiguration taskEngineConfiguration = new TaskanaEngineConfiguration(ds, false,
            getSchemaName());

        TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();

        Assert.assertNotNull(te);
    }

    @Test
    public void testCreateTaskanaHistoryEventWithNonDefaultSchemaName() throws SQLException {
        resetDb("SOMECUSTOMSCHEMANAME");
        long count = historyService.createHistoryQuery().workbasketKeyIn("wbKey1").count();
        assertEquals(0, count);
        historyService.create(
            AbstractAccTest.createHistoryEvent("wbKey1", "taskId1", "type1", "Some comment", "wbKey2"));
        count = historyService.createHistoryQuery().workbasketKeyIn("wbKey1").count();
        assertEquals(1, count);

    }

}
