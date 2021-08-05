package pro.taskana.common.internal.configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.ibatis.jdbc.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.SystemException;

public class SecurityVerifier {

  public static final String SECURITY_FLAG_COLUMN_NAME = "ENFORCE_SECURITY";
  public static final String INSERT_SECURITY_FLAG_SQL =
      "INSERT INTO %s.CONFIGURATION (" + SECURITY_FLAG_COLUMN_NAME + ") VALUES (%b)";
  public static final String SELECT_SECURITY_FLAG_SQL = "SELECT %s FROM %s.CONFIGURATION";

  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityVerifier.class);
  private final String schemaName;
  private final DataSource dataSource;

  public SecurityVerifier(DataSource dataSource, String schema) {
    this.dataSource = dataSource;
    this.schemaName = schema;
  }

  public void checkSecureAccess(boolean securityEnabled) {
    try (Connection connection = dataSource.getConnection()) {

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(connection.getMetaData().toString());
      }

      SqlRunner sqlRunner = new SqlRunner(connection);

      String querySecurity =
          String.format(SELECT_SECURITY_FLAG_SQL, SECURITY_FLAG_COLUMN_NAME, schemaName);

      if ((boolean) sqlRunner.selectOne(querySecurity).get(SECURITY_FLAG_COLUMN_NAME)
          && !securityEnabled) {

        LOGGER.error("Tried to start TASKANA in unsecured mode while secured mode is enforced!");

        throw new SystemException(
            "Secured TASKANA mode is enforced, can't start in unsecured mode");
      }
    } catch (SQLException ex) {

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            String.format(
                "Security-mode is not yet set. Setting security flag to %b", securityEnabled));
      }

      setInitialSecurityMode(securityEnabled);
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Security-mode is enabled");
    }
  }

  private void setInitialSecurityMode(boolean securityEnabled) {

    try (Connection connection = dataSource.getConnection()) {

      String setSecurityFlagSql =
          String.format(INSERT_SECURITY_FLAG_SQL, schemaName, securityEnabled);

      try (PreparedStatement preparedStatement = connection.prepareStatement(setSecurityFlagSql)) {

        preparedStatement.execute();

        if (!connection.getAutoCommit()) {
          connection.commit();
        }

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(String.format("Successfully set security-mode to %b", securityEnabled));
        }

      } catch (SQLException ex) {
        LOGGER.error(
            "Caught exception while trying to set the initial TASKANA security mode. "
                + "Aborting start-up process!",
            ex);

        throw new SystemException(
            "Couldn't set initial TASKANA security mode. Aborting start-up process!");
      }

    } catch (SQLException ex) {
      LOGGER.error("Caught exception while trying to retrieve connection from datasource ", ex);
    }
  }
}
