package io.kadai.testapi.extensions;

import static io.kadai.testapi.util.ExtensionCommunicator.getClassLevelStore;
import static io.kadai.testapi.util.ExtensionCommunicator.isTopLevelClass;
import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.internal.ClassificationServiceImpl;
import io.kadai.common.api.ConfigurationService;
import io.kadai.common.api.JobService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiEngine.ConnectionManagementMode;
import io.kadai.common.api.WorkingTimeCalculator;
import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.common.internal.ConfigurationMapper;
import io.kadai.common.internal.ConfigurationServiceImpl;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.common.internal.JobMapper;
import io.kadai.common.internal.JobServiceImpl;
import io.kadai.common.internal.KadaiEngineImpl;
import io.kadai.common.internal.jobs.JobScheduler;
import io.kadai.common.internal.security.CurrentUserContextImpl;
import io.kadai.common.internal.util.ReflectionUtil;
import io.kadai.common.internal.util.SpiLoader;
import io.kadai.common.internal.workingtime.WorkingTimeCalculatorImpl;
import io.kadai.monitor.api.MonitorService;
import io.kadai.monitor.internal.MonitorServiceImpl;
import io.kadai.task.api.TaskService;
import io.kadai.task.internal.TaskServiceImpl;
import io.kadai.testapi.CleanKadaiContext;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiEngineProxy;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.WithServiceProvider.WithServiceProviders;
import io.kadai.testapi.util.ServiceProviderExtractor;
import io.kadai.user.api.UserService;
import io.kadai.user.internal.UserServiceImpl;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.internal.WorkbasketServiceImpl;
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

public class KadaiInitializationExtension
    implements TestInstancePostProcessor, TestInstancePreDestroyCallback {

  public static final String STORE_KADAI_ENTITY_MAP = "kadaiEntityMap";

  private static Map<Class<?>, Object> extractEnclosingTestInstances(Object instance) {
    HashMap<Class<?>, Object> instanceByClass = new HashMap<>();
    while (instance != null) {
      instanceByClass.put(instance.getClass(), instance);
      instance = ReflectionUtil.getEnclosingInstance(instance);
    }
    return instanceByClass;
  }

  private static KadaiConfiguration.Builder createDefaultKadaiConfigurationBuilder(Store store) {
    String schemaName = store.get(TestContainerExtension.STORE_SCHEMA_NAME, String.class);
    if (schemaName == null) {
      throw new JUnitException("Expected schemaName to be defined in store, but it's not.");
    }
    DataSource dataSource = store.get(TestContainerExtension.STORE_DATA_SOURCE, DataSource.class);
    if (dataSource == null) {
      throw new JUnitException("Expected dataSource to be defined in store, but it's not.");
    }

    return new KadaiConfiguration.Builder(dataSource, false, schemaName).initKadaiProperties();
  }

  private static Map<Class<?>, Object> generateKadaiEntityMap(KadaiEngine kadaiEngine)
      throws Exception {
    TaskService taskService = kadaiEngine.getTaskService();
    KadaiEngineProxy kadaiEngineProxy = new KadaiEngineProxy(kadaiEngine);
    MonitorService monitorService = kadaiEngine.getMonitorService();
    WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
    ClassificationService classificationService = kadaiEngine.getClassificationService();
    ConfigurationService configurationService = kadaiEngine.getConfigurationService();
    JobService jobService = kadaiEngine.getJobService();
    CurrentUserContext currentUserContext = kadaiEngine.getCurrentUserContext();
    UserService userService = kadaiEngine.getUserService();
    SqlSession sqlSession = kadaiEngineProxy.getSqlSession();
    WorkingTimeCalculator workingTimeCalculator = kadaiEngine.getWorkingTimeCalculator();
    JobMapper jobMapper = getJobMapper(kadaiEngine);
    return Map.ofEntries(
        Map.entry(KadaiConfiguration.class, kadaiEngine.getConfiguration()),
        Map.entry(KadaiEngineImpl.class, kadaiEngine),
        Map.entry(KadaiEngine.class, kadaiEngine),
        Map.entry(InternalKadaiEngine.class, kadaiEngineProxy.getEngine()),
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

  private static JobMapper getJobMapper(KadaiEngine kadaiEngine)
      throws NoSuchFieldException, IllegalAccessException {

    Field sessionManagerField = KadaiEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager = (SqlSessionManager) sessionManagerField.get(kadaiEngine);

    return sqlSessionManager.getMapper(JobMapper.class);
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context)
      throws Exception {
    Class<?> testClass = testInstance.getClass();
    if (isTopLevelClass(testClass)
        || isAnnotated(testClass, CleanKadaiContext.class)
        || isAnnotated(testClass, WithServiceProvider.class)
        || isAnnotated(testClass, WithServiceProviders.class)
        || testInstance instanceof KadaiConfigurationModifier) {
      Store store = getClassLevelStore(context);
      KadaiConfiguration.Builder kadaiConfigurationBuilder =
          createDefaultKadaiConfigurationBuilder(store);

      if (testInstance instanceof KadaiConfigurationModifier) {
        KadaiConfigurationModifier modifier = (KadaiConfigurationModifier) testInstance;
        kadaiConfigurationBuilder = modifier.modify(kadaiConfigurationBuilder);
      }

      KadaiEngine kadaiEngine;
      try (MockedStatic<SpiLoader> staticMock = Mockito.mockStatic(SpiLoader.class)) {
        ServiceProviderExtractor.extractServiceProviders(
                testClass, extractEnclosingTestInstances(testInstance))
            .forEach(
                (spi, serviceProviders) ->
                    staticMock.when(() -> SpiLoader.load(spi)).thenReturn(serviceProviders));
        kadaiEngine =
            KadaiEngine.buildKadaiEngine(
                kadaiConfigurationBuilder.build(), ConnectionManagementMode.AUTOCOMMIT);
      }

      store.put(STORE_KADAI_ENTITY_MAP, generateKadaiEntityMap(kadaiEngine));
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void preDestroyTestInstance(ExtensionContext context) {
    if (isTopLevelClass(context.getRequiredTestClass())) {
      Map<Class<?>, Object> entityMap =
          (Map<Class<?>, Object>) getClassLevelStore(context).get(STORE_KADAI_ENTITY_MAP);
      KadaiEngineImpl kadaiEngineImpl = (KadaiEngineImpl) entityMap.get(KadaiEngineImpl.class);
      Optional.ofNullable(kadaiEngineImpl.getJobScheduler()).ifPresent(JobScheduler::stop);
    }
  }
}
