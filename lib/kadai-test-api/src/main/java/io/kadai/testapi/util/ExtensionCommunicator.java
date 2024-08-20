package io.kadai.testapi.util;

import static org.junit.platform.commons.support.AnnotationSupport.isAnnotated;

import io.kadai.testapi.CleanKadaiContext;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.WithServiceProvider;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

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

  public static Class<?> getTopLevelClass(Class<?> testClazz) {
    Class<?> parent = testClazz;
    while (parent.getEnclosingClass() != null) {
      parent = parent.getEnclosingClass();
    }
    return parent;
  }

  private static Namespace determineNamespace(Class<?> testClass) {
    if (isTopLevelClass(testClass)) {
      return Namespace.create(testClass);
    } else if (isAnnotated(testClass, CleanKadaiContext.class)) {
      return Namespace.create(getTopLevelClass(testClass), testClass, CleanKadaiContext.class);
    } else if (isAnnotated(testClass, WithServiceProvider.class)) {
      return Namespace.create(getTopLevelClass(testClass), testClass, WithServiceProvider.class);
    } else if (KadaiConfigurationModifier.class.isAssignableFrom(testClass)) {
      return Namespace.create(
          getTopLevelClass(testClass), testClass, KadaiConfigurationModifier.class);
    }
    return Namespace.create(getTopLevelClass(testClass));
  }
}
