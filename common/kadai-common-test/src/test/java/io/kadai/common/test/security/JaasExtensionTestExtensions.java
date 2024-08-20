package io.kadai.common.test.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.JUnitException;

public class JaasExtensionTestExtensions {
  static class ShouldThrowParameterResolutionException implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
        throws Throwable {
      if (throwable instanceof ParameterResolutionException) {
        return;
      }
      throw throwable;
    }
  }

  static class ShouldThrowJunitException implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
        throws Throwable {
      if (throwable instanceof JUnitException) {
        JUnitException exception = (JUnitException) throwable;
        assertThat(exception.getMessage())
            .isEqualTo("Please use @TestTemplate instead of @Test for multiple accessIds");
        return;
      }
      throw throwable;
    }
  }
}
