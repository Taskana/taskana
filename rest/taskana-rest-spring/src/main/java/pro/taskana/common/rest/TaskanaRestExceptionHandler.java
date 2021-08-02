package pro.taskana.common.rest;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pro.taskana.classification.api.exceptions.ClassificationAlreadyExistException;
import pro.taskana.classification.api.exceptions.ClassificationInUseException;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.DomainNotFoundException;
import pro.taskana.common.api.exceptions.ErrorCode;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.api.exceptions.NotFoundException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.api.exceptions.TaskanaRuntimeException;
import pro.taskana.common.internal.util.MapCreator;
import pro.taskana.common.rest.models.ExceptionRepresentationModel;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidCallbackStateException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.MismatchedTaskCommentCreatorException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import pro.taskana.workbasket.api.exceptions.WorkbasketInUseException;
import pro.taskana.workbasket.api.exceptions.WorkbasketMarkedForDeletionException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** This class handles TASKANA exceptions. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class TaskanaRestExceptionHandler extends ResponseEntityExceptionHandler {

  public static final String ERROR_KEY_QUERY_MALFORMED = "QUERY_PARAMETER_MALFORMED";
  public static final String ERROR_KEY_PAYLOAD = "PAYLOAD_TOO_LARGE";

  @ExceptionHandler({
    MismatchedRoleException.class,
    MismatchedWorkbasketPermissionException.class,
    MismatchedTaskCommentCreatorException.class,
    NotAuthorizedToQueryWorkbasketException.class
  })
  protected ResponseEntity<Object> handleForbiddenExceptions(TaskanaException ex, WebRequest req) {
    return buildResponse(ex.getErrorCode(), ex, req, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler({
    ClassificationNotFoundException.class,
    TaskCommentNotFoundException.class,
    TaskNotFoundException.class,
    TaskanaHistoryEventNotFoundException.class,
    WorkbasketNotFoundException.class
  })
  protected ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest req) {
    return buildResponse(ex.getErrorCode(), ex, req, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({
    TaskAlreadyExistException.class,
    ClassificationAlreadyExistException.class,
    ConcurrencyException.class,
    WorkbasketAlreadyExistException.class,
    WorkbasketAccessItemAlreadyExistException.class,
    AttachmentPersistenceException.class,
    WorkbasketMarkedForDeletionException.class
  })
  protected ResponseEntity<Object> handleConflictExceptions(TaskanaException ex, WebRequest req) {
    return buildResponse(ex.getErrorCode(), ex, req, HttpStatus.CONFLICT);
  }

  @ExceptionHandler({WorkbasketInUseException.class, ClassificationInUseException.class})
  protected ResponseEntity<Object> handleWorkbasketInUse(
      WorkbasketInUseException ex, WebRequest req) {
    return buildResponse(ex.getErrorCode(), ex, req, HttpStatus.LOCKED);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex, WebRequest req) {
    return buildResponse(ErrorCode.of(ERROR_KEY_PAYLOAD), ex, req, HttpStatus.PAYLOAD_TOO_LARGE);
  }

  @ExceptionHandler(BeanInstantiationException.class)
  protected ResponseEntity<Object> handleBeanInstantiationException(
      BeanInstantiationException ex, WebRequest req) {
    if (ex.getCause() instanceof InvalidArgumentException) {
      InvalidArgumentException cause = (InvalidArgumentException) ex.getCause();
      return buildResponse(cause.getErrorCode(), ex, req, HttpStatus.BAD_REQUEST);
    }
    return buildResponse(null, ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler({
    InvalidTaskStateException.class,
    InvalidCallbackStateException.class,
    InvalidOwnerException.class,
    InvalidArgumentException.class,
    DomainNotFoundException.class,
    TaskanaException.class
  })
  protected ResponseEntity<Object> handleBadRequestExceptions(TaskanaException ex, WebRequest req) {
    return buildResponse(ex.getErrorCode(), ex, req, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest req) {
    return buildResponse(
        ErrorCode.of(InvalidArgumentException.ERROR_KEY), ex, req, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(TaskanaRuntimeException.class)
  protected ResponseEntity<Object> handleInternalServerExceptions(
      TaskanaRuntimeException ex, WebRequest req) {
    return buildResponse(ex.getErrorCode(), ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest req) {
    return buildResponse(ErrorCode.of("UNKNOWN_ERROR"), ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  @NonNull
  protected ResponseEntity<Object> handleBindException(
      BindException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatus status,
      @NonNull WebRequest request) {

    MalformedQueryParameter[] wrongQueryParameters =
        ex.getBindingResult().getFieldErrors().stream()
            .map(this::extractMalformedQueryParameters)
            .flatMap(Collection::stream)
            .toArray(MalformedQueryParameter[]::new);

    // if we have no wrong query parameter then this BindException is representing something else.
    // Therefore, we only create an ErrorCode when we have found a wrong query parameter.
    ErrorCode errorCode =
        wrongQueryParameters.length != 0
            ? ErrorCode.of(
                ERROR_KEY_QUERY_MALFORMED,
                MapCreator.of("malformedQueryParameters", wrongQueryParameters))
            : null;

    return buildResponse(errorCode, ex, request, HttpStatus.BAD_REQUEST);
  }

  private ResponseEntity<Object> buildResponse(
      ErrorCode errorCode, Throwable ex, WebRequest req, HttpStatus status) {
    ExceptionRepresentationModel errorData =
        new ExceptionRepresentationModel(errorCode, status, ex, req);
    logger.error(
        String.format("Error occurred during processing of rest request: %s", errorData), ex);
    return ResponseEntity.status(status).body(errorData);
  }

  private List<MalformedQueryParameter> extractMalformedQueryParameters(FieldError fieldError) {
    if (fieldError.contains(TypeMismatchException.class)) {
      TypeMismatchException typeMismatchException = fieldError.unwrap(TypeMismatchException.class);
      if (typeMismatchException.getCause() instanceof ConversionFailedException) {
        ConversionFailedException conversionFailedException =
            (ConversionFailedException) typeMismatchException.getCause();
        Class<?> targetType = conversionFailedException.getTargetType().getType();
        if (targetType.isEnum()) {
          String queryParameter = fieldError.getField();
          // the redundancy below exists because we want to keep the enums sorted by their ordinal
          // value for the error output and want to use the contains performance boost of a HashSet.
          List<String> enumConstants =
              Arrays.stream(targetType.getEnumConstants())
                  .map(Object::toString)
                  .collect(Collectors.toList());
          Set<String> enumConstantSet = new HashSet<>(enumConstants);

          return getRejectedValues(typeMismatchException)
              .filter(value -> !enumConstantSet.contains(value))
              .map(value -> new MalformedQueryParameter(queryParameter, value, enumConstants))
              .collect(Collectors.toList());
        }
      }
    }

    return Collections.emptyList();
  }

  private Stream<String> getRejectedValues(TypeMismatchException ex) {
    Object value = ex.getValue();
    if (value != null && value.getClass().isArray()) {
      return Arrays.stream((Object[]) value).map(Objects::toString);
    }
    if (value != null && value.getClass().isAssignableFrom(Collection.class)) {
      return ((Collection<?>) value).stream().map(Objects::toString);
    }
    return Stream.of(value).map(Objects::toString);
  }

  public static class MalformedQueryParameter implements Serializable {
    private final String queryParameter;
    private final String actualValue;
    private final Collection<String> expectedValues;

    MalformedQueryParameter(
        String queryParameter, String actualValue, Collection<String> expectedValues) {
      this.queryParameter = queryParameter;
      this.actualValue = actualValue;
      this.expectedValues = expectedValues;
    }

    @SuppressWarnings("unused")
    public String getActualValue() {
      return actualValue;
    }

    @SuppressWarnings("unused")
    public Collection<String> getExpectedValues() {
      return expectedValues;
    }

    @SuppressWarnings("unused")
    public String getQueryParameter() {
      return queryParameter;
    }
  }
}
