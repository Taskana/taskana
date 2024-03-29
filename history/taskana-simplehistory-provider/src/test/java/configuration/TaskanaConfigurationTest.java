package configuration;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.config.DataSourceGenerator;

class TaskanaConfigurationTest extends AbstractAccTest {

  @Test
  void testCreateTaskanaEngine() throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName(), false)
            .initTaskanaProperties()
            .build();

    TaskanaEngine te = TaskanaEngine.buildTaskanaEngine(configuration);

    assertThat(te).isNotNull();
  }

  @Test
  void testCreateTaskanaHistoryEventWithNonDefaultSchemaName() throws Exception {
    resetDb("SOMECUSTOMSCHEMANAME");
    long count = getHistoryService().createTaskHistoryQuery().workbasketKeyIn("wbKey1").count();
    assertThat(count).isZero();
    getHistoryService()
        .create(
            AbstractAccTest.createTaskHistoryEvent(
                "wbKey1", "taskId1", "type1", "Some comment", "wbKey2", "someUserId"));
    count = getHistoryService().createTaskHistoryQuery().workbasketKeyIn("wbKey1").count();
    assertThat(count).isOne();
  }
}
