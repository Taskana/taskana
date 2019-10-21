package acceptance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.DBWriter;
import pro.taskana.configuration.TaskanaEngineConfiguration;
import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;

/**
 * Set up database for tests.
 */
public class AbstractAccTest {

    public static SimpleHistoryServiceImpl historyService;

    protected static TaskanaEngineConfiguration taskanaEngineConfiguration;

    private static DataSource dataSource = null;

    private static String schemaName = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAccTest.class);

    private static final int POOL_TIME_TO_WAIT = 50;

    protected AbstractAccTest() {
        // not called
    }

    @BeforeClass
    public static void setupTest() throws Exception {
        resetDb(null);
    }

    public static void resetDb(String schemaName) throws SQLException {
        DataSource dataSource = getDataSource();
        DBWriter writer = new DBWriter();
        taskanaEngineConfiguration = new TaskanaEngineConfiguration(dataSource, false,
            schemaName != null && !schemaName.isEmpty() ? schemaName : getSchemaName());
        historyService = new SimpleHistoryServiceImpl();
        historyService.initialize(taskanaEngineConfiguration);

        writer.clearDB(dataSource);
        writer.generateTestData(dataSource);
    }

    /**
     * returns the DataSource used for Junit test. If the file {user.home}/taskanaUnitTest.properties is present, the
     * Datasource is created according to the properties jdbcDriver, jdbcUrl, dbUserName and dbPassword. Assuming, the
     * database has the name tskdb, a sample properties file for DB2 looks as follows:
     * jdbcDriver=com.ibm.db2.jcc.DB2Driver jdbcUrl=jdbc:db2://localhost:50000/tskdb dbUserName=db2user
     * dbPassword=db2password If any of these properties is missing, or the file doesn't exist, the default Datasource
     * for h2 in-memory db is created.
     *
     * @return dataSource for unit test
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            String userHomeDirectroy = System.getProperty("user.home");
            String propertiesFileName = userHomeDirectroy + "/taskanaUnitTest.properties";
            File f = new File(propertiesFileName);
            if (f.exists() && !f.isDirectory()) {
                dataSource = createDataSourceFromProperties(propertiesFileName);
            } else {
                dataSource = createDefaultDataSource();
            }
        }
        return dataSource;
    }

    /**
     * create Default DataSource for in-memory database.
     *
     * @return the TASKANA default datasource.
     */
    private static DataSource createDefaultDataSource() {

        String jdbcDriver = "org.h2.Driver";
        String jdbcUrl = "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0";
        String dbUserName = "sa";
        String dbPassword = "sa";
        DataSource ds = new PooledDataSource(Thread.currentThread().getContextClassLoader(), jdbcDriver,
            jdbcUrl, dbUserName, dbPassword);
        ((PooledDataSource) ds).setPoolTimeToWait(POOL_TIME_TO_WAIT);
        ((PooledDataSource) ds).forceCloseAll();  // otherwise the MyBatis pool is not initialized correctly

        return ds;
    }

    /**
     * returns the SchemaName used for Junit test. If the file {user.home}/taskanaUnitTest.properties is present, the
     * SchemaName is created according to the property schemaName. a sample properties file for DB2 looks as follows:
     * jdbcDriver=com.ibm.db2.jcc.DB2Driver jdbcUrl=jdbc:db2://localhost:50000/tskdb dbUserName=db2user
     * dbPassword=db2password schemaName=TASKANA If any of these properties is missing, or the file doesn't exist, the
     * default schemaName TASKANA is created used.
     *
     * @return String for unit test
     */
    public static String getSchemaName() {
        if (schemaName == null) {
            String userHomeDirectroy = System.getProperty("user.home");
            String propertiesFileName = userHomeDirectroy + "/taskanaUnitTest.properties";
            File f = new File(propertiesFileName);
            if (f.exists() && !f.isDirectory()) {
                schemaName = getSchemaNameFromPropertiesObject(propertiesFileName);
            } else {
                schemaName = "TASKANA";
            }
        }
        return schemaName;
    }

    /**
     * create historyEvent object.
     *
     * @param workbasketKey
     *            the workbasketKey, the task currently resides in.
     * @param taskId
     *            the taskid the event belongs to.
     * @param type
     *            the type of the event.
     * @param comment
     *            the individual comment.
     * @param previousWorkbasketId
     *            the workbasketId of the previous workbasket (if applicable).
     * @return History event object created.
     */
    public static HistoryEventImpl createHistoryEvent(String workbasketKey, String taskId, String type, String comment,
        String previousWorkbasketId) {
        HistoryEventImpl historyEvent = new HistoryEventImpl();
        historyEvent.setWorkbasketKey(workbasketKey);
        historyEvent.setTaskId(taskId);
        historyEvent.setEventType(type);
        historyEvent.setComment(comment);
        historyEvent.setOldValue(previousWorkbasketId);
        return historyEvent;
    }

    /**
     * create data source from properties file.
     *
     * @param propertiesFileName
     *            the name of the properties file.
     * @return the datasource constructed from the information in the properties file.
     */
    private static DataSource createDataSourceFromProperties(String propertiesFileName) {
        DataSource ds = null;
        try (InputStream input = new FileInputStream(propertiesFileName)) {
            Properties prop = new Properties();
            prop.load(input);
            boolean propertiesFileIsComplete = true;
            String warningMessage = "";
            String jdbcDriver = prop.getProperty("jdbcDriver");
            if (jdbcDriver == null || jdbcDriver.length() == 0) {
                propertiesFileIsComplete = false;
                warningMessage += ", jdbcDriver property missing";
            }
            String jdbcUrl = prop.getProperty("jdbcUrl");
            if (jdbcUrl == null || jdbcUrl.length() == 0) {
                propertiesFileIsComplete = false;
                warningMessage += ", jdbcUrl property missing";
            }
            String dbUserName = prop.getProperty("dbUserName");
            if (dbUserName == null || dbUserName.length() == 0) {
                propertiesFileIsComplete = false;
                warningMessage += ", dbUserName property missing";
            }
            String dbPassword = prop.getProperty("dbPassword");
            if (dbPassword == null || dbPassword.length() == 0) {
                propertiesFileIsComplete = false;
                warningMessage += ", dbPassword property missing";
            }

            if (propertiesFileIsComplete) {
                ds = new PooledDataSource(Thread.currentThread().getContextClassLoader(), jdbcDriver,
                    jdbcUrl, dbUserName, dbPassword);
                ((PooledDataSource) ds).forceCloseAll();  // otherwise the MyBatis pool is not initialized correctly
            } else {
                LOGGER.warn("propertiesFile " + propertiesFileName + " is incomplete" + warningMessage);
                LOGGER.warn("Using default Datasource for Test");
                ds = createDefaultDataSource();
            }

        } catch (FileNotFoundException e) {
            LOGGER.warn("createDataSourceFromProperties caught Exception " + e);
            LOGGER.warn("Using default Datasource for Test");
            ds = createDefaultDataSource();
        } catch (IOException e) {
            LOGGER.warn("createDataSourceFromProperties caught Exception " + e);
            LOGGER.warn("Using default Datasource for Test");
            ds = createDefaultDataSource();
        }

        return ds;
    }

    private static String getSchemaNameFromPropertiesObject(String propertiesFileName) {
        String schemaName = "TASKANA";
        try (InputStream input = new FileInputStream(propertiesFileName)) {
            Properties prop = new Properties();
            prop.load(input);
            boolean propertiesFileIsComplete = true;
            String warningMessage = "";
            schemaName = prop.getProperty("schemaName");
            if (schemaName == null || schemaName.length() == 0) {
                propertiesFileIsComplete = false;
                warningMessage += ", schemaName property missing";
            }

            if (!propertiesFileIsComplete) {
                LOGGER.warn("propertiesFile " + propertiesFileName + " is incomplete" + warningMessage);
                LOGGER.warn("Using default Datasource for Test");
                schemaName = "TASKANA";
            }

        } catch (FileNotFoundException e) {
            LOGGER.warn("getSchemaNameFromPropertiesObject caught Exception " + e);
            LOGGER.warn("Using default schemaName for Test");
        } catch (IOException e) {
            LOGGER.warn("createDataSourceFromProperties caught Exception " + e);
            LOGGER.warn("Using default Datasource for Test");
        }

        return schemaName;
    }
}
