package pro.taskana.common.internal.jobs;

public interface Clock {
  void register(ClockListener listener);

  void start();

  default void stop() {}

  @FunctionalInterface
  interface ClockListener {
    void timeElapsed();
  }
}
