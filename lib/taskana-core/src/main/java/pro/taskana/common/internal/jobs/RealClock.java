package pro.taskana.common.internal.jobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RealClock implements Clock {

  private final long initialStartDelay;
  private final long period;
  private final TimeUnit periodTimeUnit;
  private final List<ClockListener> listeners = Collections.synchronizedList(new ArrayList<>());
  private final ScheduledExecutorService timerService =
      Executors.newSingleThreadScheduledExecutor();

  public RealClock(long initialStartDelay, long period, TimeUnit periodTimeUnit) {
    this.initialStartDelay = initialStartDelay;
    this.period = period;
    this.periodTimeUnit = periodTimeUnit;
  }

  @Override
  public void register(ClockListener listener) {
    listeners.add(listener);
  }

  @Override
  public void start() {
    timerService.scheduleAtFixedRate(
        this::reportTimeElapse, initialStartDelay, period, periodTimeUnit);
  }

  @Override
  public void stop() {
    timerService.shutdown();
  }

  private void reportTimeElapse() {
    listeners.forEach(ClockListener::timeElapsed);
  }
}
