/*-
 * #%L
 * pro.taskana:taskana-rest-spring
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
package pro.taskana.common.rest.models;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import pro.taskana.common.api.exceptions.ErrorCode;

/** This class holds error data. */
public class ExceptionRepresentationModel {

  private final ErrorCode error;
  private final int status;
  private final String exception;
  private final String message;
  private String path;

  public ExceptionRepresentationModel(
      ErrorCode errorCode, HttpStatus stat, Throwable ex, WebRequest req) {
    this.error = errorCode;
    this.status = stat.value();
    this.exception = ex.getClass().getName();
    this.message = ex.getMessage();
    this.path = req.getDescription(false);
    if (this.path.startsWith("uri=")) {
      this.path = this.path.substring(4);
    }
  }

  public ErrorCode getError() {
    return error;
  }

  public int getStatus() {
    return status;
  }

  public String getException() {
    return exception;
  }

  public String getMessage() {
    return message;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String toString() {
    return "ExceptionRepresentationModel [error="
        + error
        + ", status="
        + status
        + ", exception="
        + exception
        + ", message="
        + message
        + ", path="
        + path
        + "]";
  }
}
