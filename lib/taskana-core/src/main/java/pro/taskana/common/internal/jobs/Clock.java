package pro.taskana.common.internal.jobs;

public interface Clock {
  void register(ClockListener listener);

  void start();

  void stop();

  interface ClockListener {
    void timeElapsed();
  }
}
