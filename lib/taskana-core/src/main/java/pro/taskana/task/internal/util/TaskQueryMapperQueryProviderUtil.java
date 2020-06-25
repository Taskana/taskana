package pro.taskana.task.internal.util;

public class TaskQueryMapperQueryProviderUtil {

  private static final String OPENING_WHERE_TAG = "<where>";
  private static final String CLOSING_WHERE_TAG = "</where>";
  private static final String SELECT_DISTINCT_COLUMN = "SELECT DISTINCT ${columnName} ";
  private static final String FROM_OBJECT_REFERENCE = "FROM OBJECT_REFERENCE ";
  private static final String DATABASE_WITH_UR = "<if test=\"_databaseId == 'db2'\">with UR </if> ";

  protected static String getDefaultSelectTaskSummariesQuery() {
    return getSelectTaskSummariesStatement()
        + getFromTasksStatement()
        + OPENING_WHERE_TAG
        + getWhereAccessIdCondition()
        + getWhereCondition()
        + getWhereWildcardSearchCondition()
        + "<if test='selectAndClaim == true'> AND t.STATE = 'READY' </if>"
        + CLOSING_WHERE_TAG
        + getOrderByStatement()
        + "<if test='selectAndClaim == true'> FETCH FIRST ROW ONLY FOR UPDATE </if>"
        + "<if test=\"_databaseId == 'db2'\">WITH RS USE AND KEEP UPDATE LOCKS </if> ";
  }

  @SuppressWarnings("checkstyle:LineLength")
  protected static String getDefaultSelectTaskSummariesDb2Query() {
    return "WITH X ("
        + getSelectTaskSummariesDb2Statement()
        + " ) AS ("
        + getSelectTaskSummariesStatement()
        + getFromTasksStatement()
        + OPENING_WHERE_TAG
        + getWhereCondition()
        + getWhereWildcardSearchCondition()
        + CLOSING_WHERE_TAG
        + "), Y ("
        + getSelectTaskSummariesDb2Statement()
        + ", FLAG ) AS (SELECT "
        + getSelectTaskSummariesDb2Statement()
        + ", ("
        + getWhereAccessIdDb2Condition()
        + ") FROM X ) SELECT "
        + getSelectTaskSummariesDb2Statement()
        + " FROM Y WHERE FLAG = 1 "
        + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
        + "<if test='selectAndClaim == true'> FETCH FIRST ROW ONLY FOR UPDATE WITH RS USE AND KEEP UPDATE LOCKS</if>"
        + "<if test='selectAndClaim == false'> with UR</if>";
  }

  protected static String getDefaultCountTaskSummariesQuery() {
    return "SELECT COUNT( <if test=\"useDistinctKeyword\">DISTINCT</if>  t.ID) "
        + getFromTasksStatement()
        + OPENING_WHERE_TAG
        + getWhereAccessIdCondition()
        + getWhereCondition()
        + CLOSING_WHERE_TAG;
  }

  protected static String getDefaultCountTaskSummariesDb2Query() {
    return "WITH X (ID, WORKBASKET_ID) AS ("
        + "SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> t.ID, t.WORKBASKET_ID "
        + getFromTasksStatement()
        + OPENING_WHERE_TAG
        + getWhereCondition()
        + CLOSING_WHERE_TAG
        + "), Y (ID, FLAG) AS (SELECT ID, ("
        + getWhereAccessIdDb2Condition()
        + ") FROM X ) SELECT COUNT(*) FROM Y WHERE FLAG = 1 with UR ";
  }

  protected static String getDefaultColumnTaskSummariesQuery() {
    return SELECT_DISTINCT_COLUMN
        + getFromTasksStatement()
        + OPENING_WHERE_TAG
        + getWhereAccessIdCondition()
        + getWhereCondition()
        + CLOSING_WHERE_TAG
        + getConditionalOrderByStatement()
        + DATABASE_WITH_UR;
  }

  protected static String getDefaultObjectReferencesQuery() {
    return "SELECT ID, COMPANY, SYSTEM, SYSTEM_INSTANCE, TYPE, VALUE "
        + FROM_OBJECT_REFERENCE
        + OPENING_WHERE_TAG
        + getObjectReferencesWhereCondition()
        + CLOSING_WHERE_TAG
        + DATABASE_WITH_UR;
  }

