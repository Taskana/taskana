package pro.taskana.monitor.internal;

import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereIn;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereLike;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereNotIn;

import java.util.stream.IntStream;

import pro.taskana.common.internal.util.SqlProviderUtil;

public class MonitorMapperSqlProvider {
  private MonitorMapperSqlProvider() {}

  @SuppressWarnings("unused")
  public static String getTaskCountOfWorkbaskets() {
    return OPENING_SCRIPT_TAG
        + "SELECT B.WORKBASKET_KEY, B.AGE_IN_DAYS, COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
        + "<if test=\"_databaseId == 'db2'\">"
        + "SELECT T.WORKBASKET_KEY, (DAYS(T.${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) "
        + "as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'h2'\">"
        + "SELECT T.WORKBASKET_KEY, DATEDIFF('DAY', #{now}, T.${timestamp}) as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'postgres'\">"
        + "SELECT T.WORKBASKET_KEY, DATE_PART('DAY', T.${timestamp} - #{now}) as AGE_IN_DAYS "
        + "</if> "
        + "FROM TASK AS T LEFT JOIN ATTACHMENT AS A ON T.ID = A.TASK_ID "
        + OPENING_WHERE_TAG
        + taskWhereStatements()
        + "<if test=\"report.combinedClassificationFilter != null\">"
        + "AND <foreach collection='report.combinedClassificationFilter' "
        + "item='item' separator='OR'> "
        + "T.CLASSIFICATION_ID = #{item.taskClassificationId}"
        + "<if test=\"item.attachmentClassificationId != null\">"
        + "AND A.CLASSIFICATION_ID = #{item.attachmentClassificationId}"
        + "</if>"
        + "</foreach>"
        + "</if>"
        + "AND T.${timestamp} IS NOT NULL "
        + CLOSING_WHERE_TAG
        + ") AS B "
        + "GROUP BY B.WORKBASKET_KEY, B.AGE_IN_DAYS"
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String getTaskCountOfCategories() {
    return OPENING_SCRIPT_TAG
        + "SELECT B.CLASSIFICATION_CATEGORY, B.AGE_IN_DAYS, "
        + "COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
        + "SELECT CLASSIFICATION_CATEGORY, "
        + "<if test=\"_databaseId == 'db2'\">"
        + "(DAYS(${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'h2'\">"
        + "DATEDIFF('DAY', #{now}, ${timestamp}) as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'postgres'\">"
        + "DATE_PART('DAY', ${timestamp} - #{now}) as AGE_IN_DAYS "
        + "</if> "
        + "FROM TASK T "
        + OPENING_WHERE_TAG
        + taskWhereStatements()
        + "AND ${timestamp} IS NOT NULL "
        + CLOSING_WHERE_TAG
        + ") AS B "
        + "GROUP BY B.CLASSIFICATION_CATEGORY, B.AGE_IN_DAYS "
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String getTaskCountOfClassifications() {
    return OPENING_SCRIPT_TAG
        + "SELECT B.CLASSIFICATION_KEY, B.AGE_IN_DAYS, "
        + "COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
        + "SELECT CLASSIFICATION_KEY, "
        + "<if test=\"_databaseId == 'db2'\">"
        + "(DAYS(${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'h2'\">"
        + "DATEDIFF('DAY', #{now}, ${timestamp}) as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'postgres'\">"
        + "DATE_PART('DAY', ${timestamp} - #{now}) as AGE_IN_DAYS "
        + "</if> "
        + "FROM TASK T "
        + OPENING_WHERE_TAG
        + taskWhereStatements()
        + "AND ${timestamp} IS NOT NULL "
        + CLOSING_WHERE_TAG
        + ") AS B "
        + "GROUP BY B.CLASSIFICATION_KEY, B.AGE_IN_DAYS "
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String getTaskCountOfDetailedClassifications() {
    return OPENING_SCRIPT_TAG
        + "SELECT B.TASK_CLASSIFICATION_KEY, B.ATTACHMENT_CLASSIFICATION_KEY, "
        + "B.AGE_IN_DAYS, COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
        + "SELECT T.CLASSIFICATION_KEY as TASK_CLASSIFICATION_KEY, "
        + "A.CLASSIFICATION_KEY as ATTACHMENT_CLASSIFICATION_KEY, "
        + "<if test=\"_databaseId == 'db2'\">"
        + "(DAYS(T.${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'h2'\">"
        + "DATEDIFF('DAY', #{now}, T.${timestamp}) as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'postgres'\">"
        + "DATE_PART('DAY', T.${timestamp} - #{now}) as AGE_IN_DAYS "
        + "</if> "
        + "FROM TASK AS T LEFT JOIN ATTACHMENT AS A ON T.ID = A.TASK_ID "
        + OPENING_WHERE_TAG
        + taskWhereStatements()
        + "AND T.${timestamp} IS NOT NULL "
        + CLOSING_WHERE_TAG
        + ") AS B "
        + "GROUP BY B.TASK_CLASSIFICATION_KEY, B.ATTACHMENT_CLASSIFICATION_KEY, B.AGE_IN_DAYS "
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String getTaskCountOfTaskCustomFieldValues() {
    return OPENING_SCRIPT_TAG
        + "SELECT B.CUSTOM_FIELD, B.AGE_IN_DAYS, COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
        + "SELECT ${report.taskCustomField} as CUSTOM_FIELD, "
        + "<if test=\"_databaseId == 'db2'\">"
        + "(DAYS(${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'h2'\">"
        + "DATEDIFF('DAY', #{now}, ${timestamp}) as AGE_IN_DAYS "
        + "</if> "
        + "<if test=\"_databaseId == 'postgres'\">"
        + "DATE_PART('DAY', ${timestamp} - #{now}) as AGE_IN_DAYS "
        + "</if> "
        + "FROM TASK T "
        + OPENING_WHERE_TAG
        + taskWhereStatements()
        + "AND ${timestamp} IS NOT NULL "
        + CLOSING_WHERE_TAG
        + ") AS B "
        + "GROUP BY B.CUSTOM_FIELD, B.AGE_IN_DAYS "
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String getTaskIdsForSelectedItems() {
    return OPENING_SCRIPT_TAG
        + "SELECT T.ID FROM TASK T "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT A ON T.ID = A.TASK_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + taskWhereStatements()
        + "<if test=\"combinedClassificationFilter != null\">"
        + "AND <foreach collection='combinedClassificationFilter' item='item' separator='OR'> "
        + "T.CLASSIFICATION_ID = #{item.taskClassificationId} "
        + "<if test=\"item.attachmentClassificationId != null\">"
        + "AND A.CLASSIFICATION_ID = #{item.attachmentClassificationId} "
        + "</if>"
        + "</foreach>"
        + "</if>"
        + "AND T.${timestamp} IS NOT NULL AND ( "
        + "<foreach collection='selectedItems' item='selectedItem' separator=' OR '>"
        + "#{selectedItem.key} = T.${groupedBy} AND "
        + "<if test=\"joinWithAttachments and combinedClassificationFilter == null\">"
        + "<if test='selectedItem.subKey != null'>"
        + "A.CLASSIFICATION_KEY = #{selectedItem.subKey} AND "
        + "</if>"
        + "</if>"
        + "<if test=\"_databaseId == 'db2'\">"
        + "#{selectedItem.upperAgeLimit} >= (DAYS(${timestamp})"
        + " - DAYS(CAST(#{now} as TIMESTAMP))) AND "
        + "#{selectedItem.lowerAgeLimit} &lt;= (DAYS(${timestamp})"
        + " - DAYS(CAST(#{now} as TIMESTAMP))) "
        + "</if> "
        + "<if test=\"_databaseId == 'h2'\">"
        + "#{selectedItem.upperAgeLimit} >= DATEDIFF('DAY', #{now}, ${timestamp}) AND "
        + "#{selectedItem.lowerAgeLimit} &lt;= DATEDIFF('DAY', #{now}, ${timestamp}) "
        + "</if> "
        + "<if test=\"_databaseId == 'postgres'\">"
        + "#{selectedItem.upperAgeLimit} >= DATE_PART('day', ${timestamp} - #{now} ) AND "
        + "#{selectedItem.lowerAgeLimit} &lt;= DATE_PART('day', ${timestamp} - #{now} ) "
        + "</if> "
        + "</foreach>) "
        + CLOSING_WHERE_TAG
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String getTasksCountByState() {
    return OPENING_SCRIPT_TAG
        + "SELECT WORKBASKET_KEY, STATE, COUNT(STATE) as COUNT "
        + "FROM TASK AS T"
        + OPENING_WHERE_TAG
        + whereIn("domains", "DOMAIN")
        + whereIn("states", "STATE")
        + whereIn("workbasketIds", "WORKBASKET_ID")
        + "<if test='priorityMinimum != null'>"
        + "AND priority >= #{priorityMinimum} "
        + "</if>"
        + CLOSING_WHERE_TAG
        + "GROUP BY WORKBASKET_KEY, STATE"
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String getTasksCountForStatusGroupedByOrgLevel() {
    return OPENING_SCRIPT_TAG
        + "SELECT A.AGE_IN_DAYS, A.ORG_LEVEL_1, A.ORG_LEVEL_2, A.ORG_LEVEL_3, A.ORG_LEVEL_4, "
        + "'${status}' AS STATUS, COUNT(A.AGE_IN_DAYS) AS COUNT FROM ("
        // This subquery prevents the repetition of the AGE_IN_DAYS column calculation
        // (like everywhere else in the Mappers...)in the group by clause.
        // DB2 is not able to reuse computed columns in the group by statement. Even if this adds
        // a little
        // overhead / complexity. It's worth the trade-off of not computing the AGE_IN_DAYS column
        // twice.
        + "SELECT W.ORG_LEVEL_1, W.ORG_LEVEL_2, W.ORG_LEVEL_3, W.ORG_LEVEL_4, "
        + "<if test=\"_databaseId == 'db2'\">"
        + "(DAYS(T.${status}) - DAYS(CAST(#{now} as TIMESTAMP)))"
        + "</if>"
        + "<if test=\"_databaseId == 'h2'\">"
        + "DATEDIFF('DAY', #{now}, T.${status})"
        + "</if>"
        + "<if test=\"_databaseId == 'postgres'\">"
        + "DATE_PART('DAY', T.${status} - #{now})"
        + "</if>"
        + " as AGE_IN_DAYS "
        + "FROM TASK AS T INNER JOIN WORKBASKET AS W ON T.WORKBASKET_KEY=W.KEY "
        + OPENING_WHERE_TAG
        + "<if test=\"status.name() == 'COMPLETED'\">"
        + "T.COMPLETED IS NOT NULL "
        + "</if>"
        + taskWhereStatements()
        + CLOSING_WHERE_TAG
        + ") AS A "
        + "GROUP BY A.AGE_IN_DAYS, A.ORG_LEVEL_1, A.ORG_LEVEL_2, A.ORG_LEVEL_3, A.ORG_LEVEL_4 "
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String getCustomAttributeValuesForReport() {
    return OPENING_SCRIPT_TAG
        + "SELECT DISTINCT ${customField} "
        + "FROM TASK T "
        + "<if test=\"combinedClassificationFilter != null\">"
        + "LEFT JOIN ATTACHMENT A ON T.ID = A.TASK_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + taskWhereStatements()
        + "<if test=\"combinedClassificationFilter != null\">"
        + "AND <foreach collection='combinedClassificationFilter' item='item' separator='OR'> "
        + "T.CLASSIFICATION_ID = #{item.taskClassificationId} "
        + "<if test=\"item.attachmentClassificationId != null\">"
        + "AND A.CLASSIFICATION_ID = #{item.attachmentClassificationId} "
        + "</if>"
        + "</foreach>"
        + "</if>"
        + CLOSING_WHERE_TAG
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String getTaskCountByPriority() {
    return OPENING_SCRIPT_TAG
        + "SELECT T.WORKBASKET_KEY, T.PRIORITY, COUNT(T.PRIORITY) as COUNT "
        + "FROM TASK as T "
        + "INNER JOIN WORKBASKET as W ON W.ID = T.WORKBASKET_ID "
        + OPENING_WHERE_TAG
        + taskWhereStatements()
        + workbasketWhereStatements()
        + CLOSING_WHERE_TAG
        + "GROUP BY T.WORKBASKET_KEY, T.PRIORITY"
        + CLOSING_SCRIPT_TAG;
  }

  private static StringBuilder whereCustomStatements(
      String baseCollection, String baseColumn, int customBound, StringBuilder sb) {
    IntStream.rangeClosed(1, customBound)
        .forEach(
            x -> {
              String column = baseColumn + "_" + x;
              whereIn(baseCollection + x + "In", column, sb);
              whereNotIn(baseCollection + x + "NotIn", column, sb);
              whereLike(baseCollection + x + "Like", column, sb);
            });
    return sb;
  }

  private static StringBuilder taskWhereStatements() {
    StringBuilder sb = new StringBuilder();
    SqlProviderUtil.whereIn("report.workbasketIds", "T.WORKBASKET_ID", sb);
    SqlProviderUtil.whereIn("report.states", "T.STATE", sb);
    SqlProviderUtil.whereIn("report.classificationCategories", "T.CLASSIFICATION_CATEGORY", sb);
    SqlProviderUtil.whereIn("report.domains", "T.DOMAIN", sb);
    SqlProviderUtil.whereIn("report.classificationIds", "T.CLASSIFICATION_ID", sb);
    SqlProviderUtil.whereNotIn("report.excludedClassificationIds", "T.CLASSIFICATION_ID", sb);
    whereCustomStatements("report.custom", "T.CUSTOM", 16, sb);
    return sb;
  }

  private static StringBuilder workbasketWhereStatements() {
    StringBuilder sb = new StringBuilder();
    SqlProviderUtil.whereIn("report.workbasketTypes", "W.TYPE", sb);
    return sb;
  }
}
