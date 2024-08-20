package io.kadai.testapi.builder;

import static io.kadai.testapi.builder.ObjectReferenceBuilder.newObjectReference;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import org.junit.jupiter.api.Test;

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
