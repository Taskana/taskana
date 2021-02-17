package pro.taskana;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.archunit.base.Optional;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;

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
          "pro.taskana.workbasket.api",
          "pro.taskana.workbasket.internal",
          "pro.taskana.spi.routing.api",
          "pro.taskana.spi.routing.internal",
          "pro.taskana.spi.task.api",
          "pro.taskana.spi.task.internal");
  private static JavaClasses importedClasses;

  @BeforeAll
  static void init() {
    // time intensive operation should only be done once
    importedClasses = new ClassFileImporter().importPackages("pro.taskana", "acceptance");
  }

  @Test
  void apiClassesShouldNotDependOnInternalClasses() {
    ArchRule myRule =
        classes()
            .that()
            .haveNameNotMatching(".*ScheduledJob.Type")
            .and()
            .resideInAPackage("..api..")
            .should()
            .onlyDependOnClassesThat()
            .resideOutsideOfPackage("..internal..");
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
  void onlyExceptionsShouldResideInExceptionPackage() {
    ArchRule myRule =
        classes().that().resideInAPackage("..exceptions").should().beAssignableTo(Throwable.class);
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
                "acceptance.*" // all our acceptance tests
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
            .filter(d -> !"common".equals(d))
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
                .haveNameNotMatching(".*ScheduledJob.Type")
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
            .filter(d -> !"monitor".equals(d))
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
        (method) -> {
          List<String> values = new ArrayList<>();
          final Optional<Select> selectAnnotation = method.tryGetAnnotationOfType(Select.class);
          final Optional<Update> updateAnnotation = method.tryGetAnnotationOfType(Update.class);
          final Optional<Insert> insertAnnotation = method.tryGetAnnotationOfType(Insert.class);
          final Optional<Delete> deleteAnnotation = method.tryGetAnnotationOfType(Delete.class);

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
          return values;
        };

    return new ArchCondition<>("not use the SQL function 'CURRENT_TIMESTAMP'") {
      @Override
      public void check(JavaClass javaClass, ConditionEvents events) {
        for (JavaMethod method : javaClass.getAllMethods()) {
          List<String> sqlStrings = getSqlStringsFromMethod.apply(method);

          if (sqlStrings.isEmpty()) {
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
}
