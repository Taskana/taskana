package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.config.DataSourceGenerator;

/** Test of configuration. */
class TaskanaEngineConfigTest {

  @Test
  void should_ReturnTaskanaEngine_When_BuildingWithConfiguration() throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration taskEngineConfiguration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName()).build();

    TaskanaEngine te = TaskanaEngine.buildTaskanaEngine(taskEngineConfiguration);

    assertThat(te).isNotNull();
  }

  @Test
  void should_SetCorpusChristiEnabled_When_PropertyIsSet() throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration taskEngineConfiguration =
        new TaskanaConfiguration.Builder(
                ds,
                false,
                DataSourceGenerator.getSchemaName(),
                true,
                "/corpusChristiEnabled.properties",
                "|")
            .build();

    assertThat(taskEngineConfiguration.isCorpusChristiEnabled()).isTrue();
  }

  @Test
  void should_ReturnTheTwoCustomHolidays_When_TwoCustomHolidaysAreConfiguredInThePropertiesFile()
      throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration taskEngineConfiguration =
        new TaskanaConfiguration.Builder(
                ds,
                false,
                DataSourceGenerator.getSchemaName(),
                true,
                "/custom_holiday_taskana.properties",
                "|")
            .build();
    assertThat(taskEngineConfiguration.getCustomHolidays()).contains(CustomHoliday.of(31, 7));
    assertThat(taskEngineConfiguration.getCustomHolidays()).contains(CustomHoliday.of(16, 12));
  }

  @Test
  void should_ReturnEmptyCustomHolidaysList_When_AllCustomHolidaysAreInWrongFormatInPropertiesFile()
      throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration taskEngineConfiguration =
        new TaskanaConfiguration.Builder(
                ds,
                false,
                DataSourceGenerator.getSchemaName(),
                true,
                "/custom_holiday_with_wrong_format_taskana.properties",
                "|")
            .build();
    assertThat(taskEngineConfiguration.getCustomHolidays()).isEmpty();
  }
}
