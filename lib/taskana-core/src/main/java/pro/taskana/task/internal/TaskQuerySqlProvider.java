package pro.taskana.task.internal;

import java.util.stream.IntStream;

@SuppressWarnings({"checkstyle:LineLength", "checkstyle:Indentation"})
public class TaskQuerySqlProvider {
  private static final String OPENING_SCRIPT_TAG = "<script>";
  private static final String CLOSING_SCRIPT_TAG = "</script>";
  private static final String OPENING_WHERE_TAG = "<where>";
  private static final String CLOSING_WHERE_TAG = "</where>";

  private TaskQuerySqlProvider() {}

  public static String queryTaskSummaries() {
    return OPENING_SCRIPT_TAG
        + "SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> t.ID, t.EXTERNAL_ID, t.CREATED, t.CLAIMED, t.COMPLETED, t.MODIFIED, t.PLANNED, t.DUE, t.NAME, t.CREATOR, t.DESCRIPTION, t.NOTE, t.PRIORITY, t.STATE, t.CLASSIFICATION_KEY, "
        + "t.CLASSIFICATION_CATEGORY, t.CLASSIFICATION_ID, t.WORKBASKET_ID, t.DOMAIN, t.WORKBASKET_KEY, t.BUSINESS_PROCESS_ID, t.PARENT_BUSINESS_PROCESS_ID, t.OWNER, t.POR_COMPANY, t.POR_SYSTEM, t.POR_INSTANCE, t.POR_TYPE, "
        + "t.POR_VALUE, t.IS_READ, t.IS_TRANSFERRED, t.CUSTOM_1, t.CUSTOM_2, t.CUSTOM_3, t.CUSTOM_4, t.CUSTOM_5, t.CUSTOM_6, t.CUSTOM_7, t.CUSTOM_8, t.CUSTOM_9, t.CUSTOM_10, t.CUSTOM_11, t.CUSTOM_12, t.CUSTOM_13, t.CUSTOM_14, t.CUSTOM_15, t.CUSTOM_16"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", a.CLASSIFICATION_ID, a.CLASSIFICATION_KEY, a.CHANNEL, a.REF_VALUE, a.RECEIVED"
        + "</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">"
        + ", c.NAME "
        + "</if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">"
        + ", ac.NAME "
        + "</if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">"
        + ", w.NAME "
        + "</if>"
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
        + "<if test=\"joinWithWorkbaskets\">"
        + "LEFT JOIN WORKBASKET AS w ON t.WORKBASKET_ID = w.ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + "<if test='accessIdIn != null'> "
        + "AND t.WORKBASKET_ID IN ( "
        + "SELECT WID from (SELECT WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ FROM WORKBASKET_ACCESS_LIST AS s where "
        + "ACCESS_ID IN (<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "group by WORKBASKET_ID ) AS f where max_read = 1 ) "
        + "</if> "
        + commonTaskWhereStatement()
        + "<if test='wildcardSearchValueLike != null and wildcardSearchFieldIn != null'>AND (<foreach item='item' collection='wildcardSearchFieldIn' separator=' OR '>UPPER(t.${item}) LIKE #{wildcardSearchValueLike}</foreach>)</if> "
        + commonTaskObjectReferenceWhereStatement()
        + "<if test='selectAndClaim == true'> AND t.STATE = 'READY' </if>"
        + CLOSING_WHERE_TAG
        + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
        + "<if test='selectAndClaim == true'> FETCH FIRST ROW ONLY FOR UPDATE </if>"
        + "<if test=\"_databaseId == 'db2'\">WITH RS USE AND KEEP UPDATE LOCKS </if> "
        + CLOSING_SCRIPT_TAG;
  }

  public static String queryTaskSummariesDb2() {
    return OPENING_SCRIPT_TAG
        + "WITH X (ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, STATE, TCLASSIFICATION_KEY, "
        + "CLASSIFICATION_CATEGORY, CLASSIFICATION_ID, WORKBASKET_ID, DOMAIN, WORKBASKET_KEY, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, "
        + "POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, "
        + "CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10, CUSTOM_11, CUSTOM_12, CUSTOM_13, CUSTOM_14, CUSTOM_15, CUSTOM_16"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", ACLASSIFICATION_ID, ACLASSIFICATION_KEY, CHANNEL, REF_VALUE, RECEIVED"
        + "</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">"
        + ", CNAME "
        + "</if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">"
        + ", ACNAME "
        + "</if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">"
        + ", WNAME "
        + "</if>"
        + " ) "
        + " AS (SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> t.ID, t.EXTERNAL_ID, t.CREATED, t.CLAIMED, t.COMPLETED, t.MODIFIED, t.PLANNED, t.DUE, t.NAME, t.CREATOR, t.DESCRIPTION, t.NOTE, t.PRIORITY, t.STATE, t.CLASSIFICATION_KEY, "
        + " t.CLASSIFICATION_CATEGORY, t.CLASSIFICATION_ID, t.WORKBASKET_ID, t.DOMAIN, t.WORKBASKET_KEY, t.BUSINESS_PROCESS_ID, t.PARENT_BUSINESS_PROCESS_ID, t.OWNER, "
        + "t.POR_COMPANY, t.POR_SYSTEM, t.POR_INSTANCE, t.POR_TYPE, t.POR_VALUE, t.IS_READ, t.IS_TRANSFERRED, t.CUSTOM_1, t.CUSTOM_2, t.CUSTOM_3, t.CUSTOM_4, t.CUSTOM_5, "
        + "t.CUSTOM_6, t.CUSTOM_7, t.CUSTOM_8, t.CUSTOM_9, t.CUSTOM_10, t.CUSTOM_11, t.CUSTOM_12, t.CUSTOM_13, t.CUSTOM_14, t.CUSTOM_15, t.CUSTOM_16"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", a.CLASSIFICATION_ID, a.CLASSIFICATION_KEY, a.CHANNEL, a.REF_VALUE, a.RECEIVED"
        + "</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">"
        + ", c.NAME "
        + "</if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">"
        + ", ac.NAME "
        + "</if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">"
        + ", w.NAME "
        + "</if>"
        + " FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT a ON t.ID = a.TASK_ID "
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
        + OPENING_WHERE_TAG
        + commonTaskWhereStatement()
        + commonTaskObjectReferenceWhereStatement()
        + "<if test='wildcardSearchValueLike != null and wildcardSearchFieldIn != null'>AND (<foreach item='item' collection='wildcardSearchFieldIn' separator=' OR '>UPPER(t.${item}) LIKE #{wildcardSearchValueLike}</foreach>)</if> "
        + CLOSING_WHERE_TAG
        + "), Y (ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, STATE, TCLASSIFICATION_KEY, "
        + " CLASSIFICATION_CATEGORY, CLASSIFICATION_ID, WORKBASKET_ID, DOMAIN, WORKBASKET_KEY, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, "
        + "POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, "
        + "CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10, CUSTOM_11, CUSTOM_12, CUSTOM_13, CUSTOM_14, CUSTOM_15, CUSTOM_16"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", ACLASSIFICATION_ID, ACLASSIFICATION_KEY, CHANNEL, REF_VALUE, RECEIVED"
        + "</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">"
        + ", CNAME "
        + "</if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">"
        + ", ACNAME "
        + "</if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">"
        + ", WNAME "
        + "</if>"
        + ", FLAG ) "
        + "AS "
        + "(SELECT ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, STATE, TCLASSIFICATION_KEY, "
        + " CLASSIFICATION_CATEGORY, CLASSIFICATION_ID, WORKBASKET_ID, DOMAIN, WORKBASKET_KEY, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, "
        + "POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, "
        + "CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10, CUSTOM_11, CUSTOM_12, CUSTOM_13, CUSTOM_14, CUSTOM_15, CUSTOM_16"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", ACLASSIFICATION_ID, ACLASSIFICATION_KEY, CHANNEL, REF_VALUE, RECEIVED"
        + "</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">"
        + ", CNAME "
        + "</if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">"
        + ", ACNAME "
        + "</if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">"
        + ", WNAME "
        + "</if>"
        + ", (SELECT 1 FROM WORKBASKET_ACCESS_LIST s WHERE "
        + "<if test='accessIdIn != null'> "
        + "s.ACCESS_ID IN (<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) and "
        + "</if>"
        + "s.WORKBASKET_ID = X.WORKBASKET_ID AND "
        + "s.perm_read = 1 "
        + "fetch first 1 rows only "
        + ") FROM X )"
        + "SELECT ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, DESCRIPTION, NOTE, PRIORITY, STATE, TCLASSIFICATION_KEY, "
        + " CLASSIFICATION_CATEGORY, CLASSIFICATION_ID, WORKBASKET_ID, DOMAIN, WORKBASKET_KEY, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, "
        + "POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, "
        + "CUSTOM_6, CUSTOM_7, CUSTOM_8, CUSTOM_9, CUSTOM_10, CUSTOM_11, CUSTOM_12, CUSTOM_13, CUSTOM_14, CUSTOM_15, CUSTOM_16"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">"
        + ", ACLASSIFICATION_ID, ACLASSIFICATION_KEY, CHANNEL, REF_VALUE, RECEIVED "
        + "</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">"
        + ", CNAME "
        + "</if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">"
        + ", ACNAME "
        + "</if>"
        + "<if test=\"addWorkbasketNameToSelectClauseForOrdering\">"
        + ", WNAME "
        + "</if>"
        + " FROM Y WHERE FLAG = 1 "
        + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >"
        + "${item}"
        + "</foreach>"
        + "</if> "
        + "<if test='selectAndClaim == true'>FETCH FIRST ROW ONLY FOR UPDATE WITH RS USE AND KEEP UPDATE LOCKS</if>"
        + "<if test='selectAndClaim == false'> with UR</if>"
        + CLOSING_SCRIPT_TAG;
  }

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

  public static String countQueryTasks() {
    return OPENING_SCRIPT_TAG
        + "SELECT COUNT( <if test=\"useDistinctKeyword\">DISTINCT</if>  t.ID) FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT AS a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + "<if test='accessIdIn != null'> "
        + "AND t.WORKBASKET_ID IN ( "
        + "select WID from (select WORKBASKET_ID as WID, MAX(PERM_READ::int) as MAX_READ FROM WORKBASKET_ACCESS_LIST AS s where "
        + "ACCESS_ID IN (<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "group by WORKBASKET_ID ) AS f where max_read = 1 ) "
        + "</if> "
        + commonTaskWhereStatement()
        + commonTaskObjectReferenceWhereStatement()
        + "<if test='wildcardSearchValueLike != null and wildcardSearchFieldIn != null'>AND (<foreach item='item' collection='wildcardSearchFieldIn' separator=' OR '>UPPER(t.${item}) LIKE #{wildcardSearchValueLike}</foreach>)</if> "
        + CLOSING_WHERE_TAG
        + CLOSING_SCRIPT_TAG;
  }

  public static String countQueryTasksDb2() {
    return OPENING_SCRIPT_TAG
        + "WITH X (ID, WORKBASKET_ID) AS (SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> t.ID, t.WORKBASKET_ID FROM TASK t "
        + "<if test=\"joinWithAttachments\">"
        + "LEFT JOIN ATTACHMENT AS a ON t.ID = a.TASK_ID "
        + "</if>"
        + "<if test=\"joinWithClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS c ON t.CLASSIFICATION_ID = c.ID "
        + "</if>"
        + "<if test=\"joinWithAttachmentClassifications\">"
        + "LEFT JOIN CLASSIFICATION AS ac ON a.CLASSIFICATION_ID = ac.ID "
        + "</if>"
        + OPENING_WHERE_TAG
        + commonTaskWhereStatement()
        + commonTaskObjectReferenceWhereStatement()
        + "<if test='wildcardSearchValueLike != null and wildcardSearchFieldIn != null'>AND (<foreach item='item' collection='wildcardSearchFieldIn' separator=' OR '>UPPER(t.${item}) LIKE #{wildcardSearchValueLike}</foreach>)</if> "
        + CLOSING_WHERE_TAG
        + "), Y (ID, FLAG) AS "
        + "(SELECT ID, (SELECT 1 FROM WORKBASKET_ACCESS_LIST s WHERE "
        + "<if test='accessIdIn != null'> "
        + "s.ACCESS_ID IN (<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) and "
        + "</if>"
        + "s.WORKBASKET_ID = X.WORKBASKET_ID AND "
        + "s.perm_read = 1 "
        + "fetch first 1 rows only "
        + ") FROM X ) "
        + "SELECT COUNT(*)  FROM Y WHERE FLAG = 1 "
        + "with UR "
        + CLOSING_SCRIPT_TAG;
  }

  public static String countQueryObjectReferences() {
    return OPENING_SCRIPT_TAG
        + "SELECT COUNT(ID) FROM OBJECT_REFERENCE "
        + OPENING_WHERE_TAG
        + commonObjectReferenceWhereStatement()
        + CLOSING_WHERE_TAG
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + CLOSING_SCRIPT_TAG;
  }

  public static String queryTaskColumnValues() {
    return OPENING_SCRIPT_TAG
        + "SELECT DISTINCT ${columnName} "
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
        + OPENING_WHERE_TAG
        + "<if test='accessIdIn != null'> "
        + "AND t.WORKBASKET_ID IN ( "
        + "select WID from (select WORKBASKET_ID as WID, MAX(PERM_READ) as MAX_READ FROM WORKBASKET_ACCESS_LIST where "
        + "ACCESS_ID IN (<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "group by WORKBASKET_ID ) where max_read = 1 ) "
        + "</if> "
        + commonTaskWhereStatement()
        + CLOSING_WHERE_TAG
        + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >"
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
        + "<otherwise>"
        + "${item}"
        + "</otherwise>"
        + "</choose>"
        + "</foreach></if> "
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

  private static String commonTaskObjectReferenceWhereStatement() {
    return "<if test='objectReferences != null'>"
        + "AND (<foreach item='item' collection='objectReferences' separator=' OR '> "
        + "<if test='item.company != null'>t.POR_COMPANY = #{item.company} </if>"
        + "<if test='item.system != null'> <if test='item.company != null'>AND</if> t.POR_SYSTEM = #{item.system} </if>"
        + "<if test='item.systemInstance != null'> <if test='item.company != null or item.system != null'>AND</if> t.POR_INSTANCE = #{item.systemInstance} </if>"
        + "<if test='item.type != null'> <if test='item.company != null or item.system != null or item.systemInstance != null'>AND</if> t.POR_TYPE = #{item.type} </if>"
        + "<if test='item.value != null'> <if test='item.company != null or item.system != null or item.systemInstance != null or item.type != null'>AND</if> t.POR_VALUE = #{item.value} </if>"
        + "</foreach>)</if>";
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

  private static String commonTaskWhereStatement() {
    StringBuilder sb = new StringBuilder();
    whereIn("taskIds", "t.ID", sb);
    whereIn("priority", "PRIORITY", sb);
    whereIn("externalIdIn", "t.EXTERNAL_ID", sb);
    whereIn("nameIn", "t.NAME", sb);
    whereIn("creatorIn", "CREATOR", sb);
    whereIn("stateIn", "STATE", sb);
    whereIn("callbackStateIn", "t.CALLBACK_STATE", sb);
    whereIn("workbasketIdIn", "WORKBASKET_ID", sb);
    whereIn("classificationKeyIn", "t.CLASSIFICATION_KEY", sb);
    whereIn("classificationIdIn", "t.CLASSIFICATION_ID", sb);
    whereIn("classificationCategoryIn", "CLASSIFICATION_CATEGORY", sb);
    whereIn("classificationNameIn", "c.NAME", sb);
    whereIn("attachmentClassificationNameIn", "ac.NAME", sb);
    whereIn("ownerIn", "OWNER", sb);
    whereIn("porCompanyIn", "POR_COMPANY", sb);
    whereIn("porSystemIn", "POR_SYSTEM", sb);
    whereIn("porSystemInstanceIn", "POR_INSTANCE", sb);
    whereIn("porTypeIn", "POR_TYPE", sb);
    whereIn("porValueIn", "POR_VALUE", sb);
    whereIn("parentBusinessProcessIdIn", "PARENT_BUSINESS_PROCESS_ID", sb);
    whereIn("businessProcessIdIn", "BUSINESS_PROCESS_ID", sb);
    whereIn("attachmentClassificationKeyIn", "a.CLASSIFICATION_KEY", sb);
    whereIn("attachmentClassificationIdIn", "a.CLASSIFICATION_ID", sb);
    whereIn("attachmentChannelIn", "a.CHANNEL", sb);
    whereIn("attachmentReferenceIn", "a.REF_VALUE", sb);
    whereInTime("createdIn", "t.CREATED", sb);
    whereInTime("claimedIn", "t.CLAIMED", sb);
    whereInTime("completedIn", "t.COMPLETED", sb);
    whereInTime("modifiedIn", "t.MODIFIED", sb);
    whereInTime("plannedIn", "t.PLANNED", sb);
    whereInTime("dueIn", "t.DUE", sb);
    whereInTime("attachmentReceivedIn", "a.RECEIVED", sb);
    whereNotIn("classificationKeyNotIn", "t.CLASSIFICATION_KEY", sb);
    whereLike("externalIdLike", "t.EXTERNAL_ID", sb);
    whereLike("nameLike", "t.NAME", sb);
    whereLike("creatorLike", "CREATOR", sb);
    whereLike("noteLike", "NOTE", sb);
    whereLike("classificationKeyLike", "t.CLASSIFICATION_KEY", sb);
    whereLike("classificationCategoryLike", "CLASSIFICATION_CATEGORY", sb);
    whereLike("classificationNameLike", "c.NAME", sb);
    whereLike("attachmentClassificationNameLike", "ac.NAME", sb);
    whereLike("ownerLike", "OWNER", sb);
    whereLike("porCompanyLike", "POR_COMPANY", sb);
    whereLike("porSystemLike", "POR_SYSTEM", sb);
    whereLike("porSystemInstanceLike", "POR_INSTANCE", sb);
    whereLike("porTypeLike", "POR_TYPE", sb);
    whereLike("porValueLike", "POR_VALUE", sb);
    whereLike("parentBusinessProcessIdLike", "PARENT_BUSINESS_PROCESS_ID", sb);
    whereLike("businessProcessIdLike", "BUSINESS_PROCESS_ID", sb);
    whereLike("attachmentClassificationKeyLike", "a.CLASSIFICATION_KEY", sb);
    whereLike("attachmentClassificationIdLike", "a.CLASSIFICATION_ID", sb);
    whereLike("attachmentChannelLike", "a.CHANNEL", sb);
    whereLike("attachmentReferenceLike", "a.REF_VALUE", sb);
    whereLike("description", "DESCRIPTION", sb);
    whereCustomStatements(sb);
    sb.append("<if test='isRead != null'>AND IS_READ = #{isRead}</if> ");
    sb.append("<if test='isTransferred != null'>AND IS_TRANSFERRED = #{isTransferred}</if> ");
    sb.append(
        "<if test='workbasketKeyDomainIn != null'>AND (<foreach item='item' collection='workbasketKeyDomainIn' separator=' OR '>(WORKBASKET_KEY = #{item.key} AND DOMAIN = #{item.domain})</foreach>)</if> ");
    return sb.toString();
  }

  private static void whereCustomStatements(StringBuilder sb) {
    IntStream.rangeClosed(1, 16)
        .forEach(
            x -> {
              String collectionIn = "custom" + x + "In";
              String collectionNotIn = "custom" + x + "NotIn";
              String collectionLike = "custom" + x + "Like";
              String column = "CUSTOM_" + x;
              whereIn(collectionIn, column, sb);
              whereLike(collectionLike, column, sb);
              whereNotIn(collectionNotIn, column, sb);
            });
  }

  private static void whereIn(String collection, String column, StringBuilder sb) {
    sb.append("<if test='")
        .append(collection)
        .append(" != null'>AND ")
        .append(column)
        .append(" IN(<foreach item='item' collection='")
        .append(collection)
        .append("' separator=',' >#{item}</foreach>)</if> ");
  }

  private static void whereNotIn(String collection, String column, StringBuilder sb) {
    sb.append("<if test='")
        .append(collection)
        .append(" != null'>AND ")
        .append(column)
        .append(" NOT IN(<foreach item='item' collection='")
        .append(collection)
        .append("' separator=',' >#{item}</foreach>)</if> ");
  }

  private static void whereInTime(String collection, String column, StringBuilder sb) {
    sb.append("<if test='")
        .append(collection)
        .append(" !=null'> AND (<foreach item='item' collection='")
        .append(collection)
        .append("' separator=' OR ' > ( <if test='item.begin!=null'> ")
        .append(column)
        .append(
            " &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> ")
        .append(column)
        .append(" &lt;=#{item.end} </if>)</foreach>)</if> ");
  }

  private static void whereLike(String collection, String column, StringBuilder sb) {
    sb.append("<if test='")
        .append(collection)
        .append(" != null'>AND (<foreach item='item' collection='")
        .append(collection)
        .append("' separator=' OR '>UPPER(")
        .append(column)
        .append(") LIKE #{item}</foreach>)</if> ");
  }
}
