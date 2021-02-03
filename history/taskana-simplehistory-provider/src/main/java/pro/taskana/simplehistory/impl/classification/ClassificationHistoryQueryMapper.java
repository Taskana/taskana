package pro.taskana.simplehistory.impl.classification;

import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import pro.taskana.simplehistory.impl.ClassificationHistoryQueryImpl;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;

/** This class is the mybatis mapping of ClassificationHistoryQueries. */
@SuppressWarnings("checkstyle:LineLength")
public interface ClassificationHistoryQueryMapper {

  @Select(
      "<script>"
          + "SELECT ID, EVENT_TYPE, CREATED, USER_ID, CLASSIFICATION_ID, APPLICATION_ENTRY_POINT, CATEGORY, DOMAIN, KEY, NAME,"
          + "PARENT_ID, PARENT_KEY, PRIORITY, SERVICE_LEVEL, TYPE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8 "
          + "FROM CLASSIFICATION_HISTORY_EVENT"
          + "<where>"
          // IN-Queries
          + "<if test='idIn != null'>AND UPPER(ID) IN (<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='eventTypeIn != null'>AND UPPER(EVENT_TYPE) IN (<foreach item='item' collection='eventTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=',' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='userIdIn != null'>AND UPPER(USER_ID) IN (<foreach item='item' collection='userIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='classificationIdIn != null'>AND UPPER(CLASSIFICATION_ID) IN (<foreach item='item' collection='classificationIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='applicationEntryPointIn != null'>AND UPPER(APPLICATION_ENTRY_POINT) IN (<foreach item='item' collection='applicationEntryPointIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='categoryIn != null'>AND UPPER(CATEGORY) IN (<foreach item='item' collection='categoryIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND UPPER(DOMAIN) IN (<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='keyIn != null'>AND UPPER(KEY) IN (<foreach item='item' collection='keyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='nameIn != null'>AND UPPER(NAME) IN (<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentIdIn != null'>AND UPPER(PARENT_ID) IN (<foreach item='item' collection='parentIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentKeyIn != null'>AND UPPER(PARENT_KEY) IN (<foreach item='item' collection='parentKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='priorityIn != null'>AND UPPER(PRIORITY) IN (<foreach item='item' collection='priorityIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='serviceLevelIn != null'>AND UPPER(SERVICE_LEVEL) IN (<foreach item='item' collection='serviceLevelIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='typeIn != null'>AND UPPER(TYPE) IN (<foreach item='item' collection='typeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND UPPER(CUSTOM_1) IN (<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND UPPER(CUSTOM_2) IN (<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND UPPER(CUSTOM_3) IN (<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND UPPER(CUSTOM_4) IN (<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom5In != null'>AND UPPER(CUSTOM_5) IN (<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom6In != null'>AND UPPER(CUSTOM_6) IN (<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom7In != null'>AND UPPER(CUSTOM_7) IN (<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom8In != null'>AND UPPER(CUSTOM_8) IN (<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>)</if> "
          // LIKE-Queries
          + "<if test='eventTypeLike != null'>AND (<foreach item='item' collection='eventTypeLike' separator= ' OR ' >UPPER(EVENT_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='userIdLike != null'>AND (<foreach item='item' collection='userIdLike' separator=' OR ' > UPPER(USER_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='classificationIdLike != null'>AND (<foreach item='item' collection='classificationIdLike' separator=' OR ' >UPPER(CLASSIFICATION_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='applicationEntryPointLike != null'>AND (<foreach item='item' collection='applicationEntryPointLike' separator=' OR ' >UPPER(APPLICATION_ENTRY_POINT) LIKE #{item}</foreach>)</if> "
          + "<if test='categoryLike != null'>AND (<foreach item='item' collection='categoryLike' separator=' OR ' >UPPER(CATEGORY) LIKE #{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >UPPER(DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='keyLike != null'>AND (<foreach item='item' collection='keyLike' separator=' OR ' >UPPER(KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR ' >UPPER(NAME) LIKE #{item}</foreach>)</if> "
          + "<if test='parentIdLike != null'>AND (<foreach item='item' collection='parentIdLike' separator=' OR ' >UPPER(PARENT_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='parentKeyLike != null'>AND (<foreach item='item' collection='parentKeyLike' separator=' OR ' >UPPER(PARENT_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='serviceLevelLike != null'>AND (<foreach item='item' collection='serviceLevelLike' separator=' OR ' >UPPER(SERVICE_LEVEL) LIKE #{item}</foreach>)</if> "
          + "<if test='typeLike != null'>AND (<foreach item='item' collection='typeLike' separator=' OR ' >UPPER(TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >UPPER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >UPPER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >UPPER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >UPPER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR ' >UPPER(CUSTOM_5) LIKE #{item}</foreach>)</if> "
          + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR ' >UPPER(CUSTOM_6) LIKE #{item}</foreach>)</if> "
          + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR ' >UPPER(CUSTOM_7) LIKE #{item}</foreach>)</if> "
          + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR ' >UPPER(CUSTOM_8) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "eventType", column = "EVENT_TYPE")
  @Result(property = "created", column = "CREATED")
  @Result(property = "userId", column = "USER_ID")
  @Result(property = "classificationId", column = "CLASSIFICATION_ID")
  @Result(property = "applicationEntryPoint", column = "APPLICATION_ENTRY_POINT")
  @Result(property = "category", column = "CATEGORY")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "key", column = "KEY")
  @Result(property = "name", column = "NAME")
  @Result(property = "parentId", column = "PARENT_ID")
  @Result(property = "parentKey", column = "PARENT_KEY")
  @Result(property = "priority", column = "PRIORITY")
  @Result(property = "serviceLevel", column = "SERVICE_LEVEL")
  @Result(property = "type", column = "TYPE")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "custom5", column = "CUSTOM_5")
  @Result(property = "custom6", column = "CUSTOM_6")
  @Result(property = "custom7", column = "CUSTOM_7")
  @Result(property = "custom8", column = "CUSTOM_8")
  List<ClassificationHistoryEvent> queryHistoryEvents(
      ClassificationHistoryQueryImpl historyEventQuery);

  @Select(
      "<script>"
          + "SELECT COUNT(ID) "
          + "FROM CLASSIFICATION_HISTORY_EVENT"
          + "<where>"
          + "<if test='idIn != null'>AND UPPER(ID) IN (<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='eventTypeIn != null'>AND UPPER(EVENT_TYPE) IN (<foreach item='item' collection='eventTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=',' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='userIdIn != null'>AND UPPER(USER_ID) IN (<foreach item='item' collection='userIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='classificationIdIn != null'>AND UPPER(CLASSIFICATION_ID) IN (<foreach item='item' collection='classificationIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='applicationEntryPointIn != null'>AND UPPER(APPLICATION_ENTRY_POINT) IN (<foreach item='item' collection='applicationEntryPointIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='categoryIn != null'>AND UPPER(CATEGORY) IN (<foreach item='item' collection='categoryIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND UPPER(DOMAIN) IN (<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='keyIn != null'>AND UPPER(KEY) IN (<foreach item='item' collection='keyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='nameIn != null'>AND UPPER(NAME) IN (<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentIdIn != null'>AND UPPER(PARENT_ID) IN (<foreach item='item' collection='parentIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentKeyIn != null'>AND UPPER(PARENT_KEY) IN (<foreach item='item' collection='parentKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='priorityIn != null'>AND UPPER(PRIORITY) IN (<foreach item='item' collection='priorityIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='serviceLevelIn != null'>AND UPPER(SERVICE_LEVEL) IN (<foreach item='item' collection='serviceLevelIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='typeIn != null'>AND UPPER(TYPE) IN (<foreach item='item' collection='typeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND UPPER(CUSTOM_1) IN (<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND UPPER(CUSTOM_2) IN (<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND UPPER(CUSTOM_3) IN (<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND UPPER(CUSTOM_4) IN (<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom5In != null'>AND UPPER(CUSTOM_5) IN (<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom6In != null'>AND UPPER(CUSTOM_6) IN (<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom7In != null'>AND UPPER(CUSTOM_7) IN (<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom8In != null'>AND UPPER(CUSTOM_8) IN (<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>)</if> "
          // LIKE-Queries
          + "<if test='eventTypeLike != null'>AND (<foreach item='item' collection='eventTypeLike' separator= ' OR ' >UPPER(EVENT_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='userIdLike != null'>AND (<foreach item='item' collection='userIdLike' separator=' OR ' > UPPER(USER_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='classificationIdLike != null'>AND (<foreach item='item' collection='classificationIdLike' separator=' OR ' >UPPER(CLASSIFICATION_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='applicationEntryPointLike != null'>AND (<foreach item='item' collection='applicationEntryPointLike' separator=' OR ' >UPPER(APPLICATION_ENTRY_POINT) LIKE #{item}</foreach>)</if> "
          + "<if test='categoryLike != null'>AND (<foreach item='item' collection='categoryLike' separator=' OR ' >UPPER(CATEGORY) LIKE #{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >UPPER(DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='keyLike != null'>AND (<foreach item='item' collection='keyLike' separator=' OR ' >UPPER(KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR ' >UPPER(NAME) LIKE #{item}</foreach>)</if> "
          + "<if test='parentIdLike != null'>AND (<foreach item='item' collection='parentIdLike' separator=' OR ' >UPPER(PARENT_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='parentKeyLike != null'>AND (<foreach item='item' collection='parentKeyLike' separator=' OR ' >UPPER(PARENT_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='serviceLevelLike != null'>AND (<foreach item='item' collection='serviceLevelLike' separator=' OR ' >UPPER(SERVICE_LEVEL) LIKE #{item}</foreach>)</if> "
          + "<if test='typeLike != null'>AND (<foreach item='item' collection='typeLike' separator=' OR ' >UPPER(TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >UPPER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >UPPER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >UPPER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >UPPER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR ' >UPPER(CUSTOM_5) LIKE #{item}</foreach>)</if> "
          + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR ' >UPPER(CUSTOM_6) LIKE #{item}</foreach>)</if> "
          + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR ' >UPPER(CUSTOM_7) LIKE #{item}</foreach>)</if> "
          + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR ' >UPPER(CUSTOM_8) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
          + "</script>")
  long countHistoryEvents(ClassificationHistoryQueryImpl historyEventQuery);

  @Select(
      "<script>SELECT DISTINCT ${columnName} "
          + "FROM CLASSIFICATION_HISTORY_EVENT"
          + "<where>"
          + "<if test='idIn != null'>AND UPPER(ID) IN (<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='eventTypeIn != null'>AND UPPER(EVENT_TYPE) IN (<foreach item='item' collection='eventTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=',' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='userIdIn != null'>AND UPPER(USER_ID) IN (<foreach item='item' collection='userIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='classificationIdIn != null'>AND UPPER(CLASSIFICATION_ID) IN (<foreach item='item' collection='classificationIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='applicationEntryPointIn != null'>AND UPPER(APPLICATION_ENTRY_POINT) IN (<foreach item='item' collection='applicationEntryPointIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='categoryIn != null'>AND UPPER(CATEGORY) IN (<foreach item='item' collection='categoryIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND UPPER(DOMAIN) IN (<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='keyIn != null'>AND UPPER(KEY) IN (<foreach item='item' collection='keyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='nameIn != null'>AND UPPER(NAME) IN (<foreach item='item' collection='nameIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentIdIn != null'>AND UPPER(PARENT_ID) IN (<foreach item='item' collection='parentIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='parentKeyIn != null'>AND UPPER(PARENT_KEY) IN (<foreach item='item' collection='parentKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='priorityIn != null'>AND UPPER(PRIORITY) IN (<foreach item='item' collection='priorityIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='serviceLevelIn != null'>AND UPPER(SERVICE_LEVEL) IN (<foreach item='item' collection='serviceLevelIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='typeIn != null'>AND UPPER(TYPE) IN (<foreach item='item' collection='typeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND UPPER(CUSTOM_1) IN (<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND UPPER(CUSTOM_2) IN (<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND UPPER(CUSTOM_3) IN (<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND UPPER(CUSTOM_4) IN (<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom5In != null'>AND UPPER(CUSTOM_5) IN (<foreach item='item' collection='custom5In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom6In != null'>AND UPPER(CUSTOM_6) IN (<foreach item='item' collection='custom6In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom7In != null'>AND UPPER(CUSTOM_7) IN (<foreach item='item' collection='custom7In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom8In != null'>AND UPPER(CUSTOM_8) IN (<foreach item='item' collection='custom8In' separator=',' >#{item}</foreach>)</if> "
          // LIKE-Queries
          + "<if test='eventTypeLike != null'>AND (<foreach item='item' collection='eventTypeLike' separator= ' OR ' >UPPER(EVENT_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='userIdLike != null'>AND (<foreach item='item' collection='userIdLike' separator=' OR ' >UPPER(USER_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='classificationIdLike != null'>AND (<foreach item='item' collection='classificationIdLike' separator=' OR ' >UPPER(CLASSIFICATION_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='applicationEntryPointLike != null'>AND (<foreach item='item' collection='applicationEntryPointLike' separator=' OR ' >UPPER(APPLICATION_ENTRY_POINT) LIKE #{item}</foreach>)</if> "
          + "<if test='categoryLike != null'>AND (<foreach item='item' collection='categoryLike' separator=' OR ' >UPPER(CATEGORY) LIKE #{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >UPPER(DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='keyLike != null'>AND (<foreach item='item' collection='keyLike' separator=' OR ' >UPPER(KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='nameLike != null'>AND (<foreach item='item' collection='nameLike' separator=' OR ' >UPPER(NAME) LIKE #{item}</foreach>)</if> "
          + "<if test='parentIdLike != null'>AND (<foreach item='item' collection='parentIdLike' separator=' OR ' >UPPER(PARENT_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='parentKeyLike != null'>AND (<foreach item='item' collection='parentKeyLike' separator=' OR ' >UPPER(PARENT_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='serviceLevelLike != null'>AND (<foreach item='item' collection='serviceLevelLike' separator=' OR ' >UPPER(SERVICE_LEVEL) LIKE #{item}</foreach>)</if> "
          + "<if test='typeLike != null'>AND (<foreach item='item' collection='typeLike' separator=' OR ' >UPPER(TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >UPPER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >UPPER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >UPPER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >UPPER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='custom5Like != null'>AND (<foreach item='item' collection='custom5Like' separator=' OR ' >UPPER(CUSTOM_5) LIKE #{item}</foreach>)</if> "
          + "<if test='custom6Like != null'>AND (<foreach item='item' collection='custom6Like' separator=' OR ' >UPPER(CUSTOM_6) LIKE #{item}</foreach>)</if> "
          + "<if test='custom7Like != null'>AND (<foreach item='item' collection='custom7Like' separator=' OR ' >UPPER(CUSTOM_7) LIKE #{item}</foreach>)</if> "
          + "<if test='custom8Like != null'>AND (<foreach item='item' collection='custom8Like' separator=' OR ' >UPPER(CUSTOM_8) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
          + "</script>")
  List<String> queryHistoryColumnValues(ClassificationHistoryQueryImpl historyQuery);
}
