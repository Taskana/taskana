/*-
 * #%L
 * pro.taskana:taskana-aspect-logging
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.internal.logging;

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

  @Pointcut(
      "!@annotation(pro.taskana.common.internal.logging.NoLogging)"
          + " && !within(@pro.taskana.common.internal.logging.NoLogging *)"
          + " && execution(* pro.taskana..*(..))"
          + " && !execution(* lambda*(..))"
          + " && !execution(* access*(..))"
          + " && !execution(String *.toString())"
          + " && !execution(int *.hashCode())"
          + " && !execution(boolean *.canEqual(Object))"
          + " && !execution(boolean *.equals(Object))")
  public void traceLogging() {}

  // This method exists, so that we can mock the system property during testing.
  public static boolean isLoggingAspectEnabled() {
    return LazyHolder.LOGGING_ASPECT_ENABLED;
  }

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

  // This Initialization-on-demand holder idiom is necessary so that the retrieval of the system
  // property will be executed during the execution of the first JointPoint.
  // This allows us to set the system property during test execution BEFORE retrieving the system
  // property.
  private static class LazyHolder {
    private static final boolean LOGGING_ASPECT_ENABLED =
        "true".equals(System.getProperty(ENABLE_LOGGING_ASPECT_PROPERTY_KEY));
  }
}
