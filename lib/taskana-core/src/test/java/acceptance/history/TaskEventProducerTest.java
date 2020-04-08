package acceptance.history;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.Test;

/** Acceptance test for historyEventProducer class. */
class TaskEventProducerTest extends AbstractAccTest {

  @Test
  void testHistoryEventProducerIsNotEnabled() {
    assertThat(taskanaEngine.isHistoryEnabled()).isFalse();
  }
}
