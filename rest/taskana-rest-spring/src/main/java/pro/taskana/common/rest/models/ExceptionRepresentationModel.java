package pro.taskana.common.rest.models;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import pro.taskana.common.api.exceptions.ErrorCode;

/** This class holds error data. */
@Getter
@ToString
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
}
