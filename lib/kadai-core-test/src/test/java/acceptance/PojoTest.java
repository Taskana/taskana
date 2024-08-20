package acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.Rule;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.Tester;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

/** check classes with a custom equals and hashcode implementation for correctness. */
class PojoTest {

  private static final List<Class<?>> POJO_CLASSES = getPojoClasses();

  @Test
  void testsThatPojoClassesAreFound() {
    assertThat(POJO_CLASSES).isNotEmpty();
  }

  @TestFactory
  Stream<DynamicTest> equalsContract() {
    return POJO_CLASSES.stream()
        .map(
            cl ->
                DynamicTest.dynamicTest(
                    "Check Hash and Equals for " + cl.getSimpleName(),
                    () -> verifyHashAndEquals(cl)));
  }

  @TestFactory
  Stream<DynamicTest> validateGetters() {
    return POJO_CLASSES.stream()
        .map(
            cl ->
                DynamicTest.dynamicTest(
                    "Check Getter exist for " + cl.getSimpleName(),
                    () -> validateWithRules(cl, new GetterMustExistRule())));
  }

  @TestFactory
  Stream<DynamicTest> validateSetters() {
    return POJO_CLASSES.stream()
        .map(
            cl ->
                DynamicTest.dynamicTest(
                    "Check Setter for " + cl.getSimpleName(),
                    () -> validateWithRules(cl, new SetterMustExistRule())));
  }

  @TestFactory
  @Disabled("because of the truncation of all Instant member variables")
  Stream<DynamicTest> validateGetAndSet() {
    return POJO_CLASSES.stream()
        .map(
            cl ->
                DynamicTest.dynamicTest(
                    "Test set & get " + cl.getSimpleName(),
                    () -> validateWithTester(cl, new GetterTester(), new SetterTester())));
  }

  @TestFactory
  Stream<DynamicTest> validateNoStaticExceptFinalFields() {
    return POJO_CLASSES.stream()
        .map(
            cl ->
                DynamicTest.dynamicTest(
                    "Check static fields for " + cl.getSimpleName(),
                    () -> validateWithRules(cl, new NoStaticExceptFinalRule())));
  }

  @TestFactory
  Stream<DynamicTest> validateNoPublicFields() {
    return POJO_CLASSES.stream()
        .map(
            cl ->
                DynamicTest.dynamicTest(
                    "Check public fields for " + cl.getSimpleName(),
                    () -> validateWithRules(cl, new NoPublicFieldsRule())));
  }

  private void validateWithRules(Class<?> cl, Rule... rules) {
    ValidatorBuilder.create().with(rules).build().validate(PojoClassFactory.getPojoClass(cl));
  }

  private void validateWithTester(Class<?> cl, Tester... testers) {
    ValidatorBuilder.create().with(testers).build().validate(PojoClassFactory.getPojoClass(cl));
  }

  private void verifyHashAndEquals(Class<?> cl) {
    EqualsVerifier.forClass(cl)
        .suppress(Warning.NONFINAL_FIELDS, Warning.STRICT_INHERITANCE)
        .withRedefinedSuperclass()
        .verify();
  }

  private static List<Class<?>> getPojoClasses() {
    // TODO how to identify pojos? Is overwritten equals method enough?
    return new ClassFileImporter()
        .importPackages("io.kadai").stream()
            .filter(javaClass -> javaClass.tryGetMethod("equals", Object.class).isPresent())
            .filter(
                javaClass ->
                    !javaClass.getSimpleName().equals("TaskHistoryEvent")
                        && !javaClass.getSimpleName().equals("WorkbasketHistoryEvent")
                        && !javaClass.getSimpleName().equals("ClassificationHistoryEvent")
                        && !javaClass.getSimpleName().equals("ComparableVersion")
                        && !javaClass.getSimpleName().equals("StringItem")
                        && !javaClass.getSimpleName().equals("BigIntegerItem")
                        && !javaClass.getSimpleName().equals("IntItem")
                        && !javaClass.getSimpleName().equals("LongItem"))
            .map(JavaClass::reflect)
            .collect(Collectors.toList());
  }
}
