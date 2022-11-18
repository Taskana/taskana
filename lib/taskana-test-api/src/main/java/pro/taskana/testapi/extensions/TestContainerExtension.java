package pro.taskana.testapi.extensions;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static pro.taskana.testapi.DockerContainerCreator.createDataSource;
import static pro.taskana.testapi.DockerContainerCreator.createDockerContainer;
import static pro.taskana.testapi.OracleSchemaHelper.initOracleSchema;
import static pro.taskana.testapi.util.ExtensionCommunicator.getClassLevelStore;
import static pro.taskana.testapi.util.ExtensionCommunicator.isTopLevelClass;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.testcontainers.containers.JdbcDatabaseContainer;

import pro.taskana.common.internal.configuration.DB;
import pro.taskana.testapi.CleanTaskanaContext;
import pro.taskana.testapi.TaskanaEngineConfigurationModifier;
import pro.taskana.testapi.WithServiceProvider;

public class TestContainerExtension implements InvocationInterceptor {

  public static final String STORE_DATA_SOURCE = "datasource";
  public static final String STORE_SCHEMA_NAME = "schemaName";

  public static final DataSource DATA_SOURCE;

  public static final DB EXECUTION_DATABASE = retrieveDatabaseFromEnv();

  static {
    Optional<JdbcDatabaseContainer<?>> container = createDockerContainer(EXECUTION_DATABASE);
    if (container.isPresent()) {
      container.get().start();
      DATA_SOURCE = createDataSource(container.get());
    } else {
      DATA_SOURCE = createDataSourceForH2();
    }
  }

  @Override
  public <T> T interceptTestClassConstructor(
      Invocation<T> invocation,
      ReflectiveInvocationContext<Constructor<T>> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {
    Class<?> testClass = extensionContext.getRequiredTestClass();
    if (isTopLevelClass(testClass) || isAnnotated(testClass, CleanTaskanaContext.class)) {
      Store store = getClassLevelStore(extensionContext);
      String schemaName = determineSchemaName();
      store.put(STORE_SCHEMA_NAME, schemaName);
      store.put(STORE_DATA_SOURCE, DATA_SOURCE);
      if (DB.isOracle(EXECUTION_DATABASE.dbProductId)) {
        initOracleSchema(DATA_SOURCE, schemaName);
      }
    } else if (TaskanaEngineConfigurationModifier.class.isAssignableFrom(testClass)
        || isAnnotated(testClass, WithServiceProvider.class)) {
      // since the implementation of TaskanaEngineConfigurationModifier implies the generation of a
      // new TaskanaEngine, we have to copy the schema name and datasource from the enclosing class'
      // store to the testClass store.
      // This allows the following extensions to generate a new TaskanaEngine for the testClass.
      Store parentStore = getClassLevelStore(extensionContext, testClass.getEnclosingClass());
      Store store = getClassLevelStore(extensionContext);
      copyValue(TestContainerExtension.STORE_SCHEMA_NAME, parentStore, store);
      copyValue(TestContainerExtension.STORE_DATA_SOURCE, parentStore, store);
    }
    return invocation.proceed();
  }

  private static void copyValue(String key, Store source, Store destination) {
    Object value = source.get(key);
    destination.put(key, value);
  }

  private static String determineSchemaName() {
    String uniqueId = "A" + UUID.randomUUID().toString().replace("-", "_");
    if (EXECUTION_DATABASE == DB.ORACLE) {
      uniqueId = uniqueId.substring(0, 26);
    } else if (EXECUTION_DATABASE == DB.POSTGRES) {
      uniqueId = uniqueId.toLowerCase();
    }
    return uniqueId;
  }

  private static DB retrieveDatabaseFromEnv() {
    String property = System.getenv("DB");
    DB db;
    try {
      db = DB.valueOf(property);
    } catch (Exception ex) {
      db = DB.H2;
    }
    return db;
  }

  private static DataSource createDataSourceForH2() {
    PooledDataSource ds =
        new PooledDataSource(
            Thread.currentThread().getContextClassLoader(),
            "org.h2.Driver",
            "jdbc:h2:mem:"
                + "taskana"
                + ";NON_KEYWORDS=KEY,VALUE;LOCK_MODE=0;"
                + "INIT=CREATE SCHEMA IF NOT EXISTS TASKANA\\;"
                + "SET COLLATION DEFAULT_de_DE ",
            "sa",
            "sa");
    ds.setPoolTimeToWait(50);
    ds.forceCloseAll(); // otherwise, the MyBatis pool is not initialized correctly

    return ds;
  }
}
