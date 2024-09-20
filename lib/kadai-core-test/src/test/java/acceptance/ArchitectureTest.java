package acceptance;

import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideOutsideOfPackage;
import static com.tngtech.archunit.core.domain.properties.CanBeAnnotated.Predicates.annotatedWith;
import static com.tngtech.archunit.lang.conditions.ArchPredicates.are;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import static com.tngtech.archunit.library.GeneralCodingRules.ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION;
import static com.tngtech.archunit.library.GeneralCodingRules.THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.USE_JAVA_UTIL_LOGGING;
import static com.tngtech.archunit.library.GeneralCodingRules.USE_JODATIME;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static com.tngtech.archunit.library.freeze.FreezingArchRule.freeze;
import static io.kadai.common.internal.util.CheckedFunction.wrap;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toCollection;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.dependencies.SliceAssignment;
import com.tngtech.archunit.library.dependencies.SliceIdentifier;
import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.WorkingTimeCalculator;
import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.api.exceptions.KadaiRuntimeException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.common.internal.Interval;
import io.kadai.common.internal.KadaiEngineImpl;
import io.kadai.common.internal.jobs.JobScheduler;
import io.kadai.common.internal.logging.LoggingAspect;
import io.kadai.common.internal.workingtime.HolidaySchedule;
import io.kadai.testapi.KadaiIntegrationTest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.UpdateProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Test architecture of classes in KADAI. For more info and examples see <a
 * href="https://www.archunit.org/userguide/html/000_Index.html">ArchUnit User Guide</a>.
 */
class ArchitectureTest {

  // region Test setup
  private static final List<String> KADAI_ROOT_PACKAGES =
      List.of(
          "io.kadai.classification",
          "io.kadai.common",
          "io.kadai.monitor",
          "io.kadai.spi",
          "io.kadai.task",
          "io.kadai.user",
          "io.kadai.workbasket");

  private static JavaClasses importedClasses;

  @BeforeAll
  static void init() {
    // time intensive operation should only be done once
    importedClasses = new ClassFileImporter().importPackages("io.kadai", "acceptance");
  }

  // endregion

  // region Coding Guidelines

  @Test
  void testMethodNamesShouldMatchAccordingToOurGuidelines() {
    methods()
        .that(
            are(
                annotatedWith(Test.class)
                    .or(annotatedWith(ParameterizedTest.class))
                    .or(annotatedWith(TestFactory.class))
                    .or(annotatedWith(TestTemplate.class))))
        .and()
        .areNotDeclaredIn(ArchitectureTest.class)
        .and()
        .areNotDeclaredIn(PojoTest.class) // we have to find a proper naming for those tests
        .should()
        .bePackagePrivate()
        .andShould()
        .haveNameMatching("^should_[A-Z][^_]+(_(For|When)_[A-Z][^_]+)?$")
        .check(importedClasses);
  }

  @Test
  void classesShouldNotUseJunit5Assertions() {
    classes()
        .that()
        .areNotAssignableFrom(ArchitectureTest.class)
        .should()
        .onlyDependOnClassesThat()
        .areNotAssignableTo(org.junit.jupiter.api.Assertions.class)
        .because("we consistently want to use assertj in our tests")
        .check(importedClasses);
  }

  @Test
  void mapperClassesShouldNotUseCurrentTimestampSqlFunction() {
    classes()
        .that()
        .haveSimpleNameEndingWith("Mapper")
        .should(notUseCurrentTimestampSqlFunction())
        .check(importedClasses);
  }

  @Test
  void kadaiIntegrationTestsShouldOnlyHavePackagePrivateFields() {
    classes()
        .that()
        .areAnnotatedWith(KadaiIntegrationTest.class)
        .or(areNestedKadaiIntegrationTestClasses())
        .should(onlyHaveFieldsWithNoModifierAndPrivateConstants())
        .check(importedClasses);
  }

  @Test
  void nestedKadaiIntegrationTestsShouldBeAnnotatedWithTestInstance() {
    classes()
        .that(areNestedKadaiIntegrationTestClasses())
        .should(beAnnotatedWithTestInstancePerClass())
        .check(importedClasses);
  }

  @Test
  void noClassShouldThrowGenericException() {
    noClasses().should(THROW_GENERIC_EXCEPTIONS).check(importedClasses);
  }

