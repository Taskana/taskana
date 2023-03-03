/*-
 * #%L
 * pro.taskana:taskana-test-api
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
package pro.taskana.testapi.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.testapi.builder.ObjectReferenceBuilder.newObjectReference;

import org.junit.jupiter.api.Test;

import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.ObjectReferenceImpl;

class ObjectReferenceBuilderTest {

  @Test
  void should_PopulateObjectReference_When_UsingEveryBuilderFunction() {
    final ObjectReference objectReference =
        newObjectReference()
            .company("Company1")
            .system("System1")
            .systemInstance("Instance1")
            .type("Type1")
            .value("Value1")
            .build();

    ObjectReferenceImpl expectedObjectReference = new ObjectReferenceImpl();
    expectedObjectReference.setCompany("Company1");
    expectedObjectReference.setSystem("System1");
    expectedObjectReference.setSystemInstance("Instance1");
    expectedObjectReference.setType("Type1");
    expectedObjectReference.setValue("Value1");

    assertThat(objectReference)
        .hasNoNullFieldsOrPropertiesExcept("id", "taskId")
        .isEqualTo(expectedObjectReference);
  }
}
