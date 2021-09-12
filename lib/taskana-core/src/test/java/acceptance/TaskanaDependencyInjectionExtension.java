package acceptance;

import java.util.Map;
import javax.sql.DataSource;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.JUnitException;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.internal.ClassificationServiceImpl;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.internal.JobServiceImpl;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.monitor.internal.MonitorServiceImpl;
import pro.taskana.task.internal.TaskServiceImpl;
import pro.taskana.workbasket.internal.WorkbasketServiceImpl;

public class TaskanaDependencyInjectionExtension implements ParameterResolver {

  private final Map<Class<?>, Object> instanceByClass;

  public TaskanaDependencyInjectionExtension(DataSource dataSource) throws Exception {
    String schemaName = DataSourceGenerator.getSchemaName();
    TaskanaEngineConfiguration taskanaEngineConfiguration =
        new TaskanaEngineConfiguration(dataSource, false, schemaName);
    taskanaEngineConfiguration.setGermanPublicHolidaysEnabled(true);
    TaskanaEngine taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
    taskanaEngine.setConnectionManagementMode(ConnectionManagementMode.AUTOCOMMIT);
    instanceByClass =
        Map.ofEntries(
            Map.entry(TaskanaEngineConfiguration.class, taskanaEngineConfiguration),
            Map.entry(TaskanaEngineImpl.class, taskanaEngine),
            Map.entry(TaskServiceImpl.class, taskanaEngine.getTaskService()),
            Map.entry(MonitorServiceImpl.class, taskanaEngine.getMonitorService()),
            Map.entry(WorkbasketServiceImpl.class, taskanaEngine.getWorkbasketService()),
            Map.entry(ClassificationServiceImpl.class, taskanaEngine.getClassificationService()),
            Map.entry(JobServiceImpl.class, taskanaEngine.getJobService()),
            Map.entry(
                WorkingDaysToDaysConverter.class, taskanaEngine.getWorkingDaysToDaysConverter()));
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return instanceByClass.keySet().stream()
        .anyMatch(getParameterType(parameterContext)::isAssignableFrom);
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return instanceByClass.keySet().stream()
        .filter(getParameterType(parameterContext)::isAssignableFrom)
        .map(instanceByClass::get)
        .findFirst()
        .orElseThrow(() -> new JUnitException("This should never happen."));
  }

  private Class<?> getParameterType(ParameterContext parameterContext) {
    return parameterContext.getParameter().getType();
  }
}
