package acceptance;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.internal.util.ReflectionUtil;
import pro.taskana.testapi.extensions.TestContainerExtension;

class TaskanaConfigurationTest {

  @TestFactory
  Stream<DynamicTest> should_SaveUnmodifiableCollections() {
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(
                TestContainerExtension.createDataSourceForH2(), false, "TASKANA")
            .initTaskanaProperties()
            .build();

    Stream<Field> fields =
        ReflectionUtil.retrieveAllFields(TaskanaConfiguration.class).stream()
            .filter(f -> Collection.class.isAssignableFrom(f.getType()));

    ThrowingConsumer<Field> testCase =
        field -> {
          field.setAccessible(true);
          Collection<?> o = (Collection<?>) field.get(configuration);

          // PLEASE do not change this to the _assertThatThrownBy_ syntax.
          // That syntax does not respect the given description and thus might confuse future devs.
          assertThatExceptionOfType(UnsupportedOperationException.class)
              .as("Field '%s' should be an unmodifiable Collection", field.getName())
              .isThrownBy(() -> o.add(null));
        };

    return DynamicTest.stream(fields, Field::getName, testCase);
  }

  @TestFactory
  Stream<DynamicTest> should_SaveUnmodifiableMaps() {
    TaskanaConfiguration configuration =
        new TaskanaConfiguration.Builder(
                TestContainerExtension.createDataSourceForH2(), false, "TASKANA")
            .initTaskanaProperties()
            .build();

    Stream<Field> fields =
        ReflectionUtil.retrieveAllFields(TaskanaConfiguration.class).stream()
            .filter(f -> Map.class.isAssignableFrom(f.getType()));

    ThrowingConsumer<Field> testCase =
        field -> {
          field.setAccessible(true);
          Map<?, ?> o = (Map<?, ?>) field.get(configuration);

          // PLEASE do not change this to the _assertThatThrownBy_ syntax.
          // That syntax does not respect the given description and thus might confuse future devs.
          assertThatExceptionOfType(UnsupportedOperationException.class)
              .as("Field '%s' should be an unmodifiable Collection", field.getName())
              .isThrownBy(() -> o.put(null, null));
        };

    return DynamicTest.stream(fields, Field::getName, testCase);
  }
}
