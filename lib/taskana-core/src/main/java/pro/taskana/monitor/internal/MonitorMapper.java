package pro.taskana.monitor.internal;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.item.DetailedMonitorQueryItem;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.monitor.api.reports.item.TimestampQueryItem;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/** This class is the mybatis mapping of task monitoring. */
@SuppressWarnings({"checkstyle:LineLength", "checkstyle:Indentation"})
public interface MonitorMapper {

  @Select(
      "<script>"
          + "SELECT B.WORKBASKET_KEY, B.AGE_IN_DAYS, COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
          + "<if test=\"_databaseId == 'db2'\">SELECT T.WORKBASKET_KEY, (DAYS(T.${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'h2'\">SELECT T.WORKBASKET_KEY, DATEDIFF('DAY', #{now}, T.${timestamp}) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'postgres'\">SELECT T.WORKBASKET_KEY, DATE_PART('DAY', T.${timestamp} - #{now}) as AGE_IN_DAYS </if> "
          + "FROM TASK AS T LEFT JOIN ATTACHMENT AS A ON T.ID = A.TASK_ID "
          + "<where>"
          + "<if test=\"workbasketIds != null\">"
          + "T.WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
          + "</if>"
          + "<if test=\"states != null\">"
          + "AND T.STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
          + "</if>"
          + "<if test=\"classificationCategories != null\">"
          + "AND T.CLASSIFICATION_CATEGORY IN (<foreach collection='classificationCategories' item='category' separator=','>#{category}</foreach>) "
          + "</if>"
          + "<if test=\"domains != null\">"
          + "AND T.DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
          + "</if>"
          + "<if test='classificationIds != null'>"
          + "AND T.CLASSIFICATION_ID IN (<foreach collection='classificationIds' item='classificationId' separator=','>#{classificationId}</foreach>) "
          + "</if>"
          + "<if test='excludedClassificationIds != null'>"
          + "AND T.CLASSIFICATION_ID NOT IN (<foreach collection='excludedClassificationIds' item='excludedClassificationId' separator=','>#{excludedClassificationId}</foreach>) "
          + "</if>"
          + "<if test='customAttributeFilter != null'>"
          + "AND (<foreach collection='customAttributeFilter.keys' item='key' separator=' AND '>(T.${key} = '${customAttributeFilter.get(key)}')</foreach>) "
          + "</if>"
          + "<if test=\"combinedClassificationFilter != null\">"
          + "AND <foreach collection='combinedClassificationFilter' item='item' separator='OR'> "
          + "T.CLASSIFICATION_ID = #{item.taskClassificationId}"
          + "<if test=\"item.attachmentClassificationId != null\">"
          + "AND A.CLASSIFICATION_ID = #{item.attachmentClassificationId}"
          + "</if>"
          + "</foreach>"
          + "</if>"
          + "AND T.${timestamp} IS NOT NULL "
          + "</where>"
          + ") AS B "
          + "GROUP BY B.WORKBASKET_KEY, B.AGE_IN_DAYS"
          + "</script>")
  @Result(column = "WORKBASKET_KEY", property = "key")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<MonitorQueryItem> getTaskCountOfWorkbaskets(
      @Param("now") Instant now,
      @Param("workbasketIds") List<String> workbasketIds,
      @Param("states") List<TaskState> states,
      @Param("classificationCategories") List<String> classificationCategories,
      @Param("domains") List<String> domains,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("classificationIds") List<String> classificationIds,
      @Param("excludedClassificationIds") List<String> excludedClassificationIds,
      @Param("customAttributeFilter") Map<TaskCustomField, String> customAttributeFilter,
      @Param("combinedClassificationFilter")
          List<CombinedClassificationFilter> combinedClassificationFilter);

