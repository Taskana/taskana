package testapi.extensions;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static pro.taskana.common.test.DockerContainerCreator.createDataSource;
import static pro.taskana.common.test.DockerContainerCreator.createDockerContainer;
import static testapi.util.ExtensionCommunicator.getClassLevelStore;
import static testapi.util.ExtensionCommunicator.isTopLevelClass;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.platform.commons.support.AnnotationSupport;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import testapi.CleanTaskanaContext;
import testapi.TaskanaEngineConfigurationModifier;
import testapi.WithServiceProvider;

import pro.taskana.common.internal.configuration.DB;

public class TestContainerExtension implements AfterAllCallback, InvocationInterceptor {

  public static final String STORE_DATA_SOURCE = "datasource";
  public static final String STORE_CONTAINER = "container";
  public static final String STORE_SCHEMA_NAME = "schemaName";

  @Override
  public <T> T interceptTestClassConstructor(
      Invocation<T> invocation,
      ReflectiveInvocationContext<Constructor<T>> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {
    Class<?> testClass = extensionContext.getRequiredTestClass();
    if (isTopLevelClass(testClass) || isAnnotated(testClass, CleanTaskanaContext.class)) {
      Store store = getClassLevelStore(extensionContext);
      DB db = retrieveDatabaseFromEnv();
      store.put(STORE_SCHEMA_NAME, determineSchemaName(db));

      createDockerContainer(db)
          .ifPresentOrElse(
              container -> {
                container.start();
                store.put(STORE_DATA_SOURCE, createDataSource(container));
                store.put(STORE_CONTAINER, container);
              },
              () -> store.put(STORE_DATA_SOURCE, createDataSourceForH2()));

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

  @Override
  public void afterAll(ExtensionContext context) {
    Class<?> testClass = context.getRequiredTestClass();
    if (isTopLevelClass(testClass)
        || AnnotationSupport.isAnnotated(testClass, CleanTaskanaContext.class)) {
      Optional.ofNullable(getClassLevelStore(context).get(STORE_CONTAINER))
          .map(JdbcDatabaseContainer.class::cast)
          .ifPresent(GenericContainer::stop);
    }
  }

  private static void copyValue(String key, Store source, Store destination) {
    Object value = source.get(key);
    destination.put(key, value);
  }

  private static String determineSchemaName(DB db) {
    return db == DB.POSTGRES ? "taskana" : "TASKANA";
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
                + UUID.randomUUID()
                + ";LOCK_MODE=0;"
                + "INIT=CREATE SCHEMA IF NOT EXISTS TASKANA\\;"
                + "SET COLLATION DEFAULT_de_DE ",
            "sa",
            "sa");
    ds.setPoolTimeToWait(50);
    ds.forceCloseAll(); // otherwise, the MyBatis pool is not initialized correctly

    return ds;
  }
}
