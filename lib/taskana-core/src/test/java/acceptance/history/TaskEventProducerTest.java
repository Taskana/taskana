package acceptance.history;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import acceptance.AbstractAccTest;

/**
 * Acceptance test for historyEventProducer class.
 */
public class TaskEventProducerTest extends AbstractAccTest {

    @Test
    public void testHistoryEventProducerIsNotEnabled() {
        assertFalse(taskanaEngine.isHistoryEnabled());
    }
}