  @Select(
      "<script>"
          + "SELECT B.CLASSIFICATION_CATEGORY, B.AGE_IN_DAYS, COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
          + "<if test=\"_databaseId == 'db2'\">SELECT CLASSIFICATION_CATEGORY, (DAYS(${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'h2'\">SELECT CLASSIFICATION_CATEGORY, DATEDIFF('DAY', #{now}, ${timestamp}) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'postgres'\">SELECT CLASSIFICATION_CATEGORY, DATE_PART('DAY', ${timestamp} - #{now}) as AGE_IN_DAYS </if> "
          + "FROM TASK "
          + "<where>"
          + "<if test=\"workbasketIds != null\">"
          + "WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
          + "</if>"
          + "<if test=\"states != null\">"
          + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
          + "</if>"
          + "<if test=\"classificationCategories != null\">"
          + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='classificationCategories' item='category' separator=','>#{category}</foreach>) "
          + "</if>"
          + "<if test=\"domains != null\">"
          + "AND DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
          + "</if>"
          + "<if test='classificationIds != null'>"
          + "AND CLASSIFICATION_ID IN (<foreach collection='classificationIds' item='classificationId' separator=','>#{classificationId}</foreach>) "
          + "</if>"
          + "<if test='excludedClassificationIds != null'>"
          + "AND CLASSIFICATION_ID NOT IN (<foreach collection='excludedClassificationIds' item='excludedClassificationId' separator=','>#{excludedClassificationId}</foreach>) "
          + "</if>"
          + "<if test='customAttributeFilter != null'>"
          + "AND (<foreach collection='customAttributeFilter.keys' item='key' separator=' AND '>(${key} = '${customAttributeFilter.get(key)}')</foreach>) "
          + "</if>"
          + "AND ${timestamp} IS NOT NULL "
          + "</where>"
          + ") AS B "
          + "GROUP BY B.CLASSIFICATION_CATEGORY, B.AGE_IN_DAYS "
          + "</script>")
  @Result(column = "CLASSIFICATION_CATEGORY", property = "key")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<MonitorQueryItem> getTaskCountOfCategories(
      @Param("now") Instant now,
      @Param("workbasketIds") List<String> workbasketIds,
      @Param("states") List<TaskState> states,
      @Param("classificationCategories") List<String> classificationCategories,
      @Param("domains") List<String> domains,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("classificationIds") List<String> classificationIds,
      @Param("excludedClassificationIds") List<String> excludedClassificationIds,
      @Param("customAttributeFilter") Map<TaskCustomField, String> customAttributeFilter);

  @Select(
      "<script>"
          + "SELECT B.CLASSIFICATION_KEY, B.AGE_IN_DAYS, COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
          + "<if test=\"_databaseId == 'db2'\">SELECT CLASSIFICATION_KEY, (DAYS(${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'h2'\">SELECT CLASSIFICATION_KEY, DATEDIFF('DAY', #{now}, ${timestamp}) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'postgres'\">SELECT CLASSIFICATION_KEY, DATE_PART('DAY', ${timestamp} - #{now}) as AGE_IN_DAYS </if> "
          + "FROM TASK "
          + "<where>"
          + "<if test=\"workbasketIds != null\">"
          + "WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
          + "</if>"
          + "<if test=\"states != null\">"
          + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
          + "</if>"
          + "<if test=\"classificationCategories != null\">"
          + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='classificationCategories' item='category' separator=','>#{category}</foreach>) "
          + "</if>"
          + "<if test=\"domains != null\">"
          + "AND DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
          + "</if>"
          + "<if test='classificationIds != null'>"
          + "AND CLASSIFICATION_ID IN (<foreach collection='classificationIds' item='classificationId' separator=','>#{classificationId}</foreach>) "
          + "</if>"
          + "<if test='excludedClassificationIds != null'>"
          + "AND CLASSIFICATION_ID NOT IN (<foreach collection='excludedClassificationIds' item='excludedClassificationId' separator=','>#{excludedClassificationId}</foreach>) "
          + "</if>"
          + "<if test='customAttributeFilter != null'>"
          + "AND (<foreach collection='customAttributeFilter.keys' item='key' separator=' AND '>(${key} = '${customAttributeFilter.get(key)}')</foreach>) "
          + "</if>"
          + "AND ${timestamp} IS NOT NULL "
          + "</where>"
          + ") AS B "
          + "GROUP BY B.CLASSIFICATION_KEY, B.AGE_IN_DAYS "
          + "</script>")
  @Result(column = "CLASSIFICATION_KEY", property = "key")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<MonitorQueryItem> getTaskCountOfClassifications(
      @Param("now") Instant now,
      @Param("workbasketIds") List<String> workbasketIds,
      @Param("states") List<TaskState> states,
      @Param("classificationCategories") List<String> classificationCategories,
      @Param("domains") List<String> domains,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("classificationIds") List<String> classificationIds,
      @Param("excludedClassificationIds") List<String> excludedClassificationIds,
      @Param("customAttributeFilter") Map<TaskCustomField, String> customAttributeFilter);

