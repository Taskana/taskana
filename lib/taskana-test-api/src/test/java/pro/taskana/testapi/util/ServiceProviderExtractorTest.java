package pro.taskana.testapi.util;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.testapi.util.ServiceProviderExtractor.extractServiceProviders;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.platform.commons.JUnitException;

import pro.taskana.common.internal.util.ReflectionUtil;
import pro.taskana.spi.priority.api.PriorityServiceProvider;
import pro.taskana.spi.task.api.CreateTaskPreprocessor;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.testapi.WithServiceProvider;

class ServiceProviderExtractorTest {

  static class StaticCreateTaskPreprocessor implements CreateTaskPreprocessor {
    @Override
    public void processTaskBeforeCreation(Task taskToProcess) {
      // implementation not important for the tests
    }
  }

  static class DummyTaskPreprocessor1 implements CreateTaskPreprocessor {
    @Override
    public void processTaskBeforeCreation(Task taskToProcess) {
      // implementation not important for the tests
    }
  }

  static class DummyTaskPreprocessor2 implements CreateTaskPreprocessor {
    @Override
    public void processTaskBeforeCreation(Task taskToProcess) {
      // implementation not important for the tests
    }
  }

  static class DummyPriorityServiceProvider1 implements PriorityServiceProvider {
    @Override
    public OptionalInt calculatePriority(TaskSummary taskSummary) {
      // implementation not important for the tests
      return OptionalInt.empty();
    }
  }

  static class DummyPriorityServiceProvider2 implements PriorityServiceProvider {
    @Override
    public OptionalInt calculatePriority(TaskSummary taskSummary) {
      // implementation not important for the tests
      return OptionalInt.empty();
    }
  }

  private static class PrivateStaticCreateTaskPreprocessor implements CreateTaskPreprocessor {
    @Override
    public void processTaskBeforeCreation(Task taskToProcess) {
      // implementation not important for the tests
    }
  }

  @SuppressWarnings("InnerClassMayBeStatic")
  class NonStaticCreateTaskPreprocessorOutsideOfTestClass implements CreateTaskPreprocessor {
    @Override
    public void processTaskBeforeCreation(Task taskToProcess) {
      // implementation not important for the tests
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ServiceProviderInstantiation {

    private final Map<Class<?>, Object> enclosingTestInstancesByClass =
        Map.ofEntries(
            entry(ServiceProviderInstantiation.class, this),
            entry(ServiceProviderExtractorTest.class, ServiceProviderExtractorTest.this));

    @Test
    void should_ReturnEmptyMap_When_NoServiceProviderIsDefined() {
      class ExampleTestClassWithNoServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithNoServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).isEmpty();
    }

    @Test
    void should_InstantiateServiceProvider_When_ServiceProviderIsTopLevelClass() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = TopLevelCreateTaskPreprocessor.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(TopLevelCreateTaskPreprocessor.class)
          .extracting(ReflectionUtil::getEnclosingInstance)
          .containsOnlyNulls();
    }

    @Test
    void should_InstantiateServiceProvider_When_ServiceProviderIsStaticMemberClass() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = StaticCreateTaskPreprocessor.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(StaticCreateTaskPreprocessor.class)
          .extracting(ReflectionUtil::getEnclosingInstance)
          .containsOnlyNulls();
    }

