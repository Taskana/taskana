package pro.taskana.common.internal.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.internal.stubbing.answers.CallsRealMethods;
import outside.of.pro.taskana.OutsideOfProTaskanaPackageLoggingTestClass;
import pro.taskana.AtProTaskanaRootPackageLoggingTestClass;
import uk.org.lidalia.slf4jext.Level;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

@NoLogging
class LoggingAspectTest {

  @BeforeEach
  public void clearLoggers() {
    TestLoggerFactory.clear();
  }

  @BeforeAll
  public static void setup() {
    System.setProperty(LoggingAspect.ENABLE_LOGGING_ASPECT_PROPERTY_KEY, "true");
  }

  @Test
  void should_NotLogMethodCalls_When_ClassDoesNotResideWithinTaskanaPackage() {
    OutsideOfProTaskanaPackageLoggingTestClass loggingTestClass =
        new OutsideOfProTaskanaPackageLoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.doStuff();

    assertThat(logger.getLoggingEvents()).isEmpty();
  }

  @Test
  void should_LogMethod_When_ClassResidesAtTaskanaRootPackage() {
    AtProTaskanaRootPackageLoggingTestClass loggingTestClass =
        new AtProTaskanaRootPackageLoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.doStuff();

    verifyLoggingStatement(logger, "doStuff", "", null);
  }

  @Test
  void should_LogInternalMethod_When_ClassResidesAtTaskanaSubPackage() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.logInternalMethod();

    verifyLoggingStatement(logger, "logInternalMethod", "", null);
  }

  @Test
  void should_NotLogInternalMethod_When_SystemPropertyIsNotSet() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    try (MockedStatic<LoggingAspect> loggingAspectMockedStatic =
        Mockito.mockStatic(LoggingAspect.class, new CallsRealMethods())) {
      loggingAspectMockedStatic.when(LoggingAspect::isLoggingAspectEnabled).thenReturn(false);

      loggingTestClass.logInternalMethod();
    }
    assertThat(logger.getLoggingEvents()).isEmpty();
  }

  @Test
  void should_LogInternalMethodWithReturnValue_When_ClassResidesAtTaskanaSubPackage() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.logInternalMethodWithReturnValue();

    verifyLoggingStatement(logger, "logInternalMethodWithReturnValue", "", "test string");
  }

  @Test
  void should_LogInternalMethodWithReturnValueNull_When_ClassResidesAtTaskanaSubPackage() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.logInternalMethodWithReturnValueNull();

    verifyLoggingStatement(logger, "logInternalMethodWithReturnValueNull", "", "null");
  }

  @Test
  void should_LogInternalMethodWithArguments_When_ClassResidesAtTaskanaSubPackage() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.logInternalMethodWithArguments("message");

    verifyLoggingStatement(logger, "logInternalMethodWithArguments", "param = message", null);
  }

  @Test
  void should_LogInternalMethodWithReturnValueAndArguments_When_ClassResidesAtTaskanaSubPackage() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.logInternalMethodWithReturnValueAndArguments("message");

    verifyLoggingStatement(
        logger, "logInternalMethodWithReturnValueAndArguments", "param = message", "return value");
  }

  @Test
  void should_NotLogExternalMethod_When_AMethodCallsAMethodOutsideOfTaskanaPackage() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.callsExternalMethod();

    verifyLoggingStatement(logger, "callsExternalMethod", "", null);
  }

  @Test
  void should_LogMultipleMethods_When_SubMethodIsCalled() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.logInternalMethodWrapper();
    assertThat(logger.getLoggingEvents())
        .extracting(LoggingEvent::getMessage)
        .areExactly(2, new Condition<>(message -> message.startsWith("entry to"), "entry log"))
        .areExactly(2, new Condition<>(message -> message.startsWith("exit from"), "exit log"));
  }

  @Test
  void should_NotLogInternalMethod_When_MethodIsAnnotatedWithNoLogging() {
    LoggingTestClass loggingTestClass = new LoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(loggingTestClass.getClass());

    loggingTestClass.doNotLogInternalMethod();

    assertThat(logger.getLoggingEvents()).isEmpty();
  }

  @Test
  void should_NotLogInternalMethod_When_ClassIsAnnotatedWithNoLogging() {
    NoLoggingTestClass noLoggingTestClass = new NoLoggingTestClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(noLoggingTestClass.getClass());

    noLoggingTestClass.doNotLogInternalMethod();

    assertThat(logger.getLoggingEvents()).isEmpty();
  }

  @Test
  void should_NotLogInternalMethod_When_SuperClassIsAnnotatedWithNoLogging() {
    NoLoggingTestSubClass noLoggingTestSubClass = new NoLoggingTestSubClass();
    TestLogger logger = TestLoggerFactory.getTestLogger(noLoggingTestSubClass.getClass());

    noLoggingTestSubClass.doNotLogInternalMethod();

    assertThat(logger.getLoggingEvents()).isEmpty();
  }

  private void verifyLoggingStatement(
      TestLogger logger, String methodName, String arguments, Object returnValue) {
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
}
