package pro.taskana;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.lang.ArchRule;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/**
 * Test architecture of classes in taskana. For more info and examples see
 * https://www.archunit.org/userguide/html/000_Index.html
 */
class ArchitectureTest {
  private static JavaClasses importedClasses;

  @BeforeAll
  static void init() throws ClassNotFoundException {
    // time intensive operation should only be done once
    importedClasses =
        new ClassFileImporter()
            .withImportOption(new DoNotIncludeTests())
            .importPackages("pro.taskana");
  }

  @Test
  void apiClassesShouldNotDependOnInternalClasses() {
    ArchRule myRule =
        classes()
            .that()
            .haveSimpleNameNotEndingWith("TaskanaHistoryEvent")
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

  /**
   * Solution would be nice, but is not achievable without big changes
   * https://www.archunit.org/userguide/html/000_Index.html#_cycle_checks
   */
  @Disabled
  @Test
  void freeOfCycles() {
    ArchRule myRule = slices().matching("pro.taskana.(*)..").should().beFreeOfCycles();
    myRule.check(importedClasses);
  }

  /**
   * Test for cycles with subpackages
   * https://www.archunit.org/userguide/html/000_Index.html#_cycle_checks
   */
  @TestFactory
  Stream<DynamicTest> freeOfCycles_subpackages() {

    Stream<String> packagesToTest =
        Stream.of(
            "pro.taskana.common.internal.(*)..",
            "pro.taskana.common.api.(*)..",
            "pro.taskana.classification.api.(*)..",
            "pro.taskana.classification.internal.(*)..",
            "pro.taskana.history.api.(*)..",
            "pro.taskana.history.internal.(*)..",
            // to be fixed soon
            // "pro.taskana.report.api.(*)..",
            "pro.taskana.report.internal.(*)..",
            "pro.taskana.task.api.(*)..",
            "pro.taskana.task.internal.(*)..",
            "pro.taskana.workbasket.api.(*)..",
            "pro.taskana.workbasket.internal.(*)..");
    return packagesToTest.map(
        p ->
            dynamicTest(
                p.replaceAll(Pattern.quote("pro.taskana."), "") + " is free of cycles",
                () -> slices().matching(p).should().beFreeOfCycles().check(importedClasses)));
  }

  @Disabled("TBD")
  @TestFactory
  Stream<DynamicTest> commonClassesShouldNotDependOnOtherDomainClasses() {

    Stream<String> packagesToTest =
        Stream.of("..workbasket..", "..report..", "..history..", "..task..", "..classification..");
    return packagesToTest.map(
        p ->
            dynamicTest(
                p.replaceAll(Pattern.quote("."), "") + " should not be used by common",
                () ->
                    noClasses()
                        .that()
                        .resideInAPackage("..common..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAPackage(p)
                        .check(importedClasses)));
  }

  @TestFactory
  Stream<DynamicTest> classesShouldNotDependOnReportDomainClasses() {

    Stream<String> packagesToTest =
        Stream.of(
            "..workbasket..",
            "..history..",
            "..task..",
            // TBD
            // "..common..",
            "..classification..");
    return packagesToTest.map(
        p ->
            dynamicTest(
                "Domain " + p.replaceAll(Pattern.quote("."), "") + " should not depend on reports",
                () ->
                    noClasses()
                        .that()
                        .resideInAPackage(p)
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage("..report..")
                        .check(importedClasses)));
  }

  @Disabled
  @Test
  void freeOfCyclicDependencies() {
    ArchRule myRule = slices().matching("pro.taskana.(*)..").should().notDependOnEachOther();
    myRule.check(importedClasses);
  }
}
