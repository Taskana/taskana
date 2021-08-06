package pro.taskana.workbasket.internal;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import pro.taskana.workbasket.api.WorkbasketPermission;

public class WorkbasketAccessSqlProvider {

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
        + ") VALUES (#{workbasketAccessItem.id},"
        + " #{workbasketAccessItem.workbasketId}, #{workbasketAccessItem.accessId},"
        + " #{workbasketAccessItem.accessName}, #{workbasketAccessItem.permRead},"
        + " #{workbasketAccessItem.permOpen}, #{workbasketAccessItem.permAppend},"
        + " #{workbasketAccessItem.permTransfer}, #{workbasketAccessItem.permDistribute},"
        + " #{workbasketAccessItem.permCustom1}, #{workbasketAccessItem.permCustom2},"
        + " #{workbasketAccessItem.permCustom3}, #{workbasketAccessItem.permCustom4},"
        + " #{workbasketAccessItem.permCustom5}, #{workbasketAccessItem.permCustom6},"
        + " #{workbasketAccessItem.permCustom7}, #{workbasketAccessItem.permCustom8},"
        + " #{workbasketAccessItem.permCustom9}, #{workbasketAccessItem.permCustom10},"
        + " #{workbasketAccessItem.permCustom11}, #{workbasketAccessItem.permCustom12})";
  }

  public static String update() {
    return "UPDATE WORKBASKET_ACCESS_LIST SET "
        + "WORKBASKET_ID = #{workbasketAccessItem."
        + "workbasketId}, ACCESS_ID = #{workbasketAccessItem.accessId}, "
        + "ACCESS_NAME = #{workbasketAccessItem.accessName}, "
        + "PERM_READ = #{workbasketAccessItem.permRead}, PERM_OPEN = #{workbasketAccessItem."
        + "permOpen}, PERM_APPEND = #{workbasketAccessItem.permAppend}, "
        + "PERM_TRANSFER = #{workbasketAccessItem.permTransfer}, "
        + "PERM_DISTRIBUTE = #{workbasketAccessItem.permDistribute}, "
        + "PERM_CUSTOM_1 = #{workbasketAccessItem.permCustom1}, "
        + "PERM_CUSTOM_2 = #{workbasketAccessItem.permCustom2}, "
        + "PERM_CUSTOM_3 = #{workbasketAccessItem.permCustom3}, "
        + "PERM_CUSTOM_4 = #{workbasketAccessItem.permCustom4}, "
        + "PERM_CUSTOM_5 = #{workbasketAccessItem.permCustom5}, "
        + "PERM_CUSTOM_6 = #{workbasketAccessItem.permCustom6}, "
        + "PERM_CUSTOM_7 = #{workbasketAccessItem.permCustom7}, "
        + "PERM_CUSTOM_8 = #{workbasketAccessItem.permCustom8}, "
        + "PERM_CUSTOM_9 = #{workbasketAccessItem.permCustom9}, "
        + "PERM_CUSTOM_10 = #{workbasketAccessItem.permCustom10}, "
        + "PERM_CUSTOM_11 = #{workbasketAccessItem.permCustom11}, "
        + "PERM_CUSTOM_12 = #{workbasketAccessItem.permCustom12} "
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
        + "</otherwise></choose>FROM WORKBASKET_ACCESS_LIST WHERE WORKBASKET_ID in (SELECT ID FROM"
        + " WORKBASKET WHERE UPPER(KEY) = UPPER(#{workbasketKey}) AND UPPER(DOMAIN) ="
        + " UPPER(#{domain}) )AND ACCESS_ID IN(<foreach item='item' collection='accessIds'"
        + " separator=',' >#{item}</foreach>)<if test=\"_databaseId == 'db2'\">with UR</if>"
        + "</script>";
  }

  private static String getMaximumPermissionStatement(boolean isNotDb2) {
    return Arrays.stream(WorkbasketPermission.values())
        .map(
            perm -> {
              String temp = "MAX(PERM_" + perm;
              if (isNotDb2) {
                temp += "::int";
              }
              temp += ") AS P_" + perm;
              return temp;
            })
        .collect(Collectors.joining(", "));
  }

  private static String commonSelectStatements() {
    return "WORKBASKET_ID, ACCESS_ID, ACCESS_NAME, "
        + Arrays.stream(WorkbasketPermission.values())
            .map(Objects::toString)
            .collect(Collectors.joining(", PERM_", "PERM_", " "));
  }
}
