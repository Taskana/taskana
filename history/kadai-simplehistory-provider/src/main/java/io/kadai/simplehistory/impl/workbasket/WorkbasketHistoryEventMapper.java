package io.kadai.simplehistory.impl.workbasket;

import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

/** This class is the mybatis mapping of workbasket history events. */
@SuppressWarnings("checkstyle:LineLength")
public interface WorkbasketHistoryEventMapper {

  @Insert(
      "<script>INSERT INTO WORKBASKET_HISTORY_EVENT (ID,WORKBASKET_ID,"
          + " EVENT_TYPE, CREATED, USER_ID, DOMAIN, KEY, TYPE, OWNER, "
          + " CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORGLEVEL_1,"
          + " ORGLEVEL_2, ORGLEVEL_3, ORGLEVEL_4, DETAILS)"
          + " VALUES ( #{historyEvent.id}, #{historyEvent.workbasketId},"
          + " #{historyEvent.eventType}, #{historyEvent.created}, #{historyEvent.userId},"
          + " #{historyEvent.domain}, #{historyEvent.key}, "
          + " #{historyEvent.type}, #{historyEvent.owner}, "
          + " #{historyEvent.custom1}, #{historyEvent.custom2}, #{historyEvent.custom3}, "
          + "#{historyEvent.custom4}, #{historyEvent.orgLevel1}, #{historyEvent.orgLevel2}, "
          + "#{historyEvent.orgLevel3}, #{historyEvent.orgLevel4}, #{historyEvent.details}) "
          + "</script>")
  void insert(@Param("historyEvent") WorkbasketHistoryEvent historyEvent);

  @Select(
      "<script>"
          + "SELECT ID, WORKBASKET_ID, EVENT_TYPE, CREATED, USER_ID, DOMAIN, KEY, TYPE, OWNER,  "
          + "CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, ORGLEVEL_1, ORGLEVEL_2, ORGLEVEL_3, ORGLEVEL_4, DETAILS "
          + "FROM WORKBASKET_HISTORY_EVENT WHERE ID = #{id} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Result(property = "id", column = "ID")
  @Result(property = "workbasketId", column = "WORKBASKET_ID")
  @Result(property = "eventType", column = "EVENT_TYPE")
  @Result(property = "created", column = "CREATED")
  @Result(property = "userId", column = "USER_ID")
  @Result(property = "domain", column = "DOMAIN")
  @Result(property = "key", column = "KEY")
  @Result(property = "type", column = "TYPE")
  @Result(property = "owner", column = "OWNER")
  @Result(property = "custom1", column = "CUSTOM_1")
  @Result(property = "custom2", column = "CUSTOM_2")
  @Result(property = "custom3", column = "CUSTOM_3")
  @Result(property = "custom4", column = "CUSTOM_4")
  @Result(property = "orgLevel1", column = "ORGLEVEL_1")
  @Result(property = "orgLevel2", column = "ORGLEVEL_2")
  @Result(property = "orgLevel3", column = "ORGLEVEL_3")
  @Result(property = "orgLevel4", column = "ORGLEVEL_4")
  @Result(property = "details", column = "DETAILS")
  WorkbasketHistoryEvent findById(@Param("id") String id);
}