  @Select(
      "<script>"
          + "SELECT B.TASK_CLASSIFICATION_KEY, B.ATTACHMENT_CLASSIFICATION_KEY, B.AGE_IN_DAYS, COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
          + "<if test=\"_databaseId == 'db2'\">SELECT T.CLASSIFICATION_KEY as TASK_CLASSIFICATION_KEY, A.CLASSIFICATION_KEY as ATTACHMENT_CLASSIFICATION_KEY, (DAYS(T.${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'h2'\">SELECT T.CLASSIFICATION_KEY as TASK_CLASSIFICATION_KEY, A.CLASSIFICATION_KEY as ATTACHMENT_CLASSIFICATION_KEY, DATEDIFF('DAY', #{now}, T.${timestamp}) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'postgres'\">SELECT T.CLASSIFICATION_KEY as TASK_CLASSIFICATION_KEY, A.CLASSIFICATION_KEY as ATTACHMENT_CLASSIFICATION_KEY, DATE_PART('DAY', T.${timestamp} - #{now}) as AGE_IN_DAYS </if> "
          + "FROM TASK AS T LEFT JOIN ATTACHMENT AS A ON T.ID = A.TASK_ID "
          + "<where>"
          + "<if test=\"workbasketIds != null\">"
          + "T.WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
          + "</if>"
          + "<if test=\"states != null\">"
          + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
          + "</if>"
          + "<if test=\"classificationCategories != null\">"
          + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='classificationCategories' item='category' separator=','>#{category}</foreach>) "
          + "</if>"
          + "<if test=\"domains != null\">"
          + "AND DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
          + "</if>"
          + "<if test='classificationIds != null'>"
          + "AND T.CLASSIFICATION_ID IN (<foreach collection='classificationIds' item='classificationId' separator=','>#{classificationId}</foreach>) "
          + "</if>"
          + "<if test='excludedClassificationIds != null'>"
          + "AND T.CLASSIFICATION_ID NOT IN (<foreach collection='excludedClassificationIds' item='excludedClassificationId' separator=','>#{excludedClassificationId}</foreach>) "
          + "</if>"
          + "<if test='customAttributeFilter != null'>"
          + "AND (<foreach collection='customAttributeFilter.keys' item='key' separator=' AND '>(T.${key} = '${customAttributeFilter.get(key)}')</foreach>) "
          + "</if>"
          + "AND T.${timestamp} IS NOT NULL "
          + "</where>"
          + ") AS B "
          + "GROUP BY B.TASK_CLASSIFICATION_KEY, B.ATTACHMENT_CLASSIFICATION_KEY, B.AGE_IN_DAYS "
          + "</script>")
  @Result(column = "TASK_CLASSIFICATION_KEY", property = "key")
  @Result(column = "ATTACHMENT_CLASSIFICATION_KEY", property = "attachmentKey")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<DetailedMonitorQueryItem> getTaskCountOfDetailedClassifications(
      @Param("now") Instant now,
      @Param("workbasketIds") List<String> workbasketIds,
      @Param("states") List<TaskState> states,
      @Param("classificationCategories") List<String> classificationCategories,
      @Param("domains") List<String> domains,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("classificationIds") List<String> classificationIds,
      @Param("excludedClassificationIds") List<String> excludedClassificationIds,
      @Param("customAttributeFilter") Map<TaskCustomField, String> customAttributeFilter);

