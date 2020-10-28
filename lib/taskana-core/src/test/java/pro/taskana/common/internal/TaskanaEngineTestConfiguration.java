package pro.taskana.common.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Integration Test for TaskanaEngineConfiguration. */
public final class TaskanaEngineTestConfiguration {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskanaEngineTestConfiguration.class);
  private static final int POOL_TIME_TO_WAIT = 50;
  private static DataSource dataSource;
  private static String schemaName = null;

  static {
    String userHomeDirectroy = System.getProperty("user.home");
    String propertiesFileName = userHomeDirectroy + "/taskanaUnitTest.properties";
    File f = new File(propertiesFileName);
    if (f.exists() && !f.isDirectory()) {
      dataSource = createDataSourceFromProperties(propertiesFileName);
    } else {
      dataSource = createDefaultDataSource();
    }
  }

  private TaskanaEngineTestConfiguration() {}

  /**
   * returns the Datasource used for Junit test. If the file {user.home}/taskanaUnitTest.properties
   * is present, the Datasource is created according to the properties jdbcDriver, jdbcUrl,
   * dbUserName and dbPassword. Assuming, the database has the name tskdb, a sample properties file
   * for DB2 looks as follows: jdbcDriver=com.ibm.db2.jcc.DB2Driver
   * jdbcUrl=jdbc:db2://localhost:50000/tskdb dbUserName=db2user dbPassword=db2password If any of
   * these properties is missing, or the file doesn't exist, the default Datasource for h2 in-memory
   * db is created.
   *
   * @return dataSource for unit test
   */
  public static DataSource getDataSource() {
    return dataSource;
  }

  /**
   * returns the SchemaName used for Junit test. If the file {user.home}/taskanaUnitTest.properties
   * is present, the SchemaName is created according to the property schemaName. a sample properties
   * file for DB2 looks as follows: jdbcDriver=com.ibm.db2.jcc.DB2Driver
   * jdbcUrl=jdbc:db2://localhost:50000/tskdb dbUserName=db2user dbPassword=db2password
   * schemaName=TASKANA If any of these properties is missing, or the file doesn't exist, the
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
   * create data source from properties file.
   *
   * @param propertiesFileName the name of the property file
   * @return the parsed datasource.
   */
  public static DataSource createDataSourceFromProperties(String propertiesFileName) {
    DataSource ds;
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
        ds =
            new PooledDataSource(
                Thread.currentThread().getContextClassLoader(),
                jdbcDriver,
                jdbcUrl,
                dbUserName,
                dbPassword);
        ((PooledDataSource) ds)
            .forceCloseAll(); // otherwise the MyBatis pool is not initialized correctly
      } else {
        LOGGER.warn("propertiesFile " + propertiesFileName + " is incomplete" + warningMessage);
        LOGGER.warn("Using default Datasource for Test");
        ds = createDefaultDataSource();
      }

    } catch (IOException e) {
      LOGGER.warn("createDataSourceFromProperties caught Exception " + e);
      LOGGER.warn("Using default Datasource for Test");
      ds = createDefaultDataSource();
    }

    return ds;
  }

  static String getSchemaNameFromPropertiesObject(String propertiesFileName) {
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

  /**
   * create Default Datasource for in-memory database.
   *
   * @return the default datasource.
   */
  private static DataSource createDefaultDataSource() {
    // JdbcDataSource ds = new JdbcDataSource();
    // ds.setURL("jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0");
    // ds.setPassword("sa");
    // ds.setUser("sa");

    String jdbcDriver = "org.h2.Driver";
    String jdbcUrl =
        "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;"
            + "INIT=CREATE SCHEMA IF NOT EXISTS TASKANA\\;"
            + "SET COLLATION DEFAULT_de_DE ";
    String dbUserName = "sa";
    String dbPassword = "sa";
    PooledDataSource ds =
        new PooledDataSource(
            Thread.currentThread().getContextClassLoader(),
            jdbcDriver,
            jdbcUrl,
            dbUserName,
            dbPassword);
    ds.setPoolTimeToWait(POOL_TIME_TO_WAIT);
    ds.forceCloseAll(); // otherwise the MyBatis pool is not initialized correctly

    return ds;
  }
}