  @Test
  void noClassShouldAccessStandardStreams() {
    noClasses().should(ACCESS_STANDARD_STREAMS).check(importedClasses);
  }

  @Test
  void utilityClassesShouldNotBeInitializable() {
    classes()
        .that()
        .resideInAPackage("..util..")
        .and()
        .areNotNestedClasses()
        .should()
        .haveOnlyPrivateConstructors()
        .check(importedClasses);
  }

  @Test
  void noClassesShouldUseFieldInjection() {
    noFields()
        .should(BE_ANNOTATED_WITH_AN_INJECTION_ANNOTATION)
        .as("no classes should use field injection")
        .because(
            "field injection is considered harmful; use constructor injection or setter"
                + " injection instead; see https://stackoverflow.com/q/39890849 for"
                + " detailed explanations")
        .check(importedClasses);
  }

  @Test
  void noClassesShouldUseJavaUtilLogging() {
    noClasses().should(USE_JAVA_UTIL_LOGGING).check(importedClasses);
  }

  @Test
  void noClassesShouldUseJodatime() {
    noClasses()
        .should(USE_JODATIME)
        .because("modern Java projects use the [java.time] API instead")
        .check(importedClasses);
  }

  // endregion

  // region Dependencies
  @Test
  void apiClassesShouldNotDependOnInternalClasses() {
    classes()
        .that()
        .resideInAPackage("..api..")
        .and()
        .areNotAssignableFrom(KadaiEngine.class)
        .and()
        .areNotAssignableTo(Interval.class)
        .should()
        .onlyDependOnClassesThat(
            resideOutsideOfPackage("..io.kadai..internal..").or(assignableTo(LoggingAspect.class)))
        .check(importedClasses);
  }

  @Test
  @Disabled("this has way too many false positives during regular development without refactoring")
  void packagesShouldBeFreeOfCyclicDependencies() {
    // Frozen, so it can be improved over time:
    // https://www.archunit.org/userguide/html/000_Index.html#_freezing_arch_rules
    freeze(slices().matching("io.kadai.(**)").should().beFreeOfCycles()).check(importedClasses);
  }

  @Test
  @Disabled("this has way too many false positives during regular development without refactoring")
  void classesShouldBeFreeOfCyclicDependencies() {
    SliceAssignment everySingleClass =
        new SliceAssignment() {
          // this will specify which classes belong together in the same slice
          @Override
          public SliceIdentifier getIdentifierOf(JavaClass javaClass) {
            return SliceIdentifier.of(javaClass.getFullName());
          }

          // this will be part of the rule description if the test fails
          @Override
          public String getDescription() {
            return "every single class";
          }
        };

    freeze(slices().assignedFrom(everySingleClass).should().beFreeOfCycles())
        .check(importedClasses);
  }

  @Test
  void moduleTaskShouldOnlyDependOn() {
    // FIXME should not depend on spi
    moduleShouldOnlyDependOn("task", List.of("workbasket", "classification", "common", "spi"));
  }

  @Test
  void moduleClassificationShouldOnlyDependOn() {
    moduleShouldOnlyDependOn("workbasket", List.of("common"));
  }

  @Test
  void moduleWorkbasketShouldOnlyDependOn() {
    moduleShouldOnlyDependOn("workbasket", List.of("common"));
  }

  @Test
  @Disabled("Test is failing for an unknown reason")
  void moduleMonitorShouldOnlyDependOn() {
    // FIXME fails for some unknown reason...
    moduleShouldOnlyDependOn("monitor", List.of("common", "classification", "task", "workbasket"));
  }

  @Test
  void moduleUserShouldOnlyDependOn() {
    moduleShouldOnlyDependOn("user", List.of("common"));
  }

  @Test
  void moduleSpiShouldOnlyDependOn() {
    // FIXME should not depend on task, classification and workbasket
    moduleShouldOnlyDependOn("spi", List.of("common", "task", "classification", "workbasket"));
  }

  @TestFactory
  Stream<DynamicTest> rootModulesShouldExist() {
    Function<String, String> descriptionProvider = p -> String.format("Package '%s' exists", p);

    ThrowingConsumer<String> testProvider =
        p -> assertThat(importedClasses.containPackage(p)).isTrue();

    return DynamicTest.stream(KADAI_ROOT_PACKAGES.stream(), descriptionProvider, testProvider);
  }

