package pro.taskana.mappings;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.impl.SelectedItem;
import pro.taskana.impl.report.impl.CombinedClassificationFilter;
import pro.taskana.impl.report.impl.DetailedMonitorQueryItem;
import pro.taskana.impl.report.impl.MonitorQueryItem;
import pro.taskana.impl.report.impl.TaskQueryItem;

/**
 * This class is the mybatis mapping of task monitoring.
 */
public interface TaskMonitorMapper {

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT T.WORKBASKET_KEY, (DAYS(T.DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT T.WORKBASKET_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, T.DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'postgres'\">SELECT T.WORKBASKET_KEY, DATE_PART('DAY', T.DUE - CURRENT_TIMESTAMP) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK AS T LEFT JOIN ATTACHMENT AS A ON T.ID = A.TASK_ID "
        + "<where>"
        + "<if test=\"workbasketIds != null\">"
        + "T.WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
        + "</if>"
        + "<if test=\"states != null\">"
        + "AND T.STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "</if>"
        + "<if test=\"categories != null\">"
        + "AND T.CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
        + "</if>"
        + "<if test=\"domains != null\">"
        + "AND T.DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
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
        + "<if test=\"combinedClassificationFilter != null\">"
        + "AND <foreach collection='combinedClassificationFilter' item='item' separator='OR'> "
        + "T.CLASSIFICATION_ID = #{item.taskClassificationId}"
        + "<if test=\"item.attachmentClassificationId != null\">"
        + "AND A.CLASSIFICATION_ID = #{item.attachmentClassificationId}"
        + "</if>"
        + "</foreach>"
        + "</if>"
        + "AND T.DUE IS NOT NULL "
        + "</where>"
        + "<if test=\"_databaseId == 'db2'\">GROUP BY T.WORKBASKET_KEY, (DAYS(T.DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY T.WORKBASKET_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, T.DUE)</if> "
        + "<if test=\"_databaseId == 'postgres'\">GROUP BY T.WORKBASKET_KEY, DATE_PART('DAY', T.DUE - CURRENT_TIMESTAMP)</if> "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results({
        @Result(column = "WORKBASKET_KEY", property = "key"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")})
    List<MonitorQueryItem> getTaskCountOfWorkbaskets(@Param("workbasketIds") List<String> workbasketIds,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories,
        @Param("domains") List<String> domains,
        @Param("classificationIds") List<String> classificationIds,
        @Param("excludedClassificationIds") List<String> excludedClassificationIds,
        @Param("customAttributeFilter") Map<CustomField, String> customAttributeFilter,
        @Param("combinedClassificationFilter") List<CombinedClassificationFilter> combinedClassificationFilter);

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT CLASSIFICATION_CATEGORY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT CLASSIFICATION_CATEGORY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'postgres'\">SELECT CLASSIFICATION_CATEGORY, DATE_PART('DAY', DUE - CURRENT_TIMESTAMP) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK "
        + "<where>"
        + "<if test=\"workbasketIds != null\">"
        + "WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
        + "</if>"
        + "<if test=\"states != null\">"
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "</if>"
        + "<if test=\"categories != null\">"
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
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
        + "AND DUE IS NOT NULL "
        + "</where>"
        + "<if test=\"_databaseId == 'db2'\">GROUP BY CLASSIFICATION_CATEGORY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY CLASSIFICATION_CATEGORY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE)</if> "
        + "<if test=\"_databaseId == 'postgres'\">GROUP BY CLASSIFICATION_CATEGORY, DATE_PART('DAY', DUE - CURRENT_TIMESTAMP)</if> "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results({
        @Result(column = "CLASSIFICATION_CATEGORY", property = "key"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")})
    List<MonitorQueryItem> getTaskCountOfCategories(@Param("workbasketIds") List<String> workbasketIds,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories,
        @Param("domains") List<String> domains,
        @Param("classificationIds") List<String> classificationIds,
        @Param("excludedClassificationIds") List<String> excludedClassificationIds,
        @Param("customAttributeFilter") Map<CustomField, String> customAttributeFilter);

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT CLASSIFICATION_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT CLASSIFICATION_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'postgres'\">SELECT CLASSIFICATION_KEY, DATE_PART('DAY', DUE - CURRENT_TIMESTAMP) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK "
        + "<where>"
        + "<if test=\"workbasketIds != null\">"
        + "WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
        + "</if>"
        + "<if test=\"states != null\">"
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "</if>"
        + "<if test=\"categories != null\">"
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
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
        + "AND DUE IS NOT NULL "
        + "</where>"
        + "<if test=\"_databaseId == 'db2'\">GROUP BY CLASSIFICATION_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY CLASSIFICATION_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE)</if> "
        + "<if test=\"_databaseId == 'postgres'\">GROUP BY CLASSIFICATION_KEY, DATE_PART('DAY', DUE - CURRENT_TIMESTAMP)</if> "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results({
        @Result(column = "CLASSIFICATION_KEY", property = "key"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")})
    List<MonitorQueryItem> getTaskCountOfClassifications(@Param("workbasketIds") List<String> workbasketIds,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories,
        @Param("domains") List<String> domains,
        @Param("classificationIds") List<String> classificationIds,
        @Param("excludedClassificationIds") List<String> excludedClassificationIds,
        @Param("customAttributeFilter") Map<CustomField, String> customAttributeFilter);

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT T.CLASSIFICATION_KEY as TASK_CLASSIFICATION_KEY, A.CLASSIFICATION_KEY as ATTACHMENT_CLASSIFICATION_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT T.CLASSIFICATION_KEY as TASK_CLASSIFICATION_KEY, A.CLASSIFICATION_KEY as ATTACHMENT_CLASSIFICATION_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'postgres'\">SELECT T.CLASSIFICATION_KEY as TASK_CLASSIFICATION_KEY, A.CLASSIFICATION_KEY as ATTACHMENT_CLASSIFICATION_KEY, DATE_PART('DAY', DUE - CURRENT_TIMESTAMP) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK AS T LEFT JOIN ATTACHMENT AS A ON T.ID = A.TASK_ID "
        + "<where>"
        + "<if test=\"workbasketIds != null\">"
        + "T.WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
        + "</if>"
        + "<if test=\"states != null\">"
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "</if>"
        + "<if test=\"categories != null\">"
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
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
        + "AND DUE IS NOT NULL "
        + "</where>"
        + "<if test=\"_databaseId == 'db2'\">GROUP BY T.CLASSIFICATION_KEY, A.CLASSIFICATION_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY T.CLASSIFICATION_KEY, A.CLASSIFICATION_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE)</if> "
        + "<if test=\"_databaseId == 'postgres'\">GROUP BY T.CLASSIFICATION_KEY, A.CLASSIFICATION_KEY, DATE_PART('DAY', DUE - CURRENT_TIMESTAMP)</if> "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results({
        @Result(column = "TASK_CLASSIFICATION_KEY", property = "key"),
        @Result(column = "ATTACHMENT_CLASSIFICATION_KEY", property = "attachmentKey"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")})
    List<DetailedMonitorQueryItem> getTaskCountOfDetailedClassifications(
        @Param("workbasketIds") List<String> workbasketIds,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories,
        @Param("domains") List<String> domains,
        @Param("classificationIds") List<String> classificationIds,
        @Param("excludedClassificationIds") List<String> excludedClassificationIds,
        @Param("customAttributeFilter") Map<CustomField, String> customAttributeFilter);

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT ${customField} as CUSTOM_FIELD, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT ${customField} as CUSTOM_FIELD, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'postgres'\">SELECT ${customField} as CUSTOM_FIELD, DATE_PART('DAY', DUE - CURRENT_TIMESTAMP) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK "
        + "<where>"
        + "<if test=\"workbasketIds != null\">"
        + "WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
        + "</if>"
        + "<if test=\"states != null\">"
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "</if>"
        + "<if test=\"categories != null\">"
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
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
        + "AND DUE IS NOT NULL "
        + "</where>"
        + "<if test=\"_databaseId == 'db2'\">GROUP BY ${customField}, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY ${customField}, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE)</if> "
        + "<if test=\"_databaseId == 'postgres'\">GROUP BY ${customField}, DATE_PART('DAY', DUE - CURRENT_TIMESTAMP)</if> "
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    @Results({
        @Result(column = "CUSTOM_FIELD", property = "key"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks")})
    List<MonitorQueryItem> getTaskCountOfCustomFieldValues(
        @Param("customField") CustomField customField,
        @Param("workbasketIds") List<String> workbasketIds,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories,
        @Param("domains") List<String> domains,
        @Param("classificationIds") List<String> classificationIds,
        @Param("excludedClassificationIds") List<String> excludedClassificationIds,
        @Param("customAttributeFilter") Map<CustomField, String> customAttributeFilter);

    @Select("<script>"
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
        + "<if test=\"categories != null\">"
        + "AND T.CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
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
        + "AND T.DUE IS NOT NULL AND ( "
        + "<foreach collection='selectedItems' item='selectedItem' separator=' OR '>"
        + "#{selectedItem.key} = T.${groupedBy} AND "
        + "<if test=\"joinWithAttachments\">"
        + "A.CLASSIFICATION_KEY = #{selectedItem.subKey} AND "
        + "</if>"
        + "<if test=\"_databaseId == 'db2'\">"
        + "#{selectedItem.upperAgeLimit} >= (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) AND "
        + "#{selectedItem.lowerAgeLimit} &lt;= (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) "
        + "</if> "
        + "<if test=\"_databaseId == 'h2'\">"
        + "#{selectedItem.upperAgeLimit} >= DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) AND "
        + "#{selectedItem.lowerAgeLimit} &lt;= DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) "
        + "</if> "
        + "<if test=\"_databaseId == 'postgres'\">"
        + "#{selectedItem.upperAgeLimit} >= DATE_PART('day', DUE - CURRENT_TIMESTAMP ) AND "
        + "#{selectedItem.lowerAgeLimit} &lt;= DATE_PART('day', DUE - CURRENT_TIMESTAMP ) "
        + "</if> "
        + "</foreach>) "
        + "</where>"
        + "<if test=\"_databaseId == 'db2'\">with UR </if> "
        + "</script>")
    List<String> getTaskIdsForSelectedItems(@Param("workbasketIds") List<String> workbasketIds,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories,
        @Param("domains") List<String> domains,
        @Param("classificationIds") List<String> classificationIds,
        @Param("excludedClassificationIds") List<String> excludedClassificationIds,
        @Param("customAttributeFilter") Map<CustomField, String> customAttributeFilter,
        @Param("groupedBy") String groupedBy, @Param("selectedItems") List<SelectedItem> selectedItems,
        @Param("joinWithAttachments") boolean joinWithAttachments);

    @Select("<script>"
        + "SELECT DOMAIN, STATE, COUNT(STATE) as COUNT "
        + "FROM TASK "
        + "<where>"
        + "<if test='domains != null'>"
        + "DOMAIN IN (<foreach collection='domains' item='domain' separator=','>#{domain}</foreach>) "
        + "</if>"
        + "<if test='states != null'>"
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "</if>"
        + "</where>"
        + "GROUP BY DOMAIN, STATE"
        + "</script>")
    @Results({
        @Result(column = "DOMAIN", property = "domain"),
        @Result(column = "STATE", property = "state"),
        @Result(column = "COUNT", property = "count"),
    })
    List<TaskQueryItem> getTasksCountByState(@Param("domains") List<String> domains,
        @Param("states") List<TaskState> states);

    @Select("<script>"
        + "SELECT DISTINCT ${customField} "
        + "FROM TASK "
        + "<where>"
        + "<if test='workbasketIds != null'>"
        + "WORKBASKET_ID IN (<foreach collection='workbasketIds' item='workbasketId' separator=','>#{workbasketId}</foreach>) "
        + "</if>"
        + "<if test='states != null'>"
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "</if>"
        + "<if test='categories != null'>"
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
        + "</if>"
        + "<if test='domains != null'>"
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
        + "</where>"
        + "</script>")
    List<String> getCustomAttributeValuesForReport(@Param("workbasketIds") List<String> workbasketIds,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories, @Param("domains") List<String> domains,
        @Param("classificationIds") List<String> classificationIds,
        @Param("excludedClassificationIds") List<String> excludedClassificationIds,
        @Param("customAttributeFilter") Map<CustomField, String> customAttributeFilter,
        @Param("customField") CustomField customField);

}
