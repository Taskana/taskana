package acceptance;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import pro.taskana.common.test.config.TestContainerExtension;

public class TaskanaIntegrationTestExtension implements ParameterResolver, AfterAllCallback {

  private final TaskanaDependencyInjectionExtension dependencyInjectionExtension;
  private final TestContainerExtension testContainerExtension;

  public TaskanaIntegrationTestExtension() throws Exception {
    testContainerExtension = new TestContainerExtension();
    dependencyInjectionExtension =
        new TaskanaDependencyInjectionExtension(testContainerExtension.getDataSource());
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

  @Override
  public void afterAll(ExtensionContext context) {
    testContainerExtension.afterAll(context);
  }
}
