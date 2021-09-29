package testapi.extensions;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static testapi.util.ExtensionCommunicator.getClassLevelStore;
import static testapi.util.ExtensionCommunicator.isTopLevelClass;

import acceptance.TaskanaEngineProxy;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.JUnitException;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import testapi.CleanTaskanaContext;
import testapi.TaskanaEngineConfigurationModifier;
import testapi.WithServiceProvider;
import testapi.util.ServiceProviderExtractor;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.internal.ClassificationServiceImpl;
import pro.taskana.common.api.ConfigurationService;
import pro.taskana.common.api.JobService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.ConfigurationMapper;
import pro.taskana.common.internal.ConfigurationServiceImpl;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.security.CurrentUserContextImpl;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.internal.MonitorServiceImpl;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.internal.TaskServiceImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.internal.WorkbasketServiceImpl;

public class TaskanaInitializationExtension implements TestInstancePostProcessor {

  public static final String STORE_TASKANA_ENTITY_MAP = "taskanaEntityMap";

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context)
      throws Exception {
    Class<?> testClass = testInstance.getClass();
    if (isTopLevelClass(testClass)
        || isAnnotated(testClass, CleanTaskanaContext.class)
        || isAnnotated(testClass, WithServiceProvider.class)
        || testInstance instanceof TaskanaEngineConfigurationModifier) {
      Store store = getClassLevelStore(context);
      TaskanaEngineConfiguration taskanaEngineConfiguration =
          createDefaultTaskanaEngineConfiguration(store);

      if (testInstance instanceof TaskanaEngineConfigurationModifier) {
        TaskanaEngineConfigurationModifier modifier =
            (TaskanaEngineConfigurationModifier) testInstance;
        modifier.modify(taskanaEngineConfiguration);
      }

      TaskanaEngine taskanaEngine;
      try (MockedStatic<SpiLoader> staticMock = Mockito.mockStatic(SpiLoader.class)) {
        ServiceProviderExtractor.extractServiceProviders(testClass)
            .forEach(
                (spi, serviceProviders) ->
                    staticMock.when(() -> SpiLoader.load(spi)).thenReturn(serviceProviders));
        taskanaEngine =
            taskanaEngineConfiguration.buildTaskanaEngine(ConnectionManagementMode.AUTOCOMMIT);
      }

      store.put(STORE_TASKANA_ENTITY_MAP, generateTaskanaEntityMap(taskanaEngine));
    }
  }

  private static TaskanaEngineConfiguration createDefaultTaskanaEngineConfiguration(Store store) {
    String schemaName = store.get(TestContainerExtension.STORE_SCHEMA_NAME, String.class);
    if (schemaName == null) {
      throw new JUnitException("Expected schemaName to be defined in store, but it's not.");
    }
    DataSource dataSource = store.get(TestContainerExtension.STORE_DATA_SOURCE, DataSource.class);
    if (dataSource == null) {
      throw new JUnitException("Expected dataSource to be defined in store, but it's not.");
    }

    return new TaskanaEngineConfiguration(dataSource, false, schemaName);
  }

  private static Map<Class<?>, Object> generateTaskanaEntityMap(TaskanaEngine taskanaEngine)
      throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    TaskanaEngineProxy taskanaEngineProxy = new TaskanaEngineProxy(taskanaEngine);
    MonitorService monitorService = taskanaEngine.getMonitorService();
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    JobService jobService = taskanaEngine.getJobService();
    CurrentUserContext currentUserContext = taskanaEngine.getCurrentUserContext();
    SqlSession sqlSession = taskanaEngineProxy.getSqlSession();
    return Map.ofEntries(
        Map.entry(TaskanaEngineConfiguration.class, taskanaEngine.getConfiguration()),
        Map.entry(TaskanaEngineImpl.class, taskanaEngine),
        Map.entry(TaskanaEngine.class, taskanaEngine),
        Map.entry(InternalTaskanaEngine.class, taskanaEngineProxy.getEngine()),
        Map.entry(TaskService.class, taskService),
        Map.entry(TaskServiceImpl.class, taskService),
        Map.entry(MonitorService.class, monitorService),
        Map.entry(MonitorServiceImpl.class, monitorService),
        Map.entry(WorkbasketService.class, workbasketService),
        Map.entry(WorkbasketServiceImpl.class, workbasketService),
        Map.entry(ClassificationService.class, classificationService),
        Map.entry(ClassificationServiceImpl.class, classificationService),
        Map.entry(ConfigurationService.class, taskanaEngine.getConfigurationService()),
        Map.entry(ConfigurationServiceImpl.class, taskanaEngine.getConfigurationService()),
        Map.entry(JobService.class, jobService),
        Map.entry(JobServiceImpl.class, jobService),
        Map.entry(CurrentUserContext.class, currentUserContext),
        Map.entry(CurrentUserContextImpl.class, currentUserContext),
        Map.entry(WorkingDaysToDaysConverter.class, taskanaEngine.getWorkingDaysToDaysConverter()),
        Map.entry(ConfigurationMapper.class, sqlSession.getMapper(ConfigurationMapper.class)));
  }
}
