package acceptance.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.task.internal.ObjectReferenceBuilder.newObjectReference;

import org.junit.jupiter.api.Test;

import pro.taskana.task.api.models.ObjectReference;

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

    ObjectReference expectedObjectReference = new ObjectReference();
    expectedObjectReference.setCompany("Company1");
    expectedObjectReference.setSystem("System1");
    expectedObjectReference.setSystemInstance("Instance1");
    expectedObjectReference.setType("Type1");
    expectedObjectReference.setValue("Value1");

    assertThat(objectReference)
        .hasNoNullFieldsOrPropertiesExcept("id")
        .isEqualTo(expectedObjectReference);
  }
}
