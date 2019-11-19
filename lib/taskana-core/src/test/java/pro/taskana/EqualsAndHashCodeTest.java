package pro.taskana;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import pro.taskana.impl.AttachmentImpl;
import pro.taskana.impl.AttachmentSummaryImpl;
import pro.taskana.impl.ClassificationImpl;
import pro.taskana.impl.ClassificationSummaryImpl;
import pro.taskana.impl.TaskImpl;
import pro.taskana.impl.TaskSummaryImpl;
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.impl.WorkbasketImpl;
import pro.taskana.impl.WorkbasketSummaryImpl;

/**
 * check classes with a custom equals and hashcode implementation for correctness.
 */
class EqualsAndHashCodeTest {

    @TestFactory
    Collection<DynamicTest> equalsContract() {
        return
            getPojoClasses().stream()
                .map(cl -> DynamicTest.dynamicTest("Check Hash and Equals for " + cl.getSimpleName(),
                    () -> {
                        EqualsVerifier.forClass(cl)
                            .suppress(Warning.NONFINAL_FIELDS, Warning.STRICT_INHERITANCE)
                            .withRedefinedSuperclass()
                            .verify();
                    }))
                .collect(Collectors.toList());
    }

    //TODO find a way to dynamically create a list with custom implemented equals or hash methods.
    private List<Class<?>> getPojoClasses() {
        return Arrays.asList(
            KeyDomain.class,
            ObjectReference.class,
            TimeInterval.class,
            AttachmentImpl.class,
            AttachmentSummaryImpl.class,
            ClassificationImpl.class,
            ClassificationSummaryImpl.class,
            TaskImpl.class,
            TaskSummaryImpl.class,
            WorkbasketAccessItemImpl.class,
            WorkbasketImpl.class,
            WorkbasketSummaryImpl.class
        );
    }
}
