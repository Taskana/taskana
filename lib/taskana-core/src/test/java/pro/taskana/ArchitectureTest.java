package pro.taskana;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test architecture of classes in taskana. For more info and examples see
 * https://www.archunit.org/userguide/html/000_Index.html
 */
class ArchitectureTest {

  private static JavaClasses importedClasses;

  @BeforeAll
  static void init() {
    // time intensive operation should only be done once
    importedClasses = new ClassFileImporter().importPackages("pro.taskana");
  }

  @Ignore
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
  @Disabled
  void freeOfCycles() {
    ArchRule myRule = slices().matching("pro.taskana.(*)..").should().beFreeOfCycles();
    myRule.check(importedClasses);
  }

  @Test
  @Disabled
  void freeOfCyclicDependencies() {
    ArchRule myRule = slices().matching("pro.taskana.(*)..").should().notDependOnEachOther();
    myRule.check(importedClasses);
  }
}
