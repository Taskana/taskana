package pro.taskana.sampledata;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import pro.taskana.configuration.DbSchemaCreator;

/**
 * Test SampleDataGenerator.
 */
class SampleDataGeneratorTest {

    @Test
    void getScriptsValidSql() {
        PooledDataSource pooledDataSource = new PooledDataSource("org.h2.Driver",
            "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;INIT=CREATE SCHEMA IF NOT EXISTS TASKANA", "sa", "sa");
        Assertions.assertDoesNotThrow(() -> new DbSchemaCreator(pooledDataSource, "TASKANA").run());
        Assertions.assertDoesNotThrow(() -> new SampleDataGenerator(pooledDataSource).generateSampleData("TASKANA"));
        pooledDataSource.forceCloseAll();
    }

}
