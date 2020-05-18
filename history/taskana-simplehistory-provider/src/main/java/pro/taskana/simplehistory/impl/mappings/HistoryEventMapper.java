package pro.taskana.simplehistory.impl.mappings;

import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;

/** This class is the mybatis mapping of workbaskets. */
@SuppressWarnings("checkstyle:LineLength")
public interface HistoryEventMapper {

  @Insert(
      "<script>INSERT INTO HISTORY_EVENTS (ID,BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID,"
          + " EVENT_TYPE, CREATED, USER_ID, DOMAIN, WORKBASKET_KEY, POR_COMPANY, POR_SYSTEM, POR_INSTANCE,"
          + " POR_TYPE, POR_VALUE, TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY, ATTACHMENT_CLASSIFICATION_KEY, "
          + " OLD_VALUE, NEW_VALUE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, DETAILS)"
          + " VALUES ( #{historyEvent.id}, #{historyEvent.businessProcessId}, #{historyEvent.parentBusinessProcessId}, #{historyEvent.taskId},"
          + " #{historyEvent.eventType}, #{historyEvent.created}, #{historyEvent.userId}, #{historyEvent.domain}, #{historyEvent.workbasketKey},"
          + " #{historyEvent.porCompany}, #{historyEvent.porSystem}, #{historyEvent.porInstance}, #{historyEvent.porType},"
          + " #{historyEvent.porValue}, #{historyEvent.taskClassificationKey}, #{historyEvent.taskClassificationCategory},"
          + " #{historyEvent.attachmentClassificationKey}, #{historyEvent.oldValue}, #{historyEvent.newValue},"
          + " #{historyEvent.custom1}, #{historyEvent.custom2}, #{historyEvent.custom3}, #{historyEvent.custom4},"
          + " #{historyEvent.details}) "
          + "</script>")
  void insert(@Param("historyEvent") TaskanaHistoryEvent historyEvent);

  @Select(
      "<script>"
          + "SELECT ID, BUSINESS_PROCESS_ID, PARENT_BUSINESS_PROCESS_ID, TASK_ID, EVENT_TYPE, CREATED, USER_ID, DOMAIN, WORKBASKET_KEY, "
          + "POR_COMPANY, POR_SYSTEM, POR_INSTANCE, POR_TYPE, POR_VALUE, TASK_CLASSIFICATION_KEY, TASK_CLASSIFICATION_CATEGORY,"
          + "ATTACHMENT_CLASSIFICATION_KEY, OLD_VALUE, NEW_VALUE, CUSTOM_1, CUSTOM_2, CUSTOM_3, CUSTOM_4, DETAILS "
          + "FROM HISTORY_EVENTS WHERE ID = #{id} "
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  @Results(
      value = {
        @Result(property = "id", column = "ID"),
        @Result(property = "businessProcessId", column = "BUSINESS_PROCESS_ID"),
        @Result(property = "parentBusinessProcessId", column = "PARENT_BUSINESS_PROCESS_ID"),
        @Result(property = "taskId", column = "TASK_ID"),
        @Result(property = "eventType", column = "EVENT_TYPE"),
        @Result(property = "created", column = "CREATED"),
        @Result(property = "userId", column = "USER_ID"),
        @Result(property = "domain", column = "DOMAIN"),
        @Result(property = "workbasketKey", column = "WORKBASKET_KEY"),
        @Result(property = "porCompany", column = "POR_COMPANY"),
        @Result(property = "porSystem", column = "POR_SYSTEM"),
        @Result(property = "porInstance", column = "POR_INSTANCE"),
        @Result(property = "porType", column = "POR_TYPE"),
        @Result(property = "porValue", column = "POR_VALUE"),
        @Result(property = "taskClassificationKey", column = "TASK_CLASSIFICATION_KEY"),
        @Result(property = "taskClassificationCategory", column = "TASK_CLASSIFICATION_CATEGORY"),
        @Result(property = "attachmentClassificationKey", column = "ATTACHMENT_CLASSIFICATION_KEY"),
        @Result(property = "oldValue", column = "OLD_VALUE"),
        @Result(property = "newValue", column = "NEW_VALUE"),
        @Result(property = "custom1", column = "CUSTOM_1"),
        @Result(property = "custom2", column = "CUSTOM_2"),
        @Result(property = "custom3", column = "CUSTOM_3"),
        @Result(property = "custom4", column = "CUSTOM_4"),
        @Result(property = "details", column = "DETAILS")
      })
  TaskanaHistoryEvent findById(@Param("id") String id);

  @Delete(
      "<script>DELETE FROM HISTORY_EVENTS WHERE TASK_ID IN(<foreach item='item' collection='taskIds' separator=',' >#{item}</foreach>)</script>")
  void deleteMultipleByTaskIds(@Param("taskIds") List<String> taskIds);

}
