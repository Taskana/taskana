package acceptance.history;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.Test;

/** Acceptance test for HistoryEventManager class. */
class HistoryEventManagerTest extends AbstractAccTest {

  @Test
  void testHistoryEventManagerIsNotEnabled() {
    assertThat(taskanaEngine.isHistoryEnabled()).isFalse();
  }
}
