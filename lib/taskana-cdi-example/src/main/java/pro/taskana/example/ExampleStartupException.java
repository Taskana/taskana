package pro.taskana.example;

public class ExampleStartupException extends RuntimeException {

  public ExampleStartupException(Throwable cause) {
    super("Can't bootstrap CDI example application", cause);
  }
}
