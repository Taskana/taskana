package pro.taskana.impl.configuration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngine;

/**
 * Test of configuration.
 */
class TaskanaEngineTestConfigurationTest {

    @Test
    void testCreateTaskanaEngine() throws SQLException {
        DataSource ds = TaskanaEngineTestConfiguration.getDataSource();
        pro.taskana.configuration.TaskanaEngineConfiguration taskEngineConfiguration = new pro.taskana.configuration.TaskanaEngineConfiguration(
            ds, false,
            TaskanaEngineTestConfiguration.getSchemaName());

        TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();

        assertNotNull(te);
    }
}
