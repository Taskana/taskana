package io.kadai.testapi.extensions;

import static io.kadai.testapi.util.ExtensionCommunicator.getClassLevelStore;
import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedFields;

import io.kadai.testapi.KadaiInject;
import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.JUnitException;

public class KadaiDependencyInjectionExtension
    implements ParameterResolver, TestInstancePostProcessor {

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Map<Class<?>, Object> instanceByClass = getKadaiEntityMap(extensionContext);
    return instanceByClass != null
        && instanceByClass.containsKey(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return getKadaiEntityMap(extensionContext).get(parameterContext.getParameter().getType());
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context)
      throws Exception {
    Map<Class<?>, Object> instanceByClass = getKadaiEntityMap(context);
    if (instanceByClass == null) {
      throw new JUnitException("Something went wrong! Could not find KADAI entity Map in store.");
    }

    for (Field field : findAnnotatedFields(testInstance.getClass(), KadaiInject.class)) {
      Object toInject = instanceByClass.get(field.getType());
      if (toInject != null) {
        field.setAccessible(true);
        field.set(testInstance, toInject);
      } else {
        throw new JUnitException(
            String.format(
                "Cannot inject field '%s'. " + "Type '%s' is not an injectable KADAI type",
                field.getName(), field.getType()));
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<Class<?>, Object> getKadaiEntityMap(ExtensionContext extensionContext) {
    return (Map<Class<?>, Object>)
        getClassLevelStore(extensionContext)
            .get(KadaiInitializationExtension.STORE_KADAI_ENTITY_MAP);
  }
}
