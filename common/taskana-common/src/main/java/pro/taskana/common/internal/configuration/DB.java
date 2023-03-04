package pro.taskana.common.internal.configuration;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.UnsupportedDatabaseException;

/** Supported versions of databases. */
public enum DB {
  H2("H2", "h2"),
  DB2("DB2", "db2"),
  ORACLE("Oracle", "oracle"),
  POSTGRES("PostgreSQL", "postgres");

  public final String dbProductName;
  public final String dbProductId;

  DB(String dbProductName, String dbProductId) {
    this.dbProductName = dbProductName;
    this.dbProductId = dbProductId;
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
