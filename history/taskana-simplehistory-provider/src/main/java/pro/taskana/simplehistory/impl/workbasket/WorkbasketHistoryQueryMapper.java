package pro.taskana.simplehistory.impl.workbasket;

import java.util.List;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import pro.taskana.simplehistory.impl.WorkbasketHistoryQueryImpl;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;

/** This class is the mybatis mapping of WorkbasketHistoryQueries. */
@SuppressWarnings("checkstyle:LineLength")
public interface WorkbasketHistoryQueryMapper {

  @Select(
      "<script>"
          + "SELECT ID, WORKBASKET_ID, EVENT_TYPE, CREATED, USER_ID, DOMAIN, WORKBASKET_KEY, WORKBASKET_TYPE,"
          + "OWNER, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORGLEVEL_1, ORGLEVEL_2, ORGLEVEL_3, ORGLEVEL_4 "
          + "FROM WORKBASKET_HISTORY_EVENT"
          + "<where>"
          // IN-Queries
          + "<if test='idIn != null'>AND UPPER(ID) IN (<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketIdIn != null'>AND UPPER(WORKBASKET_ID) IN (<foreach item='item' collection='workbasketIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='eventTypeIn != null'>AND UPPER(EVENT_TYPE) IN (<foreach item='item' collection='eventTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=',' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='userIdIn != null'>AND UPPER(USER_ID) IN (<foreach item='item' collection='userIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND UPPER(DOMAIN) IN (<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyIn != null'>AND UPPER(WORKBASKET_KEY) IN (<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketTypeIn != null'>AND UPPER(WORKBASKET_TYPE) IN (<foreach item='item' collection='workbasketTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='ownerIn != null'>AND UPPER(OWNER) IN (<foreach item='item' collection='ownerIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND UPPER(CUSTOM_1) IN (<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND UPPER(CUSTOM_2) IN (<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND UPPER(CUSTOM_3) IN (<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND UPPER(CUSTOM_4) IN (<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel1In != null'>AND UPPER(ORGLEVEL_1) IN (<foreach item='item' collection='orgLevel1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel2In != null'>AND UPPER(ORGLEVEL_2) IN (<foreach item='item' collection='orgLevel2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel3In != null'>AND UPPER(ORGLEVEL_3) IN (<foreach item='item' collection='orgLevel3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel4In != null'>AND UPPER(ORGLEVEL_4) IN (<foreach item='item' collection='orgLevel4In' separator=',' >#{item}</foreach>)</if> "
          // LIKE-Queries
          + "<if test='workbasketIdLike != null'>AND (<foreach item='item' collection='workbasketIdLike' separator=' OR ' >UPPER(WORKBASKET_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='eventTypeLike != null'>AND (<foreach item='item' collection='eventTypeLike' separator=' OR ' >UPPER(EVENT_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='userIdLike != null'>AND (<foreach item='item' collection='userIdLike' separator=' OR ' >UPPER(USER_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >UPPER(DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='workbasketKeyLike != null'>AND (<foreach item='item' collection='workbasketKeyLike' separator=' OR ' >UPPER(WORKBASKET_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='workbasketTypeLike != null'>AND (<foreach item='item' collection='workbasketTypeLike' separator=' OR ' >UPPER(WORKBASKET_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='ownerLike != null'>AND (<foreach item='item' collection='ownerLike' separator=' OR ' >UPPER(OWNER) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >UPPER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >UPPER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >UPPER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >UPPER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel1Like != null'>AND (<foreach item='item' collection='orgLevel1Like' separator=' OR ' >UPPER(ORGLEVEL_1) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel2Like != null'>AND (<foreach item='item' collection='orgLevel2Like' separator=' OR ' >UPPER(ORGLEVEL_2) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel3Like != null'>AND (<foreach item='item' collection='orgLevel3Like' separator=' OR ' >UPPER(ORGLEVEL_3) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel4Like != null'>AND (<foreach item='item' collection='orgLevel4Like' separator=' OR ' >UPPER(ORGLEVEL_4) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
          + "</script>")
  @Results(
      value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "workbasketId", column = "WORKBASKET_ID"),
        @Result(property = "eventType", column = "EVENT_TYPE"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "userId", column = "USER_ID"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
        @Result(property = "workbasketType", column = "WORKBASKET_TYPE"),
        @Result(property = "owner", column = "OWNER"),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "orgLevel1", column = "ORGLEVEL_1"),
        @Result(property = "orgLevel2", column = "ORGLEVEL_2"),
        @Result(property = "orgLevel3", column = "ORGLEVEL_3"),
        @Result(property = "orgLevel4", column = "ORGLEVEL_4")
      })
  List<WorkbasketHistoryEvent> queryHistoryEvents(WorkbasketHistoryQueryImpl historyEventQuery);

  @Select(
      "<script>"
          + "SELECT COUNT(ID) "
          + "FROM WORKBASKET_HISTORY_EVENT"
          + "<where>"
          + "<if test='idIn != null'>AND UPPER(ID) IN (<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketIdIn != null'>AND UPPER(WORKBASKET_ID) IN (<foreach item='item' collection='workbasketIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='eventTypeIn != null'>AND UPPER(EVENT_TYPE) IN (<foreach item='item' collection='eventTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=',' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='userIdIn != null'>AND UPPER(USER_ID) IN (<foreach item='item' collection='userIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND UPPER(DOMAIN) IN (<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyIn != null'>AND UPPER(WORKBASKET_KEY) IN (<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketTypeIn != null'>AND UPPER(WORKBASKET_TYPE) IN (<foreach item='item' collection='workbasketTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='ownerIn != null'>AND UPPER(OWNER) IN (<foreach item='item' collection='ownerIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND UPPER(CUSTOM_1) IN (<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND UPPER(CUSTOM_2) IN (<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND UPPER(CUSTOM_3) IN (<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND UPPER(CUSTOM_4) IN (<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel1In != null'>AND UPPER(ORGLEVEL_1) IN (<foreach item='item' collection='orgLevel1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel2In != null'>AND UPPER(ORGLEVEL_2) IN (<foreach item='item' collection='orgLevel2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel3In != null'>AND UPPER(ORGLEVEL_3) IN (<foreach item='item' collection='orgLevel3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel4In != null'>AND UPPER(ORGLEVEL_4) IN (<foreach item='item' collection='orgLevel4In' separator=',' >#{item}</foreach>)</if> "
          // LIKE-Queries
          + "<if test='workbasketIdLike != null'>AND (<foreach item='item' collection='workbasketIdLike' separator=' OR ' >UPPER(WORKBASKET_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='eventTypeLike != null'>AND (<foreach item='item' collection='eventTypeLike' separator=' OR ' >UPPER(EVENT_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='userIdLike != null'>AND (<foreach item='item' collection='userIdLike' separator=' OR ' >UPPER(USER_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >UPPER(DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='workbasketKeyLike != null'>AND (<foreach item='item' collection='workbasketKeyLike' separator=' OR ' >UPPER(WORKBASKET_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='workbasketTypeLike != null'>AND (<foreach item='item' collection='workbasketTypeLike' separator=' OR ' >UPPER(WORKBASKET_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='ownerLike != null'>AND (<foreach item='item' collection='ownerLike' separator=' OR ' >UPPER(OWNER) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >UPPER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >UPPER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >UPPER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >UPPER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel1Like != null'>AND (<foreach item='item' collection='orgLevel1Like' separator=' OR ' >UPPER(ORGLEVEL_1) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel2Like != null'>AND (<foreach item='item' collection='orgLevel2Like' separator=' OR ' >UPPER(ORGLEVEL_2) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel3Like != null'>AND (<foreach item='item' collection='orgLevel3Like' separator=' OR ' >UPPER(ORGLEVEL_3) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel4Like != null'>AND (<foreach item='item' collection='orgLevel4Like' separator=' OR ' >UPPER(ORGLEVEL_4) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "</script>")
  long countHistoryEvents(WorkbasketHistoryQueryImpl historyEventQuery);

  @Select(
      "<script>SELECT DISTINCT ${columnName} "
          + "FROM WORKBASKET_HISTORY_EVENT"
          + "<where>"
          + "<if test='idIn != null'>AND UPPER(ID) IN (<foreach item='item' collection='idIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketIdIn != null'>AND UPPER(WORKBASKET_ID) IN (<foreach item='item' collection='workbasketIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='eventTypeIn != null'>AND UPPER(EVENT_TYPE) IN (<foreach item='item' collection='eventTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='createdIn !=null'> AND ( <foreach item='item' collection='createdIn' separator=',' > ( <if test='item.begin!=null'> CREATED &gt;= #{item.begin} </if> <if test='item.begin!=null and item.end!=null'> AND </if><if test='item.end!=null'> CREATED &lt;=#{item.end} </if>)</foreach>)</if> "
          + "<if test='userIdIn != null'>AND UPPER(USER_ID) IN (<foreach item='item' collection='userIdIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='domainIn != null'>AND UPPER(DOMAIN) IN (<foreach item='item' collection='domainIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketKeyIn != null'>AND UPPER(WORKBASKET_KEY) IN (<foreach item='item' collection='workbasketKeyIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='workbasketTypeIn != null'>AND UPPER(WORKBASKET_TYPE) IN (<foreach item='item' collection='workbasketTypeIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='ownerIn != null'>AND UPPER(OWNER) IN (<foreach item='item' collection='ownerIn' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom1In != null'>AND UPPER(CUSTOM_1) IN (<foreach item='item' collection='custom1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom2In != null'>AND UPPER(CUSTOM_2) IN (<foreach item='item' collection='custom2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom3In != null'>AND UPPER(CUSTOM_3) IN (<foreach item='item' collection='custom3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='custom4In != null'>AND UPPER(CUSTOM_4) IN (<foreach item='item' collection='custom4In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel1In != null'>AND UPPER(ORGLEVEL_1) IN (<foreach item='item' collection='orgLevel1In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel2In != null'>AND UPPER(ORGLEVEL_2) IN (<foreach item='item' collection='orgLevel2In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel3In != null'>AND UPPER(ORGLEVEL_3) IN (<foreach item='item' collection='orgLevel3In' separator=',' >#{item}</foreach>)</if> "
          + "<if test='orgLevel4In != null'>AND UPPER(ORGLEVEL_4) IN (<foreach item='item' collection='orgLevel4In' separator=',' >#{item}</foreach>)</if> "
          // LIKE-Queries
          + "<if test='workbasketIdLike != null'>AND (<foreach item='item' collection='workbasketIdLike' separator=' OR ' >UPPER(WORKBASKET_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='eventTypeLike != null'>AND (<foreach item='item' collection='eventTypeLike' separator=' OR ' >UPPER(EVENT_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='userIdLike != null'>AND (<foreach item='item' collection='userIdLike' separator=' OR ' >UPPER(USER_ID) LIKE #{item}</foreach>)</if> "
          + "<if test='domainLike != null'>AND (<foreach item='item' collection='domainLike' separator=' OR ' >UPPER(DOMAIN) LIKE #{item}</foreach>)</if> "
          + "<if test='workbasketKeyLike != null'>AND (<foreach item='item' collection='workbasketKeyLike' separator=' OR ' >UPPER(WORKBASKET_KEY) LIKE #{item}</foreach>)</if> "
          + "<if test='workbasketTypeLike != null'>AND (<foreach item='item' collection='workbasketTypeLike' separator=' OR ' >UPPER(WORKBASKET_TYPE) LIKE #{item}</foreach>)</if> "
          + "<if test='ownerLike != null'>AND (<foreach item='item' collection='ownerLike' separator=' OR ' >UPPER(OWNER) LIKE #{item}</foreach>)</if> "
          + "<if test='custom1Like != null'>AND (<foreach item='item' collection='custom1Like' separator=' OR ' >UPPER(CUSTOM_1) LIKE #{item}</foreach>)</if> "
          + "<if test='custom2Like != null'>AND (<foreach item='item' collection='custom2Like' separator=' OR ' >UPPER(CUSTOM_2) LIKE #{item}</foreach>)</if> "
          + "<if test='custom3Like != null'>AND (<foreach item='item' collection='custom3Like' separator=' OR ' >UPPER(CUSTOM_3) LIKE #{item}</foreach>)</if> "
          + "<if test='custom4Like != null'>AND (<foreach item='item' collection='custom4Like' separator=' OR ' >UPPER(CUSTOM_4) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel1Like != null'>AND (<foreach item='item' collection='orgLevel1Like' separator=' OR ' >UPPER(ORGLEVEL_1) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel2Like != null'>AND (<foreach item='item' collection='orgLevel2Like' separator=' OR ' >UPPER(ORGLEVEL_2) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel3Like != null'>AND (<foreach item='item' collection='orgLevel3Like' separator=' OR ' >UPPER(ORGLEVEL_3) LIKE #{item}</foreach>)</if> "
          + "<if test='orgLevel4Like != null'>AND (<foreach item='item' collection='orgLevel4Like' separator=' OR ' >UPPER(ORGLEVEL_4) LIKE #{item}</foreach>)</if> "
          + "</where>"
          + "<if test='!orderBy.isEmpty()'>ORDER BY <foreach item='item' collection='orderBy' separator=',' >${item}</foreach></if> "
          + "</script>")
  List<String> queryHistoryColumnValues(WorkbasketHistoryQueryImpl historyQuery);
}
