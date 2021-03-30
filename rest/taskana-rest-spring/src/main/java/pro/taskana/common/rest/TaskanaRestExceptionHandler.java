package pro.taskana.common.rest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.NotFoundException;
import pro.taskana.common.rest.models.TaskanaErrorData;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.InvalidWorkbasketException;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;

/** This class handles taskana exceptions. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class TaskanaRestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(InvalidArgumentException.class)
  protected ResponseEntity<Object> handleInvalidArgument(
      InvalidArgumentException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.BAD_REQUEST, false);
  }

  @ExceptionHandler(NotAuthorizedException.class)
  protected ResponseEntity<Object> handleNotAuthorized(NotAuthorizedException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(NotFoundException.class)
  protected ResponseEntity<Object> handleTaskNotFound(NotFoundException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(TaskAlreadyExistException.class)
  protected ResponseEntity<Object> handleTaskAlreadyExist(
      TaskAlreadyExistException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(NotAuthorizedToQueryWorkbasketException.class)
  protected ResponseEntity<Object> handleNotAuthorizedToQueryWorkbasket(
      NotAuthorizedToQueryWorkbasketException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(InvalidStateException.class)
  protected ResponseEntity<Object> handleInvalidState(InvalidStateException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InvalidOwnerException.class)
  protected ResponseEntity<Object> handleInvalidOwner(InvalidOwnerException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ClassificationAlreadyExistException.class)
  protected ResponseEntity<Object> handleClassificationAlreadyExist(
      ClassificationAlreadyExistException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(DuplicateKeyException.class)
  protected ResponseEntity<Object> handleDuplicateKey(DuplicateKeyException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ConcurrencyException.class)
  protected ResponseEntity<Object> handleConcurrencyException(
      ConcurrencyException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(WorkbasketInUseException.class)
  protected ResponseEntity<Object> handleWorkbasketInUse(
      WorkbasketInUseException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.LOCKED);
  }

  @ExceptionHandler(WorkbasketAlreadyExistException.class)
  protected ResponseEntity<Object> handleWorkbasketAlreadyExist(
      WorkbasketAlreadyExistException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(WorkbasketAccessItemAlreadyExistException.class)
  protected ResponseEntity<Object> handleWorkbasketAccessItemAlreadyExist(
      WorkbasketAccessItemAlreadyExistException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InvalidWorkbasketException.class)
  protected ResponseEntity<Object> handleInvalidWorkbasket(
      InvalidWorkbasketException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DomainNotFoundException.class)
  protected ResponseEntity<Object> handleDomainNotFound(
      DomainNotFoundException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.PAYLOAD_TOO_LARGE);
  }

  @Override
  protected ResponseEntity<Object> handleBindException(
      BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    return buildResponse(ex, request, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.BAD_REQUEST);
  }

  private ResponseEntity<Object> buildResponse(Throwable ex, WebRequest req, HttpStatus status) {
    return buildResponse(ex, req, status, true);
  }

  private ResponseEntity<Object> buildResponse(
      Throwable ex, WebRequest req, HttpStatus status, boolean logExceptionOnError) {
    TaskanaErrorData errorData = new TaskanaErrorData(status, ex, req);
    if (logExceptionOnError) {
      logger.error(
          String.format(
              "Error occurred during processing of rest request: %s", errorData.toString()),
          ex);
    } else {
      if (logger.isDebugEnabled()) {
        logger.debug(
            String.format(
                "Error occurred during processing of rest request: %s", errorData.toString()),
            ex);
      }
    }
    return ResponseEntity.status(status).body(errorData);
  }
}
