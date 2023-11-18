package pro.taskana.common.internal;

public class TaskanaCdiStartupException extends RuntimeException {

  public TaskanaCdiStartupException(Throwable cause) {
    super("Can't init TaskanaProducers", cause);
  }
}
