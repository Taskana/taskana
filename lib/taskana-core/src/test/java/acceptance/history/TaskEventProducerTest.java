package acceptance.history;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.history.HistoryEventProducer;

/**
 * Acceptance test for historyEventProducer class.
 */
public class TaskEventProducerTest extends AbstractAccTest {

    @Test
    public void testHistoryEventProducerIsNotEnabled() {
        HistoryEventProducer historyEventProducer = taskanaEngine.getHistoryEventProducer();
        assertFalse(historyEventProducer.isEnabled());
    }
}
