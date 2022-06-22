package pro.taskana.task.internal;

import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.CLOSING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.DB2_WITH_UR;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_SCRIPT_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.OPENING_WHERE_TAG;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereCustomIntStatements;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereCustomStatements;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereIn;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereLike;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereNotIn;
import static pro.taskana.common.internal.util.SqlProviderUtil.whereNotLike;

import java.util.Arrays;
import java.util.stream.Collectors;

import pro.taskana.common.internal.util.SqlProviderUtil;
import pro.taskana.task.api.TaskQueryColumnName;

public class TaskQuerySqlProvider {
  private TaskQuerySqlProvider() {}

  @SuppressWarnings("unused")
  public static String queryTaskSummaries() {
    return OPENING_SCRIPT_TAG
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
        + "LEFT JOIN ATTACHMENT AS a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE AS o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithWorkbaskets\">"
        + "LEFT JOIN WORKBASKET AS w ON t.WORKBASKET_ID = w.ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO AS u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskWhereStatement()
        + "<if test='selectAndClaim == true'> AND t.STATE = 'READY' </if>"
        + CLOSING_WHERE_TAG
        + "<if test='!orderBy.isEmpty()'>"
        + "ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach>"
        + "</if> "
        + "<if test='selectAndClaim == true'> "
        + "FETCH FIRST ROW ONLY FOR UPDATE "
        + "</if>"
        + "<if test=\"_databaseId == 'db2'\">WITH RS USE AND KEEP UPDATE LOCKS </if>"
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
        + "LEFT JOIN CLASSIFICATION AS c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithWorkbaskets\">"
        + "LEFT JOIN WORKBASKET AS w ON t.WORKBASKET_ID = w.ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO AS u ON t.owner = u.USER_ID "
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
        + "s.WORKBASKET_ID = X.WORKBASKET_ID AND s.perm_read = 1 fetch first 1 rows only"
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
        + "<if test='!orderBy.isEmpty()'>"
        + "ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach>"
        + "</if> "
        + "<if test='selectAndClaim == true'>"
        + "FETCH FIRST ROW ONLY FOR UPDATE WITH RS USE AND KEEP UPDATE LOCKS"
        + "</if>"
        + "<if test='selectAndClaim == false'> with UR</if>"
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String countQueryTasks() {
    return OPENING_SCRIPT_TAG
        + "SELECT COUNT( <if test=\"useDistinctKeyword\">DISTINCT</if> t.ID) "
        + "FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT AS a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE AS o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO AS u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskWhereStatement()
        + CLOSING_WHERE_TAG
        + CLOSING_SCRIPT_TAG;
  }

  @SuppressWarnings("unused")
  public static String countQueryTasksDb2() {
    return OPENING_SCRIPT_TAG
        + "WITH X (ID, WORKBASKET_ID) AS ("
        + "SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> "
        + "t.ID, t.WORKBASKET_ID FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT AS a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE AS o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO AS u ON t.owner = u.USER_ID "
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
        + "s.WORKBASKET_ID = X.WORKBASKET_ID AND s.perm_read = 1 fetch first 1 rows only "
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
        + "LEFT JOIN ATTACHMENT AS a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + "<if test=\"joinWithSecondaryObjectReferences\">"
        + "LEFT JOIN OBJECT_REFERENCE AS o ON t.ID = o.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithUserInfo\">"
        + "LEFT JOIN USER_INFO AS u ON t.owner = u.USER_ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + checkForAuthorization()
        + commonTaskWhereStatement()
        + CLOSING_WHERE_TAG
        + "<if test='!orderBy.isEmpty()'>"
        + "ORDER BY <foreach item='item' collection='orderBy' separator=',' >"
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

  private static String db2selectFields() {
    // needs to be the same order as the commonSelectFields (TaskQueryColumnValue)
    return "ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, RECEIVED, DUE, NAME,"
        + " CREATOR, DESCRIPTION, NOTE, PRIORITY, MANUAL_PRIORITY, STATE,"
        + " CLASSIFICATION_CATEGORY, TCLASSIFICATION_KEY, CLASSIFICATION_ID, WORKBASKET_ID,"
        + " WORKBASKET_KEY, DOMAIN, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER,"
        + " POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ,"
        + " IS_TRANSFERRED, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6,"
        + " CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10, CUSTOM_11, CUSTOM_12, CUSTOM_13,"
        + " CUSTOM_14, CUSTOM_15, CUSTOM_16, CUSTOM_INT_1, CUSTOM_INT_2, CUSTOM_INT_3,"
        + " CUSTOM_INT_4, CUSTOM_INT_5, CUSTOM_INT_6, CUSTOM_INT_7, CUSTOM_INT_8 <if"
        + " test=\"addClassificationNameToSelectClauseForOrdering\">, CNAME</if><if"
        + " test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">,"
        + " ACNAME</if><if test=\"addAttachmentColumnsToSelectClauseForOrdering\">,"
        + " ACLASSIFICATION_ID, ACLASSIFICATION_KEY, CHANNEL, REF_VALUE, ARECEIVED</if><if"
        + " test=\"addWorkbasketNameToSelectClauseForOrdering\">, WNAME</if><if"
        + " test=\"joinWithUserInfo\">, ULONG_NAME </if>";
  }

  private static String checkForAuthorization() {
    return "<if test='accessIdIn != null'> AND t.WORKBASKET_ID IN ("
        + "SELECT WID "
        + "FROM ("
        + "SELECT WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ "
        + "FROM WORKBASKET_ACCESS_LIST AS s where ACCESS_ID IN "
        + "(<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "GROUP by WORKBASKET_ID) as f "
        + "WHERE MAX_READ = 1) "
        + "</if>";
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

    SqlProviderUtil.whereInInterval("attachmentReceivedWithin", "a.RECEIVED", sb);
    SqlProviderUtil.whereNotInInterval("attachmentReceivedNotWithin", "a.RECEIVED", sb);
    SqlProviderUtil.whereInInterval("claimedWithin", "t.CLAIMED", sb);
    SqlProviderUtil.whereNotInInterval("claimedNotWithin", "t.CLAIMED", sb);
    SqlProviderUtil.whereInInterval("completedWithin", "t.COMPLETED", sb);
    SqlProviderUtil.whereNotInInterval("completedNotWithin", "t.COMPLETED", sb);
    SqlProviderUtil.whereInInterval("createdWithin", "t.CREATED", sb);
    SqlProviderUtil.whereNotInInterval("createdNotWithin", "t.CREATED", sb);
    SqlProviderUtil.whereInInterval("dueWithin", "t.DUE", sb);
    SqlProviderUtil.whereNotInInterval("dueNotWithin", "t.DUE", sb);
    SqlProviderUtil.whereInInterval("modifiedWithin", "t.MODIFIED", sb);
    SqlProviderUtil.whereNotInInterval("modifiedNotWithin", "t.MODIFIED", sb);
    SqlProviderUtil.whereInInterval("plannedWithin", "t.PLANNED", sb);
    SqlProviderUtil.whereNotInInterval("plannedNotWithin", "t.PLANNED", sb);
    SqlProviderUtil.whereInInterval("receivedWithin", "t.RECEIVED", sb);
    SqlProviderUtil.whereNotInInterval("receivedNotWithin", "t.RECEIVED", sb);

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
