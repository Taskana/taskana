package pro.taskana.common.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class LocalTimeIntervalTest {

  @Test
  void naturalOrderingIsDefinedByBegin() {
    LocalTimeInterval ltiOne = new LocalTimeInterval(LocalTime.MIN, LocalTime.MAX);
    LocalTimeInterval ltiTwo =
        new LocalTimeInterval(LocalTime.MIN.plus(1, ChronoUnit.MILLIS), LocalTime.MAX);

    assertThat(ltiOne).isLessThan(ltiTwo);
  }
}
