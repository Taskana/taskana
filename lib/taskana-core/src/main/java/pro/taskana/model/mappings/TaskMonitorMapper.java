package pro.taskana.model.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import pro.taskana.Workbasket;
import pro.taskana.model.MonitorQueryItem;
import pro.taskana.model.TaskState;

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
        @Param("states") List<TaskState> states);

    @Select("<script>"
        + "<if test=\"_databaseId == 'db2'\">SELECT C.CATEGORY as CATEGORY_NAME, (DAYS(T.DUE) - DAYS(CURRENT_TIMESTAMP)) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "<if test=\"_databaseId == 'h2'\">SELECT C.CATEGORY as CATEGORY_NAME, DATEDIFF('DAY', CURRENT_TIMESTAMP, T.DUE) as AGE_IN_DAYS, COUNT(*) as NUMBER_OF_TASKS</if> "
        + "FROM TASK AS T JOIN CLASSIFICATION AS C ON T.CLASSIFICATION_KEY = C.KEY "
        + "WHERE T.WORKBASKET_KEY IN (<foreach collection='workbaskets' item='workbasket' separator=','>#{workbasket.key}</foreach>) "
        + "AND T.STATE IN (<foreach collection='states' item='state' separator=','>#{state}</foreach>) "
        + "AND T.DUE IS NOT NULL "
        + "<if test=\"_databaseId == 'db2'\">GROUP BY C.CATEGORY, (DAYS(T.DUE) - DAYS(CURRENT_TIMESTAMP))</if> "
        + "<if test=\"_databaseId == 'h2'\">GROUP BY C.CATEGORY, DATEDIFF('DAY', CURRENT_TIMESTAMP, T.DUE)</if> "
        + "</script>")
    @Results({
        @Result(column = "CATEGORY_NAME", property = "key"),
        @Result(column = "AGE_IN_DAYS", property = "ageInDays"),
        @Result(column = "NUMBER_OF_TASKS", property = "numberOfTasks") })
    List<MonitorQueryItem> getTaskCountOfCategoriesByWorkbasketsAndStates(
        @Param("workbaskets") List<Workbasket> workbaskets,
        @Param("states") List<TaskState> states);

}
