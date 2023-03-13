package pro.taskana.simplehistory.impl.classification;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;

/** This class is the mybatis mapping of classification history events. */
@SuppressWarnings("checkstyle:LineLength")
public interface ClassificationHistoryEventMapper {

  @Insert(
      "<script>INSERT INTO CLASSIFICATION_HISTORY_EVENT (ID,"
          + " EVENT_TYPE, CREATED, USER_ID, CLASSIFICATION_ID, APPLICATION_ENTRY_POINT, CATEGORY,"
          + " DOMAIN, KEY, NAME, PARENT_ID, PARENT_KEY, PRIORITY, SERVICE_LEVEL, TYPE,"
          + " CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, DETAILS)"
          + " VALUES ( #{historyEvent.id}, #{historyEvent.eventType},"
          + " #{historyEvent.created}, #{historyEvent.userId}, #{historyEvent.classificationId}, "
          + " #{historyEvent.applicationEntryPoint}, #{historyEvent.category}, "
          + " #{historyEvent.domain}, #{historyEvent.key}, #{historyEvent.name}, #{historyEvent.parentId}, "
          + " #{historyEvent.parentKey}, #{historyEvent.priority}, #{historyEvent.serviceLevel}, #{historyEvent.type}, "
          + " #{historyEvent.custom1}, #{historyEvent.custom2}, #{historyEvent.custom3}, "
          + "#{historyEvent.custom4}, #{historyEvent.custom5}, #{historyEvent.custom6}, "
          + "#{historyEvent.custom7}, #{historyEvent.custom8}, #{historyEvent.details}) "
          + "</script>")
  void insert(@Param("historyEvent") ClassificationHistoryEvent historyEvent);

  @Select(
      "<script>"
          + "SELECT ID, EVENT_TYPE, CREATED, USER_ID, CLASSIFICATION_ID, APPLICATION_ENTRY_POINT, CATEGORY,"
          + " DOMAIN, KEY, NAME, PARENT_ID, PARENT_KEY, PRIORITY, SERVICE_LEVEL, TYPE,"
          + " CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, CUSTOM_5, CUSTOM_6, CUSTOM_7, CUSTOM_8, DETAILS"
          + " FROM CLASSIFICATION_HISTORY_EVENT WHERE ID = #{id} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
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
  @Result(property = "details", column = "DETAILS")
  ClassificationHistoryEvent findById(@Param("id") String id);
}
