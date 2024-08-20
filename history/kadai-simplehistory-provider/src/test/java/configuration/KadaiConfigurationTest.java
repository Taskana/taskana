package configuration;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.test.config.DataSourceGenerator;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;

class KadaiConfigurationTest extends AbstractAccTest {

  @Test
  void testCreateKadaiEngine() throws Exception {
    DataSource ds = DataSourceGenerator.getDataSource();
    KadaiConfiguration configuration =
        new KadaiConfiguration.Builder(ds, false, DataSourceGenerator.getSchemaName(), false)
            .initKadaiProperties()
            .build();

    KadaiEngine te = KadaiEngine.buildKadaiEngine(configuration);

    assertThat(te).isNotNull();
  }

  @Test
  void testCreateKadaiHistoryEventWithNonDefaultSchemaName() throws Exception {
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
