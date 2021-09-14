package pro.taskana.common.internal.util;

import java.util.stream.IntStream;

public class SqlProviderUtil {
  public static final String OPENING_SCRIPT_TAG = "<script>";
  public static final String CLOSING_SCRIPT_TAG = "</script>";
  public static final String OPENING_WHERE_TAG = "<where>";
  public static final String CLOSING_WHERE_TAG = "</where>";
  public static final String DB2_WITH_UR = "<if test=\"_databaseId == 'db2'\">with UR </if>";

  private SqlProviderUtil() {}

  public static StringBuilder whereIn(String collection, String column, StringBuilder sb) {
    return sb.append("<if test='")
        .append(collection)
        .append(" != null'>AND ")
        .append(column)
        .append(" IN(<foreach item='item' collection='")
        .append(collection)
        .append("' separator=',' >#{item}</foreach>)</if> ");
  }

  public static StringBuilder whereIn(String collection, String column) {
    return whereIn(collection, column, new StringBuilder());
  }

  public static StringBuilder whereNotIn(String collection, String column, StringBuilder sb) {
    return sb.append("<if test='")
        .append(collection)
        .append(" != null'>AND ")
        .append(column)
        .append(" NOT IN(<foreach item='item' collection='")
        .append(collection)
        .append("' separator=',' >#{item}</foreach>)</if> ");
  }

  public static StringBuilder whereNotIn(String collection, String column) {
    return whereNotIn(collection, column, new StringBuilder());
  }

  public static StringBuilder whereInTime(String collection, String column, StringBuilder sb) {
    return sb.append("<if test='")
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

  public static StringBuilder whereInTime(String collection, String column) {
    return whereInTime(collection, column, new StringBuilder());
  }

  public static StringBuilder whereLike(String collection, String column, StringBuilder sb) {
    return sb.append("<if test='")
        .append(collection)
        .append(" != null'>AND (<foreach item='item' collection='")
        .append(collection)
        .append("' separator=' OR '>UPPER(")
        .append(column)
        .append(") LIKE #{item}</foreach>)</if> ");
  }

  public static StringBuilder whereLike(String collection, String column) {
    return whereLike(collection, column, new StringBuilder());
  }

  public static StringBuilder whereCustomStatements(
      String baseCollection, String baseColumn, int customBound, StringBuilder sb) {
    IntStream.rangeClosed(1, customBound)
        .forEach(
            x -> {
              String column = baseColumn + "_" + x;
              whereIn(baseCollection + x + "In", column, sb);
              whereLike(baseCollection + x + "Like", column, sb);
              whereNotIn(baseCollection + x + "NotIn", column, sb);
            });
    return sb;
  }

  public static StringBuilder whereCustomStatements(
      String baseCollection, String baseColumn, int customBound) {
    return whereCustomStatements(baseCollection, baseColumn, customBound, new StringBuilder());
  }
}
