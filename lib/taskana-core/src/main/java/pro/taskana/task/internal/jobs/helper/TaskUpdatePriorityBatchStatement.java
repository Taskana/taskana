package pro.taskana.task.internal.jobs.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;

/** Update a lot of priorities for tasks in a performant way without the need to write SQL. */
@Slf4j
public class TaskUpdatePriorityBatchStatement {

  private final PreparedStatement preparedStatement;

  public TaskUpdatePriorityBatchStatement(Connection connection) throws SQLException {
    preparedStatement = connection.prepareStatement("update TASK set PRIORITY = ? where ID = ?");
  }

  public void addPriorityUpdate(String taskId, int priority) throws SQLException {
    preparedStatement.setInt(1, priority);
    preparedStatement.setString(2, taskId);
    log.debug("Job update priority to {} for task {}.", priority, taskId);
    preparedStatement.addBatch();
  }

  public void executeBatch() throws SQLException {
    preparedStatement.executeBatch();
  }
}
