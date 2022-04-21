package acceptance;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static java.util.function.Predicate.not;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.base.Optional;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClass.Predicates;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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
import org.junit.jupiter.api.function.ThrowingConsumer;
import testapi.TaskanaIntegrationTest;

import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.api.exceptions.TaskanaRuntimeException;
import pro.taskana.common.internal.logging.LoggingAspect;
import pro.taskana.common.internal.util.CheckedFunction;
import pro.taskana.common.internal.util.MapCreator;

/**
 * Test architecture of classes in taskana. For more info and examples see
 * https://www.archunit.org/userguide/html/000_Index.html.
 */
class ArchitectureTest {
  private static final List<String> TASKANA_SUB_PACKAGES =
      List.of(
          "pro.taskana.sampledata",
          "pro.taskana.common.internal",
          "pro.taskana.common.api",
          "pro.taskana.common.test",
          "pro.taskana.classification.api",
          "pro.taskana.classification.internal",
          "pro.taskana.spi.history.api",
          "pro.taskana.spi.history.internal",
          "pro.taskana.monitor.api",
          "pro.taskana.monitor.internal",
          "pro.taskana.task.api",
          "pro.taskana.task.internal",
          "pro.taskana.user.api",
          "pro.taskana.user.api.exceptions",
          "pro.taskana.user.api.models",
          "pro.taskana.user.internal",
          "pro.taskana.workbasket.api",
          "pro.taskana.workbasket.internal",
          "pro.taskana.spi.routing.api",
          "pro.taskana.spi.routing.internal",
          "pro.taskana.spi.task.api",
          "pro.taskana.spi.task.internal",
          "pro.taskana.spi.priority.api",
          "pro.taskana.spi.priority.internal");
  private static JavaClasses importedClasses;

  @BeforeAll
  static void init() {
    // time intensive operation should only be done once
    importedClasses =
        new ClassFileImporter().importPackages("pro.taskana", "acceptance", "testapi");
  }

  @Test
  void apiClassesShouldNotDependOnInternalClasses() {
    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("..api..")
            .should()
            .onlyDependOnClassesThat(
                Predicates.resideOutsideOfPackage("..pro.taskana..internal..")
                    .or(
                        Predicates.assignableTo(LoggingAspect.class)
                            .or(Predicates.assignableTo(MapCreator.class))));
    myRule.check(importedClasses);
  }

  @Test
  void utilityClassesShouldNotBeInitializable() {
    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("..util..")
            .and()
            .areNotNestedClasses()
            .should()
            .haveOnlyPrivateConstructors();

    myRule.check(importedClasses);
  }

  @Test
  @Disabled("until we have renamed all tests")
  void testMethodNamesShouldMatchAccordingToOurGuidelines() {
    ArchRule myRule =
        methods()
            .that()
            .areAnnotatedWith(Test.class)
            .or()
            .areAnnotatedWith(TestFactory.class)
            .and()
            .areNotDeclaredIn(ArchitectureTest.class)
            .should()
            .bePackagePrivate()
            .andShould()
            .haveNameMatching("^should_[A-Z][^_]+_(For|When)_[A-Z][^_]+$");

    myRule.check(importedClasses);
  }

  @Test
  void exceptionsShouldBePlacedInExceptionPackage() {
    ArchRule myRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Exception")
            .should()
            .resideInAPackage("..exceptions..");

    myRule.check(importedClasses);
  }

  @Test
  void exceptionsThatShouldNotHaveToStringMethod() {
    ArchRule myRule =
        classes()
            .that()
            .areAssignableTo(TaskanaException.class)
            .or()
            .areAssignableTo(TaskanaRuntimeException.class)
            .and()
            .doNotBelongToAnyOf(TaskanaRuntimeException.class, TaskanaException.class)
            .should(notImplementToString());

    myRule.check(importedClasses);
  }

  @Test
  void exceptionsThatShouldHaveToStringMethod() {
    ArchRule myRule =
        classes()
            .that()
            .areAssignableFrom(TaskanaRuntimeException.class)
            .or()
            .areAssignableFrom(TaskanaException.class)
            .should(implementToString());

    myRule.check(importedClasses);
  }

  @Test
  void onlyExceptionsShouldResideInExceptionPackage() {
    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("..exceptions")
            .and()
            .doNotBelongToAnyOf(ErrorCode.class)
            .should()
            .beAssignableTo(
                Predicates.assignableTo(TaskanaException.class)
                    .or(Predicates.assignableTo(TaskanaRuntimeException.class)))
            .andShould()
            .bePublic()
            .andShould()
            .haveSimpleNameEndingWith("Exception");
    myRule.check(importedClasses);
  }

  @Test
  void noClassShouldThrowGenericException() {
    NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(importedClasses);
  }

  @Test
  void noClassShouldAccessStandardStreams() {
    NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(importedClasses);
  }

