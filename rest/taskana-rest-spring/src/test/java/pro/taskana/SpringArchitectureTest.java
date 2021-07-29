package pro.taskana;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tngtech.archunit.base.Optional;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SpringArchitectureTest {
  private static final List<String> TASKANA_SUB_PACKAGES =
      List.of(
          "pro.taskana.common.rest",
          "pro.taskana.classification.rest",
          "pro.taskana.task.rest",
          "pro.taskana.workbasket.rest",
          "pro.taskana.monitor.rest");
  private static JavaClasses importedClasses;

  @BeforeAll
  static void init() {
    // time intensive operation should only be done once
    importedClasses = new ClassFileImporter().importPackages("pro.taskana", "acceptance");
  }

  @Test
  void should_AnnotateAllFieldsWithJsonProperty_When_ImplementingQueryParameter() {
    ArchRule myRule =
        classes()
            .that()
            .implement(pro.taskana.common.rest.QueryParameter.class)
            .should(shouldOnlyHaveAnnotatedFields());

    myRule.check(importedClasses);
  }

  private ArchCondition<JavaClass> shouldOnlyHaveAnnotatedFields() {
    return new ArchCondition<JavaClass>("have all fields without a @JsonProperty annotation") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents events) {
        for (JavaField field : javaClass.getAllFields()) {
          if (!field.reflect().isSynthetic()) {
            boolean annotationIsNotPresent =
                Stream.of(JsonProperty.class, JsonIgnore.class)
                    .map(field::tryGetAnnotationOfType)
                    .noneMatch(Optional::isPresent);
            if (annotationIsNotPresent) {
              events.add(
                  SimpleConditionEvent.violated(
                      javaClass,
                      String.format(
                          "Field '%s' in class '%s' is not annotated by @JsonProperty",
                          field, javaClass)));
            }
          }
        }
      }
    };
  }
}
