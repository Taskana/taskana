/*-
 * #%L
 * pro.taskana:taskana-common
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
package pro.taskana.common.api.exceptions;

/** The common base class for TASKANA's runtime exceptions. */
public class TaskanaRuntimeException extends RuntimeException {

  private final ErrorCode errorCode;

  protected TaskanaRuntimeException(String message, ErrorCode errorCode) {
    this(message, errorCode, null);
  }

  protected TaskanaRuntimeException(String message, ErrorCode errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }

  @Override
  public String toString() {
    return "TaskanaRuntimeException [errorCode=" + errorCode + ", message=" + getMessage() + "]";
  }
}
