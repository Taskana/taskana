package acceptance.jobs.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaRuntimeException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.internal.jobs.helper.SqlConnectionRunner;

@ExtendWith(JaasExtension.class)
class SqlConnectionRunnerAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    // required if single tests modify database
    // TODO split test class into readOnly & modifying tests to improve performance
    resetDb(false);
  }

  @Test
  void should_executeSimpleQuery() {
    // given
    SqlConnectionRunner runner = new SqlConnectionRunner(taskanaEngine);
    String taskId = "TKI:000000000000000000000000000000000050";

    // when
    runner.runWithConnection(
        connection -> {
          PreparedStatement preparedStatement =
              connection.prepareStatement("select * from TASK where ID = ?");
          preparedStatement.setString(1, taskId);
          final ResultSet resultSet = preparedStatement.executeQuery();

          // then
          assertThat(resultSet.next()).isTrue();
        });
  }

  @Test
  void should_catchSqlExceptionAndThrowSystemException() {
    // given
    SqlConnectionRunner runner = new SqlConnectionRunner(taskanaEngine);

    // when
    assertThatThrownBy(
            () ->
                runner.runWithConnection(
                    connection -> {
                      throw new SQLException("test");
                    }))
        .isInstanceOf(SystemException.class);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_SetOriginalSchema_When_ExceptionThrown() throws Exception {

    taskanaEngine.getConfiguration().setSchemaName("NotExisting");
    SqlConnectionRunner runner = new SqlConnectionRunner(taskanaEngine);

    // TSK-1749
    assertThat(runner.getConnection().getSchema()).matches("TASKANA\\s|TASKANA|taskana");
    String taskId = "TKI:000000000000000000000000000000000000";

    assertThatThrownBy(
            () ->
                runner.runWithConnection(
                    connection -> {
                      PreparedStatement preparedStatement =
                          connection.prepareStatement("select * from TASK where ID = ?");
                      preparedStatement.setString(1, taskId);
                      preparedStatement.executeQuery();
                    }))
        .isInstanceOf(TaskanaRuntimeException.class);

    // TSK-1749
    assertThat(runner.getConnection().getSchema()).matches("TASKANA\\s|TASKANA|taskana");
  }
}
