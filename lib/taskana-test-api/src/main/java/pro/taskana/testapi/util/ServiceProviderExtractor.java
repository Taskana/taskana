package pro.taskana.testapi.util;

import static org.junit.platform.commons.support.AnnotationSupport.findRepeatableAnnotations;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.support.ReflectionSupport;

import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.spi.task.api.AfterRequestChangesProvider;
import pro.taskana.spi.task.api.AfterRequestReviewProvider;
import pro.taskana.spi.task.api.CreateTaskPreprocessor;
import pro.taskana.spi.task.api.ReviewRequiredProvider;
import pro.taskana.testapi.WithServiceProvider;

public class ServiceProviderExtractor {

  private static final Set<Class<?>> TASKANA_SERVICE_PROVIDER_INTERFACES =
      Set.of(
          TaskanaHistory.class,
          PriorityServiceProvider.class,
          TaskRoutingProvider.class,
          CreateTaskPreprocessor.class,
          ReviewRequiredProvider.class,
          AfterRequestReviewProvider.class,
          AfterRequestChangesProvider.class);

  private ServiceProviderExtractor() {
    throw new IllegalStateException("utility class");
  }

  public static Map<Class<?>, List<Object>> extractServiceProviders(
      Class<?> testClass, Map<Class<?>, Object> enclosingTestInstancesByClass) {
    List<WithServiceProvider> withServiceProviders =
        findRepeatableAnnotations(testClass, WithServiceProvider.class);

    return groupServiceProvidersByServiceProviderInterface(withServiceProviders).entrySet().stream()
        .peek(entry -> validateServiceProviders(entry.getKey(), entry.getValue()))
        .collect(
            Collectors.toMap(
                Entry::getKey,
                entry ->
                    instantiateServiceProviders(entry.getValue(), enclosingTestInstancesByClass)));
  }

  private static void validateServiceProviders(Class<?> spi, List<Class<?>> serviceProviders) {
    if (!TASKANA_SERVICE_PROVIDER_INTERFACES.contains(spi)) {
      throw new JUnitException(String.format("SPI '%s' is not a TASKANA SPI.", spi));
    }
    if (!serviceProviders.stream().allMatch(spi::isAssignableFrom)) {
      throw new JUnitException(
          String.format(
              "At least one ServiceProvider does not implement the requested SPI '%s'", spi));
    }
  }

  private static Map<Class<?>, List<Class<?>>> groupServiceProvidersByServiceProviderInterface(
      List<WithServiceProvider> withServiceProviders) {
    return withServiceProviders.stream()
        .collect(
            Collectors.groupingBy(
                WithServiceProvider::serviceProviderInterface,
                Collectors.flatMapping(
                    annotation -> Arrays.stream(annotation.serviceProviders()),
                    Collectors.toList())));
  }

  private static List<Object> instantiateServiceProviders(
      List<Class<?>> serviceProviders, Map<Class<?>, Object> enclosingTestInstancesByClass) {
    return serviceProviders.stream()
        .map(clz -> instantiateClass(clz, enclosingTestInstancesByClass))
        .collect(Collectors.toList());
  }

  private static Object instantiateClass(
      Class<?> clz, Map<Class<?>, Object> enclosingTestInstancesByClass) {
    // we don't have to consider anonymous classes since they can't be passed as an argument to
    // the WithServiceProvider annotation.
    if (clz.isLocalClass() || (clz.isMemberClass() && !Modifier.isStatic(clz.getModifiers()))) {
      try {
        Class<?> motherClass = clz.getEnclosingClass();
        Object motherInstance =
            enclosingTestInstancesByClass.getOrDefault(
                motherClass, instantiateClass(motherClass, enclosingTestInstancesByClass));
        return ReflectionSupport.newInstance(clz, motherInstance);
      } catch (Exception e) {
        //noinspection ConstantConditions
        if (NoSuchMethodException.class == e.getClass()) {
          throw new JUnitException(
              "test-api does not support local class which accesses method variables");
        }
        throw e;
      }
    }
    return ReflectionSupport.newInstance(clz);
  }
}
