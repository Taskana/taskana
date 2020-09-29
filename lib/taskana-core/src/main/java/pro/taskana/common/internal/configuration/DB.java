package pro.taskana.common.internal.configuration;

import java.sql.Connection;
import java.sql.SQLException;

import pro.taskana.common.api.exceptions.UnsupportedDatabaseException;

/** Supported versions of databases. */
public enum DB {
  H2("H2", "h2"),
  DB2("DB2", "db2"),
  POSTGRESS("PostgreSQL", "postgres"),
  ORACLE("Oracle", "oracle");

  public final String dbProductname;
  public final String dbProductId;

  DB(String dbProductname, String dbProductId) {
    this.dbProductname = dbProductname;
    this.dbProductId = dbProductId;
  }

  public static boolean isDb2(String dbProductName) {
    return dbProductName != null && dbProductName.contains(DB2.dbProductname);
  }

  public static boolean isH2(String dbProductName) {
    return dbProductName != null && dbProductName.contains(H2.dbProductname);
  }

  public static boolean isPostgreSql(String dbProductName) {
    return POSTGRESS.dbProductname.equals(dbProductName);
  }

  public static boolean isOracleDb(String dbProductName) {
    return ORACLE.dbProductname.equals(dbProductName);
  }

  public static String getDatabaseProductName(Connection connection)
          throws SQLException {
    return connection.getMetaData().getDatabaseProductName();
  }

  public static Boolean getDatabaseBooleanValue(Object columnValue, Connection connection)
          throws SQLException {
    return getDatabaseBooleanValue(columnValue, getDatabaseProductName(connection));
  }

  public static Boolean getDatabaseBooleanValue(Object columnValue, String dbProductName) {
    if (columnValue == null) {
      return null;
    }
    if (isOracleDb(dbProductName)) {
      return columnValue.equals('Y');
    }
    return Boolean.TRUE.equals(columnValue);
  }

  public static String getDatabaseProductId(String dbProductName) {

    if (isDb2(dbProductName)) {
      return DB2.dbProductId;
    } else if (isH2(dbProductName)) {
      return H2.dbProductId;
    } else if (isPostgreSql(dbProductName)) {
      return POSTGRESS.dbProductId;
    } else if (isOracleDb(dbProductName)) {
      return ORACLE.dbProductId;
    } else {
      throw new UnsupportedDatabaseException(dbProductName);
    }
  }
}
