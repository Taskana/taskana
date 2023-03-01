/*-
 * #%L
 * pro.taskana:taskana-common-test
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
package pro.taskana.common.test.security;

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
