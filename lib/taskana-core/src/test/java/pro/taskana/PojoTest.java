package pro.taskana;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

/**
 * check classes with a custom equals and hashcode implementation for correctness.
 */
class PojoTest {

    @Test
    void testsThatPojoClassesAreFound() {
        Assertions.assertTrue(getPojoClasses().count() > 0);
    }

    @TestFactory
    Collection<DynamicTest> equalsContract() {
        return
            getPojoClasses()
                .map(cl -> DynamicTest.dynamicTest("Check Hash and Equals for " + cl.getSimpleName(),
                    () -> verifyHashAndEquals(cl)))
                .collect(Collectors.toList());
    }

    @TestFactory
    Collection<DynamicTest> validateGetters() {
        return
            getPojoClasses()
                .map(cl -> DynamicTest.dynamicTest("Check Getter exist for " + cl.getSimpleName(),
                    () -> validateWithRules(cl, new GetterMustExistRule())
                ))
                .collect(Collectors.toList());
    }

    @TestFactory
    Collection<DynamicTest> validateSetters() {
        return
            getPojoClasses()
                .map(cl -> DynamicTest.dynamicTest("Check Setter for " + cl.getSimpleName(),
                    () -> validateWithRules(cl, new SetterMustExistRule())
                ))
                .collect(Collectors.toList());
    }

    @TestFactory
    Collection<DynamicTest> validateGetAndSet() {
        return
            getPojoClasses()
                .map(cl -> DynamicTest.dynamicTest("Test set & get " + cl.getSimpleName(),
                    () -> validateWithTester(cl, new GetterTester(), new SetterTester())
                ))
                .collect(Collectors.toList());
    }

    @TestFactory
    Collection<DynamicTest> validateNoStaticExceptFinalFields() {
        return
            getPojoClasses()
                .map(cl -> DynamicTest.dynamicTest("Check static fields for " + cl.getSimpleName(),
                    () -> validateWithRules(cl, new NoStaticExceptFinalRule())
                ))
                .collect(Collectors.toList());
    }

    @TestFactory
    Collection<DynamicTest> validateNoPublicFields() {
        return
            getPojoClasses()
                .map(cl -> DynamicTest.dynamicTest("Check public fields for " + cl.getSimpleName(),
                    () -> validateWithRules(cl, new NoPublicFieldsRule())
                ))
                .collect(Collectors.toList());
    }

    private void validateWithRules(Class<?> cl, Rule... rules) {
        ValidatorBuilder.create()
            .with(rules)
            .build()
            .validate(PojoClassFactory.getPojoClass(cl));
    }

    private void validateWithTester(Class<?> cl, Tester... testers) {
        ValidatorBuilder.create()
            .with(testers)
            .build()
            .validate(PojoClassFactory.getPojoClass(cl));
    }

    private void verifyHashAndEquals(Class<?> cl) {
        EqualsVerifier.forClass(cl)
            .suppress(Warning.NONFINAL_FIELDS, Warning.STRICT_INHERITANCE)
            .withRedefinedSuperclass()
            .verify();
    }

    private Stream<Class<?>> getPojoClasses() {
        //TODO how to identify pojos? Is overwritten equals method enough?
        return new ClassFileImporter().importPackages("pro.taskana")
            .stream()
            .filter(javaClass -> javaClass.tryGetMethod("equals", Object.class).isPresent())
            .map(JavaClass::reflect);
    }
}