  @Select(
      "<script>"
          + "SELECT B.CUSTOM_FIELD, B.AGE_IN_DAYS, COUNT(B.AGE_IN_DAYS) AS NUMBER_OF_TASKS FROM ("
          + "<if test=\"_databaseId == 'db2'\">SELECT ${customField} as CUSTOM_FIELD, (DAYS(${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'h2'\">SELECT ${customField} as CUSTOM_FIELD, DATEDIFF('DAY', #{now}, ${timestamp}) as AGE_IN_DAYS </if> "
          + "<if test=\"_databaseId == 'postgres'\">SELECT ${customField} as CUSTOM_FIELD, DATE_PART('DAY', ${timestamp} - #{now}) as AGE_IN_DAYS </if> "
          + "FROM TASK "
          + "<where>"
          + "<if test=\"workbasketIds != null\">"
          + "WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
          + "</if>"
          + "<if test=\"states != null\">"
          + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
          + "</if>"
          + "<if test=\"classificationCategories != null\">"
          + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='classificationCategories' item='category' separator=','>#{category}</foreach>) "
          + "</if>"
          + "<if test=\"domains != null\">"
          + "AND DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
          + "</if>"
          + "<if test='classificationIds != null'>"
          + "AND CLASSIFICATION_ID IN (<foreach collection='classificationIds' item='classificationId' separator=','>#{classificationId}</foreach>) "
          + "</if>"
          + "<if test='excludedClassificationIds != null'>"
          + "AND CLASSIFICATION_ID NOT IN (<foreach collection='excludedClassificationIds' item='excludedClassificationId' separator=','>#{excludedClassificationId}</foreach>) "
          + "</if>"
          + "<if test='customAttributeFilter != null'>"
          + "AND (<foreach collection='customAttributeFilter.keys' item='key' separator=' AND '>(${key} = '${customAttributeFilter.get(key)}')</foreach>) "
          + "</if>"
          + "AND ${timestamp} IS NOT NULL "
          + "</where>"
          + ") AS B "
          + "GROUP BY B.CUSTOM_FIELD, B.AGE_IN_DAYS "
          + "</script>")
  @Result(column = "CUSTOM_FIELD", property = "key")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")
  List<MonitorQueryItem> getTaskCountOfTaskCustomFieldValues(
      @Param("now") Instant now,
      @Param("customField") TaskCustomField taskCustomField,
      @Param("workbasketIds") List<String> workbasketIds,
      @Param("states") List<TaskState> states,
      @Param("classificationCategories") List<String> classificationCategories,
      @Param("domains") List<String> domains,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("classificationIds") List<String> classificationIds,
      @Param("excludedClassificationIds") List<String> excludedClassificationIds,
      @Param("customAttributeFilter") Map<TaskCustomField, String> customAttributeFilter);

  @Select(
      "<script>"
          + "SELECT T.ID FROM TASK T "
          + "<if test=\"joinWithAttachments\">"
          + "LEFT JOIN ATTACHMENT A ON T.ID = A.TASK_ID "
          + "</if>"
          + "<where>"
          + "<if test=\"workbasketIds != null\">"
          + "T.WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
          + "</if>"
          + "<if test=\"states != null\">"
          + "AND T.STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
          + "</if>"
          + "<if test=\"classificationCategories != null\">"
          + "AND T.CLASSIFICATION_CATEGORY IN (<foreach collection='classificationCategories' item='category' separator=','>#{category}</foreach>) "
          + "</if>"
          + "<if test=\"domains != null\">"
          + "AND DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
          + "</if>"
          + "<if test='classificationIds != null'>"
          + "AND T.CLASSIFICATION_ID IN (<foreach collection='classificationIds' item='classificationId' separator=','>#{classificationId}</foreach>) "
          + "</if>"
          + "<if test='excludedClassificationIds != null'>"
          + "AND T.CLASSIFICATION_ID NOT IN (<foreach collection='excludedClassificationIds' item='excludedClassificationId' separator=','>#{excludedClassificationId}</foreach>) "
          + "</if>"
          + "<if test='customAttributeFilter != null'>"
          + "AND (<foreach collection='customAttributeFilter.keys' item='key' separator=' AND '>(${key} = '${customAttributeFilter.get(key)}')</foreach>) "
          + "</if>"
          + "<if test=\"combinedClassificationFilter != null\">"
          + "AND <foreach collection='combinedClassificationFilter' item='item' separator='OR'> "
          + "T.CLASSIFICATION_ID = #{item.taskClassificationId} "
          + "<if test=\"item.attachmentClassificationId != null\">"
          + "AND A.CLASSIFICATION_ID = #{item.attachmentClassificationId} "
          + "</if>"
          + "</foreach>"
          + "</if>"
          + "AND T.${timestamp} IS NOT NULL AND ( "
          + "<foreach collection='selectedItems' item='selectedItem' separator=' OR '>"
          + "#{selectedItem.key} = T.${groupedBy} AND "
          + "<if test=\"joinWithAttachments and combinedClassificationFilter == null\">"
          + "<if test='selectedItem.subKey != null'>"
          + "A.CLASSIFICATION_KEY = #{selectedItem.subKey} AND "
          + "</if>"
          + "</if>"
          + "<if test=\"_databaseId == 'db2'\">"
          + "#{selectedItem.upperAgeLimit} >= (DAYS(${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) AND "
          + "#{selectedItem.lowerAgeLimit} &lt;= (DAYS(${timestamp}) - DAYS(CAST(#{now} as TIMESTAMP))) "
          + "</if> "
          + "<if test=\"_databaseId == 'h2'\">"
          + "#{selectedItem.upperAgeLimit} >= DATEDIFF('DAY', #{now}, ${timestamp}) AND "
          + "#{selectedItem.lowerAgeLimit} &lt;= DATEDIFF('DAY', #{now}, ${timestamp}) "
          + "</if> "
          + "<if test=\"_databaseId == 'postgres'\">"
          + "#{selectedItem.upperAgeLimit} >= DATE_PART('day', ${timestamp} - #{now} ) AND "
          + "#{selectedItem.lowerAgeLimit} &lt;= DATE_PART('day', ${timestamp} - #{now} ) "
          + "</if> "
          + "</foreach>) "
          + "</where>"
          + "<if test=\"_databaseId == 'db2'\">with UR </if> "
          + "</script>")
  List<String> getTaskIdsForSelectedItems(
      @Param("now") Instant now,
      @Param("workbasketIds") List<String> workbasketIds,
      @Param("states") List<TaskState> states,
      @Param("classificationCategories") List<String> classificationCategories,
      @Param("domains") List<String> domains,
      @Param("classificationIds") List<String> classificationIds,
      @Param("excludedClassificationIds") List<String> excludedClassificationIds,
      @Param("customAttributeFilter") Map<TaskCustomField, String> customAttributeFilter,
      @Param("combinedClassificationFilter")
          List<CombinedClassificationFilter> combinedClassificationFilter,
      @Param("groupedBy") String groupedBy,
      @Param("timestamp") TaskTimestamp timestamp,
      @Param("selectedItems") List<SelectedItem> selectedItems,
      @Param("joinWithAttachments") boolean joinWithAttachments);

