package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.config.DataSourceGenerator;

class TaskanaEngineConfigTest {

  @Test
  void should_ReturnTaskanaEngine_When_BuildingWithConfiguration() throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaEngineConfiguration taskEngineConfiguration =
        new TaskanaEngineConfiguration(ds, false, DataSourceGenerator.getSchemaName());

    TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();

    assertThat(te).isNotNull();
  }

  @Test
  void should_SetCorpusChristiEnabled_When_PropertyIsSet() throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaEngineConfiguration taskEngineConfiguration =
        new TaskanaEngineConfiguration(
            ds,
            false,
            true,
            "/corpusChristiEnabled.properties",
            "|",
            DataSourceGenerator.getSchemaName());

    assertThat(taskEngineConfiguration.isCorpusChristiEnabled()).isTrue();
  }

  @Test
  void should_ReturnTheTwoCustomHolidays_When_TwoCustomHolidaysAreConfiguredInThePropertiesFile()
      throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaEngineConfiguration taskEngineConfiguration =
        new TaskanaEngineConfiguration(
            ds,
            false,
            true,
            "/custom_holiday_taskana.properties",
            "|",
            DataSourceGenerator.getSchemaName());
    assertThat(taskEngineConfiguration.getCustomHolidays()).contains(CustomHoliday.of(31, 7));
    assertThat(taskEngineConfiguration.getCustomHolidays()).contains(CustomHoliday.of(16, 12));
  }

  @Test
  void should_ReturnEmptyCustomHolidaysList_When_AllCustomHolidaysAreInWrongFormatInPropertiesFile()
      throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaEngineConfiguration taskEngineConfiguration =
        new TaskanaEngineConfiguration(
            ds,
            false,
            true,
            "/custom_holiday_with_wrong_format_taskana.properties",
            "|",
            DataSourceGenerator.getSchemaName());
    assertThat(taskEngineConfiguration.getCustomHolidays()).isEmpty();
  }
}
