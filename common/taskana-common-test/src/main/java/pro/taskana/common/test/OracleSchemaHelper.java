package pro.taskana.common.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.sql.DataSource;

import pro.taskana.common.api.exceptions.SystemException;

public final class OracleSchemaHelper {

  private static final String DEFAULT_PASSWORD = "testPassword";

  private OracleSchemaHelper() {
    // hide implicitpublic one
  }

  public static void initOracleSchema(DataSource dataSource, String schemaName)
      throws SystemException {
    try (Connection connection = dataSource.getConnection();
        // connect as SYSTEM user to create schemas
        Connection conn =
            DriverManager.getConnection(
                connection.getMetaData().getURL(), "SYSTEM", DEFAULT_PASSWORD);
        Statement stmt = conn.createStatement()) {
      stmt.execute("GRANT ALL PRIVILEGES TO TEST_USER");

      dropExistingSchema(schemaName, stmt);

      stmt.addBatch(
          String.format(
              "create tablespace %s datafile '%s.dat' size 5M autoextend "
                  + "on NEXT 5M MAXSIZE UNLIMITED",
              schemaName, schemaName));
      stmt.addBatch(
          String.format(
              "create temporary tablespace %s_TMP tempfile '%s_tmp.dat' size 5M autoextend "
                  + "on NEXT 5M MAXSIZE UNLIMITED",
              schemaName, schemaName));
      stmt.addBatch(
          String.format(
              "create user %s identified by %s default tablespace %s "
                  + "temporary tablespace %s_TMP",
              schemaName, DEFAULT_PASSWORD, schemaName, schemaName));
      stmt.addBatch(String.format("ALTER USER %s quota unlimited on %s", schemaName, schemaName));
      stmt.addBatch(String.format("GRANT UNLIMITED TABLESPACE TO %s", schemaName));
      stmt.executeBatch();
    } catch (Exception e) {
      throw new SystemException("Failed to setup ORACLE Schema", e);
    }
  }

  private static void dropExistingSchema(String schemaName, Statement stmt) {
    try {
      stmt.addBatch(String.format("DROP USER %s CASCADE", schemaName));
      stmt.addBatch(
          String.format(
              "DROP TABLESPACE %s_TMP INCLUDING CONTENTS AND DATAFILES CASCADE CONSTRAINTS",
              schemaName));
      stmt.addBatch(
          String.format(
              "DROP TABLESPACE %s INCLUDING CONTENTS AND DATAFILES CASCADE CONSTRAINTS",
              schemaName));
      stmt.executeBatch();
    } catch (Exception e) {
      // IGNORE exceptions from dropping
    }
  }
}
