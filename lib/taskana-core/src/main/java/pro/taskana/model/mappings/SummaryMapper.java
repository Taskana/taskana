package pro.taskana.model.mappings;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import pro.taskana.model.TaskSummary;

/**
 * This interface is the myBatis mapping of short summaries.
 */
public interface SummaryMapper {

    @Select("SELECT TASK.ID AS taskId, TASK.NAME AS taskName, TASK.WORKBASKETID AS workId, TASK.CLASSIFICATION_ID AS classificationId, "
            + "WORKBASKET.NAME AS workName, CLASSIFICATION.NAME AS classificationName "
            + "FROM TASK "
            + "LEFT JOIN WORKBASKET ON WORKBASKET.ID = TASK.WORKBASKETID "
            + "LEFT JOIN CLASSIFICATION ON CLASSIFICATION.ID = TASK.CLASSIFICATION_ID "
            + "WHERE TASK.WORKBASKETID = #{workbasketId}")
    @Results({
            @Result(property = "taskId", column = "taskId"),
            @Result(property = "taskName", column = "taskName"),
            @Result(property = "workbasketId", column = "workId"),
            @Result(property = "workbasketName", column = "workName"),
            @Result(property = "classificationId", column = "classificationId"),
            @Result(property = "classificationName", column = "classificationName")
    })
    List<TaskSummary> findTasksummariesByWorkbasketId(@Param("workbasketId") String workbasketId);
}
