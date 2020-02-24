package acceptance;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import pro.taskana.task.api.exceptions.UpdateFailedException;

public class UpdateFaileExceptionTest {
  @Test
  void testUpdateFailedException() {
    final UpdateFailedException exception = new UpdateFailedException("for test");
    assertThatThrownBy(() -> throwit()).isInstanceOf(UpdateFailedException.class);
  }

  void throwit() throws UpdateFailedException {
    throw new UpdateFailedException("for coverage");
  }
}
