package io.kadai.testapi.util;

import static org.junit.platform.commons.support.AnnotationSupport.findRepeatableAnnotations;

import io.kadai.spi.history.api.KadaiHistory;
import io.kadai.spi.priority.api.PriorityServiceProvider;
import io.kadai.spi.routing.api.TaskRoutingProvider;
import io.kadai.spi.task.api.AfterRequestChangesProvider;
import io.kadai.spi.task.api.AfterRequestReviewProvider;
import io.kadai.spi.task.api.BeforeRequestChangesProvider;
import io.kadai.spi.task.api.BeforeRequestReviewProvider;
import io.kadai.spi.task.api.CreateTaskPreprocessor;
import io.kadai.spi.task.api.ReviewRequiredProvider;
import io.kadai.spi.task.api.TaskEndstatePreprocessor;
import io.kadai.testapi.WithServiceProvider;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.support.ReflectionSupport;

public class ServiceProviderExtractor {

  private static final Set<Class<?>> KADAI_SERVICE_PROVIDER_INTERFACES =
      Set.of(
          KadaiHistory.class,
          PriorityServiceProvider.class,
          TaskRoutingProvider.class,
          CreateTaskPreprocessor.class,
          ReviewRequiredProvider.class,
          BeforeRequestReviewProvider.class,
          AfterRequestReviewProvider.class,
          BeforeRequestChangesProvider.class,
          AfterRequestChangesProvider.class,
          TaskEndstatePreprocessor.class);

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
    if (!KADAI_SERVICE_PROVIDER_INTERFACES.contains(spi)) {
      throw new JUnitException(String.format("SPI '%s' is not a KADAI SPI.", spi));
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
        .toList();
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
