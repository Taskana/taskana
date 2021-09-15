package pro.taskana.common.test.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DataSourceGenerator provides the proper {@linkplain DataSource} for all Integration tests.
 *
 * <p>If the file <b>${user.home}/taskanaUnitTest.properties</b> is present, the {@linkplain
 * DataSource} is created according to the properties <b>jdbcDriver, jdbcUrl, dbUserName and
 * dbPassword</b>. If any of these properties is missing, or the file doesn't exist, the default
 * {@linkplain DataSource} for H2 in-memory db is created.
 *
 * <p>Additionally the property <b>schemaName</b> can be defined. If that property is missing, or
 * the file doesn't exist the schemaName TASKANA will be used.
 */
public final class DataSourceGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceGenerator.class);
  private static final DataSource DATA_SOURCE;
  private static final String SCHEMA_NAME;

  private static final String DEFAULT_SCHEMA_NAME = "TASKANA";
  private static final int POOL_TIME_TO_WAIT = 50;

  static {
    String propertiesFileName = System.getProperty("user.home") + "/taskanaUnitTest.properties";
    File f = new File(propertiesFileName);
    if (f.exists() && !f.isDirectory()) {
      DATA_SOURCE = createDataSourceFromProperties(propertiesFileName);
      SCHEMA_NAME = getSchemaNameFromPropertiesObject(propertiesFileName);
    } else {
      DATA_SOURCE = createDataSourceForH2();
      SCHEMA_NAME = DEFAULT_SCHEMA_NAME;
    }
  }

  private DataSourceGenerator() {}

  public static DataSource getDataSource() {
    return DATA_SOURCE;
  }

  public static String getSchemaName() {
    return SCHEMA_NAME;
  }

  private static DataSource createDataSourceFromProperties(String propertiesFileName) {
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
            .forceCloseAll(); // otherwise, the MyBatis pool is not initialized correctly
      } else {
        LOGGER.warn("propertiesFile {} is incomplete {}", propertiesFileName, warningMessage);
        LOGGER.warn("Using default Datasource for Test");
        ds = createDataSourceForH2();
      }

    } catch (IOException e) {
      LOGGER.warn("createDataSourceFromProperties caught Exception ", e);
      LOGGER.warn("Using default Datasource for Test");
      ds = createDataSourceForH2();
    }

    return ds;
  }

  private static String getSchemaNameFromPropertiesObject(String propertiesFileName) {
    String schemaName = DEFAULT_SCHEMA_NAME;
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
        LOGGER.warn("propertiesFile {} is incomplete {}", propertiesFileName, warningMessage);
        LOGGER.warn("Using default Datasource for Test");
        schemaName = DEFAULT_SCHEMA_NAME;
      }

    } catch (IOException e) {
      LOGGER.warn("getSchemaNameFromPropertiesObject caught Exception ", e);
      LOGGER.warn("Using default schemaName for Test");
    }

    return schemaName;
  }

  private static DataSource createDataSourceForH2() {
    String jdbcDriver = "org.h2.Driver";
    String jdbcUrl =
        "jdbc:h2:mem:taskana;LOCK_MODE=0;"
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
    ds.forceCloseAll(); // otherwise, the MyBatis pool is not initialized correctly

    return ds;
  }
}