  @Test
  @Disabled("Needs to be replaced")
  void allClassesAreInsideApiOrInternal() {
    classes()
        .that()
        .resideOutsideOfPackages("acceptance..", "testapi..", "..test..")
        .should()
        .resideInAnyPackage("..api..", "..internal..")
        .check(importedClasses);
  }

  @TestFactory
  Stream<DynamicTest> commonClassesShouldNotDependOnOtherPackages() {

    Stream<String> input = KADAI_ROOT_PACKAGES.stream().filter(not("io.kadai.common"::equals));

    Function<String, String> descriptionProvider =
        p -> String.format("Common classes of %s should not depend on domain classes", p);

    ThrowingConsumer<String> testDefinitionProvider =
        rootPackage ->
            classes()
                .that()
                .resideInAPackage("..common..")
                .and()
                .areNotAssignableTo(KadaiEngine.class)
                .and()
                .areNotAssignableTo(InternalKadaiEngine.class)
                .and()
                .areNotAssignableTo(JobScheduler.class)
                .should()
                .onlyDependOnClassesThat()
                .resideOutsideOfPackage(rootPackage + "..")
                .check(importedClasses);

    return DynamicTest.stream(input, descriptionProvider, testDefinitionProvider);
  }

  @Test
  void classesShouldNotDependOnMonitorDomainClasses() {
    noClasses()
        .that()
        .resideInAPackage("io.kadai..")
        .and()
        .areNotAssignableTo(KadaiEngine.class)
        .and()
        .resideOutsideOfPackages("..monitor..", "io.kadai.testapi..")
        .should()
        .dependOnClassesThat()
        .resideInAPackage("..monitor..")
        .check(importedClasses);
  }

  // endregion

  // region Structure

  @Test
  void exceptionsShouldNotImplementToStringMethod() {
    classes()
        .that()
        .areAssignableTo(KadaiException.class)
        .or()
        .areAssignableTo(KadaiRuntimeException.class)
        .and()
        .doNotBelongToAnyOf(KadaiRuntimeException.class, KadaiException.class)
        .should(notImplementToString())
        .check(importedClasses);
  }

  @Test
  void rootExceptionsShouldImplementToStringMethod() {
    classes()
        .that()
        .areAssignableFrom(KadaiRuntimeException.class)
        .or()
        .areAssignableFrom(KadaiException.class)
        .should(implementToString())
        .check(importedClasses);
  }

  @Test
  void exceptionsShouldBePlacedInExceptionPackage() {
    classes()
        .that()
        .areAssignableTo(Throwable.class)
        .should()
        .resideInAPackage("..exceptions")
        .check(importedClasses);
  }

  @Test
  void exceptionsPackageShouldOnlyContainExceptions() {
    classes()
        .that()
        .resideInAPackage("..exceptions..")
        .and()
        .doNotBelongToAnyOf(ErrorCode.class)
        .should()
        .beAssignableTo(Throwable.class)
        .check(importedClasses);
  }

  @Test
  void exceptionsShouldHaveSuffixException() {
    classes()
        .that()
        .areAssignableTo(Throwable.class)
        .should()
        .haveSimpleNameEndingWith("Exception")
        .check(importedClasses);
  }

  @Test
  void exceptionsShouldInheritFromKadaiRootExceptions() {
    classes()
        .that()
        .areAssignableTo(Throwable.class)
        .should()
        .beAssignableTo(
            assignableTo(KadaiException.class).or(assignableTo(KadaiRuntimeException.class)))
        .check(importedClasses);
  }

  @Test
  void exceptionsShouldBePublic() {
    classes().that().areAssignableTo(Throwable.class).should().bePublic().check(importedClasses);
  }

  @Test
  void classesShouldNotUseWorkingDaysToDaysConverter() {
    classes()
        .that()
        .areNotAssignableFrom(ArchitectureTest.class)
        .and()
        .areNotAssignableTo(WorkingTimeCalculator.class)
        .and()
        .areNotAssignableTo(KadaiEngineImpl.class)
        .and()
        .haveSimpleNameNotEndingWith("Test")
        .should()
        .onlyDependOnClassesThat()
        .areNotAssignableTo(HolidaySchedule.class)
        .because(
            "we want to enforce the usage of the WorkingTimeCalculator"
                + " instead of the WorkingDaysToDaysConverter")
        .check(importedClasses);
  }

