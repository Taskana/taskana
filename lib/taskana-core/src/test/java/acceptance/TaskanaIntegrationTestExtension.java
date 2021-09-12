package acceptance;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import pro.taskana.common.test.config.DataSourceGenerator;

public class TaskanaIntegrationTestExtension implements ParameterResolver {

  private final TaskanaDependencyInjectionExtension dependencyInjectionExtension;

  public TaskanaIntegrationTestExtension() throws Exception {
    dependencyInjectionExtension =
        new TaskanaDependencyInjectionExtension(DataSourceGenerator.getDataSource());
  }

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return dependencyInjectionExtension.supportsParameter(parameterContext, extensionContext);
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return dependencyInjectionExtension.resolveParameter(parameterContext, extensionContext);
  }
}
