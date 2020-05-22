package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.sampledata.SampleDataGenerator;

class TaskanaSecurityConfigAccTest {

  @BeforeEach
  void cleanDb() throws SQLException {
    DataSource dataSource = TaskanaEngineTestConfiguration.getDataSource();
    String schemaName = TaskanaEngineTestConfiguration.getSchemaName();

    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.dropDb();
    DbSchemaCreator dbSchemaCreator = new DbSchemaCreator(dataSource, schemaName);
    dbSchemaCreator.run();
  }

  @Test
  void should_ThrowException_When_CreatingUnsecuredEngineCfgWhileSecurityIsEnforced()
      throws SQLException {

    setSecurityFlag(true);

    ThrowingCallable createUnsecuredTaskanaEngineConfiguration =
        () -> {
          TaskanaEngineConfiguration taskanaEngineConfiguration =
              new TaskanaEngineConfiguration(
                  TaskanaEngineTestConfiguration.getDataSource(),
                  false,
                  false,
                  TaskanaEngineTestConfiguration.getSchemaName());
        };

    assertThatThrownBy(createUnsecuredTaskanaEngineConfiguration)
        .isInstanceOf(SystemException.class)
        .hasMessageContaining("Secured TASKANA mode is enforced, can't start in unsecured mode");
  }

  @Test
  void should_StartUpNormally_When_CreatingUnsecuredEngineCfgWhileSecurityIsNotEnforced()
      throws SQLException {

    setSecurityFlag(false);

    ThrowingCallable createUnsecuredTaskanaEngineConfiguration =
        () -> {
          TaskanaEngineConfiguration taskanaEngineConfiguration =
              new TaskanaEngineConfiguration(
                  TaskanaEngineTestConfiguration.getDataSource(),
                  false,
                  false,
                  TaskanaEngineTestConfiguration.getSchemaName());
        };

    assertThatCode(createUnsecuredTaskanaEngineConfiguration).doesNotThrowAnyException();
  }

  @Test
  void should_SetSecurityFlagToFalse_When_CreatingUnsecureEngineCfgAndSecurityFlagIsNotSet()
      throws SQLException {

    assertThat(retrieveSecurityFlag()).isNull();

    ThrowingCallable createUnsecuredTaskanaEngineConfiguration =
        () -> {
          TaskanaEngineConfiguration taskanaEngineConfiguration =
              new TaskanaEngineConfiguration(
                  TaskanaEngineTestConfiguration.getDataSource(),
                  false,
                  false,
                  TaskanaEngineTestConfiguration.getSchemaName());
        };

    assertThatCode(createUnsecuredTaskanaEngineConfiguration).doesNotThrowAnyException();

    assertThat(retrieveSecurityFlag()).isFalse();
  }

  @Test
  void should_SetSecurityFlagToTrue_When_CreatingSecureEngineCfgAndSecurityFlagIsNotSet()
      throws SQLException {

    assertThat(retrieveSecurityFlag()).isNull();

    ThrowingCallable createSecuredTaskanaEngineConfiguration =
        () -> {
          TaskanaEngineConfiguration taskanaEngineConfiguration =
              new TaskanaEngineConfiguration(
                  TaskanaEngineTestConfiguration.getDataSource(),
                  false,
                  true,
                  TaskanaEngineTestConfiguration.getSchemaName());
        };

    assertThatCode(createSecuredTaskanaEngineConfiguration).doesNotThrowAnyException();

    assertThat(retrieveSecurityFlag()).isTrue();
  }

  private Boolean retrieveSecurityFlag() throws SQLException {

    try (Connection connection = TaskanaEngineTestConfiguration.getDataSource().getConnection()) {

      String selectSecurityFlagSql =
          String.format(
              "SELECT * FROM %s.CONFIGURATION", TaskanaEngineTestConfiguration.getSchemaName());

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(selectSecurityFlagSql);

      if (resultSet.next()) {
        return resultSet.getBoolean(1);
      }
      statement.close();
      return null;
    }
  }

  private void setSecurityFlag(boolean securityFlag) throws SQLException {

    try (Connection connection = TaskanaEngineTestConfiguration.getDataSource().getConnection()) {

      String sql =
          String.format(
              "INSERT INTO %s.CONFIGURATION VALUES (%b)",
              TaskanaEngineTestConfiguration.getSchemaName(), securityFlag);

      Statement statement = connection.createStatement();
      statement.execute(sql);
      if (!connection.getAutoCommit()) {
        connection.commit();
      }
      statement.close();
    }
  }
}
