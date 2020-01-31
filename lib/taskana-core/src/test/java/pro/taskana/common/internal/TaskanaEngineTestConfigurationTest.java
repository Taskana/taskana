package pro.taskana.common.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;

/** Test of configuration. */
class TaskanaEngineTestConfigurationTest {

  @Test
  void testCreateTaskanaEngine() throws SQLException {
    DataSource ds = TaskanaEngineTestConfiguration.getDataSource();
    TaskanaEngineConfiguration taskEngineConfiguration =
        new TaskanaEngineConfiguration(ds, false, TaskanaEngineTestConfiguration.getSchemaName());

    TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();

    assertNotNull(te);
  }
}
