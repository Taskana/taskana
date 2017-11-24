package pro.taskana.impl.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.configuration.TaskanaEngineConfiguration;
/**
 * Integration Test for TaskanaEngineConfiguration.
 * @author EH
 */
public class TaskanaEngineConfigurationTest {
    private static DataSource dataSource = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineConfigurationTest.class);
    private static final int POOL_TIME_TO_WAIT = 50;

    @Test
    public void testCreateTaskanaEngine() throws FileNotFoundException, SQLException, LoginException {
        DataSource ds = getDataSource();
        TaskanaEngineConfiguration taskEngineConfiguration = new TaskanaEngineConfiguration(ds, false);

        TaskanaEngine te = taskEngineConfiguration.buildTaskanaEngine();

        Assert.assertNotNull(te);
    }

    /**
     * returns the Datasource used for Junit test.
     * If the file {user.home}/taskanaUnitTest.properties is present, the Datasource is created according to the
     * properties jdbcDriver, jdbcUrl, dbUserName and dbPassword.
     * Assuming, the database has the name tskdb, a sample properties file for DB2 looks as follows:
     *
     *      jdbcDriver=com.ibm.db2.jcc.DB2Driver
     *      jdbcUrl=jdbc:db2://localhost:50000/tskdb
     *      dbUserName=db2user
     *      dbPassword=db2password
     *
     * If any of these properties is missing, or the file doesn't exist, the default Datasource for h2 in-memory db is created.
     *
     * @return dataSource for unit test
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            String userHomeDirectroy  = System.getProperty("user.home");
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
     * create Default Datasource for in-memory database.
     * @return
     */
    private static DataSource createDefaultDataSource() {
//        JdbcDataSource ds = new JdbcDataSource();
//        ds.setURL("jdbc:h2:mem:taskana");
//        ds.setPassword("sa");
//        ds.setUser("sa");

        String jdbcDriver = "org.h2.Driver";
        String jdbcUrl = "jdbc:h2:mem:taskana";
        String dbUserName = "sa";
        String dbPassword = "sa";
        DataSource ds = new PooledDataSource(Thread.currentThread().getContextClassLoader(), jdbcDriver,
                jdbcUrl, dbUserName, dbPassword);
        ((PooledDataSource) ds).setPoolTimeToWait(POOL_TIME_TO_WAIT);
        ((PooledDataSource) ds).forceCloseAll();  // otherwise the MyBatis pool is not initialized correctly

        return ds;
    }

    /**
     * create data source from properties file.
     * @param propertiesFileName
     * @return
     */
    private static DataSource createDataSourceFromProperties(String propertiesFileName) {
        DataSource ds = null;
        try (InputStream input = new FileInputStream(propertiesFileName)) {
            Properties prop = new Properties();
            prop.load(input);
            boolean propertiesFileIsComplete = true;
            String warningMessage = "";
            String jdbcDriver  = prop.getProperty("jdbcDriver");
            if (jdbcDriver == null || jdbcDriver.length() == 0) {
                propertiesFileIsComplete = false;
                warningMessage += ", jdbcDriver property missing";
            }
            String jdbcUrl     = prop.getProperty("jdbcUrl");
            if (jdbcUrl == null || jdbcUrl.length() == 0) {
                propertiesFileIsComplete = false;
                warningMessage += ", jdbcUrl property missing";
            }
            String dbUserName  = prop.getProperty("dbUserName");
            if (dbUserName == null || dbUserName.length() == 0) {
                propertiesFileIsComplete = false;
                warningMessage += ", dbUserName property missing";
            }
            String dbPassword  = prop.getProperty("dbPassword");
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


}
