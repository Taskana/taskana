package acceptance.jobs;

import java.util.ArrayList;
import java.util.List;

import pro.taskana.common.internal.jobs.Clock;

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
