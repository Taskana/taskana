package configuration;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;

/**
 * Unit Test for TaskanaEngineConfigurationTest.
 *
 * @author MMR
 */
class TaskanaEngineConfigurationTest extends AbstractAccTest {

  @Test
  void testCreateTaskanaEngine() throws Exception {
    DataSource ds = getDataSource();
    TaskanaEngineConfiguration taskEngineConfiguration =
        new TaskanaEngineConfiguration(ds, false, getSchemaName());

    TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();

    assertThat(te).isNotNull();
  }

  @Test
  void testCreateTaskanaHistoryEventWithNonDefaultSchemaName() throws Exception {
    resetDb("SOMECUSTOMSCHEMANAME");
    long count = getHistoryService().createHistoryQuery().workbasketKeyIn("wbKey1").count();
    assertThat(count).isZero();
    getHistoryService()
        .create(
            AbstractAccTest.createHistoryEvent(
                "wbKey1", "taskId1", "type1", "Some comment", "wbKey2", "someUserId"));
    count = getHistoryService().createHistoryQuery().workbasketKeyIn("wbKey1").count();
    assertThat(count).isOne();
  }
}
