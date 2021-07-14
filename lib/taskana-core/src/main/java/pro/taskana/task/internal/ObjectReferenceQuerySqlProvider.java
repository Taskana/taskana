package pro.taskana.task.internal;

import static pro.taskana.common.internal.util.SqlProviderUtil.whereIn;

public class ObjectReferenceQuerySqlProvider {
  private static final String OPENING_SCRIPT_TAG = "<script>";
  private static final String CLOSING_SCRIPT_TAG = "</script>";
  private static final String OPENING_WHERE_TAG = "<where>";
  private static final String CLOSING_WHERE_TAG = "</where>";

  private ObjectReferenceQuerySqlProvider() {}

  public static String queryObjectReferences() {
    return OPENING_SCRIPT_TAG
        + "SELECT ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE "
        + "FROM OBJECT_REFERENCE "
        + OPENING_WHERE_TAG
        + commonObjectReferenceWhereStatement()
        + CLOSING_WHERE_TAG
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + CLOSING_SCRIPT_TAG;
  }

  public static String countQueryObjectReferences() {
    return OPENING_SCRIPT_TAG
        + "SELECT COUNT(ID) "
        + "FROM OBJECT_REFERENCE "
        + OPENING_WHERE_TAG
        + commonObjectReferenceWhereStatement()
        + CLOSING_WHERE_TAG
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + CLOSING_SCRIPT_TAG;
  }

  public static String queryObjectReferenceColumnValues() {
    return OPENING_SCRIPT_TAG
        + "SELECT DISTINCT ${columnName} "
        + "FROM OBJECT_REFERENCE "
        + OPENING_WHERE_TAG
        + commonObjectReferenceWhereStatement()
        + CLOSING_WHERE_TAG
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + CLOSING_SCRIPT_TAG;
  }

  private static String commonObjectReferenceWhereStatement() {
    StringBuilder sb = new StringBuilder();
    whereIn("company", "COMPANY", sb);
    whereIn("system", "SYSTEM", sb);
    whereIn("systemInstance", "SYSTEM_INSTANCE", sb);
    whereIn("type", "TYPE", sb);
    whereIn("value", "VALUE", sb);
    return sb.toString();
  }
}
