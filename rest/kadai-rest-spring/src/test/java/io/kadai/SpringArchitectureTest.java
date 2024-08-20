package io.kadai;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static java.util.function.Predicate.not;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import io.kadai.common.rest.QueryParameter;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SpringArchitectureTest {
  private static JavaClasses importedClasses;

  @BeforeAll
  static void init() {
    // time intensive operation should only be done once
    importedClasses = new ClassFileImporter().importPackages("io.kadai", "acceptance");
  }

  @Test
  void should_AnnotateAllFieldsWithJsonProperty_When_ImplementingQueryParameter() {
    ArchRule myRule =
        classes().that().implement(QueryParameter.class).should(shouldOnlyHaveAnnotatedFields());

    myRule.check(importedClasses);
  }

  private ArchCondition<JavaClass> shouldOnlyHaveAnnotatedFields() {
    return new ArchCondition<>("all fields should have a @JsonProperty or @JsonIgnore annotation") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents events) {
        javaClass.getAllFields().stream()
            .filter(not(field -> field.reflect().isSynthetic()))
            .filter(
                field ->
                    Stream.of(JsonProperty.class, JsonIgnore.class)
                        .map(field::tryGetAnnotationOfType)
                        .noneMatch(Optional::isPresent))
            .map(
                field ->
                    SimpleConditionEvent.violated(
                        javaClass,
                        String.format(
                            "Field '%s' in class '%s' is not annotated with a json annotation",
                            field, javaClass)))
            .forEach(events::add);
      }
    };
  }
}