  @Select(
      "<script>"
          + "SELECT WORKBASKET_KEY, STATE, COUNT(STATE) as COUNT "
          + "FROM TASK "
          + "<where>"
          + "<if test='domains != null'>"
          + "DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
          + "</if>"
          + "<if test='states != null'>"
          + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
          + "</if>"
          + "<if test='workbasketIds != null'>"
          + "AND WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
          + "</if>"
          + "<if test='priorityMinimum != null'>"
          + "AND priority >= #{priorityMinimum} "
          + "</if>"
          + "</where>"
          + "GROUP BY WORKBASKET_KEY, STATE"
          + "</script>")
  @Result(column = "WORKBASKET_KEY", property = "workbasketKey")
  @Result(column = "STATE", property = "state")
  @Result(column = "COUNT", property = "count")
  List<TaskQueryItem> getTasksCountByState(
      @Param("domains") List<String> domains,
      @Param("states") List<TaskState> states,
      @Param("workbasketIds") List<String> workbasketIds,
      @Param("priorityMinimum") Integer priorityMinimum);

  @Select(
      "<script>"
          + "SELECT DISTINCT ${customField} "
          + "FROM TASK T"
          + "<if test=\"combinedClassificationFilter != null\">"
          + "LEFT JOIN ATTACHMENT A ON T.ID = A.TASK_ID "
          + "</if>"
          + "<where>"
          + "<if test='workbasketIds != null'>"
          + "T.WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
          + "</if>"
          + "<if test='states != null'>"
          + "AND T.STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
          + "</if>"
          + "<if test='classificationCategories != null'>"
          + "AND T.CLASSIFICATION_CATEGORY IN (<foreach collection='classificationCategories' item='category' separator=','>#{category}</foreach>) "
          + "</if>"
          + "<if test='domains != null'>"
          + "AND T.DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
          + "</if>"
          + "<if test='classificationIds != null'>"
          + "AND T.CLASSIFICATION_ID IN (<foreach collection='classificationIds' item='classificationId' separator=','>#{classificationId}</foreach>) "
          + "</if>"
          + "<if test='excludedClassificationIds != null'>"
          + "AND T.CLASSIFICATION_ID NOT IN (<foreach collection='excludedClassificationIds' item='excludedClassificationId' separator=','>#{excludedClassificationId}</foreach>) "
          + "</if>"
          + "<if test='customAttributeFilter != null'>"
          + "AND (<foreach collection='customAttributeFilter.keys' item='key' separator=' AND '>(T.${key} = '${customAttributeFilter.get(key)}')</foreach>) "
          + "</if>"
          + "<if test=\"combinedClassificationFilter != null\">"
          + "AND <foreach collection='combinedClassificationFilter' item='item' separator='OR'> "
          + "T.CLASSIFICATION_ID = #{item.taskClassificationId} "
          + "<if test=\"item.attachmentClassificationId != null\">"
          + "AND A.CLASSIFICATION_ID = #{item.attachmentClassificationId} "
          + "</if>"
          + "</foreach>"
          + "</if>"
          + "</where>"
          + "</script>")
  List<String> getCustomAttributeValuesForReport(
      @Param("workbasketIds") List<String> workbasketIds,
      @Param("states") List<TaskState> states,
      @Param("classificationCategories") List<String> classificationCategories,
      @Param("domains") List<String> domains,
      @Param("classificationIds") List<String> classificationIds,
      @Param("excludedClassificationIds") List<String> excludedClassificationIds,
      @Param("customAttributeFilter") Map<TaskCustomField, String> customAttributeFilter,
      @Param("combinedClassificationFilter")
          List<CombinedClassificationFilter> combinedClassificationFilter,
      @Param("customField") TaskCustomField taskCustomField);

