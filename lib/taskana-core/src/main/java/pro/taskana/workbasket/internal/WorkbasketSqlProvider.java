package pro.taskana.workbasket.internal;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.common.internal.util.Pair;

public class WorkbasketSqlProvider {

  public static final List<Pair<String, String>> columns =
      Arrays.asList(
          Pair.of("ID", "#{workbasket.id}"),
          Pair.of("KEY", "#{workbasket.key}"),
          Pair.of("CREATED", "#{workbasket.created}"),
          Pair.of("MODIFIED", "#{workbasket.modified}"),
          Pair.of("NAME", "#{workbasket.name}"),
          Pair.of("DOMAIN", "#{workbasket.domain}"),
          Pair.of("TYPE", "#{workbasket.type}"),
          Pair.of("DESCRIPTION", "#{workbasket.description}"),
          Pair.of("OWNER", "#{workbasket.owner}"),
          Pair.of("CUSTOM_1", "#{workbasket.custom1}"),
          Pair.of("CUSTOM_2", "#{workbasket.custom2}"),
          Pair.of("CUSTOM_3", "#{workbasket.custom3}"),
          Pair.of("CUSTOM_4", "#{workbasket.custom4}"),
          Pair.of("ORG_LEVEL_1", "#{workbasket.orgLevel1}"),
          Pair.of("ORG_LEVEL_2", "#{workbasket.orgLevel2}"),
          Pair.of("ORG_LEVEL_3", "#{workbasket.orgLevel3}"),
          Pair.of("ORG_LEVEL_4", "#{workbasket.orgLevel4}"),
          Pair.of("MARKED_FOR_DELETION", "#{workbasket.markedForDeletion}"));

  public static String findById() {
    return "<script>"
        + "SELECT "
        + commonSelectFields(false)
        + " FROM WORKBASKET WHERE ID = #{id}"
        + "<if test=\"_databaseId == 'db2'\">with UR </if>"
        + "</script>";
  }

  public static String findSummaryById() {
    return "<script>"
        + "SELECT "
        + commonSelectFields(true)
        + " FROM WORKBASKET WHERE ID = #{id} "
        + "<if test=\"_databaseId == 'db2'\">with UR </if>"
        + "</script>";
  }

  public static String findByKeyAndDomain() {
    return "<script>"
        + "SELECT "
        + commonSelectFields(false)
        + " FROM WORKBASKET WHERE UPPER(KEY) = UPPER(#{key}) and UPPER(DOMAIN) = UPPER(#{domain}) "
        + "<if test=\"_databaseId == 'db2'\">with UR </if>"
        + "</script>";
  }

  public static String findDistributionTargets() {
    return "<script>"
        + "SELECT "
        + commonSelectFields(true)
        + " FROM WORKBASKET "
        + "WHERE ID IN (SELECT TARGET_ID FROM DISTRIBUTION_TARGETS WHERE SOURCE_ID = #{id}) "
        + "<if test=\"_databaseId == 'db2'\">with UR </if>"
        + "</script>";
  }

  public static String findDistributionSources() {
    return "<script>"
        + "SELECT "
        + commonSelectFields(true)
        + " FROM WORKBASKET "
        + "WHERE ID IN (SELECT SOURCE_ID FROM DISTRIBUTION_TARGETS WHERE TARGET_ID = #{id}) "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>";
  }

  public static String findAll() {
    return "<script>"
        + "SELECT * FROM WORKBASKET ORDER BY ID "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>";
  }

  public static String insert() {
    return "<script>"
        + "INSERT INTO WORKBASKET ("
        + commonSelectFields(false)
        + ") "
        + "VALUES ("
        + valueReferences()
        + ") "
        + "</script>";
  }

  public static String update() {
    return "UPDATE WORKBASKET " + "SET " + updateSetStatement() + " WHERE id = #{workbasket.id}";
  }

  public static String updateByKeyAndDomain() {
    return "UPDATE WORKBASKET "
        + "SET "
        + updateSetStatement()
        + " WHERE KEY = #{workbasket.key} AND DOMAIN = #{workbasket.domain}";
  }

  public static String delete() {
    return "DELETE FROM WORKBASKET where id = #{id}";
  }

  private static String updateSetStatement() {
    return columns.stream()
        .map(col -> col.getLeft() + " = " + col.getRight())
        .collect(Collectors.joining(", "));
  }

  private static String commonSelectFields(boolean excludeMarkedForDeletion) {
    int limit = columns.size();
    if (excludeMarkedForDeletion) {
      limit -= 1;
    }
    return columns.stream().limit(limit).map(Pair::getLeft).collect(Collectors.joining(", "));
  }

  private static String valueReferences() {
    return columns.stream().map(Pair::getRight).collect(Collectors.joining(", "));
  }
}
