package pro.taskana.task.internal;

import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.DB2_WITH_UR;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereCustomIntStatements;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereCustomStatements;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereIn;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereInInterval;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereLike;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereNotIn;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereNotInInterval;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereNotLike;

import java.util.Arrays;
import java.util.stream.Collectors;
import pro.taskana.task.api.TaskQueryColumnName;

public class TaskQuerySqlProvider {
  private TaskQuerySqlProvider() {}

  @SuppressWarnings("unused")
  public static String queryTaskSummaries() {
    return OPENING_SCRIPT_TAG
        + openOuterClauseForGroupByPorOrSor()
        + "SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> "
        + commonSelectFields()
        + "<if test='groupBySor != null'>, o.VALUE as SOR_VALUE </if>"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", a.CLASSIFICATION_ID as ACLASSIFICATION_ID, "
        + "a.CLASSIFICATION_KEY as ACLASSIFICATION_KEY, a.CHANNEL as ACHANNEL, "
        + "a.REF_VALUE as AREF_VALUE, a.RECEIVED as ARECEIVED"
        + "</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">, c.NAME as CNAME </if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">, "
        + "ac.NAME as ACNAME </if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">, w.NAME as WNAME </if>"
        + "<if test=\"joinWithUserInfo\">, u.LONG_NAME</if>"
        + groupByPorIfActive()
        + groupBySorIfActive()
        + "FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithWorkbaskets\">"
        + "LEFT JOIN WORKBASKET w ON t.WORKBASKET_ID = w.ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskWhereStatement()
        + "<if test='selectAndClaim == true'> AND t.STATE = 'READY' </if>"
        + CLOSING_WHERE_TAG
        + closeOuterClauseForGroupByPor()
        + closeOuterClauseForGroupBySor()
        + "<if test='!orderByOuter.isEmpty()'>"
        + "ORDER BY <foreach item='item' collection='orderByOuter' separator=',' >${item}</foreach>"
        + "</if> "
        + "<if test='selectAndClaim == true'> "
        + "FETCH FIRST ROW ONLY FOR UPDATE "
        + "</if>"
        + "<if test=\"_databaseId == 'db2' and selectAndClaim \">WITH RS USE "
        + "AND KEEP UPDATE LOCKS </if>"
        + "<if test=\"_databaseId == 'db2' and !selectAndClaim \">WITH UR </if>"
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String queryTaskSummariesDb2() {
    return OPENING_SCRIPT_TAG
        + "WITH X ("
        + db2selectFields()
        + ") AS ("
        + "SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> "
        + commonSelectFields()
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", a.CLASSIFICATION_ID, a.CLASSIFICATION_KEY, a.CHANNEL, a.REF_VALUE, a.RECEIVED"
        + "</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">, c.NAME </if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">, ac.NAME </if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">, w.NAME </if>"
        + "<if test=\"joinWithUserInfo\">, u.LONG_NAME </if>"
        + "FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithWorkbaskets\">"
        + "LEFT JOIN WORKBASKET w ON t.WORKBASKET_ID = w.ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + commonTaskWhereStatement()
        + CLOSING_WHERE_TAG
        + "), Y ("
        + db2selectFields()
        + ", FLAG ) AS ("
        + "SELECT "
        + db2selectFields()
        + ", ("
        + "<if test='accessIdIn != null'> "
        + "SELECT 1 "
        + "FROM WORKBASKET_ACCESS_LIST s "
        + "WHERE "
        + "s.ACCESS_ID IN "
        + "(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "and "
        + "s.WORKBASKET_ID = X.WORKBASKET_ID AND s.perm_read = 1 AND s.perm_readtasks = 1"
        + " fetch first 1 rows only"
        + "</if>"
        + "<if test='accessIdIn == null'> "
        + "VALUES(1)"
        + "</if>"
        + " ) "
        + "FROM X )"
        + "SELECT "
        + db2selectFields()
        + "FROM Y "
        + "WHERE FLAG = 1 "
        + "<if test='!orderByOuter.isEmpty()'>"
        + "ORDER BY <foreach item='item' collection='orderByOuter' separator=',' >${item}</foreach>"
        + "</if> "
        + "<if test='selectAndClaim == true'>"
        + "FETCH FIRST ROW ONLY FOR UPDATE WITH RS USE AND KEEP UPDATE LOCKS"
        + "</if>"
        + "<if test='selectAndClaim == false'> with UR</if>"
        + CLOSING_SCRIPT_TAG;
  }

  /**
   * you cant lock a view in oracle. the sql code `FETCH FIRST ROW ONLY` would create in oracle a
   * view therefore we must first select a rowid based on where criteria then we select everything
   * based on rowid and lock this rowid
   *
   * @return SELECT Statement for oracle claiming
   */
  @SuppressWarnings("unused")
  public static String queryTaskSummariesOracle() {
    return OPENING_SCRIPT_TAG
        + "SELECT "
        + commonSelectFieldsOracle()
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", a2.CLASSIFICATION_ID, a2.CLASSIFICATION_KEY, a2.CHANNEL, a2.REF_VALUE, a2.RECEIVED"
        + "</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">, c2.NAME </if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">, ac2.NAME </if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">, w2.NAME </if>"
        + "<if test=\"joinWithUserInfo\">, u2.LONG_NAME </if>"
        + "FROM TASK t2 "
        + "<if test=\"joinWithAttachments\">LEFT JOIN ATTACHMENT a2 ON t2.ID = a2.TASK_ID </if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">LEFT JOIN OBJECT_REFERENCE o2 "
        + "ON t2.ID = o2.TASK_ID </if>"
        + "<if test=\"joinWithClassifications\">LEFT JOIN CLASSIFICATION c2 "
        + "ON t2.CLASSIFICATION_ID = c2.ID </if>"
        + "<if test=\"joinWithAttachmentClassifications\">LEFT JOIN CLASSIFICATION ac2 "
        + "ON a2.CLASSIFICATION_ID = ac2.ID </if>"
        + "<if test=\"joinWithWorkbaskets\">LEFT JOIN WORKBASKET w2 "
        + "ON t2.WORKBASKET_ID = w2.ID </if>"
        + "<if test=\"joinWithUserInfo\">LEFT JOIN USER_INFO u2 ON t2.owner = u2.USER_ID </if>"
        + "WHERE t2.rowid = (SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> t.rowid "
        + "FROM TASK t "
        + "<if test=\"joinWithAttachments\">LEFT JOIN ATTACHMENT a ON t.ID = a.TASK_ID </if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">LEFT JOIN OBJECT_REFERENCE o "
        + "ON t.ID = o.TASK_ID </if>"
        + "<if test=\"joinWithClassifications\">LEFT JOIN CLASSIFICATION c "
        + "ON t.CLASSIFICATION_ID = c.ID </if>"
        + "<if test=\"joinWithAttachmentClassifications\">LEFT JOIN CLASSIFICATION ac "
        + "ON a.CLASSIFICATION_ID = ac.ID </if>"
        + "<if test=\"joinWithWorkbaskets\">LEFT JOIN WORKBASKET w "
        + "ON t.WORKBASKET_ID = w.ID </if>"
        + "<if test=\"joinWithUserInfo\">LEFT JOIN USER_INFO u ON t.owner = u.USER_ID </if>"
        + OPENING_WHERE_TAG
        + commonTaskWhereStatement()
        + "<if test='selectAndClaim == true'> AND t.STATE = 'READY' </if>"
        + CLOSING_WHERE_TAG
        + "<if test='!orderByOuter.isEmpty()'>"
        + "ORDER BY <foreach item='item' collection='orderByOuter' separator=',' >${item}</foreach>"
        + "</if> "
        + "fetch first 1 rows only "
        + ") FOR UPDATE"
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String countQueryTasks() {
    return OPENING_SCRIPT_TAG
        + "SELECT COUNT( <if test=\"useDistinctKeyword\">DISTINCT</if> t.ID) "
        + "<if test=\"groupByPor or groupBySor != null\"> "
        + "FROM (SELECT t.ID, t.POR_VALUE "
        + "</if> "
        + "<if test=\"groupBySor != null\"> "
        + ", o.VALUE as SOR_VALUE "
        + "</if> "
        + groupByPorIfActive()
        + groupBySorIfActive()
        + "FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskWhereStatement()
        + CLOSING_WHERE_TAG
        + closeOuterClauseForGroupByPor()
        + closeOuterClauseForGroupBySor()
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String countQueryTasksDb2() {
    return OPENING_SCRIPT_TAG
        + "WITH X (ID, WORKBASKET_ID) AS ("
        + "SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> "
        + "t.ID, t.WORKBASKET_ID FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + commonTaskWhereStatement()
        + CLOSING_WHERE_TAG
        + "), Y (ID, FLAG) AS ("
        + "SELECT ID, ("
        + "<if test='accessIdIn != null'>"
        + "SELECT 1 FROM WORKBASKET_ACCESS_LIST s "
        + "WHERE s.ACCESS_ID IN "
        + "(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "and "
        + "s.WORKBASKET_ID = X.WORKBASKET_ID AND s.perm_read = 1 AND s.perm_readtasks = 1"
        + " fetch first 1 rows only "
        + "</if> "
        + "<if test='accessIdIn == null'>"
        + "VALUES(1)"
        + "</if> "
        + ") "
        + "FROM X ) SELECT COUNT(*) "
        + "FROM Y WHERE FLAG = 1 with UR"
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String queryTaskColumnValues() {
    return OPENING_SCRIPT_TAG
        + "SELECT DISTINCT ${columnName} "
        + "<if test=\"joinWithUserInfo\">, u.LONG_NAME </if>"
        + "FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskWhereStatement()
        + CLOSING_WHERE_TAG
        + "<if test='!orderByInner.isEmpty()'>"
        + "ORDER BY <foreach item='item' collection='orderByInner' separator=',' >"
        + "<choose>"
        + "<when test=\"item.contains('TCLASSIFICATION_KEY ASC')\">"
        + "t.CLASSIFICATION_KEY ASC"
        + "</when>"
        + "<when test=\"item.contains('TCLASSIFICATION_KEY DESC')\">"
        + "t.CLASSIFICATION_KEY DESC"
        + "</when>"
        + "<when test=\"item.contains('ACLASSIFICATION_KEY ASC')\">"
        + "a.CLASSIFICATION_KEY ASC"
        + "</when>"
        + "<when test=\"item.contains('ACLASSIFICATION_KEY DESC')\">"
        + "a.CLASSIFICATION_KEY DESC"
        + "</when>"
        + "<when test=\"item.contains('ACLASSIFICATION_ID ASC')\">"
        + "a.CLASSIFICATION_ID ASC"
        + "</when>"
        + "<when test=\"item.contains('ACLASSIFICATION_ID DESC')\">"
        + "a.CLASSIFICATION_ID DESC"
        + "</when>"
        + "<when test=\"item.contains('CLASSIFICATION_NAME DESC')\">"
        + "c.NAME DESC"
        + "</when>"
        + "<when test=\"item.contains('CLASSIFICATION_NAME ASC')\">"
        + "c.NAME ASC"
        + "</when>"
        + "<when test=\"item.contains('A_CLASSIFICATION_NAME DESC')\">"
        + "ac.NAME DESC"
        + "</when>"
        + "<when test=\"item.contains('A_CLASSIFICATION_NAME ASC')\">"
        + "ac.NAME ASC"
        + "</when>"
        + "<otherwise>${item}</otherwise>"
        + "</choose>"
        + "</foreach>"
        + "</if> "
        + DB2_WITH_UR
        + CLOSING_SCRIPT_TAG;
  }

  private static String commonSelectFields() {
    // includes only the names that start with a t, because other columns are conditional
    return Arrays.stream(TaskQueryColumnName.values())
        .map(TaskQueryColumnName::toString)
        .filter(column -> column.startsWith("t"))
        .collect(Collectors.joining(", "));
  }

  private static String commonSelectFieldsOracle() {
    return commonSelectFields().replace("t.id", "t2.id").replace(", t", ", t2");
  }

  private static String db2selectFields() {
    // needs to be the same order as the commonSelectFields (TaskQueryColumnValue)
    return "ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, RECEIVED, DUE, NAME, "
        + "CREATOR, DESCRIPTION, NOTE, PRIORITY, MANUAL_PRIORITY, STATE, CLASSIFICATION_CATEGORY, "
        + "TCLASSIFICATION_KEY, CLASSIFICATION_ID, "
        + "WORKBASKET_ID, WORKBASKET_KEY, DOMAIN, "
        + "BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, POR_COMPANY, POR_SYSTEM, "
        + "POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_1, CUSTOM_2, "
        + "CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10, "
        + "CUSTOM_11, CUSTOM_12, CUSTOM_13, CUSTOM_14, CUSTOM_15, CUSTOM_16, "
        + "CUSTOM_INT_1, CUSTOM_INT_2, CUSTOM_INT_3,  CUSTOM_INT_4,  CUSTOM_INT_5, "
        + "CUSTOM_INT_6, CUSTOM_INT_7, CUSTOM_INT_8"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">, CNAME</if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">, ACNAME</if>"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", ACLASSIFICATION_ID, ACLASSIFICATION_KEY, CHANNEL, REF_VALUE, ARECEIVED"
        + "</if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">, WNAME</if>"
        + "<if test=\"joinWithUserInfo\">, ULONG_NAME </if>";
  }

  private static String checkForAuthorization() {
    return "<if test='accessIdIn != null'> AND t.WORKBASKET_ID IN ("
        + "SELECT WID "
        + "FROM ("
        + "<choose>"
        + "<when test=\"_databaseId == 'db2' || _databaseId == 'oracle'\">"
        + "SELECT WORKBASKET_ID as WID, MAX(PERM_READ) as MAX_READ, "
        + "MAX(PERM_READTASKS) as MAX_READTASKS "
        + "</when>"
        + "<otherwise>"
        + "SELECT WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ, "
        + "MAX(PERM_READTASKS::int) as MAX_READTASKS "
        + "</otherwise>"
        + "</choose>"
        + "FROM WORKBASKET_ACCESS_LIST s where ACCESS_ID IN "
        + "(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "GROUP by WORKBASKET_ID) f "
        + "WHERE MAX_READ = 1 AND MAX_READTASKS = 1) "
        + "</if>";
  }

  private static String groupByPorIfActive() {
    return "<if test=\"groupByPor\"> "
        + ", ROW_NUMBER() OVER (PARTITION BY POR_VALUE "
        + "<if test='!orderByInner.isEmpty() and !orderByInner.get(0).equals(\"POR_VALUE ASC\") "
        + "and !orderByInner.get(0).equals(\"POR_VALUE DESC\")'>"
        + "ORDER BY <foreach item='item' collection='orderByInner' separator=',' >${item}</foreach>"
        + "</if> "
        + "<if test='orderByInner.isEmpty() or orderByInner.get(0).equals(\"POR_VALUE ASC\") "
        + "or orderByInner.get(0).equals(\"POR_VALUE DESC\")'>"
        + "ORDER BY DUE ASC"
        + "</if> "
        + ")"
        + "AS rn"
        + "</if> ";
  }

  private static String groupBySorIfActive() {
    return "<if test='groupBySor != null'> "
        + ", ROW_NUMBER() OVER (PARTITION BY o.VALUE "
        + "<if test='!orderByInner.isEmpty()'>"
        + "ORDER BY <foreach item='item' collection='orderByInner' separator=',' >${item}</foreach>"
        + "</if> "
        + "<if test='orderByInner.isEmpty()'>"
        + "ORDER BY DUE ASC"
        + "</if> "
        + ")"
        + "AS rn"
        + "</if> ";
  }

  private static String openOuterClauseForGroupByPorOrSor() {
    return "<if test=\"groupByPor or groupBySor != null\"> " + "SELECT * FROM (" + "</if> ";
  }

  private static String closeOuterClauseForGroupByPor() {
    return "<if test=\"groupByPor\"> "
        + ") t LEFT JOIN"
        + " (SELECT POR_VALUE as PVALUE, COUNT(POR_VALUE) AS R_COUNT "
        + "FROM (SELECT DISTINCT t.id , POR_VALUE "
        + "FROM TASK t"
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithWorkbaskets\">"
        + "LEFT JOIN WORKBASKET w ON t.WORKBASKET_ID = w.ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskWhereStatement()
        + "<if test='selectAndClaim == true'> AND t.STATE = 'READY' </if>"
        + CLOSING_WHERE_TAG
        + ") as y "
        + "GROUP BY POR_VALUE) AS tt ON t.POR_VALUE=tt.PVALUE "
        + "WHERE rn = 1"
        + "</if> ";
  }

  private static String closeOuterClauseForGroupBySor() {
    return "<if test='groupBySor != null'> "
        + ") t LEFT JOIN"
        + " (SELECT o.VALUE, COUNT(o.VALUE) AS R_COUNT "
        + "FROM TASK t "
        + "LEFT JOIN OBJECT_REFERENCE o on t.ID=o.TASK_ID "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithWorkbaskets\">"
        + "LEFT JOIN WORKBASKET w ON t.WORKBASKET_ID = w.ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskWhereStatement()
        + "AND o.TYPE=#{groupBySor} "
        + CLOSING_WHERE_TAG
        + "GROUP BY o.VALUE) AS tt ON t.SOR_VALUE=tt.VALUE "
        + "WHERE rn = 1"
        + "</if> ";
  }

  private static String commonTaskObjectReferenceWhereStatement() {
    return "<if test='objectReferences != null'>"
        + "AND (<foreach item='item' collection='objectReferences' separator=' OR '> "
        + "<if test='item.company != null'>t.POR_COMPANY = #{item.company} </if>"
        + "<if test='item.system != null'> "
        + "<if test='item.company != null'>AND</if> "
        + "t.POR_SYSTEM = #{item.system} </if>"
        + "<if test='item.systemInstance != null'> "
        + "<if test='item.company != null or item.system != null'>AND</if> "
        + "t.POR_INSTANCE = #{item.systemInstance} </if>"
        + "<if test='item.type != null'> "
        + "<if test='item.company != null or item.system != null or item.systemInstance != null'>"
        + "AND</if> "
        + "t.POR_TYPE = #{item.type} </if>"
        + "<if test='item.value != null'> "
        + "<if test='item.company != null or item.system != null "
        + "or item.systemInstance != null or item.type != null'>"
        + "AND</if> "
        + "t.POR_VALUE = #{item.value} "
        + "</if>"
        + "</foreach>)"
        + "</if>";
  }

  private static String commonTaskSecondaryObjectReferencesWhereStatement() {
    return "<if test='secondaryObjectReferences != null'>"
        + "AND (<foreach item='item' collection='secondaryObjectReferences' separator=' OR '> "
        + "<if test='item.company != null'>o.COMPANY = #{item.company} </if>"
        + "<if test='item.system != null'> "
        + "<if test='item.company != null'>AND</if> "
        + "o.SYSTEM = #{item.system} </if>"
        + "<if test='item.systemInstance != null'> "
        + "<if test='item.company != null or item.system != null'>AND</if> "
        + "o.SYSTEM_INSTANCE = #{item.systemInstance} </if>"
        + "<if test='item.type != null'> "
        + "<if test='item.company != null or item.system != null or item.systemInstance != null'>"
        + "AND</if> "
        + "o.TYPE = #{item.type} </if>"
        + "<if test='item.value != null'> "
        + "<if test='item.company != null or item.system != null "
        + "or item.systemInstance != null or item.type != null'>"
        + "AND</if> "
        + "o.VALUE = #{item.value} "
        + "</if>"
        + "</foreach>)"
        + "</if>";
  }

  private static void commonWhereClauses(String filter, String channel, StringBuilder sb) {
    whereIn(filter + "In", channel, sb);
    whereNotIn(filter + "NotIn", channel, sb);
    whereLike(filter + "Like", channel, sb);
    whereNotLike(filter + "NotLike", channel, sb);
  }

  private static StringBuilder commonTaskWhereStatement() {
    StringBuilder sb = new StringBuilder();
    commonWhereClauses("attachmentChannel", "a.CHANNEL", sb);
    commonWhereClauses("attachmentClassificationKey", "a.CLASSIFICATION_KEY", sb);
    commonWhereClauses("attachmentClassificationName", "ac.NAME", sb);
    commonWhereClauses("attachmentReference", "a.REF_VALUE", sb);
    commonWhereClauses("businessProcessId", "t.BUSINESS_PROCESS_ID", sb);
    commonWhereClauses("classificationCategory", "CLASSIFICATION_CATEGORY", sb);
    commonWhereClauses("classificationKey", "t.CLASSIFICATION_KEY", sb);
    commonWhereClauses("classificationParentKey", "c.PARENT_KEY", sb);
    commonWhereClauses("classificationName", "c.NAME", sb);
    commonWhereClauses("creator", "t.CREATOR", sb);
    commonWhereClauses("name", "t.NAME", sb);
    commonWhereClauses("owner", "t.OWNER", sb);
    commonWhereClauses("parentBusinessProcessId", "t.PARENT_BUSINESS_PROCESS_ID", sb);
    commonWhereClauses("porCompany", "t.POR_COMPANY", sb);
    commonWhereClauses("porSystem", "t.POR_SYSTEM", sb);
    commonWhereClauses("porSystemInstance", "t.POR_INSTANCE", sb);
    commonWhereClauses("porType", "t.POR_TYPE", sb);
    commonWhereClauses("porValue", "t.POR_VALUE", sb);

    whereIn("sorCompanyIn", "o.COMPANY", sb);
    whereLike("sorCompanyLike", "o.COMPANY", sb);
    whereIn("sorSystemIn", "o.SYSTEM", sb);
    whereLike("sorSystemLike", "o.SYSTEM", sb);
    whereIn("sorSystemInstanceIn", "o.SYSTEM_INSTANCE", sb);
    whereLike("sorSystemInstanceLike", "o.SYSTEM_INSTANCE", sb);
    whereIn("sorTypeIn", "o.TYPE", sb);
    whereLike("sorTypeLike", "o.TYPE", sb);
    whereIn("sorValueIn", "o.VALUE", sb);
    whereLike("sorValueLike", "o.VALUE", sb);

    whereIn("attachmentClassificationIdIn", "a.CLASSIFICATION_ID", sb);
    whereNotIn("attachmentClassificationIdNotIn", "a.CLASSIFICATION_ID", sb);
    whereIn("callbackStateIn", "t.CALLBACK_STATE", sb);
    whereNotIn("callbackStateNotIn", "t.CALLBACK_STATE", sb);
    whereIn("classificationIdIn", "t.CLASSIFICATION_ID", sb);
    whereNotIn("classificationIdNotIn", "t.CLASSIFICATION_ID", sb);
    whereIn("externalIdIn", "t.EXTERNAL_ID", sb);
    whereNotIn("externalIdNotIn", "t.EXTERNAL_ID", sb);
    whereIn("priority", "t.PRIORITY", sb);
    whereNotIn("priorityNotIn", "t.PRIORITY", sb);
    whereIn("ownerLongNameIn", "u.LONG_NAME", sb);
    whereNotIn("ownerLongNameNotIn", "u.LONG_NAME", sb);
    whereIn("stateIn", "t.STATE", sb);
    whereNotIn("stateNotIn", "t.STATE", sb);
    whereIn("taskId", "t.ID", sb);
    whereNotIn("taskIdNotIn", "t.ID", sb);
    whereIn("workbasketIdIn", "t.WORKBASKET_ID", sb);
    whereNotIn("workbasketIdNotIn", "t.WORKBASKET_ID", sb);
    whereLike("descriptionLike", "t.DESCRIPTION", sb);
    whereNotLike("descriptionNotLike", "t.DESCRIPTION", sb);
    whereLike("noteLike", "t.NOTE", sb);
    whereNotLike("noteNotLike", "t.NOTE", sb);

    whereInInterval("attachmentReceivedWithin", "a.RECEIVED", sb);
    whereNotInInterval("attachmentReceivedNotWithin", "a.RECEIVED", sb);
    whereInInterval("claimedWithin", "t.CLAIMED", sb);
    whereNotInInterval("claimedNotWithin", "t.CLAIMED", sb);
    whereInInterval("completedWithin", "t.COMPLETED", sb);
    whereNotInInterval("completedNotWithin", "t.COMPLETED", sb);
    whereInInterval("createdWithin", "t.CREATED", sb);
    whereNotInInterval("createdNotWithin", "t.CREATED", sb);
    whereInInterval("dueWithin", "t.DUE", sb);
    whereNotInInterval("dueNotWithin", "t.DUE", sb);
    whereInInterval("modifiedWithin", "t.MODIFIED", sb);
    whereNotInInterval("modifiedNotWithin", "t.MODIFIED", sb);
    whereInInterval("plannedWithin", "t.PLANNED", sb);
    whereNotInInterval("plannedNotWithin", "t.PLANNED", sb);
    whereInInterval("receivedWithin", "t.RECEIVED", sb);
    whereNotInInterval("receivedNotWithin", "t.RECEIVED", sb);
    whereInInterval("priorityWithin", "t.PRIORITY", sb);
    whereNotInInterval("priorityNotWithin", "t.PRIORITY", sb);

    whereLike("ownerLongNameLike", "u.LONG_NAME", sb);
    whereNotLike("ownerLongNameNotLike", "u.LONG_NAME", sb);
    whereCustomStatements("custom", "t.CUSTOM", 16, sb);
    whereCustomIntStatements("customInt", "t.CUSTOM_INT", 8, sb);

    sb.append("<if test='isRead != null'>AND IS_READ = #{isRead}</if> ");
    sb.append("<if test='isTransferred != null'>AND IS_TRANSFERRED = #{isTransferred}</if> ");
    sb.append(
        "<if test='workbasketKeyDomainIn != null'>AND (<foreach item='item'"
            + " collection='workbasketKeyDomainIn' separator=' OR '>(t.WORKBASKET_KEY = #{item.key}"
            + " AND t.DOMAIN = #{item.domain})</foreach>)</if> ");
    sb.append(
        "<if test='workbasketKeyDomainNotIn != null'>AND (<foreach item='item'"
            + " collection='workbasketKeyDomainNotIn' separator=' OR '>(t.WORKBASKET_KEY !="
            + " #{item.key} OR t.DOMAIN != #{item.domain})</foreach>)</if> ");
    sb.append(
        "<if test='wildcardSearchValueLike != null and wildcardSearchFieldIn != null'>AND ("
            + "<foreach item='item' collection='wildcardSearchFieldIn' separator=' OR '>"
            + "LOWER(t.${item}) "
            + "LIKE #{wildcardSearchValueLike}"
            + "</foreach>)"
            + "</if> ");
    sb.append("<if test='withoutAttachment'> AND a.ID IS NULL</if> ");
    sb.append(commonTaskObjectReferenceWhereStatement());
    sb.append(commonTaskSecondaryObjectReferencesWhereStatement());
    return sb;
  }
}
