package pro.taskana.task.internal.jobs.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Update a lot of priorities for tasks in a performant way without the need to write SQL. */
public class TaskUpdatePriorityBatchStatement {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskUpdatePriorityBatchStatement.class);
  private final PreparedStatement preparedStatement;

  public TaskUpdatePriorityBatchStatement(Connection connection) throws SQLException {
    preparedStatement = connection.prepareStatement("update TASK set PRIORITY = ? where ID = ?");
  }

  public void addPriorityUpdate(String taskId, Integer priority) throws SQLException {
    preparedStatement.setInt(1, priority);
    preparedStatement.setString(2, taskId);
    LOGGER.debug("Job update priority to {} for task {}.", priority, taskId);
    preparedStatement.addBatch();
  }

  public void executeBatch() throws SQLException {
    preparedStatement.executeBatch();
  }
}
