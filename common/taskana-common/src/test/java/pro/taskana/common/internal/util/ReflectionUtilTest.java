package pro.taskana.common.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ReflectionUtilTest {

  @Test
  void should_RetrieveAllFieldsFromClassAndSuperClass() {
    List<Field> fields = ReflectionUtil.retrieveAllFields(SubSubTestClass.class);

    assertThat(fields)
        .extracting(Field::getName)
        .containsExactlyInAnyOrder("fieldA", "fieldB", "fieldC");
  }

  @Test
  void should_WrapPrimitiveClassToItsWrapperClass() {
    Class<Integer> wrap = ReflectionUtil.wrap(int.class);

    assertThat(wrap).isEqualTo(Integer.class);
  }

  @Test
  void should_NotWrapNonPrimitiveClass() {
    Class<TestClass> wrap = ReflectionUtil.wrap(TestClass.class);

    assertThat(wrap).isEqualTo(TestClass.class);
  }

  static class TestClass {
    @SuppressWarnings("unused")
    String fieldA;
  }

  static class SubTestClass extends TestClass {
    @SuppressWarnings("unused")
    String fieldB;
  }

  static class SubSubTestClass extends SubTestClass {
    @SuppressWarnings("unused")
    String fieldC;
  }
}
