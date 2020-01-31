package pro.taskana;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test architecture of classes in taskana. For more info and examples see
 * https://www.archunit.org/userguide/html/000_Index.html
 */
@SuppressWarnings({"checkstyle:EmptyLineSeparator"})
class ArchitectureTest {
  private static JavaClasses importedClasses;

  DescribedPredicate<JavaClass> doNotContain(String... namesToBeExcluded) {
    return new DescribedPredicate<JavaClass>("should not be checked") {
      @Override
      public boolean apply(JavaClass input) {
        String matchingClassName =
            Arrays.asList(namesToBeExcluded).stream()
                .filter(name -> input.getName().contains(name))
                .findFirst()
                .orElse(null);

        return (matchingClassName == null);

      };
    };
  }

  @BeforeAll
  static void init() throws ClassNotFoundException {
    // time intensive operation should only be done once
    importedClasses = new ClassFileImporter().importPackages("pro.taskana");
  }

  @Test
  @Disabled
  void mapperShouldBePlacedInMappingsPackage() {
    ArchRule myRule =
        classes()
            .that()
            .haveSimpleNameEndingWith("Mapper")
            .should()
            .resideInAPackage("..mappings..");

    myRule.check(importedClasses);
  }

  @Test
  void apiClassesShouldNotDependOnInternalClasses() {
    ArchRule myRule =
        classes()
            .that()
            .resideInAPackage("..api")
            .should()
            .onlyDependOnClassesThat()
            .resideOutsideOfPackage("..internal..");
    myRule.check(importedClasses.that(doNotContain("TaskanaHistory",
        "BulkOperationResults")));
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
        classes().that().resideInAPackage("..exceptions").should()
            .beAssignableTo(Throwable.class);
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
  @Disabled
  void freeOfCycles() {
    ArchRule myRule = slices().matching("pro.taskana.(*)..")
                          .should().beFreeOfCycles();
    myRule.check(importedClasses);
  }

  @Test
  @Disabled
  void freeOfCyclicDependencies() {
    ArchRule myRule = slices().matching("pro.taskana.(*)..").should()
                          .notDependOnEachOther();
    myRule.check(importedClasses);
  }

  private boolean containsName(JavaClass input, String name) {
    return !input.getName().contains(name);
  }
}
