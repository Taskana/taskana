package pro.taskana.testapi.util;

import static org.junit.platform.commons.support.AnnotationSupport.findRepeatableAnnotations;
import static pro.taskana.common.internal.util.CheckedFunction.wrap;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.platform.commons.JUnitException;

import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.spi.task.api.CreateTaskPreprocessor;
import pro.taskana.testapi.WithServiceProvider;

public class ServiceProviderExtractor {

  private static final Set<Class<?>> TASKANA_SERVICE_PROVIDER_INTERFACES =
      Set.of(
          TaskanaHistory.class,
          PriorityServiceProvider.class,
          TaskRoutingProvider.class,
          CreateTaskPreprocessor.class);

  private ServiceProviderExtractor() {
    throw new IllegalStateException("utility class");
  }

  public static Map<Class<?>, List<Object>> extractServiceProviders(Class<?> testClass) {
    List<WithServiceProvider> withServiceProviders =
        findRepeatableAnnotations(testClass, WithServiceProvider.class);

    return groupServiceProvidersByServiceProviderInterface(withServiceProviders).entrySet().stream()
        .peek(entry -> validateServiceProviders(entry.getKey(), entry.getValue()))
        .collect(
            Collectors.toMap(
                Entry::getKey, entry -> instantiateServiceProviders(entry.getValue())));
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

  private static List<Object> instantiateServiceProviders(List<Class<?>> serviceProviders) {
    return serviceProviders.stream()
        .map(wrap(Class::getDeclaredConstructor))
        .map(wrap(Constructor::newInstance))
        .collect(Collectors.toList());
  }
}
