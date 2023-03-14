package pro.taskana.testapi.extensions;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;
import static pro.taskana.testapi.util.ExtensionCommunicator.getClassLevelStore;
import static pro.taskana.testapi.util.ExtensionCommunicator.isTopLevelClass;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.jupiter.api.extension.TestInstancePreDestroyCallback;
import org.junit.platform.commons.JUnitException;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.internal.ClassificationServiceImpl;
import pro.taskana.common.api.ConfigurationService;
import pro.taskana.common.api.JobService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.ConfigurationMapper;
import pro.taskana.common.internal.ConfigurationServiceImpl;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.JobMapper;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.jobs.JobScheduler;
import pro.taskana.common.internal.security.CurrentUserContextImpl;
import pro.taskana.common.internal.util.ReflectionUtil;
import pro.taskana.common.internal.util.SpiLoader;
import pro.taskana.common.internal.workingtime.WorkingTimeCalculatorImpl;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.internal.MonitorServiceImpl;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.internal.TaskServiceImpl;
import pro.taskana.testapi.CleanTaskanaContext;
import pro.taskana.testapi.TaskanaConfigurationModifier;
import pro.taskana.testapi.TaskanaEngineProxy;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.WithServiceProvider.WithServiceProviders;
import pro.taskana.testapi.util.ServiceProviderExtractor;
import pro.taskana.user.api.UserService;
import pro.taskana.user.internal.UserServiceImpl;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.internal.WorkbasketServiceImpl;

public class TaskanaInitializationExtension
    implements TestInstancePostProcessor, TestInstancePreDestroyCallback {

  public static final String STORE_TASKANA_ENTITY_MAP = "taskanaEntityMap";

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context)
      throws Exception {
    Class<?> testClass = testInstance.getClass();
    if (isTopLevelClass(testClass)
        || isAnnotated(testClass, CleanTaskanaContext.class)
        || isAnnotated(testClass, WithServiceProvider.class)
        || isAnnotated(testClass, WithServiceProviders.class)
        || testInstance instanceof TaskanaConfigurationModifier) {
      Store store = getClassLevelStore(context);
      TaskanaConfiguration.Builder taskanaConfigurationBuilder =
          createDefaultTaskanaConfigurationBuilder(store);

      if (testInstance instanceof TaskanaConfigurationModifier) {
        TaskanaConfigurationModifier modifier = (TaskanaConfigurationModifier) testInstance;
        taskanaConfigurationBuilder = modifier.modify(taskanaConfigurationBuilder);
      }

      TaskanaEngine taskanaEngine;
      try (MockedStatic<SpiLoader> staticMock = Mockito.mockStatic(SpiLoader.class)) {
        ServiceProviderExtractor.extractServiceProviders(
                testClass, extractEnclosingTestInstances(testInstance))
            .forEach(
                (spi, serviceProviders) ->
                    staticMock.when(() -> SpiLoader.load(spi)).thenReturn(serviceProviders));
        taskanaEngine =
            TaskanaEngine.buildTaskanaEngine(
                taskanaConfigurationBuilder.build(), ConnectionManagementMode.AUTOCOMMIT);
      }

      store.put(STORE_TASKANA_ENTITY_MAP, generateTaskanaEntityMap(taskanaEngine));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void preDestroyTestInstance(ExtensionContext context) {
    if (isTopLevelClass(context.getRequiredTestClass())) {
      Map<Class<?>, Object> entityMap =
          (Map<Class<?>, Object>) getClassLevelStore(context).get(STORE_TASKANA_ENTITY_MAP);
      TaskanaEngineImpl taskanaEngineImpl =
          (TaskanaEngineImpl) entityMap.get(TaskanaEngineImpl.class);
      Optional.ofNullable(taskanaEngineImpl.getJobScheduler()).ifPresent(JobScheduler::stop);
    }
  }

  private static Map<Class<?>, Object> extractEnclosingTestInstances(Object instance) {
    HashMap<Class<?>, Object> instanceByClass = new HashMap<>();
    while (instance != null) {
      instanceByClass.put(instance.getClass(), instance);
      instance = ReflectionUtil.getEnclosingInstance(instance);
    }
    return instanceByClass;
  }

  private static TaskanaConfiguration.Builder createDefaultTaskanaConfigurationBuilder(
      Store store) {
    String schemaName = store.get(TestContainerExtension.STORE_SCHEMA_NAME, String.class);
    if (schemaName == null) {
      throw new JUnitException("Expected schemaName to be defined in store, but it's not.");
    }
    DataSource dataSource = store.get(TestContainerExtension.STORE_DATA_SOURCE, DataSource.class);
    if (dataSource == null) {
      throw new JUnitException("Expected dataSource to be defined in store, but it's not.");
    }

    return new TaskanaConfiguration.Builder(dataSource, false, schemaName).initTaskanaProperties();
  }

  private static Map<Class<?>, Object> generateTaskanaEntityMap(TaskanaEngine taskanaEngine)
      throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    TaskanaEngineProxy taskanaEngineProxy = new TaskanaEngineProxy(taskanaEngine);
    MonitorService monitorService = taskanaEngine.getMonitorService();
    WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
    ClassificationService classificationService = taskanaEngine.getClassificationService();
    ConfigurationService configurationService = taskanaEngine.getConfigurationService();
    JobService jobService = taskanaEngine.getJobService();
    CurrentUserContext currentUserContext = taskanaEngine.getCurrentUserContext();
    UserService userService = taskanaEngine.getUserService();
    SqlSession sqlSession = taskanaEngineProxy.getSqlSession();
    WorkingTimeCalculator workingTimeCalculator = taskanaEngine.getWorkingTimeCalculator();
    JobMapper jobMapper = getJobMapper(taskanaEngine);
    return Map.ofEntries(
        Map.entry(TaskanaConfiguration.class, taskanaEngine.getConfiguration()),
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
        Map.entry(ConfigurationService.class, configurationService),
        Map.entry(ConfigurationServiceImpl.class, configurationService),
        Map.entry(JobService.class, jobService),
        Map.entry(JobServiceImpl.class, jobService),
        Map.entry(CurrentUserContext.class, currentUserContext),
        Map.entry(CurrentUserContextImpl.class, currentUserContext),
        Map.entry(WorkingTimeCalculator.class, workingTimeCalculator),
        Map.entry(WorkingTimeCalculatorImpl.class, workingTimeCalculator),
        Map.entry(ConfigurationMapper.class, sqlSession.getMapper(ConfigurationMapper.class)),
        Map.entry(UserService.class, userService),
        Map.entry(UserServiceImpl.class, userService),
        Map.entry(JobMapper.class, jobMapper));
  }

  private static JobMapper getJobMapper(TaskanaEngine taskanaEngine)
      throws NoSuchFieldException, IllegalAccessException {

    Field sessionManagerField = TaskanaEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager =
        (SqlSessionManager) sessionManagerField.get(taskanaEngine);

    return sqlSessionManager.getMapper(JobMapper.class);
  }
}
