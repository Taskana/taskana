package acceptance.jobs;

import io.kadai.common.internal.jobs.Clock;
import java.util.ArrayList;
import java.util.List;

public class FakeClock implements Clock {

  List<ClockListener> listeners = new ArrayList<>();

  @Override
  public void register(ClockListener listener) {
    listeners.add(listener);
  }

  @Override
  public void start() {
    listeners.forEach(ClockListener::timeElapsed);
  }
}
