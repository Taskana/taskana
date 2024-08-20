package io.kadai.sampledata;

import static org.assertj.core.api.Assertions.assertThatCode;

import io.kadai.common.internal.configuration.DbSchemaCreator;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.jupiter.api.Test;

/** Test SampleDataGenerator. */
class SampleDataGeneratorTest {

  private static final String JDBC_URL =
      "jdbc:h2:mem:kadai;NON_KEYWORDS=KEY,VALUE;"
          + "IGNORECASE=TRUE;LOCK_MODE=0;INIT=CREATE SCHEMA IF NOT EXISTS KADAI";

  @Test
  void getScriptsValidSql() {
    PooledDataSource pooledDataSource = new PooledDataSource("org.h2.Driver", JDBC_URL, "sa", "sa");
    assertThatCode(() -> new DbSchemaCreator(pooledDataSource, "KADAI").run())
        .doesNotThrowAnyException();
    assertThatCode(() -> new SampleDataGenerator(pooledDataSource, "KADAI").generateSampleData())
        .doesNotThrowAnyException();

    pooledDataSource.forceCloseAll();
  }
}
