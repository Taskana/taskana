package pro.taskana.rest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.configuration.SpringTaskanaEngineConfiguration;
import pro.taskana.exceptions.SystemException;
import pro.taskana.sampledata.SampleDataGenerator;

/**
 * Test that the schema name can be customized.
 *
 */

@TaskanaSpringBootTest
class TestSchemaNameCustomizable {

    String schemaName = "CUSTOMSCHEMANAME";
    boolean isPostgres = false;

    @Autowired
    private DataSource dataSource;

    void resetDb() {
        SampleDataGenerator sampleDataGenerator;
        try {
            if ("PostgreSQL".equals(dataSource.getConnection().getMetaData().getDatabaseProductName())) {
                isPostgres = true;
                schemaName = schemaName.toLowerCase();
            }
            new SpringTaskanaEngineConfiguration(dataSource, true, true, schemaName);
            sampleDataGenerator = new SampleDataGenerator(dataSource);
            sampleDataGenerator.generateSampleData(schemaName);
        } catch (SQLException e) {
            throw new SystemException("tried to reset DB and caught Exception " + e, e);
        }
    }

    @Test
    void chekCustomSchemaNameIsDefined() {
        resetDb();
        ResultSet rs;
        try {
            Statement stmt = dataSource.getConnection().createStatement();
            if (isPostgres) {
                rs = stmt.executeQuery(
                    "SELECT * FROM pg_catalog.pg_tables where schemaname = '" + schemaName.toLowerCase() + "'");

            } else {
                rs = stmt.executeQuery(
                    "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + schemaName + "'");
            }
            while (rs.next()) {
                String tableName = rs.getString(isPostgres ? "tablename" : "TABLE_NAME");
                if (tableName.equals(isPostgres ? "workbasket" : "WORKBASKET")) {
                    Assert.assertEquals(tableName, isPostgres ? "workbasket" : "WORKBASKET");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
