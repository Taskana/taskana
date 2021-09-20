package testapi.util;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import testapi.CleanTaskanaContext;
import testapi.TaskanaEngineConfigurationModifier;
import testapi.WithServiceProvider;

public class ExtensionCommunicator {

  private ExtensionCommunicator() {
    throw new IllegalStateException("utility class");
  }

  public static boolean isTopLevelClass(Class<?> testClass) {
    return testClass.getEnclosingClass() == null;
  }

  public static Store getClassLevelStore(ExtensionContext context) {
    return getClassLevelStore(context, context.getRequiredTestClass());
  }

  public static Store getClassLevelStore(ExtensionContext context, Class<?> testClass) {
    return context.getStore(determineNamespace(testClass));
  }

  private static Namespace determineNamespace(Class<?> testClass) {
    if (isTopLevelClass(testClass)) {
      return Namespace.create(testClass);
    } else if (isAnnotated(testClass, CleanTaskanaContext.class)) {
      return Namespace.create(testClass.getEnclosingClass(), testClass, CleanTaskanaContext.class);
    } else if (isAnnotated(testClass, WithServiceProvider.class)) {
      return Namespace.create(testClass.getEnclosingClass(), testClass, WithServiceProvider.class);
    } else if (TaskanaEngineConfigurationModifier.class.isAssignableFrom(testClass)) {
      return Namespace.create(
          testClass.getEnclosingClass(), testClass, TaskanaEngineConfigurationModifier.class);
    }
    return Namespace.create(testClass.getEnclosingClass());
  }
}
