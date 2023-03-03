/*-
 * #%L
 * pro.taskana:taskana-common
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.internal.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;

import pro.taskana.common.internal.util.TopLevelTestClass.FirstNestedClass;
import pro.taskana.common.internal.util.TopLevelTestClass.FirstNestedClass.SecondNestedClass;

class ReflectionUtilTest {

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

  @Test
  void should_ReturnNull_For_TopLevelClass() {
    TopLevelTestClass topLevelTestClass = new TopLevelTestClass();

    Object enclosingInstance = ReflectionUtil.getEnclosingInstance(topLevelTestClass);

    assertThat(enclosingInstance).isNull();
  }

  @Test
  void should_ReturnTopLevelInstance_For_NestedInstance() {
    TopLevelTestClass topLevelTestClass = new TopLevelTestClass();
    FirstNestedClass firstNestedClass = topLevelTestClass.new FirstNestedClass();

    Object enclosingInstance = ReflectionUtil.getEnclosingInstance(firstNestedClass);

    assertThat(enclosingInstance).isSameAs(topLevelTestClass);
  }

  @Test
  void should_ReturnNestedInstance_For_NestedNestedInstance() {
    TopLevelTestClass topLevelTestClass = new TopLevelTestClass();
    FirstNestedClass firstNestedClass = topLevelTestClass.new FirstNestedClass();
    SecondNestedClass secondNestedClass = firstNestedClass.new SecondNestedClass();

    Object enclosingInstance = ReflectionUtil.getEnclosingInstance(secondNestedClass);

    assertThat(enclosingInstance).isSameAs(firstNestedClass);
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

@SuppressWarnings({"checkstyle:OneTopLevelClass", "InnerClassMayBeStatic", "unused"})
class TopLevelTestClass {
  String someField;

  class FirstNestedClass {
    String someField;

    class SecondNestedClass {
      String someField;
    }
  }
}
