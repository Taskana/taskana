package pro.taskana.user.internal;

import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.DB2_WITH_UR;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_SCRIPT_TAG;

public class UserMapperSqlProvider {

  private static final String USER_INFO_COLUMNS =
      "USER_ID, FIRST_NAME, LASTNAME, FULL_NAME, LONG_NAME, E_MAIL, PHONE, MOBILE_PHONE, "
          + "ORG_LEVEL_4, ORG_LEVEL_3, ORG_LEVEL_2, ORG_LEVEL_1, DATA ";
  private static final String USER_INFO_VALUES =
      "#{id}, #{firstName}, #{lastName}, #{fullName}, #{longName}, #{email}, #{phone}, "
          + "#{mobilePhone}, #{orgLevel4}, #{orgLevel3}, #{orgLevel2}, #{orgLevel1}, #{data} ";

  private UserMapperSqlProvider() {}

  public static String findById() {
    return OPENING_SCRIPT_TAG
        + "SELECT "
        + USER_INFO_COLUMNS
        + "FROM USER_INFO "
        + "WHERE USER_ID = #{id} "
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  public static String findByIds() {
    return OPENING_SCRIPT_TAG
        + "SELECT "
        + USER_INFO_COLUMNS
        + "FROM USER_INFO "
        + "WHERE USER_ID IN (<foreach item='id' collection='ids' separator=',' >#{id}</foreach>) "
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  public static String findGroupsById() {
    return OPENING_SCRIPT_TAG
        + "SELECT GROUP_ID FROM GROUP_INFO WHERE USER_ID = #{id} "
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  public static String insert() {
    return "INSERT INTO USER_INFO ( " + USER_INFO_COLUMNS + ") VALUES(" + USER_INFO_VALUES + ")";
  }

  public static String insertGroups() {
    return OPENING_SCRIPT_TAG
        + "INSERT INTO GROUP_INFO "
        + "(USER_ID, GROUP_ID) VALUES "
        + "<foreach item='group' collection='groups' open='(' separator='),(' close=')'>"
        + "#{id}, #{group}"
        + "</foreach> "
        + CLOSING_SCRIPT_TAG;
  }

  public static String update() {
    return "UPDATE USER_INFO "
        + "SET FIRST_NAME = #{firstName}, "
        + "LASTNAME = #{lastName}, FULL_NAME = #{fullName}, LONG_NAME = #{longName}, "
        + "E_MAIL = #{email}, PHONE = #{phone}, MOBILE_PHONE = #{mobilePhone}, "
        + "ORG_LEVEL_4 = #{orgLevel4}, ORG_LEVEL_3 = #{orgLevel3}, "
        + "ORG_LEVEL_2 = #{orgLevel2}, ORG_LEVEL_1 = #{orgLevel1}, DATA = #{data} "
        + "WHERE USER_ID = #{id} ";
  }

  public static String delete() {
    return "DELETE FROM USER_INFO WHERE USER_ID = #{id} ";
  }

  public static String deleteGroups() {
    return "DELETE FROM GROUP_INFO WHERE USER_ID = #{id} ";
  }
}
