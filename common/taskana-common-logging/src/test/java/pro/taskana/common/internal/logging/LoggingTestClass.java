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
