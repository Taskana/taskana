package pro.taskana.common.internal.util;

public class SqlProviderUtil {
  public static final String OPENING_SCRIPT_TAG = "<script>";
  public static final String CLOSING_SCRIPT_TAG = "</script>";
  public static final String OPENING_WHERE_TAG = "<where>";
  public static final String CLOSING_WHERE_TAG = "</where>";
  public static final String DB2_WITH_UR = "<if test=\"_databaseId == 'db2'\">with UR </if>";

  private SqlProviderUtil() {}

  public static void whereIn(String collection, String column, StringBuilder sb) {
    sb.append("<if test='")
        .append(collection)
        .append(" != null'>AND ")
        .append(column)
        .append(" IN(<foreach item='item' collection='")
        .append(collection)
        .append("' separator=',' >#{item}</foreach>)</if> ");
  }

  public static void whereNotIn(String collection, String column, StringBuilder sb) {
    sb.append("<if test='")
        .append(collection)
        .append(" != null'>AND ")
        .append(column)
        .append(" NOT IN(<foreach item='item' collection='")
        .append(collection)
        .append("' separator=',' >#{item}</foreach>)</if> ");
  }

  public static void whereInTime(String collection, String column, StringBuilder sb) {
    sb.append("<if test='")
        .append(collection)
        .append(" !=null'> AND (<foreach item='item' collection='")
        .append(collection)
        .append("' separator=' OR ' > ( <if test='item.begin!=null'> ")
        .append(column)
        .append(
            " &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND"
                + " </if><if test='item.end!=null'> ")
        .append(column)
        .append(" &lt;=#{item.end} </if>)</foreach>)</if> ");
  }

  public static void whereLike(String collection, String column, StringBuilder sb) {
    sb.append("<if test='")
        .append(collection)
        .append(" != null'>AND (<foreach item='item' collection='")
        .append(collection)
        .append("' separator=' OR '>UPPER(")
        .append(column)
        .append(") LIKE #{item}</foreach>)</if> ");
  }
}
