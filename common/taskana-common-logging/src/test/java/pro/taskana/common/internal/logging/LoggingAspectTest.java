package pro.taskana.common.internal.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

class LoggingAspectTest {

  TestLogger logger = TestLoggerFactory.getTestLogger(LoggingTestClass.class);

  @BeforeEach
  public void clearLoggers() {
    TestLoggerFactory.clear();
  }

  @Test
  void should_Log_For_InternalMethod() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();

    loggingTestClass.logInternalMethod();

    verifyLoggingStatement("logInternalMethod", "", null);
  }

  @Test
  void should_Log_For_InternalMethodWithReturnValue() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();

    loggingTestClass.logInternalMethodWithReturnValue();

    verifyLoggingStatement("logInternalMethodWithReturnValue", "", "test string");
  }

  @Test
  void should_Log_For_InternalMethodWithReturnValueNull() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();

    loggingTestClass.logInternalMethodWithReturnValueNull();

    verifyLoggingStatement("logInternalMethodWithReturnValueNull", "", "null");
  }

  @Test
  void should_Log_For_InternalMethodWithArguments() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();

    loggingTestClass.logInternalMethodWithArguments("message");

    verifyLoggingStatement("logInternalMethodWithArguments", "param = message", null);
  }

  @Test
  void should_Log_For_InternalMethodWithReturnValueAndArguments() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();

    loggingTestClass.logInternalMethodWithReturnValueAndArguments("message");

    verifyLoggingStatement(
        "logInternalMethodWithReturnValueAndArguments", "param = message", "return value");
  }

  @Test
  void should_NotLog_For_ExternalMethod() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();

    loggingTestClass.callsExternalMethod();

    verifyLoggingStatement("callsExternalMethod", "", null);
  }

  @Test
  void should_LogMultipleMethods_When_SubMethodIsCalled() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();

    loggingTestClass.logInternalMethodWrapper();
    assertThat(logger.getLoggingEvents())
        .extracting(LoggingEvent::getMessage)
        .areExactly(2, new Condition<>(message -> message.startsWith("entry to"), "entry log"))
        .areExactly(2, new Condition<>(message -> message.startsWith("exit from"), "exit log"));
  }

  @Test
  void should_NotLogInternalMethod_When_MethodIsAnnotatedWithNoLogging() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();

    loggingTestClass.doNotLogInternalMethod();

    assertThat(logger.getLoggingEvents()).isEmpty();
  }

  @Test
  void should_NotLogInternalMethod_When_ClassIsAnnotatedWithNoLogging() {
    NoLoggingTestClass noLoggingTestClass = new NoLoggingTestClass();

    noLoggingTestClass.doNotLogInternalMethod();

    assertThat(logger.getLoggingEvents()).isEmpty();
  }

  @Test
  void should_NotLogInternalMethod_When_SuperClassIsAnnotatedWithNoLogging() {
    NoLoggingTestSubClass noLoggingTestSubClass = new NoLoggingTestSubClass();

    noLoggingTestSubClass.doNotLogInternalMethod();

    assertThat(logger.getLoggingEvents()).isEmpty();
  }

  private void verifyLoggingStatement(String methodName, String arguments, Object returnValue) {
    assertThat(logger.getLoggingEvents()).hasSize(2);
    LoggingEvent entryLoggingEvent = logger.getLoggingEvents().get(0);
    assertThat(entryLoggingEvent.getLevel()).isEqualTo(Level.TRACE);
    assertThat(entryLoggingEvent.getMessage()).startsWith("entry to");
    assertThat(entryLoggingEvent.getArguments()).containsExactly(methodName, arguments);

    LoggingEvent exitLoggingEvent = logger.getLoggingEvents().get(1);

    assertThat(exitLoggingEvent.getLevel()).isEqualTo(Level.TRACE);
    assertThat(exitLoggingEvent.getMessage()).startsWith("exit from");

    if (returnValue == null) {
      assertThat(exitLoggingEvent.getArguments()).containsExactly(methodName);
    } else {
      assertThat(exitLoggingEvent.getMessage()).contains("Returning: ");
      assertThat(exitLoggingEvent.getArguments()).containsExactly(methodName, returnValue);
    }
  }

  static class LoggingTestClass {
    public void logInternalMethod() {}

    @SuppressWarnings("UnusedReturnValue")
    String logInternalMethodWithReturnValue() {
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

  @NoLogging
  static class NoLoggingTestClass {
    public void doNotLogInternalMethod() {}
  }

  static class NoLoggingTestSubClass extends NoLoggingTestClass {}
}
