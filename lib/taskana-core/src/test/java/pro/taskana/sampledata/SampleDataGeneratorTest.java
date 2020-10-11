package pro.taskana.sampledata;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.jupiter.api.Test;

import pro.taskana.common.internal.configuration.DbSchemaCreator;

/** Test SampleDataGenerator. */
class SampleDataGeneratorTest {

  private static final String JDBC_URL =
      "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;INIT=CREATE SCHEMA IF NOT EXISTS TASKANA";

  @Test
  void getScriptsValidSql() {
    PooledDataSource pooledDataSource = new PooledDataSource("org.h2.Driver", JDBC_URL, "sa", "sa");
    assertThatCode(() -> new DbSchemaCreator(pooledDataSource, "TASKANA").run())
        .doesNotThrowAnyException();
    assertThatCode(() -> new SampleDataGenerator(pooledDataSource, "TASKANA").generateSampleData())
        .doesNotThrowAnyException();

    pooledDataSource.forceCloseAll();
  }
}