  @Test
  void everySubpackageShouldBeTestsForCyclicDependencies() {
    List<Pattern> excludePackages =
        Stream.of(
                "pro.taskana", // from TaskanaEngineConfiguration
                "acceptance.*", // all our acceptance tests
                "testapi.*" // our test API
                )
            .map(Pattern::compile)
            .collect(Collectors.toList());
    ArchRule myRule = classes().should(beDefinedInTaskanaSubPackages(excludePackages));
    myRule.check(importedClasses);
  }

  @TestFactory
  Stream<DynamicTest> everyPackageWhichIsTestedForCyclicDependenciesShouldExist() {
    return DynamicTest.stream(
        TASKANA_SUB_PACKAGES.iterator(),
        p -> String.format("package '%s' exists", p),
        p -> assertThat(importedClasses.containPackage(p)).isTrue());
  }

  /*
   * Test for cycles with subpackages
   * https://www.archunit.org/userguide/html/000_Index.html#_cycle_checks
   */
  @TestFactory
  Stream<DynamicTest> everySubPackageShouldBeFreeOfCyclicDependencies() {
    Stream<String> packagesToTest = TASKANA_SUB_PACKAGES.stream().map(s -> s + ".(*)..");
    ThrowingConsumer<String> testMethod =
        p -> slices().matching(p).should().beFreeOfCycles().check(importedClasses);
    return DynamicTest.stream(
        packagesToTest.iterator(),
        p -> p.replaceAll(Pattern.quote("pro.taskana."), "") + " is free of cycles",
        testMethod);
  }

  @TestFactory
  Stream<DynamicTest> commonClassesShouldNotDependOnOtherDomainClasses() {
    Stream<String> packagesToTest =
        TASKANA_SUB_PACKAGES.stream()
            .map(p -> p.split("\\.")[2])
            .distinct()
            .filter(not("common"::equals))
            .map(d -> ".." + d + "..");
    ThrowingConsumer<String> testMethod =
        p ->
            noClasses()
                .that()
                .haveNameNotMatching(".*TaskanaEngine.*")
                .and()
                .haveSimpleNameNotEndingWith("ObjectAttributeChangeDetectorTest")
                .and()
                .haveSimpleNameNotEndingWith("AbstractTaskanaJob")
                .and()
                .resideInAPackage("..common..")
                .and()
                .resideOutsideOfPackage("..common.test..")
                .should()
                .dependOnClassesThat()
                .resideInAPackage(p)
                .check(importedClasses);
    return DynamicTest.stream(
        packagesToTest.iterator(), p -> p + " should not be used by common", testMethod);
  }

  @TestFactory
  Stream<DynamicTest> classesShouldNotDependOnMonitorDomainClasses() {
    Stream<String> packagesToTest =
        TASKANA_SUB_PACKAGES.stream()
            .map(p -> p.split("\\.")[2])
            .distinct()
            .filter(not("monitor"::equals))
            .map(d -> ".." + d + "..");

    ThrowingConsumer<String> testMethod =
        p ->
            noClasses()
                .that()
                .resideInAPackage(p)
                .and()
                .haveNameNotMatching(".*TaskanaEngine.*")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("..monitor..")
                .check(importedClasses);
    return DynamicTest.stream(
        packagesToTest.iterator(),
        p -> String.format("Domain %s should not depend on monitor", p),
        testMethod);
  }

  @Test
  void classesShouldNotUseJunit5Assertions() {
    ArchRule rule =
        noClasses()
            .that()
            .haveSimpleNameNotEndingWith("ArchitectureTest")
            .should()
            .dependOnClassesThat()
            .haveFullyQualifiedName(org.junit.jupiter.api.Assertions.class.getName())
            .because("we consistently want to use assertj in our tests");
    rule.check(importedClasses);
  }

  @Test
  void mapperClassesShouldNotUseCurrentTimestampSqlFunction() {
    ArchRule rule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Mapper")
            .should(notUseCurrentTimestampSqlFunction());

    rule.check(importedClasses);
  }

  @Test
  void taskanaIntegrationTestsShouldOnlyHavePackagePrivateFields() {
    ArchRule rule =
        classes()
            .that()
            .areAnnotatedWith(TaskanaIntegrationTest.class)
            .or(areNestedTaskanaIntegrationTestClasses())
            .should(onlyHaveFieldsWithNoModifierAndPrivateConstants());

    rule.check(importedClasses);
  }

  @Test
  void nestedTaskanaIntegrationTestsShouldBeAnnotatedWithTestInstance() {
    ArchRule rule =
        classes()
            .that(areNestedTaskanaIntegrationTestClasses())
            .should(beAnnotatedWithTestInstancePerClass());

    rule.check(importedClasses);
  }

