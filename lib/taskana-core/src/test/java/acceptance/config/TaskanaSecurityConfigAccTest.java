package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.sampledata.SampleDataGenerator;

class TaskanaSecurityConfigAccTest {

  @BeforeEach
  void cleanDb() throws Exception {
    DataSource dataSource = DataSourceGenerator.getDataSource();
    String schemaName = DataSourceGenerator.getSchemaName();

    DbSchemaCreator dbSchemaCreator = new DbSchemaCreator(dataSource, schemaName);
    dbSchemaCreator.run();
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.clearDb();
  }

  @Test
  void should_ThrowException_When_CreatingUnsecuredEngineCfgWhileSecurityIsEnforced()
      throws Exception {

    setSecurityFlag(true);

    assertThatThrownBy(() -> createTaskanaEngine(false))
        .isInstanceOf(SystemException.class)
        .hasMessageContaining("Secured TASKANA mode is enforced, can't start in unsecured mode");
  }

  @Test
  void should_StartUpNormally_When_CreatingUnsecuredEngineCfgWhileSecurityIsNotEnforced()
      throws Exception {

    setSecurityFlag(false);

    assertThatCode(() -> createTaskanaEngine(false)).doesNotThrowAnyException();
  }

  @Test
  void should_SetSecurityFlagToFalse_When_CreatingUnsecureEngineCfgAndSecurityFlagIsNotSet()
      throws Exception {

    assertThat(retrieveSecurityFlag()).isNull();

    assertThatCode(() -> createTaskanaEngine(false)).doesNotThrowAnyException();

    assertThat(retrieveSecurityFlag()).isFalse();
  }

  @Test
  void should_SetSecurityFlagToTrue_When_CreatingSecureEngineCfgAndSecurityFlagIsNotSet()
      throws Exception {

    assertThat(retrieveSecurityFlag()).isNull();

    assertThatCode(() -> createTaskanaEngine(true)).doesNotThrowAnyException();

    assertThat(retrieveSecurityFlag()).isTrue();
  }

  private void createTaskanaEngine(boolean securityEnabled) throws SQLException {
    new TaskanaEngineConfiguration(
            DataSourceGenerator.getDataSource(),
            false,
            securityEnabled,
            DataSourceGenerator.getSchemaName())
        .buildTaskanaEngine();
  }

  private Boolean retrieveSecurityFlag() throws Exception {

    try (Connection connection = DataSourceGenerator.getDataSource().getConnection()) {

      String selectSecurityFlagSql =
          String.format(
              "SELECT ENFORCE_SECURITY FROM %s.CONFIGURATION", DataSourceGenerator.getSchemaName());

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(selectSecurityFlagSql);

      if (resultSet.next()) {
        return resultSet.getBoolean(1);
      }
      statement.close();
      return null;
    }
  }

  private void setSecurityFlag(boolean securityFlag) throws Exception {

    try (Connection connection = DataSourceGenerator.getDataSource().getConnection()) {

      String sql =
          String.format(
              "INSERT INTO %s.CONFIGURATION (ENFORCE_SECURITY) VALUES (%b)",
              DataSourceGenerator.getSchemaName(), securityFlag);

      Statement statement = connection.createStatement();
      statement.execute(sql);
      if (!connection.getAutoCommit()) {
        connection.commit();
      }
      statement.close();
    }
  }
}
