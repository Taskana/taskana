package pro.taskana.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import pro.taskana.Workbasket;
import pro.taskana.impl.CustomField;
import pro.taskana.impl.DetailedMonitorQueryItem;
import pro.taskana.impl.MonitorQueryItem;
import pro.taskana.impl.TaskState;

/**
 * This class is the mybatis mapping of task monitoring.
 */
public interface TaskMonitorMapper {

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT WORKBASKET_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT WORKBASKET_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK "
        + "WHERE WORKBASKET_KEY IN (<foreach collection='workbaskets' item='workbasket' separator=','>#{workbasket.key}</foreach>) "
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
        + "AND DUE IS NOT NULL "
        + "<if test=\"_databaseId == 'db2'\">GROUP BY WORKBASKET_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY WORKBASKET_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE)</if> "
        + "</script>")
    @Results({
        @Result(column = "WORKBASKET_KEY", property = "key"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks") })
    List<MonitorQueryItem> getTaskCountOfWorkbasketsByWorkbasketsAndStates(
        @Param("workbaskets") List<Workbasket> workbaskets,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories);

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT CLASSIFICATION_CATEGORY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT CLASSIFICATION_CATEGORY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK "
        + "WHERE WORKBASKET_KEY IN (<foreach collection='workbaskets' item='workbasket' separator=','>#{workbasket.key}</foreach>) "
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
        + "AND DUE IS NOT NULL "
        + "<if test=\"_databaseId == 'db2'\">GROUP BY CLASSIFICATION_CATEGORY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY CLASSIFICATION_CATEGORY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE)</if> "
        + "</script>")
    @Results({
        @Result(column = "CLASSIFICATION_CATEGORY", property = "key"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks") })
    List<MonitorQueryItem> getTaskCountOfCategoriesByWorkbasketsAndStates(
        @Param("workbaskets") List<Workbasket> workbaskets,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories);

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT CLASSIFICATION_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT CLASSIFICATION_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK "
        + "WHERE WORKBASKET_KEY IN (<foreach collection='workbaskets' item='workbasket' separator=','>#{workbasket.key}</foreach>) "
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
        + "AND DUE IS NOT NULL "
        + "<if test=\"_databaseId == 'db2'\">GROUP BY CLASSIFICATION_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY CLASSIFICATION_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE)</if> "
        + "</script>")
    @Results({
        @Result(column = "CLASSIFICATION_KEY", property = "key"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks") })
    List<MonitorQueryItem> getTaskCountOfClassificationsByWorkbasketsAndStates(
        @Param("workbaskets") List<Workbasket> workbaskets,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories);

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT T.CLASSIFICATION_KEY as TASK_CLASSIFICATION_KEY, A.CLASSIFICATION_KEY as ATTACHMENT_CLASSIFICATION_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT T.CLASSIFICATION_KEY as TASK_CLASSIFICATION_KEY, A.CLASSIFICATION_KEY as ATTACHMENT_CLASSIFICATION_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK AS T LEFT JOIN ATTACHMENT AS A ON T.ID = A.TASK_ID "
        + "WHERE T.WORKBASKET_KEY IN (<foreach collection='workbaskets' item='workbasket' separator=','>#{workbasket.key}</foreach>) "
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
        + "AND DUE IS NOT NULL "
        + "<if test=\"_databaseId == 'db2'\">GROUP BY T.CLASSIFICATION_KEY, A.CLASSIFICATION_KEY, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY T.CLASSIFICATION_KEY, A.CLASSIFICATION_KEY, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE)</if> "
        + "</script>")
    @Results({
        @Result(column = "TASK_CLASSIFICATION_KEY", property = "key"),
        @Result(column = "ATTACHMENT_CLASSIFICATION_KEY", property = "attachmentKey"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks") })
    List<DetailedMonitorQueryItem> getTaskCountOfDetailedClassificationsByWorkbasketsAndStates(
        @Param("workbaskets") List<Workbasket> workbaskets,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories);

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT ${customField} as CUSTOM_FIELD, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT ${customField} as CUSTOM_FIELD, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK "
        + "WHERE WORKBASKET_KEY IN (<foreach collection='workbaskets' item='workbasket' separator=','>#{workbasket.key}</foreach>) "
        + "AND STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "AND CLASSIFICATION_CATEGORY IN (<foreach collection='categories' item='category' separator=','>#{category}</foreach>) "
        + "AND DUE IS NOT NULL "
        + "AND ${customField} IS NOT NULL "
        + "<if test=\"_databaseId == 'db2'\">GROUP BY ${customField}, (DAYS(DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY ${customField}, DATEDIFF('DAY', CURRENT_TIMESTAMP, DUE)</if> "
        + "</script>")
    @Results({
        @Result(column = "CUSTOM_FIELD", property = "key"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks") })
    List<MonitorQueryItem> getTaskCountOfCustomFieldValuesByWorkbasketsAndStatesAndCustomField(
        @Param("workbaskets") List<Workbasket> workbaskets,
        @Param("states") List<TaskState> states,
        @Param("categories") List<String> categories,
        @Param("customField") CustomField customField);

}