  protected static String getDefaultCountObjectReferencesQuery() {
    return "SELECT COUNT(ID) "
        + FROM_OBJECT_REFERENCE
        + OPENING_WHERE_TAG
        + getObjectReferencesWhereCondition()
        + CLOSING_WHERE_TAG
        + DATABASE_WITH_UR;
  }

  protected static String getDefaultObjectReferenceColumnValuesQuery() {
    return SELECT_DISTINCT_COLUMN
        + FROM_OBJECT_REFERENCE
        + OPENING_WHERE_TAG
        + getObjectReferencesWhereCondition()
        + CLOSING_WHERE_TAG
        + DATABASE_WITH_UR;
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getSelectTaskSummariesStatement() {
    return "SELECT <if test=\"useDistinctKeyword\">DISTINCT</if> t.ID, t.EXTERNAL_ID, t.CREATED, "
        + "t.CLAIMED, t.COMPLETED, t.MODIFIED, t.PLANNED, t.DUE, t.NAME, t.CREATOR, "
        + "t.DESCRIPTION, t.NOTE, t.PRIORITY, t.STATE, t.CLASSIFICATION_KEY, "
        + "t.CLASSIFICATION_CATEGORY, t.CLASSIFICATION_ID, t.WORKBASKET_ID, t.DOMAIN, "
        + "t.WORKBASKET_KEY, t.BUSINESS_PROCESS_ID, t.PARENT_BUSINESS_PROCESS_ID, t.OWNER, "
        + "t.POR_COMPANY, t.POR_SYSTEM, t.POR_INSTANCE, t.POR_TYPE, "
        + "t.POR_VALUE, t.IS_READ, t.IS_TRANSFERRED, t.CUSTOM_1, t.CUSTOM_2, t.CUSTOM_3, "
        + "t.CUSTOM_4, t.CUSTOM_5, t.CUSTOM_6, t.CUSTOM_7, t.CUSTOM_8, t.CUSTOM_9, t.CUSTOM_10, "
        + "t.CUSTOM_11, t.CUSTOM_12, t.CUSTOM_13, t.CUSTOM_14, t.CUSTOM_15, t.CUSTOM_16"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">, a.CLASSIFICATION_ID, a.CLASSIFICATION_KEY, a.CHANNEL, a.REF_VALUE, a.RECEIVED</if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">, c.NAME </if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">, ac.NAME </if>";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getSelectTaskSummariesDb2Statement() {
    return "ID, EXTERNAL_ID, CREATED, CLAIMED, COMPLETED, MODIFIED, PLANNED, DUE, NAME, CREATOR, "
        + "DESCRIPTION, NOTE, PRIORITY, STATE, TCLASSIFICATION_KEY, "
        + "CLASSIFICATION_CATEGORY, CLASSIFICATION_ID, WORKBASKET_ID, DOMAIN, WORKBASKET_KEY, "
        + "BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, OWNER, "
        + "POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, IS_READ, IS_TRANSFERRED, "
        + "CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, "
        + "CUSTOM_9, CUSTOM_10, CUSTOM_11, CUSTOM_12, CUSTOM_13, CUSTOM_14, CUSTOM_15, CUSTOM_16"
        + "<if test=\"addAttachmentColumnsToSelectClauseForOrdering\">, ACLASSIFICATION_ID, ACLASSIFICATION_KEY, CHANNEL, REF_VALUE, RECEIVED </if>"
        + "<if test=\"addClassificationNameToSelectClauseForOrdering\">, CNAME </if>"
        + "<if test=\"addAttachmentClassificationNameToSelectClauseForOrdering\">, ACNAME </if>";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getFromTasksStatement() {
    return "FROM TASK t "
        + "<if test=\"joinWithAttachments\">LEFT JOIN ATTACHMENT AS a ON t.ID = a.TASK_ID </if>"
        + "<if test=\"joinWithClassifications\">LEFT JOIN CLASSIFICATION AS c ON t.CLASSIFICATION_ID = c.ID </if>"
        + "<if test=\"joinWithAttachmentClassifications\">LEFT JOIN CLASSIFICATION AS ac ON a.CLASSIFICATION_ID = ac.ID </if>";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getWhereAccessIdCondition() {
    return "<if test='accessIdIn != null'> "
        + "AND t.WORKBASKET_ID IN ( "
        + "SELECT WID from (SELECT WORKBASKET_ID as WID, MAX(PERM_READ::int) as "
        + "MAX_READ FROM WORKBASKET_ACCESS_LIST AS s where "
        + "ACCESS_ID IN (<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) "
        + "group by WORKBASKET_ID ) AS f where max_read = 1 ) "
        + "</if> ";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getWhereAccessIdDb2Condition() {
    return "SELECT 1 FROM WORKBASKET_ACCESS_LIST s WHERE "
        + "<if test='accessIdIn != null'> s.ACCESS_ID IN (<foreach item='item' collection='accessIdIn' separator=',' >#{item}</foreach>) and </if>"
        + "s.WORKBASKET_ID = X.WORKBASKET_ID AND s.perm_read = 1 fetch first 1 rows only ";
  }

  private static String getWhereCondition() {
    return getWhereInCondition()
        + getWhereCustomInCondition()
        + getWhereLikeCondition()
        + getWhereCustomLikeCondition();
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getWhereInCondition() {
    return "<if test='taskIds != null'>AND t.ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>)</if> "
        + "<if test='externalIdIn != null'>AND t.EXTERNAL_ID IN(<foreach item='item' collection='externalIdIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='createdIn !=null'> AND (<foreach item='item' collection='createdIn' separator=' OR '>(<if test='item.begin!=null'> t.CREATED &gt;= #{item.begin} </if><if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> t.CREATED &lt;=#{item.end} </if>)</foreach>)</if> <if test='claimedIn !=null'> AND ( <foreach item='item' collection='claimedIn' separator=' OR ' > ( <if test='item.begin!=null'> t.CLAIMED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> t.CLAIMED &lt;=#{item.end} </if>)</foreach>)</if> "
        + "<if test='completedIn !=null'> AND (<foreach item='item' collection='completedIn' separator=' OR '>(<if test='item.begin!=null'> t.COMPLETED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> t.COMPLETED &lt;=#{item.end} </if>)</foreach>)</if> "
        + "<if test='modifiedIn !=null'> AND (<foreach item='item' collection='modifiedIn' separator=' OR '>(<if test='item.begin!=null'> t.MODIFIED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> t.MODIFIED &lt;=#{item.end} </if>)</foreach>)</if> "
        + "<if test='plannedIn !=null'> AND (<foreach item='item' collection='plannedIn' separator=' OR '>(<if test='item.begin!=null'> t.PLANNED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> t.PLANNED &lt;=#{item.end} </if>)</foreach>)</if> "
        + "<if test='dueIn !=null'> AND (<foreach item='item' collection='dueIn' separator=' OR '>(<if test='item.begin!=null'> t.DUE &gt;= #{item.begin} </if><if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> t.DUE &lt;=#{item.end} </if>)</foreach>)</if> "
        + "<if test='nameIn != null'>AND t.NAME IN(<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='priority != null'>AND t.PRIORITY IN(<foreach item='item' collection='priority' separator=',' >#{item}</foreach>)</if> "
        + "<if test='stateIn != null'>AND t.STATE IN(<foreach item='item' collection='stateIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='callbackStateIn != null'>AND t.CALLBACK_STATE IN(<foreach item='item' collection='callbackStateIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='workbasketIdIn != null'>AND t.WORKBASKET_ID IN(<foreach item='item' collection='workbasketIdIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='workbasketKeyDomainIn != null'>AND (<foreach item='item' collection='workbasketKeyDomainIn' separator=' OR '>(t.WORKBASKET_KEY = #{item.key} AND t.DOMAIN = #{item.domain})</foreach>)</if> "
        + "<if test='classificationKeyIn != null'>AND t.CLASSIFICATION_KEY IN(<foreach item='item' collection='classificationKeyIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationKeyNotIn != null'>AND t.CLASSIFICATION_KEY NOT IN(<foreach item='item' collection='classificationKeyNotIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationIdIn != null'>AND t.CLASSIFICATION_ID IN(<foreach item='item' collection='classificationIdIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationCategoryIn != null'>AND t.CLASSIFICATION_CATEGORY IN(<foreach item='item' collection='classificationCategoryIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='classificationNameIn != null'>AND c.NAME IN(<foreach item='item' collection='classificationNameIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='attachmentClassificationNameIn != null'>AND ac.NAME IN(<foreach item='item' collection='attachmentClassificationNameIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='ownerIn != null'>AND t.OWNER IN(<foreach item='item' collection='ownerIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='isRead != null'>AND t.IS_READ = #{isRead}</if> "
        + "<if test='isTransferred != null'>AND t.IS_TRANSFERRED = #{isTransferred}</if> "
        + "<if test='porCompanyIn != null'>AND t.POR_COMPANY IN(<foreach item='item' collection='porCompanyIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='porSystemIn != null'>AND t.POR_SYSTEM IN(<foreach item='item' collection='porSystemIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='porSystemInstanceIn != null'>AND t.POR_INSTANCE IN(<foreach item='item' collection='porSystemInstanceIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='porTypeIn != null'>AND t.POR_TYPE IN(<foreach item='item' collection='porTypeIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='porValueIn != null'>AND t.POR_VALUE IN(<foreach item='item' collection='porValueIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='parentBusinessProcessIdIn != null'>AND t.PARENT_BUSINESS_PROCESS_ID IN(<foreach item='item' collection='parentBusinessProcessIdIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='businessProcessIdIn != null'>AND t.BUSINESS_PROCESS_ID IN(<foreach item='item' collection='businessProcessIdIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='attachmentClassificationKeyIn != null'>AND a.CLASSIFICATION_KEY IN(<foreach item='item' collection='attachmentClassificationKeyIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='attachmentClassificationIdIn != null'>AND a.CLASSIFICATION_ID IN(<foreach item='item' collection='attachmentClassificationIdIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='attachmentChannelIn != null'>AND a.CHANNEL IN(<foreach item='item' collection='attachmentChannelIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='attachmentReferenceIn != null'>AND a.REF_VALUE IN(<foreach item='item' collection='attachmentReferenceIn' separator=',' >#{item}</foreach>)</if> "
        + "<if test='attachmentReceivedIn !=null'> AND ( <foreach item='item' collection='attachmentReceivedIn' separator=' OR ' >(<if test='item.begin!=null'> a.RECEIVED &gt;= #{item.begin} </if><if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> a.RECEIVED &lt;=#{item.end} </if>)</foreach>)</if> ";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getWhereLikeCondition() {
    return "<if test='externalIdLike != null'>AND (<foreach item='item' collection='externalIdLike' separator=' OR '>UPPER(t.EXTERNAL_ID) LIKE #{item}</foreach>)</if> "
        + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR '>UPPER(t.NAME) LIKE #{item}</foreach>)</if> "
        + "<if test='creatorLike != null'>AND (<foreach item='item' collection='creatorLike' separator=' OR '>UPPER(t.CREATOR) LIKE #{item}</foreach>)</if> "
        + "<if test='description != null'>AND (<foreach item='item' collection='description' separator=' OR '>t.DESCRIPTION LIKE #{item}</foreach>)</if> "
        + "<if test='noteLike != null'>AND (<foreach item='item' collection='noteLike' separator=' OR '>UPPER(t.NOTE) LIKE #{item}</foreach>)</if> "
        + "<if test='classificationKeyLike != null'>AND (<foreach item='item' collection='classificationKeyLike' separator=' OR '>UPPER(t.CLASSIFICATION_KEY) LIKE #{item}</foreach>)</if> "
        + "<if test='classificationCategoryLike != null'>AND (<foreach item='item' collection='classificationCategoryLike' separator=' OR '>UPPER(t.CLASSIFICATION_CATEGORY) LIKE #{item}</foreach>)</if> "
        + "<if test='classificationNameLike != null'>AND (<foreach item='item' collection='classificationNameLike' separator=' OR '>UPPER(c.NAME) LIKE #{item}</foreach>)</if> "
        + "<if test='attachmentClassificationNameLike != null'>AND (<foreach item='item' collection='attachmentClassificationNameLike' separator=' OR '>UPPER(ac.NAME) LIKE #{item}</foreach>)</if> "
        + "<if test='ownerLike != null'>AND (<foreach item='item' collection='ownerLike' separator=' OR '>UPPER(t.OWNER) LIKE #{item}</foreach>)</if> "
        + "<if test='porCompanyLike != null'>AND (<foreach item='item' collection='porCompanyLike' separator=' OR '>UPPER(t.POR_COMPANY) LIKE #{item}</foreach>)</if> "
        + "<if test='porSystemLike != null'>AND (<foreach item='item' collection='porSystemLike' separator=' OR '>UPPER(t.POR_SYSTEM) LIKE #{item}</foreach>)</if> "
        + "<if test='porSystemInstanceLike != null'>AND (<foreach item='item' collection='porSystemInstanceLike' separator=' OR '>UPPER(t.POR_INSTANCE) LIKE #{item}</foreach>)</if> "
        + "<if test='porTypeLike != null'>AND (<foreach item='item' collection='porTypeLike' separator=' OR '>UPPER(t.POR_TYPE) LIKE #{item}</foreach>)</if> "
        + "<if test='porValueLike != null'>AND (<foreach item='item' collection='porValueLike' separator=' OR '>UPPER(t.POR_VALUE) LIKE #{item}</foreach>)</if> "
        + "<if test='parentBusinessProcessIdLike != null'>AND (<foreach item='item' collection='parentBusinessProcessIdLike' separator=' OR '>UPPER(t.PARENT_BUSINESS_PROCESS_ID) LIKE #{item}</foreach>)</if> "
        + "<if test='businessProcessIdLike != null'>AND (<foreach item='item' collection='businessProcessIdLike' separator=' OR '>UPPER(t.BUSINESS_PROCESS_ID) LIKE #{item}</foreach>)</if> "
        + "<if test='attachmentClassificationKeyLike != null'>AND (<foreach item='item' collection='attachmentClassificationKeyLike' separator=' OR '>UPPER(a.CLASSIFICATION_KEY) LIKE #{item}</foreach>)</if> "
        + "<if test='attachmentClassificationIdLike != null'>AND (<foreach item='item' collection='attachmentclassificationIdLike' separator=' OR '>UPPER(a.CLASSIFICATION_ID) LIKE #{item}</foreach>)</if> "
        + "<if test='attachmentChannelLike != null'>AND (<foreach item='item' collection='attachmentChannelLike' separator=' OR '>UPPER(a.CHANNEL) LIKE #{item}</foreach>)</if> "
        + "<if test='attachmentReferenceLike != null'>AND (<foreach item='item' collection='attachmentReferenceLike' separator=' OR '>UPPER(a.REF_VALUE) LIKE #{item}</foreach>)</if>";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getWhereCustomInCondition() {
    return "<if test='creatorIn != null'>AND t.CREATOR IN(<foreach item='item' collection='creatorIn' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom1In != null'>AND t.CUSTOM_1 IN(<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom2In != null'>AND t.CUSTOM_2 IN(<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom3In != null'>AND t.CUSTOM_3 IN(<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom4In != null'>AND t.CUSTOM_4 IN(<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom5In != null'>AND t.CUSTOM_5 IN(<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom6In != null'>AND t.CUSTOM_6 IN(<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom7In != null'>AND t.CUSTOM_7 IN(<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom8In != null'>AND t.CUSTOM_8 IN(<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom9In != null'>AND t.CUSTOM_9 IN(<foreach item='item' collection='custom9In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom10In != null'>AND t.CUSTOM_10 IN(<foreach item='item' collection='custom10In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom11In != null'>AND t.CUSTOM_11 IN(<foreach item='item' collection='custom11In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom12In != null'>AND t.CUSTOM_12 IN(<foreach item='item' collection='custom12In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom13In != null'>AND t.CUSTOM_13 IN(<foreach item='item' collection='custom13In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom14In != null'>AND t.CUSTOM_14 IN(<foreach item='item' collection='custom14In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom15In != null'>AND t.CUSTOM_15 IN(<foreach item='item' collection='custom15In' separator=',' >#{item}</foreach>)</if>"
        + "<if test='custom16In != null'>AND t.CUSTOM_16 IN(<foreach item='item' collection='custom16In' separator=',' >#{item}</foreach>)</if>";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getWhereCustomLikeCondition() {
    return "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR '>UPPER(t.CUSTOM_1) LIKE #{item}</foreach>)</if>"
        + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR '>UPPER(t.CUSTOM_2) LIKE #{item}</foreach>)</if>"
        + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR '>UPPER(t.CUSTOM_3) LIKE #{item}</foreach>)</if>"
        + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR '>UPPER(t.CUSTOM_4) LIKE #{item}</foreach>)</if>"
        + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR '>UPPER(t.CUSTOM_5) LIKE #{item}</foreach>)</if>"
        + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR '>UPPER(t.CUSTOM_6) LIKE #{item}</foreach>)</if>"
        + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR '>UPPER(t.CUSTOM_7) LIKE #{item}</foreach>)</if>"
        + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR '>UPPER(t.CUSTOM_8) LIKE #{item}</foreach>)</if>"
        + "<if test='custom9Like != null'>AND (<foreach item='item' collection='custom9Like' separator=' OR '>UPPER(t.CUSTOM_9) LIKE #{item}</foreach>)</if>"
        + "<if test='custom10Like != null'>AND (<foreach item='item' collection='custom10Like' separator=' OR '>UPPER(t.CUSTOM_10) LIKE #{item}</foreach>)</if>"
        + "<if test='custom11Like != null'>AND (<foreach item='item' collection='custom11Like' separator=' OR '>UPPER(t.CUSTOM_11) LIKE #{item}</foreach>)</if>"
        + "<if test='custom12Like != null'>AND (<foreach item='item' collection='custom12Like' separator=' OR '>UPPER(t.CUSTOM_12) LIKE #{item}</foreach>)</if>"
        + "<if test='custom13Like != null'>AND (<foreach item='item' collection='custom13Like' separator=' OR '>UPPER(t.CUSTOM_13) LIKE #{item}</foreach>)</if>"
        + "<if test='custom14Like != null'>AND (<foreach item='item' collection='custom14Like' separator=' OR '>UPPER(t.CUSTOM_14) LIKE #{item}</foreach>)</if>"
        + "<if test='custom15Like != null'>AND (<foreach item='item' collection='custom15Like' separator=' OR '>UPPER(t.CUSTOM_15) LIKE #{item}</foreach>)</if>"
        + "<if test='custom16Like != null'>AND (<foreach item='item' collection='custom16Like' separator=' OR '>UPPER(t.CUSTOM_16) LIKE #{item}</foreach>)</if>";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getWhereWildcardSearchCondition() {
    return "<if test='wildcardSearchValueLike != null and wildcardSearchFieldIn != null'>AND (<foreach item='item' collection='wildcardSearchFieldIn' separator=' OR '>t.${item} LIKE #{wildcardSearchValueLike}</foreach>)</if> ";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getOrderByStatement() {
    return "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> ";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getConditionalOrderByStatement() {
    return "<if test='!orderBy.isEmpty()'>ORDER BY "
        + "<foreach item='item' collection='orderBy' separator=',' >"
        + "<choose>"
        + "<when test=\"item.contains('TCLASSIFICATION_KEY ASC')\">t.CLASSIFICATION_KEY ASC</when>"
        + "<when test=\"item.contains('TCLASSIFICATION_KEY DESC')\">t.CLASSIFICATION_KEY DESC</when>"
        + "<when test=\"item.contains('ACLASSIFICATION_KEY ASC')\">a.CLASSIFICATION_KEY ASC</when>"
        + "<when test=\"item.contains('ACLASSIFICATION_KEY DESC')\">a.CLASSIFICATION_KEY DESC</when>"
        + "<when test=\"item.contains('ACLASSIFICATION_ID ASC')\">a.CLASSIFICATION_ID ASC</when>"
        + "<when test=\"item.contains('ACLASSIFICATION_ID DESC')\">a.CLASSIFICATION_ID DESC</when>"
        + "<when test=\"item.contains('CLASSIFICATION_NAME DESC')\">c.NAME DESC</when>"
        + "<when test=\"item.contains('CLASSIFICATION_NAME ASC')\">c.NAME ASC</when>"
        + "<when test=\"item.contains('A_CLASSIFICATION_NAME DESC')\">ac.NAME DESC</when>"
        + "<when test=\"item.contains('A_CLASSIFICATION_NAME ASC')\">ac.NAME ASC</when>"
        + "<otherwise>${item}</otherwise>"
        + "</choose>"
        + "</foreach>"
        + "</if> ";
  }

  @SuppressWarnings("checkstyle:LineLength")
  private static String getObjectReferencesWhereCondition() {
    return "<if test='company != null'>AND COMPANY IN(<foreach item='item' collection='company' separator=',' >#{item}</foreach>)</if> "
        + "<if test='system != null'>AND SYSTEM IN(<foreach item='item' collection='system' separator=',' >#{item}</foreach>)</if> "
        + "<if test='systemInstance != null'>AND SYSTEM_INSTANCE IN(<foreach item='item' collection='systemInstance' separator=',' >#{item}</foreach>)</if> "
        + "<if test='type != null'>AND TYPE IN(<foreach item='item' collection='type' separator=',' >#{item}</foreach>)</if> "
        + "<if test='value != null'>AND VALUE IN(<foreach item='item' collection='value' separator=',' >#{item}</foreach>)</if> ";
  }
}