  @Select(
      "<script>"
          + "SELECT A.AGE_IN_DAYS, A.ORG_LEVEL_1, A.ORG_LEVEL_2, A.ORG_LEVEL_3, A.ORG_LEVEL_4, "
          + "'${status}' AS STATUS, COUNT(A.AGE_IN_DAYS) AS COUNT FROM ("
          // This subquery prevents the repetition of the AGE_IN_DAYS column calculation
          // (like everywhere else in the Mappers...)in the group by clause.
          // DB2 is not able to reuse computed columns in the group by statement. Even if this adds
          // a little
          // overhead / complexity. It's worth the trade-off of not computing the AGE_IN_DAYS column
          // twice.
          + "SELECT W.ORG_LEVEL_1, W.ORG_LEVEL_2, W.ORG_LEVEL_3, W.ORG_LEVEL_4, "
          + "<if test=\"_databaseId == 'db2'\">(DAYS(T.${status}) - DAYS(CAST(#{now} as TIMESTAMP)))</if>"
          + "<if test=\"_databaseId == 'h2'\">DATEDIFF('DAY', #{now}, T.${status})</if>"
          + "<if test=\"_databaseId == 'postgres'\">DATE_PART('DAY', T.${status} - #{now})</if>"
          + " as AGE_IN_DAYS "
          + "FROM TASK AS T INNER JOIN WORKBASKET AS W ON T.WORKBASKET_KEY=W.KEY "
          + "<where>"
          + "<if test=\"status.name() == 'COMPLETED'\">"
          + "T.COMPLETED IS NOT NULL "
          + "</if>"
          + "<if test='classificationCategories != null'>"
          + "AND T.CLASSIFICATION_CATEGORY IN (<foreach collection='classificationCategories' item='category' separator=','>#{category}</foreach>) "
          + "</if>"
          + "<if test='classificationIds != null'>"
          + "AND T.CLASSIFICATION_ID IN (<foreach collection='classificationIds' item='classificationId' separator=','>#{classificationId}</foreach>) "
          + "</if>"
          + "<if test='excludedClassificationIds != null'>"
          + "AND T.CLASSIFICATION_ID NOT IN (<foreach collection='excludedClassificationIds' item='excludedClassificationId' separator=','>#{excludedClassificationId}</foreach>) "
          + "</if>"
          + "<if test='domains != null'>"
          + "AND T.DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
          + "</if>"
          + "<if test='customAttributeFilter != null'>"
          + "AND (<foreach collection='customAttributeFilter.keys' item='key' separator=' AND '>(T.${key} = '${customAttributeFilter.get(key)}')</foreach>) "
          + "</if>"
          + "</where>"
          + ") AS A "
          + "GROUP BY A.AGE_IN_DAYS, A.ORG_LEVEL_1, A.ORG_LEVEL_2, A.ORG_LEVEL_3, A.ORG_LEVEL_4 "
          + "</script>")
  @Result(column = "STATUS", property = "status")
  @Result(column = "AGE_IN_DAYS", property = "ageInDays")
  @Result(column = "COUNT", property = "count")
  @Result(column = "ORG_LEVEL_1", property = "orgLevel1")
  @Result(column = "ORG_LEVEL_2", property = "orgLevel2")
  @Result(column = "ORG_LEVEL_3", property = "orgLevel3")
  @Result(column = "ORG_LEVEL_4", property = "orgLevel4")
  List<TimestampQueryItem> getTasksCountForStatusGroupedByOrgLevel(
      @Param("now") Instant now,
      @Param("status") TaskTimestamp status,
      @Param("classificationCategories") List<String> classificationCategories,
      @Param("classificationIds") List<String> classificationIds,
      @Param("excludedClassificationIds") List<String> excludedClassificationIds,
      @Param("domains") List<String> domains,
      @Param("customAttributeFilter") Map<TaskCustomField, String> customAttributeFilter);
}
