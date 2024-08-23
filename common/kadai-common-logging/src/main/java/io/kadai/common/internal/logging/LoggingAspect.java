package io.kadai.common.internal.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoLogging
@Aspect
public class LoggingAspect {

  public static final String ENABLE_LOGGING_ASPECT_PROPERTY_KEY = "enableLoggingAspect";
  private static final Map<String, Logger> CLASS_TO_LOGGER = new ConcurrentHashMap<>();

  // This method exists, so that we can mock the system property during testing.
  public static boolean isLoggingAspectEnabled() {
    return LazyHolder.LOGGING_ASPECT_ENABLED;
  }

  private static String mapParametersNameValue(String[] parameterNames, Object[] values) {
    Map<String, Object> parametersNameToValue = new HashMap<>();

    if (parameterNames.length > 0) {
      for (int i = 0; i < parameterNames.length; i++) {
        parametersNameToValue.put(parameterNames[i], values[i]);
      }
    }

    StringBuilder stringBuilder = new StringBuilder();
    for (Entry<String, Object> parameter : parametersNameToValue.entrySet()) {
      stringBuilder.append(parameter.getKey()).append(" = ").append(parameter.getValue());
    }
    return stringBuilder.toString();
  }

  @Pointcut(
      "!@annotation(io.kadai.common.internal.logging.NoLogging)"
          + " && !within(@io.kadai.common.internal.logging.NoLogging *)"
          + " && execution(* io.kadai..*(..))"
          + " && !execution(* lambda*(..))"
          + " && !execution(* access*(..))"
          + " && !execution(String *.toString())"
          + " && !execution(int *.hashCode())"
          + " && !execution(boolean *.canEqual(Object))"
          + " && !execution(boolean *.equals(Object))")
  public void traceLogging() {}

  @Before("traceLogging()")
  public void beforeMethodExecuted(JoinPoint joinPoint) {
    if (isLoggingAspectEnabled()) {
      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      String declaringTypeName = methodSignature.getDeclaringTypeName();
      Logger currentLogger =
          CLASS_TO_LOGGER.computeIfAbsent(declaringTypeName, LoggerFactory::getLogger);

      if (currentLogger.isTraceEnabled()) {
        String methodName = methodSignature.getName();
        Object[] values = joinPoint.getArgs();
        String[] parameterNames = methodSignature.getParameterNames();
        String parametersValues = mapParametersNameValue(parameterNames, values);

        currentLogger.trace("entry to {}({})", methodName, parametersValues);
      }
    }
  }

  @AfterReturning(pointcut = "traceLogging()", returning = "returnedObject")
  public void afterMethodExecuted(JoinPoint joinPoint, Object returnedObject) {
    if (isLoggingAspectEnabled()) {
      MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
      String declaringTypeName = methodSignature.getDeclaringTypeName();
      Logger currentLogger =
          CLASS_TO_LOGGER.computeIfAbsent(declaringTypeName, LoggerFactory::getLogger);

      if (currentLogger.isTraceEnabled()) {
        String methodName = methodSignature.getName();
        // unfortunately necessary, because this method returns a raw type
        Class<?> returnType = methodSignature.getReturnType();
        if (returnType.isAssignableFrom(void.class)) {
          currentLogger.trace("exit from {}.", methodName);
        } else {
          currentLogger.trace(
              "exit from {}. Returning: '{}'",
              methodName,
              Objects.toString(returnedObject, "null"));
        }
      }
    }
  }

  // This Initialization-on-demand holder idiom is necessary so that the retrieval of the system
  // property will be executed during the execution of the first JointPoint.
  // This allows us to set the system property during test execution BEFORE retrieving the system
  // property.
  private static class LazyHolder {
    private static final boolean LOGGING_ASPECT_ENABLED =
        "true".equals(System.getProperty(ENABLE_LOGGING_ASPECT_PROPERTY_KEY));
  }
}
