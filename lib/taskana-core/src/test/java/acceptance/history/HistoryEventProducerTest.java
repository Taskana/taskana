package acceptance.history;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import acceptance.AbstractAccTest;
import pro.taskana.history.HistoryEventProducer;
import pro.taskana.impl.TaskanaEngineImpl;

/**
 * Acceptance test for historyEventProducer class.
 */
public class HistoryEventProducerTest extends AbstractAccTest {

    @Test
    public void testHistoryEventProducerIsNotEnabled() {
        HistoryEventProducer historyEventProducer = ((TaskanaEngineImpl) taskanaEngine).getHistoryEventProducer();
        assertEquals(false, historyEventProducer.isEnabled());
    }
}
