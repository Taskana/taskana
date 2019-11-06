package acceptance.history;

import static org.junit.Assert.assertFalse;

import org.junit.jupiter.api.Test;

import acceptance.AbstractAccTest;

/**
 * Acceptance test for historyEventProducer class.
 */
class TaskEventProducerTest extends AbstractAccTest {

    @Test
    void testHistoryEventProducerIsNotEnabled() {
        assertFalse(taskanaEngine.isHistoryEnabled());
    }
}