  private ArchCondition<JavaClass> implementToString() {
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

  private ArchCondition<JavaClass> notImplementToString() {
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

  private ArchCondition<JavaClass> beAnnotatedWithTestInstancePerClass() {
    return new ArchCondition<>("be annotated with @TestInstance(Lifecycle.PER_CLASS)") {
      @Override
      public void check(JavaClass item, ConditionEvents events) {
        Optional<TestInstance> testInstanceOptional =
            item.tryGetAnnotationOfType(TestInstance.class);
        if (!testInstanceOptional.isPresent()
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

  private DescribedPredicate<JavaClass> areNestedTaskanaIntegrationTestClasses() {
    return new DescribedPredicate<>("are nested TaskanaIntegrationTest classes") {

      @Override
      public boolean apply(JavaClass input) {
        Optional<JavaClass> enclosingClass = input.getEnclosingClass();
        return input.isAnnotatedWith(Nested.class)
            && enclosingClass.isPresent()
            && isTaskanaIntegrationTest(enclosingClass.get());
      }

      private boolean isTaskanaIntegrationTest(JavaClass input) {
        Optional<JavaClass> enclosingClass = input.getEnclosingClass();
        if (enclosingClass.isPresent()) {
          return input.isAnnotatedWith(Nested.class)
              && isTaskanaIntegrationTest(enclosingClass.get());
        } else {
          return input.isAnnotatedWith(TaskanaIntegrationTest.class);
        }
      }
    };
  }

  private ArchCondition<JavaClass> onlyHaveFieldsWithNoModifierAndPrivateConstants() {
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
                        "Field '%s' should not have any modifier "
                            + "except for static fields, which have to be private",
                        field.getFullName())));
          }
        }
      }
    };
  }

  private static ArchCondition<JavaClass> beDefinedInTaskanaSubPackages(
      List<Pattern> excludePackages) {
    return new ArchCondition<>("all be defined in TASKANA_SUB_PACKAGES") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents events) {
        if (TASKANA_SUB_PACKAGES.stream().noneMatch(p -> javaClass.getPackageName().startsWith(p))
            && excludePackages.stream()
                .noneMatch(p -> p.matcher(javaClass.getPackageName()).matches())) {
          String message =
              String.format(
                  "Package '%s' was not declared in TASKANA_SUB_PACKAGES",
                  javaClass.getPackageName());
          events.add(SimpleConditionEvent.violated(javaClass, message));
        }
      }
    };
  }

  private static ArchCondition<JavaClass> notUseCurrentTimestampSqlFunction() {
    Function<JavaMethod, List<String>> getSqlStringsFromMethod =
        CheckedFunction.wrap(
            (method) -> {
              List<String> values = new ArrayList<>();
              final Optional<Select> selectAnnotation = method.tryGetAnnotationOfType(Select.class);
              final Optional<Update> updateAnnotation = method.tryGetAnnotationOfType(Update.class);
              final Optional<Insert> insertAnnotation = method.tryGetAnnotationOfType(Insert.class);
              final Optional<Delete> deleteAnnotation = method.tryGetAnnotationOfType(Delete.class);
              final Optional<SelectProvider> selectProviderAnnotation =
                  method.tryGetAnnotationOfType(SelectProvider.class);
              final Optional<UpdateProvider> updateProviderAnnotation =
                  method.tryGetAnnotationOfType(UpdateProvider.class);
              final Optional<InsertProvider> insertProviderAnnotation =
                  method.tryGetAnnotationOfType(InsertProvider.class);
              final Optional<DeleteProvider> deleteProviderAnnotation =
                  method.tryGetAnnotationOfType(DeleteProvider.class);

              if (selectAnnotation.isPresent()) {
                values.addAll(Arrays.asList(selectAnnotation.get().value()));
              }
              if (updateAnnotation.isPresent()) {
                values.addAll(Arrays.asList(updateAnnotation.get().value()));
              }
              if (insertAnnotation.isPresent()) {
                values.addAll(Arrays.asList(insertAnnotation.get().value()));
              }
              if (deleteAnnotation.isPresent()) {
                values.addAll(Arrays.asList(deleteAnnotation.get().value()));
              }
              if (selectProviderAnnotation.isPresent()) {
                values.add(
                    executeStaticProviderMethod(
                        selectProviderAnnotation.get().type(),
                        selectProviderAnnotation.get().method()));
              }
              if (updateProviderAnnotation.isPresent()) {
                values.add(
                    executeStaticProviderMethod(
                        updateProviderAnnotation.get().type(),
                        updateProviderAnnotation.get().method()));
              }
              if (insertProviderAnnotation.isPresent()) {
                values.add(
                    executeStaticProviderMethod(
                        insertProviderAnnotation.get().type(),
                        insertProviderAnnotation.get().method()));
              }
              if (deleteProviderAnnotation.isPresent()) {
                values.add(
                    executeStaticProviderMethod(
                        deleteProviderAnnotation.get().type(),
                        deleteProviderAnnotation.get().method()));
              }
              return values;
            });

    return new ArchCondition<>("not use the SQL function 'CURRENT_TIMESTAMP'") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents events) {
        for (JavaMethod method : javaClass.getAllMethods()) {
          List<String> sqlStrings = getSqlStringsFromMethod.apply(method);

          if (sqlStrings.isEmpty()
              && !method.tryGetAnnotationOfType(SelectProvider.class).isPresent()) {
            String message =
                String.format(
                    "Method '%s#%s' does not contain any MyBatis SQL annotation",
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
}
