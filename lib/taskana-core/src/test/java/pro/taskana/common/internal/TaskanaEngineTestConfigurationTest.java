package pro.taskana.common.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.CustomHoliday;

/** Test of configuration. */
class TaskanaEngineTestConfigurationTest {

  @Test
  void testCreateTaskanaEngine() throws SQLException {
    DataSource ds = TaskanaEngineTestConfiguration.getDataSource();
    TaskanaEngineConfiguration taskEngineConfiguration =
        new TaskanaEngineConfiguration(ds, false, TaskanaEngineTestConfiguration.getSchemaName());

    TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();

    assertThat(te).isNotNull();
  }

  @Test
  void testCustomHolidayInitialisationWithTaskanaProprtiesFile() throws SQLException {
    DataSource ds = TaskanaEngineTestConfiguration.getDataSource();
    TaskanaEngineConfiguration taskEngineConfiguration =
        new TaskanaEngineConfiguration(
            ds,
            false,
            false,
            "/custom_holiday_taskana.properties",
            "|",
            TaskanaEngineTestConfiguration.getSchemaName());
    assertThat(taskEngineConfiguration.getCustomHolidays()).contains(CustomHoliday.of(31, 7));
    assertThat(taskEngineConfiguration.getCustomHolidays()).contains(CustomHoliday.of(16, 12));
  }
}
