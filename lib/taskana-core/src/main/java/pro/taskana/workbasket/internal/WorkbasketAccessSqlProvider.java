package pro.taskana.workbasket.internal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pro.taskana.common.internal.util.Pair;

public class WorkbasketAccessSqlProvider {

  public static final List<Pair<String, String>> COLUMNS =
      Arrays.asList(
          Pair.of("WORKBASKET_ID", "#{workbasketAccessItem.workbasketId}"),
          Pair.of("ACCESS_ID", "#{workbasketAccessItem.accessId}"),
          Pair.of("ACCESS_NAME", "#{workbasketAccessItem.accessName}"));

  public static final List<Pair<String, String>> PERMISSIONS =
      Arrays.asList(
          Pair.of("PERM_READ", "#{workbasketAccessItem.permRead}"),
          Pair.of("PERM_OPEN", "#{workbasketAccessItem.permOpen}"),
          Pair.of("PERM_APPEND", "#{workbasketAccessItem.permAppend}"),
          Pair.of("PERM_TRANSFER", "#{workbasketAccessItem.permTransfer}"),
          Pair.of("PERM_DISTRIBUTE", "#{workbasketAccessItem.permDistribute}"),
          Pair.of("PERM_CUSTOM_1", "#{workbasketAccessItem.permCustom1}"),
          Pair.of("PERM_CUSTOM_2", "#{workbasketAccessItem.permCustom2}"),
          Pair.of("PERM_CUSTOM_3", "#{workbasketAccessItem.permCustom3}"),
          Pair.of("PERM_CUSTOM_4", "#{workbasketAccessItem.permCustom4}"),
          Pair.of("PERM_CUSTOM_5", "#{workbasketAccessItem.permCustom5}"),
          Pair.of("PERM_CUSTOM_6", "#{workbasketAccessItem.permCustom6}"),
          Pair.of("PERM_CUSTOM_7", "#{workbasketAccessItem.permCustom7}"),
          Pair.of("PERM_CUSTOM_8", "#{workbasketAccessItem.permCustom8}"),
          Pair.of("PERM_CUSTOM_9", "#{workbasketAccessItem.permCustom9}"),
          Pair.of("PERM_CUSTOM_10", "#{workbasketAccessItem.permCustom10}"),
          Pair.of("PERM_CUSTOM_11", "#{workbasketAccessItem.permCustom11}"),
          Pair.of("PERM_CUSTOM_12", "#{workbasketAccessItem.permCustom1}"));

  public static String findById() {
    return "<script>"
        + "SELECT "
        + "ID, "
        + commonSelectStatements()
        + "FROM WORKBASKET_ACCESS_LIST WHERE ID = #{id} "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>";
  }

  public static String findByWorkbasketId() {
    return "<script>"
        + "SELECT "
        + "WBA.ID, WB.KEY, "
        + commonSelectStatements()
        + "FROM WORKBASKET_ACCESS_LIST AS WBA "
        + "LEFT JOIN WORKBASKET AS WB ON WORKBASKET_ID = WB.ID "
        + "WHERE WORKBASKET_ID = #{id} "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>";
  }

  public static String findByAccessId() {
    return "<script>"
        + "SELECT "
        + "WBA.ID, WB.KEY, "
        + commonSelectStatements()
        + "FROM WORKBASKET_ACCESS_LIST AS WBA "
        + "LEFT JOIN WORKBASKET AS WB ON WORKBASKET_ID = WB.ID "
        + "WHERE ACCESS_ID = #{id} "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>";
  }

  public static String insert() {
    return "INSERT INTO WORKBASKET_ACCESS_LIST (ID, "
        + commonSelectStatements()
        + ") VALUES (#{workbasketAccessItem.id}, "
        + insertValues()
        + ")";
  }

  public static String update() {
    return "UPDATE WORKBASKET_ACCESS_LIST SET "
        + commonUpdateStatement()
        + "WHERE id = #{workbasketAccessItem.id}";
  }

  public static String delete() {
    return "DELETE FROM WORKBASKET_ACCESS_LIST WHERE ID = #{id}";
  }

  public static String deleteAllAccessItemsForWorkbasketId() {
    return "DELETE FROM WORKBASKET_ACCESS_LIST WHERE WORKBASKET_ID = #{workbasketId}";
  }

  public static String deleteAccessItemsForAccessId() {
    return "DELETE FROM WORKBASKET_ACCESS_LIST where ACCESS_ID = #{accessId}";
  }

  public static String findByWorkbasketAndAccessId() {
    return "<script>"
        + "<choose>"
        + "<when test=\"_databaseId == 'db2'\">"
        + "SELECT "
        + getMaximumPermissionStatement(false)
        + "</when>"
        + "<otherwise>"
        + "SELECT "
        + getMaximumPermissionStatement(true)
        + "</otherwise>"
        + "</choose>"
        + "FROM WORKBASKET_ACCESS_LIST "
        + "WHERE WORKBASKET_ID = #{workbasketId} AND ACCESS_ID IN"
        + "(<foreach item='item' collection='accessIds' separator=',' >#{item}</foreach>) "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>";
  }

  public static String findByWorkbasketKeyDomainAndAccessId() {
    return "<script>"
        + "<choose>"
        + "<when test=\"_databaseId == 'db2'\">"
        + "SELECT "
        + getMaximumPermissionStatement(false)
        + "</when>"
        + "<otherwise>"
        + "SELECT "
        + getMaximumPermissionStatement(true)
        + "</otherwise>"
        + "</choose>"
        + "FROM WORKBASKET_ACCESS_LIST "
        + "WHERE WORKBASKET_ID in ("
        + "SELECT ID FROM WORKBASKET "
        + "WHERE UPPER(KEY) = UPPER(#{workbasketKey}) "
        + "AND UPPER(DOMAIN) = UPPER(#{domain}) "
        + ")"
        + "AND ACCESS_ID IN"
        + "(<foreach item='item' collection='accessIds' separator=',' >#{item}</foreach>)"
        + "<if test=\"_databaseId == 'db2'\">with UR</if>"
        + "</script>";
  }

  private static String commonUpdateStatement() {
    return Stream.concat(COLUMNS.stream(), PERMISSIONS.stream())
        .map(col -> col.getLeft() + " = " + col.getRight())
        .collect(Collectors.joining(", ", "", " "));
  }

  private static String insertValues() {
    return Stream.concat(COLUMNS.stream(), PERMISSIONS.stream())
        .map(Pair::getRight)
        .collect(Collectors.joining(", ", "", " "));
  }

  private static String getMaximumPermissionStatement(boolean isNotDb2) {
    return PERMISSIONS.stream()
        .map(
            perm -> {
              String temp = "MAX(" + perm.getLeft();
              if (isNotDb2) {
                temp += "::int";
              }
              temp += ") AS " + perm.getLeft();
              return temp;
            })
        .collect(Collectors.joining(", ", "", " "));
  }

  private static String commonSelectStatements() {
    return Stream.concat(COLUMNS.stream(), PERMISSIONS.stream())
        .map(Pair::getLeft)
        .collect(Collectors.joining(", ", "", " "));
  }
}
