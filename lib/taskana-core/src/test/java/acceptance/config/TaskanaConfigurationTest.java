package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.CustomHoliday;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.config.DataSourceGenerator;

class TaskanaConfigurationTest {

  @Test
  void should_ReturnTaskanaEngine_When_BuildingWithConfiguration() throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName())
            .initTaskanaProperties()
            .build();

    TaskanaEngine te = TaskanaEngine.buildTaskanaEngine(configuration);

    assertThat(te).isNotNull();
  }

  @Test
  void should_SetCorpusChristiEnabled_When_PropertyIsSet() {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName(), true)
            .initTaskanaProperties("/corpusChristiEnabled.properties", "|")
            .build();

    assertThat(configuration.isCorpusChristiEnabled()).isTrue();
  }

  @Test
  void should_ReturnTheTwoCustomHolidays_When_TwoCustomHolidaysAreConfiguredInThePropertiesFile() {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName(), true)
            .initTaskanaProperties("/custom_holiday_taskana.properties", "|")
            .build();
    assertThat(configuration.getCustomHolidays())
        .contains(CustomHoliday.of(31, 7), CustomHoliday.of(16, 12));
  }

  @Test
  void should_ReturnEmptyList_When_AllCustomHolidaysAreInWrongFormatInPropertiesFile() {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName(), true)
            .initTaskanaProperties("/custom_holiday_with_wrong_format_taskana.properties", "|")
            .build();
    assertThat(configuration.getCustomHolidays()).isEmpty();
  }
}
