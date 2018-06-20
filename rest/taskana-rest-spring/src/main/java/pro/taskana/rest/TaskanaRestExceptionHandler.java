package pro.taskana.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pro.taskana.exceptions.ClassificationAlreadyExistException;
import pro.taskana.exceptions.ClassificationNotFoundException;
import pro.taskana.exceptions.ConcurrencyException;
import pro.taskana.exceptions.DomainNotFoundException;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.InvalidOwnerException;
import pro.taskana.exceptions.InvalidStateException;
import pro.taskana.exceptions.InvalidWorkbasketException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.exceptions.TaskAlreadyExistException;
import pro.taskana.exceptions.TaskNotFoundException;
import pro.taskana.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.exceptions.WorkbasketInUseException;
import pro.taskana.exceptions.WorkbasketNotFoundException;

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
    protected ResponseEntity<Object> handleInvalidArgument(InvalidArgumentException ex, WebRequest req) {
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
    protected ResponseEntity<Object> handleTaskAlreadyExist(TaskAlreadyExistException ex,
        WebRequest req) {
        return buildResponse(ex, req, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NotAuthorizedToQueryWorkbasketException.class)
    protected ResponseEntity<Object> handleNotAuthorizedToQueryWorkbasket(NotAuthorizedToQueryWorkbasketException ex,
        WebRequest req) {
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
    protected ResponseEntity<Object> handleClassificationNotFound(ClassificationNotFoundException ex, WebRequest req) {
        return buildResponse(ex, req, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ClassificationAlreadyExistException.class)
    protected ResponseEntity<Object> handleClassificationAlreadyExist(ClassificationAlreadyExistException ex,
        WebRequest req) {
        return buildResponse(ex, req, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConcurrencyException.class)
    protected ResponseEntity<Object> handleConcurrencyException(ConcurrencyException ex, WebRequest req) {
        return buildResponse(ex, req, HttpStatus.LOCKED);
    }

    @ExceptionHandler(WorkbasketInUseException.class)
    protected ResponseEntity<Object> handleWorkbasketInUse(WorkbasketInUseException ex, WebRequest req) {
        return buildResponse(ex, req, HttpStatus.LOCKED);
    }

    @ExceptionHandler(WorkbasketAlreadyExistException.class)
    protected ResponseEntity<Object> handleWorkbasketAlreadyExist(WorkbasketAlreadyExistException ex, WebRequest req) {
        return buildResponse(ex, req, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(WorkbasketNotFoundException.class)
    protected ResponseEntity<Object> handleWorkbasketNotFound(WorkbasketNotFoundException ex, WebRequest req) {
        return buildResponse(ex, req, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidWorkbasketException.class)
    protected ResponseEntity<Object> handleInvalidWorkbasket(InvalidWorkbasketException ex, WebRequest req) {
        return buildResponse(ex, req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DomainNotFoundException.class)
    protected ResponseEntity<Object> handleDomainNotFound(DomainNotFoundException ex, WebRequest req) {
        return buildResponse(ex, req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest req) {
        return buildResponse(ex, req, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> buildResponse(Exception ex, WebRequest req, HttpStatus status) {
        TaskanaErrorData errorData = new TaskanaErrorData(status, ex, req);
        logError(ex, errorData);
        return new ResponseEntity<>(errorData, status);
    }

    private void logError(Exception ex, TaskanaErrorData errorData) {
        LOGGER.error(
            "Error occured during processing of rest request:\n" + errorData.toString(),
            ex);
    }

}
