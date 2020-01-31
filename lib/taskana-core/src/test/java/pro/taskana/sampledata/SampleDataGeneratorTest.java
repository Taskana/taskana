package pro.taskana.sampledata;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import pro.taskana.common.internal.configuration.DbSchemaCreator;

/** Test SampleDataGenerator. */
class SampleDataGeneratorTest {

  private static final String JDBC_URL =
      "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;INIT=CREATE SCHEMA IF NOT EXISTS TASKANA";

  @Test
  void getScriptsValidSql() {
    PooledDataSource pooledDataSource = new PooledDataSource("org.h2.Driver", JDBC_URL, "sa", "sa");
    Assertions.assertDoesNotThrow(() -> new DbSchemaCreator(pooledDataSource, "TASKANA").run());
    Assertions.assertDoesNotThrow(
        () -> new SampleDataGenerator(pooledDataSource, "TASKANA").generateSampleData());
    pooledDataSource.forceCloseAll();
  }

  @Test
  void tableExists() {
    PooledDataSource pooledDataSource = new PooledDataSource("org.h2.Driver", JDBC_URL, "sa", "sa");
    Assertions.assertDoesNotThrow(() -> new DbSchemaCreator(pooledDataSource, "TASKANA").run());

    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(pooledDataSource, "TASKANA");
    Assertions.assertTrue(sampleDataGenerator.tableExists("TASK"));
    Assertions.assertFalse(sampleDataGenerator.tableExists("TASKRANDOM"));
    pooledDataSource.forceCloseAll();
  }
}
