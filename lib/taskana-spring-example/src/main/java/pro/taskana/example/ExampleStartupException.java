package pro.taskana.example;

public class ExampleStartupException extends RuntimeException {

  public ExampleStartupException(Throwable cause) {
    super("Can't bootstrap Spring example application", cause);
  }
}
