package pro.taskana.sampledata;

import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * This class cleans the complete database.
 */
public class DBCleaner {

    public void clearDb(DataSource dataSource, String schema) throws SQLException {
        SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schema);
        sampleDataGenerator.runScripts(sampleDataGenerator::clearDb);
    }

    public void dropDb(DataSource dataSource, String schema) throws SQLException {
        SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schema);
        sampleDataGenerator.runScripts(sampleDataGenerator::dropDb);

    }
}
