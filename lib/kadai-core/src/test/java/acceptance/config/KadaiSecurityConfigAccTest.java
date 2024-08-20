package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.configuration.DB;
import io.kadai.common.internal.configuration.DbSchemaCreator;
import io.kadai.common.test.config.DataSourceGenerator;
import io.kadai.sampledata.SampleDataGenerator;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KadaiSecurityConfigAccTest {

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

    assertThatThrownBy(() -> createKadaiEngine(false))
        .isInstanceOf(SystemException.class)
        .hasMessageContaining("Secured KADAI mode is enforced, can't start in unsecured mode");
  }

  @Test
  void should_StartUpNormally_When_CreatingUnsecuredEngineCfgWhileSecurityIsNotEnforced()
      throws Exception {

    setSecurityFlag(false);

    assertThatCode(() -> createKadaiEngine(false)).doesNotThrowAnyException();
  }

  @Test
  void should_SetSecurityFlagToFalse_When_CreatingUnsecureEngineCfgAndSecurityFlagIsNotSet()
      throws Exception {

    assertThat(retrieveSecurityFlag()).isNull();

    assertThatCode(() -> createKadaiEngine(false)).doesNotThrowAnyException();

    assertThat(retrieveSecurityFlag()).isFalse();
  }

  @Test
  void should_SetSecurityFlagToTrue_When_CreatingSecureEngineCfgAndSecurityFlagIsNotSet()
      throws Exception {

    assertThat(retrieveSecurityFlag()).isNull();

    assertThatCode(() -> createKadaiEngine(true)).doesNotThrowAnyException();

    assertThat(retrieveSecurityFlag()).isTrue();
  }

  private void createKadaiEngine(boolean securityEnabled) throws SQLException {
    KadaiEngine.buildKadaiEngine(
        new KadaiConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                false,
                DataSourceGenerator.getSchemaName(),
                securityEnabled)
            .initKadaiProperties()
            .build());
  }

  private Boolean retrieveSecurityFlag() throws Exception {

    try (Connection connection = DataSourceGenerator.getDataSource().getConnection()) {

      String selectSecurityFlagSql =
          String.format(
              "SELECT ENFORCE_SECURITY FROM %s.CONFIGURATION WHERE NAME = 'MASTER'",
              DataSourceGenerator.getSchemaName());

      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(selectSecurityFlagSql);

      if (resultSet.next()) {
        Boolean securityEnabled = resultSet.getBoolean(1);
        if (resultSet.wasNull()) {
          return null;
        } else {
          return securityEnabled;
        }
      }
      statement.close();
      return null;
    }
  }

  private void setSecurityFlag(boolean securityFlag) throws Exception {

    try (Connection connection = DataSourceGenerator.getDataSource().getConnection()) {

      DB db = DB.getDB(connection);

      String sql;
      final String securityFlagAsString;
      if (DB.ORACLE == db) {
        securityFlagAsString = securityFlag ? "1" : "0";
      } else {
        securityFlagAsString = String.valueOf(securityFlag);
      }
      sql =
          String.format(
              "UPDATE %s.CONFIGURATION SET ENFORCE_SECURITY = %s WHERE NAME = 'MASTER'",
              DataSourceGenerator.getSchemaName(), securityFlagAsString);

      Statement statement = connection.createStatement();
      statement.execute(sql);
      if (!connection.getAutoCommit()) {
        connection.commit();
      }
      statement.close();
    }
  }
}
