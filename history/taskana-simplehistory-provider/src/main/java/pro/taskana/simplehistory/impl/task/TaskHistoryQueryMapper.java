package pro.taskana.simplehistory.impl.task;

import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import pro.taskana.simplehistory.impl.TaskHistoryQueryImpl;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/** This class is the mybatis mapping of TaskHistoryQueries. */
@SuppressWarnings("checkstyle:LineLength")
public interface TaskHistoryQueryMapper {

  @Select(
      "<script>"
          + "SELECT ID, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID, EVENT_TYPE, CREATED, t.USER_ID, DOMAIN, WORKBASKET_KEY, "
          + "POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY,"
          + "ATTACHMENT_CLASSIFICATION_KEY, OLD_VALUE, NEW_VALUE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4 "
          + "<if test=\"includeLongName\">, u.LONG_NAME AS USER_LONG_NAME, o.LONG_NAME AS TASK_OWNER_LONG_NAME </if>"
          + "FROM TASK_HISTORY_EVENT t "
          + "<if test=\"includeLongName\">"
          + "LEFT JOIN USER_INFO AS u ON t.USER_ID = u.USER_ID "
          + "LEFT JOIN USER_INFO AS o ON TASK_OWNER = o.USER_ID "
          + "</if>"
          + "<where>"
          // IN-Queries
          + "<if test='idIn != null'>AND UPPER(ID) IN (<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='businessProcessIdIn != null'>AND UPPER(BUSINESS_PROCESS_ID) IN (<foreach item='item' collection='businessProcessIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentBusinessProcessIdIn != null'>AND UPPER(PARENT_BUSINESS_PROCESS_ID) IN (<foreach item='item' collection='parentBusinessProcessIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='taskIdIn != null'>AND UPPER(TASK_ID) IN (<foreach item='item' collection='taskIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='eventTypeIn != null'>AND UPPER(EVENT_TYPE) IN (<foreach item='item' collection='eventTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=',' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='userIdIn != null'>AND UPPER(t.USER_ID) IN (<foreach item='item' collection='userIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND UPPER(DOMAIN) IN (<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyIn != null'>AND UPPER(WORKBASKET_KEY) IN (<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porCompanyIn != null'>AND UPPER(POR_COMPANY) IN (<foreach item='item' collection='porCompanyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porSystemIn != null'>AND UPPER(POR_SYSTEM) IN (<foreach item='item' collection='porSystemIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porInstanceIn != null'>AND UPPER(POR_INSTANCE) IN (<foreach item='item' collection='porInstanceIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porTypeIn != null'>AND UPPER(POR_TYPE) IN (<foreach item='item' collection='porTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porValueIn != null'>AND UPPER(POR_VALUE) IN (<foreach item='item' collection='porValueIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='taskClassificationKeyIn != null'>AND UPPER(TASK_CLASSIFICATION_KEY) IN (<foreach item='item' collection='taskClassificationKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='taskClassificationCategoryIn != null'>AND UPPER(TASK_CLASSIFICATION_CATEGORY) IN (<foreach item='item' collection='taskClassificationCategoryIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='attachmentClassificationKeyIn != null'>AND UPPER(ATTACHMENT_CLASSIFICATION_KEY) IN (<foreach item='item' collection='attachmentClassificationKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='oldValueIn != null'>AND UPPER(OLD_VALUE) IN (<foreach item='item' collection='oldValueIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='newValueIn != null'>AND UPPER(NEW_VALUE) IN (<foreach item='item' collection='newValueIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND UPPER(CUSTOM_1) IN (<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND UPPER(CUSTOM_2) IN (<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND UPPER(CUSTOM_3) IN (<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND UPPER(CUSTOM_4) IN (<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          // LIKE-Queries
          + "<if test='businessProcessIdLike != null'>AND (<foreach item='item' collection='businessProcessIdLike' separator='OR' > UPPER(BUSINESS_PROCESS_ID) LIKE #{item} </foreach>)</if> "
          + "<if test='parentBusinessProcessIdLike != null'>AND (<foreach item='item' collection='parentBusinessProcessIdLike' separator=' OR ' >UPPER(PARENT_BUSINESS_PROCESS_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='taskIdLike != null'>AND (<foreach item='item' collection='taskIdLike' separator=' OR ' >UPPER(TASK_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='eventTypeLike != null'>AND (<foreach item='item' collection='eventTypeLike' separator=' OR ' >UPPER(EVENT_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='userIdLike != null'>AND (<foreach item='item' collection='userIdLike' separator=' OR ' >UPPER(t.USER_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >UPPER(DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='workbasketKeyLike != null'>AND (<foreach item='item' collection='workbasketKeyLike' separator=' OR ' >UPPER(WORKBASKET_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='porCompanyLike != null'>AND (<foreach item='item' collection='porCompanyLike' separator=' OR ' >UPPER(POR_COMPANY) LIKE #{item}</foreach>)</if> "
          + "<if test='porSystemLike != null'>AND (<foreach item='item' collection='porSystemLike' separator=' OR ' >UPPER(POR_SYSTEM) LIKE #{item}</foreach>)</if> "
          + "<if test='porInstanceLike != null'>AND (<foreach item='item' collection='porInstanceLike' separator=' OR ' >UPPER(POR_INSTANCE) LIKE #{item}</foreach>)</if> "
          + "<if test='porTypeLike != null'>AND (<foreach item='item' collection='porTypeLike' separator=' OR ' >UPPER(POR_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='porValueLike != null'>AND (<foreach item='item' collection='porValueLike' separator=' OR ' >UPPER(POR_VALUE) LIKE #{item}</foreach>)</if> "
          + "<if test='taskClassificationKeyLike != null'>AND (<foreach item='item' collection='taskClassificationKeyLike' separator=' OR ' >UPPER(TASK_CLASSIFICATION_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='taskClassificationCategoryLike != null'>AND (<foreach item='item' collection='taskClassificationCategoryLike' separator=' OR ' >UPPER(TASK_CLASSIFICATION_CATEGORY) LIKE #{item}</foreach>)</if> "
          + "<if test='attachmentClassificationKeyLike != null'>AND (<foreach item='item' collection='attachmentClassificationKeyLike' separator=' OR ' >UPPER(ATTACHMENT_CLASSIFICATION_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='oldValueLike != null'>AND (<foreach item='item' collection='oldValueLike' separator=' OR ' >UPPER(OLD_VALUE) LIKE #{item}</foreach>)</if> "
          + "<if test='newValueLike != null'>AND (<foreach item='item' collection='newValueLike' separator=' OR ' >UPPER(NEW_VALUE) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >UPPER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >UPPER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >UPPER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >UPPER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID")
  @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID")
  @Result(property = "taskId", column = "TASK_ID")
  @Result(property = "taskOwnerLongName", column = "TASK_OWNER_LONG_NAME")
  @Result(property = "eventType", column = "EVENT_TYPE")
  @Result(property = "created", column = "CREATED")
  @Result(property = "userId", column = "USER_ID")
  @Result(property = "userLongName", column = "USER_LONG_NAME")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "workbasketKey", column = "WORKBASKET_KEY")
  @Result(property = "porCompany", column = "POR_COMPANY")
  @Result(property = "porSystem", column = "POR_SYSTEM")
  @Result(property = "porInstance", column = "POR_INSTANCE")
  @Result(property = "porType", column = "POR_TYPE")
  @Result(property = "porValue", column = "POR_VALUE")
  @Result(property = "taskClassificationKey", column = "TASK_CLASSIFICATION_KEY")
  @Result(property = "taskClassificationCategory", column = "TASK_CLASSIFICATION_CATEGORY")
  @Result(property = "attachmentClassificationKey", column = "ATTACHMENT_CLASSIFICATION_KEY")
  @Result(property = "oldValue", column = "OLD_VALUE")
  @Result(property = "newValue", column = "NEW_VALUE")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  List<TaskHistoryEvent> queryHistoryEvents(TaskHistoryQueryImpl historyEventQuery);

  @Select(
      "<script>"
          + "SELECT COUNT(ID) "
          + "FROM TASK_HISTORY_EVENT"
          + "<where>"
          // IN-Queries
          + "<if test='businessProcessIdIn != null'>AND UPPER(BUSINESS_PROCESS_ID) IN (<foreach item='item' collection='businessProcessIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentBusinessProcessIdIn != null'>AND UPPER(PARENT_BUSINESS_PROCESS_ID) IN (<foreach item='item' collection='parentBusinessProcessIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='taskIdIn != null'>AND UPPER(TASK_ID) IN (<foreach item='item' collection='taskIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='eventTypeIn != null'>AND UPPER(EVENT_TYPE) IN (<foreach item='item' collection='eventTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=',' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='userIdIn != null'>AND UPPER(USER_ID) IN (<foreach item='item' collection='userIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND UPPER(DOMAIN) IN (<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyIn != null'>AND UPPER(WORKBASKET_KEY) IN (<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porCompanyIn != null'>AND UPPER(POR_COMPANY) IN (<foreach item='item' collection='porCompanyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porSystemIn != null'>AND UPPER(POR_SYSTEM) IN (<foreach item='item' collection='porSystemIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porInstanceIn != null'>AND UPPER(POR_INSTANCE) IN (<foreach item='item' collection='porInstanceIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porTypeIn != null'>AND UPPER(POR_TYPE) IN (<foreach item='item' collection='porTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porValueIn != null'>AND UPPER(POR_VALUE) IN (<foreach item='item' collection='porValueIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='taskClassificationKeyIn != null'>AND UPPER(TASK_CLASSIFICATION_KEY) IN (<foreach item='item' collection='taskClassificationKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='taskClassificationCategoryIn != null'>AND UPPER(TASK_CLASSIFICATION_CATEGORY) IN (<foreach item='item' collection='taskClassificationCategoryIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='attachmentClassificationKeyIn != null'>AND UPPER(ATTACHMENT_CLASSIFICATION_KEY) IN (<foreach item='item' collection='attachmentClassificationKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='oldValueIn != null'>AND UPPER(OLD_VALUE) IN (<foreach item='item' collection='oldValueIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='newValueIn != null'>AND UPPER(NEW_VALUE) IN (<foreach item='item' collection='newValueIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND UPPER(CUSTOM_1) IN (<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND UPPER(CUSTOM_2) IN (<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND UPPER(CUSTOM_3) IN (<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND UPPER(CUSTOM_4) IN (<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          // LIKE-Queries
          + "<if test='businessProcessIdLike != null'>AND (<foreach item='item' collection='businessProcessIdLike' separator='OR' > UPPER(BUSINESS_PROCESS_ID) LIKE #{item} </foreach>)</if> "
          + "<if test='parentBusinessProcessIdLike != null'>AND (<foreach item='item' collection='parentBusinessProcessIdLike' separator=' OR ' >UPPER(PARENT_BUSINESS_PROCESS_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='taskIdLike != null'>AND (<foreach item='item' collection='taskIdLike' separator=' OR ' >UPPER(TASK_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='eventTypeLike != null'>AND (<foreach item='item' collection='eventTypeLike' separator=' OR ' >UPPER(EVENT_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='userIdLike != null'>AND (<foreach item='item' collection='userIdLike' separator=' OR ' >UPPER(USER_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >UPPER(DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='workbasketKeyLike != null'>AND (<foreach item='item' collection='workbasketKeyLike' separator=' OR ' >UPPER(WORKBASKET_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='porCompanyLike != null'>AND (<foreach item='item' collection='porCompanyLike' separator=' OR ' >UPPER(POR_COMPANY) LIKE #{item}</foreach>)</if> "
          + "<if test='porSystemLike != null'>AND (<foreach item='item' collection='porSystemLike' separator=' OR ' >UPPER(POR_SYSTEM) LIKE #{item}</foreach>)</if> "
          + "<if test='porInstanceLike != null'>AND (<foreach item='item' collection='porInstanceLike' separator=' OR ' >UPPER(POR_INSTANCE) LIKE #{item}</foreach>)</if> "
          + "<if test='porTypeLike != null'>AND (<foreach item='item' collection='porTypeLike' separator=' OR ' >UPPER(POR_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='porValueLike != null'>AND (<foreach item='item' collection='porValueLike' separator=' OR ' >UPPER(POR_VALUE) LIKE #{item}</foreach>)</if> "
          + "<if test='taskClassificationKeyLike != null'>AND (<foreach item='item' collection='taskClassificationKeyLike' separator=' OR ' >UPPER(TASK_CLASSIFICATION_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='taskClassificationCategoryLike != null'>AND (<foreach item='item' collection='taskClassificationCategoryLike' separator=' OR ' >UPPER(TASK_CLASSIFICATION_CATEGORY) LIKE #{item}</foreach>)</if> "
          + "<if test='attachmentClassificationKeyLike != null'>AND (<foreach item='item' collection='attachmentClassificationKeyLike' separator=' OR ' >UPPER(ATTACHMENT_CLASSIFICATION_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='oldValueLike != null'>AND (<foreach item='item' collection='oldValueLike' separator=' OR ' >UPPER(OLD_VALUE) LIKE #{item}</foreach>)</if> "
          + "<if test='newValueLike != null'>AND (<foreach item='item' collection='newValueLike' separator=' OR ' >UPPER(NEW_VALUE) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >UPPER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >UPPER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >UPPER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >UPPER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "</script>")
  long countHistoryEvents(TaskHistoryQueryImpl historyEventQuery);

  @Select(
      "<script>SELECT DISTINCT ${columnName} "
          + "FROM TASK_HISTORY_EVENT AS t"
          + "<if test=\"includeLongName\">"
          + "LEFT JOIN USER_INFO AS u ON t.USER_ID = u.USER_ID "
          + "LEFT JOIN USER_INFO AS o ON TASK_OWNER = o.USER_ID "
          + "</if>"
          + "<where>"
          // IN-Queries
          + "<if test='idIn != null'>AND UPPER(ID) IN (<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='businessProcessIdIn != null'>AND UPPER(BUSINESS_PROCESS_ID) IN (<foreach item='item' collection='businessProcessIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentBusinessProcessIdIn != null'>AND UPPER(PARENT_BUSINESS_PROCESS_ID) IN (<foreach item='item' collection='parentBusinessProcessIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='taskIdIn != null'>AND UPPER(TASK_ID) IN (<foreach item='item' collection='taskIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='eventTypeIn != null'>AND UPPER(EVENT_TYPE) IN (<foreach item='item' collection='eventTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=',' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='userIdIn != null'>AND UPPER(USER_ID) IN (<foreach item='item' collection='userIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND UPPER(DOMAIN) IN (<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyIn != null'>AND UPPER(WORKBASKET_KEY) IN (<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porCompanyIn != null'>AND UPPER(POR_COMPANY) IN (<foreach item='item' collection='porCompanyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porSystemIn != null'>AND UPPER(POR_SYSTEM) IN (<foreach item='item' collection='porSystemIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porInstanceIn != null'>AND UPPER(POR_INSTANCE) IN (<foreach item='item' collection='porInstanceIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porTypeIn != null'>AND UPPER(POR_TYPE) IN (<foreach item='item' collection='porTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='porValueIn != null'>AND UPPER(POR_VALUE) IN (<foreach item='item' collection='porValueIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='taskClassificationKeyIn != null'>AND UPPER(TASK_CLASSIFICATION_KEY) IN (<foreach item='item' collection='taskClassificationKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='taskClassificationCategoryIn != null'>AND UPPER(TASK_CLASSIFICATION_CATEGORY) IN (<foreach item='item' collection='taskClassificationCategoryIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='attachmentClassificationKeyIn != null'>AND UPPER(ATTACHMENT_CLASSIFICATION_KEY) IN (<foreach item='item' collection='attachmentClassificationKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='oldValueIn != null'>AND UPPER(OLD_VALUE) IN (<foreach item='item' collection='oldValueIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='newValueIn != null'>AND UPPER(NEW_VALUE) IN (<foreach item='item' collection='newValueIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND UPPER(CUSTOM_1) IN (<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND UPPER(CUSTOM_2) IN (<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND UPPER(CUSTOM_3) IN (<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND UPPER(CUSTOM_4) IN (<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          // LIKE-Queries
          + "<if test='businessProcessIdLike != null'>AND (<foreach item='item' collection='businessProcessIdLike' separator='OR' > UPPER(BUSINESS_PROCESS_ID) LIKE #{item} </foreach>)</if> "
          + "<if test='parentBusinessProcessIdLike != null'>AND (<foreach item='item' collection='parentBusinessProcessIdLike' separator=' OR ' >UPPER(PARENT_BUSINESS_PROCESS_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='taskIdLike != null'>AND (<foreach item='item' collection='taskIdLike' separator=' OR ' >UPPER(TASK_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='eventTypeLike != null'>AND (<foreach item='item' collection='eventTypeLike' separator=' OR ' >UPPER(EVENT_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='userIdLike != null'>AND (<foreach item='item' collection='userIdLike' separator=' OR ' >UPPER(USER_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >UPPER(DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='workbasketKeyLike != null'>AND (<foreach item='item' collection='workbasketKeyLike' separator=' OR ' >UPPER(WORKBASKET_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='porCompanyLike != null'>AND (<foreach item='item' collection='porCompanyLike' separator=' OR ' >UPPER(POR_COMPANY) LIKE #{item}</foreach>)</if> "
          + "<if test='porSystemLike != null'>AND (<foreach item='item' collection='porSystemLike' separator=' OR ' >UPPER(POR_SYSTEM) LIKE #{item}</foreach>)</if> "
          + "<if test='porInstanceLike != null'>AND (<foreach item='item' collection='porInstanceLike' separator=' OR ' >UPPER(POR_INSTANCE) LIKE #{item}</foreach>)</if> "
          + "<if test='porTypeLike != null'>AND (<foreach item='item' collection='porTypeLike' separator=' OR ' >UPPER(POR_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='porValueLike != null'>AND (<foreach item='item' collection='porValueLike' separator=' OR ' >UPPER(POR_VALUE) LIKE #{item}</foreach>)</if> "
          + "<if test='taskClassificationKeyLike != null'>AND (<foreach item='item' collection='taskClassificationKeyLike' separator=' OR ' >UPPER(TASK_CLASSIFICATION_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='taskClassificationCategoryLike != null'>AND (<foreach item='item' collection='taskClassificationCategoryLike' separator=' OR ' >UPPER(TASK_CLASSIFICATION_CATEGORY) LIKE #{item}</foreach>)</if> "
          + "<if test='attachmentClassificationKeyLike != null'>AND (<foreach item='item' collection='attachmentClassificationKeyLike' separator=' OR ' >UPPER(ATTACHMENT_CLASSIFICATION_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='oldValueLike != null'>AND (<foreach item='item' collection='oldValueLike' separator=' OR ' >UPPER(OLD_VALUE) LIKE #{item}</foreach>)</if> "
          + "<if test='newValueLike != null'>AND (<foreach item='item' collection='newValueLike' separator=' OR ' >UPPER(NEW_VALUE) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >UPPER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >UPPER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >UPPER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >UPPER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
          + "</script>")
  List<String> queryHistoryColumnValues(TaskHistoryQueryImpl historyQuery);
}
