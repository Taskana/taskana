package pro.taskana.configuration;

import pro.taskana.exceptions.UnsupportedDatabaseException;

/**
 * Supported versions of databases.
 */
public enum DB {

    H2("H2", "h2"),
    DB2("DB2", "db2"),
    POSTGRESS("PostgreSQL", "postgres");

    public final String dbProductname;
    public final String dbProductId;

    DB(String dbProductname, String dbProductId) {
        this.dbProductname = dbProductname;
        this.dbProductId = dbProductId;
    }

    public static boolean isDb2(String dbProductName) {
        return dbProductName.contains(DB2.dbProductname);
    }

    public static boolean isH2(String databaseProductName) {
        return databaseProductName.contains(H2.dbProductname);
    }

    public static boolean isPostgreSQL(String databaseProductName) {
        return POSTGRESS.dbProductname.equals(databaseProductName);
    }

    public static String getDatabaseProductId(String databaseProductName) {

        if (isDb2(databaseProductName)) {
            return DB2.dbProductId;
        } else if (isH2(databaseProductName)) {
            return H2.dbProductId;
        } else if (isPostgreSQL(databaseProductName)) {
            return POSTGRESS.dbProductId;
        } else {
            throw new UnsupportedDatabaseException(databaseProductName);
        }
    }
}
