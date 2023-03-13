package pro.taskana.testapi.extensions;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotatedFields;
import static pro.taskana.testapi.util.ExtensionCommunicator.getClassLevelStore;

import java.lang.reflect.Field;
import java.util.Map;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.JUnitException;
import pro.taskana.testapi.TaskanaInject;

public class TaskanaDependencyInjectionExtension
    implements ParameterResolver, TestInstancePostProcessor {

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Map<Class<?>, Object> instanceByClass = getTaskanaEntityMap(extensionContext);
    return instanceByClass != null
        && instanceByClass.containsKey(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return getTaskanaEntityMap(extensionContext).get(parameterContext.getParameter().getType());
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context)
      throws Exception {
    Map<Class<?>, Object> instanceByClass = getTaskanaEntityMap(context);
    if (instanceByClass == null) {
      throw new JUnitException("Something went wrong! Could not find TASKANA entity Map in store.");
    }

    for (Field field : findAnnotatedFields(testInstance.getClass(), TaskanaInject.class)) {
      Object toInject = instanceByClass.get(field.getType());
      if (toInject != null) {
        field.setAccessible(true);
        field.set(testInstance, toInject);
      } else {
        throw new JUnitException(
            String.format(
                "Cannot inject field '%s'. " + "Type '%s' is not an injectable TASKANA type",
                field.getName(), field.getType()));
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static Map<Class<?>, Object> getTaskanaEntityMap(ExtensionContext extensionContext) {
    return (Map<Class<?>, Object>)
        getClassLevelStore(extensionContext)
            .get(TaskanaInitializationExtension.STORE_TASKANA_ENTITY_MAP);
  }
}
