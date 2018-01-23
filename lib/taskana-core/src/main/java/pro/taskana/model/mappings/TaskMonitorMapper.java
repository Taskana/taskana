package pro.taskana.model.mappings;

import java.time.Instant;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import pro.taskana.Workbasket;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.MonitorQueryItem;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskStateCounter;

/**
 * This class is the mybatis mapping of task monitoring.
 */
public interface TaskMonitorMapper {

    @Select("<script>"
        + "SELECT STATE, COUNT (STATE) as counter "
        + "FROM TASK "
        + "WHERE STATE IN (<foreach collection='status' item='state' separator=','>#{state}</foreach>) "
        + "GROUP BY STATE"
        + "</script>")
    @Results({ @Result(column = "STATE", property = "state"), @Result(column = "counter", property = "counter") })
    List<TaskStateCounter> getTaskCountForState(@Param("status") List<TaskState> status);

    @Select("<script>"
        + "SELECT COUNT (*) "
        + "FROM TASK "
        + "WHERE WORKBASKETID = #{workbasketId} "
        + "AND DUE >= #{fromDate} "
        + "AND STATE IN (<foreach collection='status' item='state' separator=','>#{state}</foreach>)"
        + "</script>")
    long getTaskCountForWorkbasketByDaysInPastAndState(@Param("workbasketId") String workbasketId,
        @Param("fromDate") Instant fromDate, @Param("status") List<TaskState> states);

    @Select("<script>"
        + "SELECT CAST(DUE AS DATE) as DUE_DATE, WORKBASKETID, COUNT (*) as counter "
        + "FROM TASK "
        + "WHERE DUE >= #{fromDate} "
        + "AND STATE IN (<foreach collection='status' item='state' separator=','>#{state}</foreach>) "
        + "GROUP BY DUE_DATE, WORKBASKETID"
        + "</script>")
    @Results({ @Result(column = "DUE_DATE", property = "due"),
        @Result(column = "WORKBASKETID", property = "workbasketId"),
        @Result(column = "counter", property = "taskCounter") })
    List<DueWorkbasketCounter> getTaskCountByWorkbasketIdAndDaysInPastAndState(@Param("fromDate") Instant fromDate,
        @Param("status") List<TaskState> states);

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
    List<MonitorQueryItem> findByWorkbasketIdsAndStates(
        @Param("workbaskets") List<Workbasket> workbaskets,
        @Param("states") List<TaskState> states);

}
