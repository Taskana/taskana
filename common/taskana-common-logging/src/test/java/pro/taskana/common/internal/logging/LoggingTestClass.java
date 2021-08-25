package pro.taskana.common.internal.logging;

class LoggingTestClass {

  public void logInternalMethod() {}

  @SuppressWarnings("UnusedReturnValue")
  public String logInternalMethodWithReturnValue() {
    return "test string";
  }

  @SuppressWarnings("UnusedReturnValue")
  public String logInternalMethodWithReturnValueNull() {
    return null;
  }

  @SuppressWarnings("unused")
  public void logInternalMethodWithArguments(String param) {}

  @SuppressWarnings({"UnusedReturnValue", "unused"})
  public String logInternalMethodWithReturnValueAndArguments(String param) {
    return "return value";
  }

  public void logInternalMethodWrapper() {
    logInternalMethodPrivate();
  }

  @SuppressWarnings("unused")
  public void callsExternalMethod() {
    String sum = String.valueOf(5);
  }

  @NoLogging
  public void doNotLogInternalMethod() {}

  private void logInternalMethodPrivate() {}
}
