package pro.taskana.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.InvalidOwnerException;
import pro.taskana.common.api.exceptions.InvalidStateException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.workbasket.api.exceptions.InvalidWorkbasketException;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/**
 * This class handles taskana exceptions.
 *
 * @author bbr
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class TaskanaRestExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaRestExceptionHandler.class);

  @ExceptionHandler(InvalidArgumentException.class)
  protected ResponseEntity<Object> handleInvalidArgument(
      InvalidArgumentException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NotAuthorizedException.class)
  protected ResponseEntity<Object> handleNotAuthorized(NotAuthorizedException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(TaskNotFoundException.class)
  protected ResponseEntity<Object> handleTaskNotFound(TaskNotFoundException ex, WebRequest req) {
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

  @ExceptionHandler(ClassificationNotFoundException.class)
  protected ResponseEntity<Object> handleClassificationNotFound(
      ClassificationNotFoundException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.NOT_FOUND);
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

  @ExceptionHandler(WorkbasketNotFoundException.class)
  protected ResponseEntity<Object> handleWorkbasketNotFound(
      WorkbasketNotFoundException ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.NOT_FOUND);
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

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest req) {
    return buildResponse(ex, req, HttpStatus.BAD_REQUEST);
  }

  private ResponseEntity<Object> buildResponse(Exception ex, WebRequest req, HttpStatus status) {
    TaskanaErrorData errorData = new TaskanaErrorData(status, ex, req);
    logError(ex, errorData);
    return ResponseEntity.status(status).body(errorData);
  }

  private void logError(Exception ex, TaskanaErrorData errorData) {
    LOGGER.error(
        "Error occurred during processing of rest request:\n {}" + errorData.toString(), ex);
  }
}
