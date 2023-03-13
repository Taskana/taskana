package pro.taskana.common.internal.configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.UnsupportedDatabaseException;

/** Supported versions of databases. */
public enum DB {
  H2("H2", "h2", "/sql/h2/schema-detection-h2.sql", "/sql/h2/taskana-schema-h2.sql"),
  DB2("DB2", "db2", "/sql/db2/schema-detection-db2.sql", "/sql/db2/taskana-schema-db2.sql"),
  ORACLE(
      "Oracle",
      "oracle",
      "/sql/oracle/schema-detection-oracle.sql",
      "/sql/oracle/taskana-schema-oracle.sql"),
  POSTGRES(
      "PostgreSQL",
      "postgres",
      "/sql/postgres/schema-detection-postgres.sql",
      "/sql/postgres/taskana-schema-postgres.sql");

  public final String dbProductName;
  public final String dbProductId;
  public final String detectionScript;
  public final String schemaScript;

  DB(String dbProductName, String dbProductId, String detectionScript, String schemaScript) {
    this.dbProductName = dbProductName;
    this.dbProductId = dbProductId;
    this.detectionScript = detectionScript;
    this.schemaScript = schemaScript;
  }

  public static DB getDB(String dbProductId) {
    return Arrays.stream(DB.values())
        .filter(db -> dbProductId.contains(db.dbProductId))
        .findFirst()
        .orElseThrow(() -> new UnsupportedDatabaseException(dbProductId));
  }

  public static DB getDB(Connection connection) {
    String dbProductName = DB.getDatabaseProductName(connection);
    return Arrays.stream(DB.values())
        .filter(db -> dbProductName.contains(db.dbProductName))
        .findFirst()
        .orElseThrow(() -> new UnsupportedDatabaseException(dbProductName));
  }

  private static String getDatabaseProductName(Connection connection) {
    try {
      return connection.getMetaData().getDatabaseProductName();
    } catch (SQLException e) {
      throw new SystemException("Could not extract meta data from connection", e);
    }
  }
}
