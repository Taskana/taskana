package io.kadai.testapi.extensions;

import static io.kadai.testapi.DockerContainerCreator.createDataSource;
import static io.kadai.testapi.DockerContainerCreator.createDockerContainer;
import static io.kadai.testapi.OracleSchemaHelper.initOracleSchema;
import static io.kadai.testapi.util.ExtensionCommunicator.getClassLevelStore;
import static io.kadai.testapi.util.ExtensionCommunicator.isTopLevelClass;
import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

import io.kadai.common.internal.configuration.DB;
import io.kadai.testapi.CleanKadaiContext;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.WithServiceProvider;
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

  public static DataSource createDataSourceForH2() {
    PooledDataSource ds =
        new PooledDataSource(
            Thread.currentThread().getContextClassLoader(),
            "org.h2.Driver",
            "jdbc:h2:mem:"
                + "kadai"
                + ";NON_KEYWORDS=KEY,VALUE;LOCK_MODE=0;"
                + "INIT=CREATE SCHEMA IF NOT EXISTS KADAI\\;"
                + "SET COLLATION DEFAULT_de_DE ",
            "sa",
            "sa");
    ds.setPoolTimeToWait(50);
    ds.forceCloseAll(); // otherwise, the MyBatis pool is not initialized correctly

    return ds;
  }

  public static String determineSchemaName() {
    String uniqueId = "A" + UUID.randomUUID().toString().replace("-", "_");
    if (EXECUTION_DATABASE == DB.ORACLE) {
      uniqueId = uniqueId.substring(0, 26);
    } else if (EXECUTION_DATABASE == DB.POSTGRES) {
      uniqueId = uniqueId.toLowerCase();
    }
    return uniqueId;
  }

  private static void copyValue(String key, Store source, Store destination) {
    Object value = source.get(key);
    destination.put(key, value);
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

  @Override
  public <T> T interceptTestClassConstructor(
      Invocation<T> invocation,
      ReflectiveInvocationContext<Constructor<T>> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {
    Class<?> testClass = extensionContext.getRequiredTestClass();
    if (isTopLevelClass(testClass) || isAnnotated(testClass, CleanKadaiContext.class)) {
      Store store = getClassLevelStore(extensionContext);
      String schemaName = determineSchemaName();
      store.put(STORE_SCHEMA_NAME, schemaName);
      store.put(STORE_DATA_SOURCE, DATA_SOURCE);
      if (DB.ORACLE == EXECUTION_DATABASE) {
        initOracleSchema(DATA_SOURCE, schemaName);
      }
    } else if (KadaiConfigurationModifier.class.isAssignableFrom(testClass)
        || isAnnotated(testClass, WithServiceProvider.class)) {
      // since the implementation of KadaiConfigurationModifier implies the generation of a
      // new KadaiEngine, we have to copy the schema name and datasource from the enclosing class'
      // store to the testClass store.
      // This allows the following extensions to generate a new KadaiEngine for the testClass.
      Store parentStore = getClassLevelStore(extensionContext, testClass.getEnclosingClass());
      Store store = getClassLevelStore(extensionContext);
      copyValue(TestContainerExtension.STORE_SCHEMA_NAME, parentStore, store);
      copyValue(TestContainerExtension.STORE_DATA_SOURCE, parentStore, store);
    }
    return invocation.proceed();
  }
}