    @Test
    void should_InstantiateServiceProvider_When_ServiceProviderIsPrivateStaticMemberClass() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = PrivateStaticCreateTaskPreprocessor.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(PrivateStaticCreateTaskPreprocessor.class)
          .extracting(ReflectionUtil::getEnclosingInstance)
          .containsOnlyNulls();
    }

    @Test
    void should_InstantiateServiceProvider_When_ServiceProviderIsLocalClass() {
      class LocalCreateTaskPreprocessor implements CreateTaskPreprocessor {
        @Override
        public void processTaskBeforeCreation(Task taskToProcess) {
          // implementation not important for the tests
        }
      }

      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = LocalCreateTaskPreprocessor.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(LocalCreateTaskPreprocessor.class)
          .extracting(ReflectionUtil::getEnclosingInstance)
          .containsExactly(this);
    }

    @Test
    void should_InstantiateServiceProvider_When_ServiceProviderIsNonStaticMemberClass() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = NonStaticCreateTaskPreprocessorInsideOfTestClass.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(NonStaticCreateTaskPreprocessorInsideOfTestClass.class)
          .extracting(ReflectionUtil::getEnclosingInstance)
          .containsExactly(this);
    }

    @Test
    void should_InstantiateServiceProvider_When_ServiceProviderIsPrivateNonStaticMemberClass() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = PrivateNonStaticCreateTaskPreprocessor.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(PrivateNonStaticCreateTaskPreprocessor.class)
          .extracting(ReflectionUtil::getEnclosingInstance)
          .containsExactly(this);
    }

    @Test
    void should_InstantiateServiceProvider_When_ItIsNonStaticMemberClassOutsideOfTestClass() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = NonStaticCreateTaskPreprocessorOutsideOfTestClass.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(NonStaticCreateTaskPreprocessorOutsideOfTestClass.class)
          .extracting(ReflectionUtil::getEnclosingInstance)
          .containsExactly(ServiceProviderExtractorTest.this);
    }

    class NonStaticCreateTaskPreprocessorInsideOfTestClass implements CreateTaskPreprocessor {
      @Override
      public void processTaskBeforeCreation(Task taskToProcess) {
        // implementation not important for the tests
      }
    }

    class PrivateNonStaticCreateTaskPreprocessor implements CreateTaskPreprocessor {

      @Override
      public void processTaskBeforeCreation(Task taskToProcess) {
        // implementation not important for the tests
      }
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ExtractServiceProvidersFromSingleServiceProviderInterface {

    private final Map<Class<?>, Object> enclosingTestInstancesByClass =
        Map.ofEntries(
            entry(ExtractServiceProvidersFromSingleServiceProviderInterface.class, this),
            entry(ServiceProviderExtractorTest.class, ServiceProviderExtractorTest.this));

    @Test
    void should_ExtractServiceProvider() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = DummyTaskPreprocessor1.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .asList()
          .hasExactlyElementsOfTypes(DummyTaskPreprocessor1.class);
    }

    @Test
    void should_ExtractMultipleServiceProviders_When_MultipleAreDefinedInOneAnnotation() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = {DummyTaskPreprocessor1.class, DummyTaskPreprocessor2.class})
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .asList()
          .hasExactlyElementsOfTypes(DummyTaskPreprocessor1.class, DummyTaskPreprocessor2.class);
    }

    @Test
    void should_ExtractMultipleServiceProviders_When_MultipleAreDefinedInMultipleAnnotations() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = DummyTaskPreprocessor1.class)
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = DummyTaskPreprocessor2.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(DummyTaskPreprocessor1.class, DummyTaskPreprocessor2.class);
    }

    @Test
    void should_ExtractSameServiceProviderMultipleTimes_When_ItIsDefinedMultipleTimes() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = {DummyTaskPreprocessor1.class, DummyTaskPreprocessor1.class})
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders).containsOnlyKeys(CreateTaskPreprocessor.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(DummyTaskPreprocessor1.class, DummyTaskPreprocessor1.class);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ExtractMultipleServiceProvidersFromMultipleServiceProviderInterfaces {

    private final Map<Class<?>, Object> enclosingTestInstancesByClass =
        Map.ofEntries(
            entry(ExtractMultipleServiceProvidersFromMultipleServiceProviderInterfaces.class, this),
            entry(ServiceProviderExtractorTest.class, ServiceProviderExtractorTest.this));

    @Test
    void should_ExtractServiceProviders() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = DummyTaskPreprocessor1.class)
      @WithServiceProvider(
          serviceProviderInterface = PriorityServiceProvider.class,
          serviceProviders = DummyPriorityServiceProvider1.class)
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders)
          .containsOnlyKeys(CreateTaskPreprocessor.class, PriorityServiceProvider.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(DummyTaskPreprocessor1.class);
      assertThat(extractServiceProviders.get(PriorityServiceProvider.class))
          .hasExactlyElementsOfTypes(DummyPriorityServiceProvider1.class);
    }

    @Test
    void should_ExtractMultipleServiceProviders_When_MultipleAreDefinedInOneAnnotation() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = {DummyTaskPreprocessor1.class, DummyTaskPreprocessor2.class})
      @WithServiceProvider(
          serviceProviderInterface = PriorityServiceProvider.class,
          serviceProviders = {
            DummyPriorityServiceProvider1.class,
            DummyPriorityServiceProvider2.class
          })
      class ExampleTestClassWithServiceProviders {}

      Map<Class<?>, List<Object>> extractServiceProviders =
          extractServiceProviders(
              ExampleTestClassWithServiceProviders.class, enclosingTestInstancesByClass);

      assertThat(extractServiceProviders)
          .containsOnlyKeys(CreateTaskPreprocessor.class, PriorityServiceProvider.class);
      assertThat(extractServiceProviders.get(CreateTaskPreprocessor.class))
          .hasExactlyElementsOfTypes(DummyTaskPreprocessor1.class, DummyTaskPreprocessor2.class);
      assertThat(extractServiceProviders.get(PriorityServiceProvider.class))
          .hasExactlyElementsOfTypes(
              DummyPriorityServiceProvider1.class, DummyPriorityServiceProvider2.class);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ErrorHandling {

    @Test
    void should_ThrowException_When_ServiceProviderInterfaceIsUnknown() {
      @WithServiceProvider(
          serviceProviderInterface = ErrorHandling.class,
          serviceProviders = DummyTaskPreprocessor1.class)
      class ExampleTestClassWithServiceProviders {}

      ThrowingCallable call =
          () -> extractServiceProviders(ExampleTestClassWithServiceProviders.class, Map.of());

      assertThatThrownBy(call)
          .isInstanceOf(JUnitException.class)
          .hasMessage("SPI '%s' is not a TASKANA SPI.", ErrorHandling.class);
    }

    @Test
    void should_ThrowException_When_ServiceProviderIsIncompatibleToServiceProviderInterface() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = DummyPriorityServiceProvider1.class)
      class ExampleTestClassWithServiceProviders {}

      ThrowingCallable call =
          () -> extractServiceProviders(ExampleTestClassWithServiceProviders.class, Map.of());

      assertThatThrownBy(call)
          .isInstanceOf(JUnitException.class)
          .hasMessage(
              "At least one ServiceProvider does not implement the requested SPI '%s'",
              CreateTaskPreprocessor.class);
    }

    @Test
    void should_ThrowException_When_AnyServiceProviderIsIncompatibleToServiceProviderInterface() {
      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = {DummyTaskPreprocessor1.class, DummyPriorityServiceProvider1.class})
      class ExampleTestClassWithServiceProviders {}

      ThrowingCallable call =
          () -> extractServiceProviders(ExampleTestClassWithServiceProviders.class, Map.of());

      assertThatThrownBy(call)
          .isInstanceOf(JUnitException.class)
          .hasMessage(
              "At least one ServiceProvider does not implement the requested SPI '%s'",
              CreateTaskPreprocessor.class);
    }

    @Test
    void should_ThrowException_When_LocalServiceProviderUsesAMethodVariable() {
      String methodVariable = "foobar";
      class LocalCreateTaskPreprocessor implements CreateTaskPreprocessor {
        @Override
        public void processTaskBeforeCreation(Task taskToProcess) {
          // implementation not important for the tests
          taskToProcess.setOwner(methodVariable);
        }
      }

      @WithServiceProvider(
          serviceProviderInterface = CreateTaskPreprocessor.class,
          serviceProviders = LocalCreateTaskPreprocessor.class)
      class ExampleTestClassWithServiceProviders {}

      ThrowingCallable call =
          () -> extractServiceProviders(ExampleTestClassWithServiceProviders.class, Map.of());

      assertThatThrownBy(call)
          .isInstanceOf(JUnitException.class)
          .hasMessage("test-api does not support local class which accesses method variables");
    }
  }
}