  // endregion

  // region Helper Methods

  /**
   * Test the dependencies of the packages. Adds the prefix <code>io.kadai</code> to every given
   * value.
   *
   * @param module the module which should be tested
   * @param dependentModules the expected dependent modules
   */
  private void moduleShouldOnlyDependOn(String module, List<String> dependentModules) {

    String moduleTemplate = "io.kadai.%s..";

    String moduleUndertest = String.format(moduleTemplate, module);

    List<String> dependentModulesList =
        dependentModules.stream()
            .map(dp -> String.format(moduleTemplate, dp))
            .collect(toCollection(ArrayList::new));
    dependentModulesList.addAll(List.of("java..", "org.."));
    dependentModulesList.add(moduleUndertest);

    classes()
        .that()
        .resideInAPackage(moduleUndertest)
        .should()
        .onlyAccessClassesThat()
        .resideInAnyPackage(dependentModulesList.toArray(new String[0]))
        .orShould()
        .dependOnClassesThat()
        .areAssignableTo(KadaiConfiguration.class)
        .check(importedClasses);
  }

  private static ArchCondition<JavaClass> implementToString() {
    return new ArchCondition<>("implement toString()") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents conditionEvents) {
        boolean implementToString =
            Arrays.stream(javaClass.reflect().getDeclaredMethods())
                .map(Method::getName)
                .anyMatch("toString"::equals);
        if (!implementToString) {
          conditionEvents.add(
              SimpleConditionEvent.violated(
                  javaClass,
                  String.format(
                      "Class '%s' does not implement toString()", javaClass.getFullName())));
        }
      }
    };
  }

  private static ArchCondition<JavaClass> notImplementToString() {
    return new ArchCondition<>("not implement toString()") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents conditionEvents) {
        boolean implementToString =
            Arrays.stream(javaClass.reflect().getDeclaredMethods())
                .map(Method::getName)
                .anyMatch("toString"::equals);
        if (implementToString) {
          conditionEvents.add(
              SimpleConditionEvent.violated(
                  javaClass,
                  String.format("Class '%s' does implement toString()", javaClass.getFullName())));
        }
      }
    };
  }

  private static ArchCondition<JavaClass> beAnnotatedWithTestInstancePerClass() {
    return new ArchCondition<>("be annotated with @TestInstance(Lifecycle.PER_CLASS)") {
      @Override
      public void check(JavaClass item, ConditionEvents events) {
        Optional<TestInstance> testInstanceOptional =
            item.tryGetAnnotationOfType(TestInstance.class);
        if (testInstanceOptional.isEmpty()
            || testInstanceOptional.get().value() != Lifecycle.PER_CLASS) {
          events.add(
              SimpleConditionEvent.violated(
                  item,
                  String.format(
                      "Class '%s' is not annotated with @TestInstance(Lifecycle.PER_CLASS)",
                      item.getFullName())));
        }
      }
    };
  }

  private static DescribedPredicate<JavaClass> areNestedKadaiIntegrationTestClasses() {
    return new DescribedPredicate<>("are nested KadaiIntegrationTest classes") {

      @Override
      public boolean test(JavaClass input) {
        Optional<JavaClass> enclosingClass = input.getEnclosingClass();
        return input.isAnnotatedWith(Nested.class)
            && enclosingClass.isPresent()
            && isKadaiIntegrationTest(enclosingClass.get());
      }

      private boolean isKadaiIntegrationTest(JavaClass input) {
        Optional<JavaClass> enclosingClass = input.getEnclosingClass();
        return enclosingClass
            .map(
                javaClass ->
                    input.isAnnotatedWith(Nested.class) && isKadaiIntegrationTest(javaClass))
            .orElseGet(() -> input.isAnnotatedWith(KadaiIntegrationTest.class));
      }
    };
  }

  private static ArchCondition<JavaClass> onlyHaveFieldsWithNoModifierAndPrivateConstants() {
    return new ArchCondition<>("only have fields with no modifier") {
      final Set<JavaModifier> modifiersForConstants =
          Set.of(JavaModifier.PRIVATE, JavaModifier.STATIC, JavaModifier.FINAL);

      @Override
      public void check(JavaClass item, ConditionEvents events) {
        for (JavaField field : item.getAllFields()) {
          if (!field.reflect().isSynthetic()
              && !(field.getModifiers().isEmpty()
                  || field.getModifiers().equals(modifiersForConstants))) {
            events.add(
                SimpleConditionEvent.violated(
                    item,
                    String.format(
                        "Field '%s' in '%s' should not have any modifier, "
                            + "except for static fields, which have to be private",
                        field.getFullName(), item.getFullName())));
          }
        }
      }
    };
  }

  private static ArchCondition<JavaClass> notUseCurrentTimestampSqlFunction() {
    Function<JavaMethod, List<String>> getSqlStringsFromMethod =
        wrap(
            (method) -> {
              List<String> values = new ArrayList<>();

              final List<Select> select =
                  AnnotationSupport.findRepeatableAnnotations(method.reflect(), Select.class);
              final List<Update> update =
                  AnnotationSupport.findRepeatableAnnotations(method.reflect(), Update.class);
              final List<Insert> insert =
                  AnnotationSupport.findRepeatableAnnotations(method.reflect(), Insert.class);
              final List<Delete> delete =
                  AnnotationSupport.findRepeatableAnnotations(method.reflect(), Delete.class);
              select.stream().map(Select::value).map(Arrays::asList).forEach(values::addAll);
              update.stream().map(Update::value).map(Arrays::asList).forEach(values::addAll);
              insert.stream().map(Insert::value).map(Arrays::asList).forEach(values::addAll);
              delete.stream().map(Delete::value).map(Arrays::asList).forEach(values::addAll);

              final List<SelectProvider> selectProviders =
                  AnnotationSupport.findRepeatableAnnotations(
                      method.reflect(), SelectProvider.class);
              final List<UpdateProvider> updateProviders =
                  AnnotationSupport.findRepeatableAnnotations(
                      method.reflect(), UpdateProvider.class);
              final List<InsertProvider> insertProviders =
                  AnnotationSupport.findRepeatableAnnotations(
                      method.reflect(), InsertProvider.class);
              final List<DeleteProvider> deleteProviders =
                  AnnotationSupport.findRepeatableAnnotations(
                      method.reflect(), DeleteProvider.class);
              selectProviders.stream()
                  .map(wrap(a -> executeStaticProviderMethod(a.type(), a.method())))
                  .forEach(values::add);
              updateProviders.stream()
                  .map(wrap(a -> executeStaticProviderMethod(a.type(), a.method())))
                  .forEach(values::add);
              insertProviders.stream()
                  .map(wrap(a -> executeStaticProviderMethod(a.type(), a.method())))
                  .forEach(values::add);
              deleteProviders.stream()
                  .map(wrap(a -> executeStaticProviderMethod(a.type(), a.method())))
                  .forEach(values::add);
              return values;
            });

    return new ArchCondition<>("not use the SQL function 'CURRENT_TIMESTAMP'") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents events) {
        for (JavaMethod method : javaClass.getAllMethods()) {
          List<String> sqlStrings = getSqlStringsFromMethod.apply(method);

          if (sqlStrings.isEmpty()) {
            String message =
                String.format(
                    "Could not extract SQL Statement from '%s#%s'. "
                        + "Maybe an unsupported MyBatis SQL annotation is used "
                        + "or no annotation is present?",
                    javaClass.getName(), method.getName());
            events.add(SimpleConditionEvent.violated(javaClass, message));
          }

          if (sqlStrings.stream().anyMatch(s -> s.contains("CURRENT_TIMESTAMP"))) {
            String message =
                String.format(
                    "Method '%s#%s' uses 'CURRENT_TIMESTAMP' SQL function",
                    javaClass.getName(), method.getName());
            events.add(SimpleConditionEvent.violated(javaClass, message));
          }
        }
      }
    };
  }

  private static String executeStaticProviderMethod(Class<?> clazz, String methodName)
      throws Exception {
    return clazz.getMethod(methodName).invoke(null).toString();
  }

  // endregion
}
