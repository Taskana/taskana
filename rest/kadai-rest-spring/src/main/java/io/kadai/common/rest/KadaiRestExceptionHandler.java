package io.kadai.common.rest;

import static java.util.function.Predicate.not;

import io.kadai.classification.api.exceptions.ClassificationAlreadyExistException;
import io.kadai.classification.api.exceptions.ClassificationInUseException;
import io.kadai.classification.api.exceptions.ClassificationNotFoundException;
import io.kadai.classification.api.exceptions.MalformedServiceLevelException;
import io.kadai.common.api.exceptions.AutocommitFailedException;
import io.kadai.common.api.exceptions.ConcurrencyException;
import io.kadai.common.api.exceptions.ConnectionNotSetException;
import io.kadai.common.api.exceptions.DomainNotFoundException;
import io.kadai.common.api.exceptions.ErrorCode;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.api.exceptions.KadaiRuntimeException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.api.exceptions.UnsupportedDatabaseException;
import io.kadai.common.api.exceptions.WrongCustomHolidayFormatException;
import io.kadai.common.rest.models.ExceptionRepresentationModel;
import io.kadai.spi.history.api.exceptions.KadaiHistoryEventNotFoundException;
import io.kadai.task.api.exceptions.AttachmentPersistenceException;
import io.kadai.task.api.exceptions.InvalidCallbackStateException;
import io.kadai.task.api.exceptions.InvalidOwnerException;
import io.kadai.task.api.exceptions.InvalidTaskStateException;
import io.kadai.task.api.exceptions.NotAuthorizedOnTaskCommentException;
import io.kadai.task.api.exceptions.TaskAlreadyExistException;
import io.kadai.task.api.exceptions.TaskCommentNotFoundException;
import io.kadai.task.api.exceptions.TaskNotFoundException;
import io.kadai.user.api.exceptions.UserAlreadyExistException;
import io.kadai.user.api.exceptions.UserNotFoundException;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.exceptions.WorkbasketAccessItemAlreadyExistException;
import io.kadai.workbasket.api.exceptions.WorkbasketAlreadyExistException;
import io.kadai.workbasket.api.exceptions.WorkbasketInUseException;
import io.kadai.workbasket.api.exceptions.WorkbasketMarkedForDeletionException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** This class handles KADAI exceptions. */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class KadaiRestExceptionHandler extends ResponseEntityExceptionHandler {

  public static final String ERROR_KEY_QUERY_MALFORMED = "QUERY_PARAMETER_MALFORMED";
  public static final String ERROR_KEY_PAYLOAD = "PAYLOAD_TOO_LARGE";
  public static final String ERROR_KEY_UNKNOWN_ERROR = "UNKNOWN_ERROR";
  private static final Map<String, HttpStatus> HTTP_STATUS_BY_ERROR_CODE_KEY =
      Map.ofEntries(
          Map.entry(MalformedServiceLevelException.ERROR_KEY, HttpStatus.BAD_REQUEST),
          Map.entry(WrongCustomHolidayFormatException.ERROR_KEY, HttpStatus.BAD_REQUEST),
          Map.entry(DomainNotFoundException.ERROR_KEY, HttpStatus.BAD_REQUEST),
          Map.entry(InvalidArgumentException.ERROR_KEY, HttpStatus.BAD_REQUEST),
          Map.entry(ERROR_KEY_QUERY_MALFORMED, HttpStatus.BAD_REQUEST),
          Map.entry(InvalidCallbackStateException.ERROR_KEY, HttpStatus.BAD_REQUEST),
          Map.entry(InvalidOwnerException.ERROR_KEY, HttpStatus.BAD_REQUEST),
          Map.entry(InvalidTaskStateException.ERROR_KEY, HttpStatus.BAD_REQUEST),
          //
          Map.entry(NotAuthorizedException.ERROR_KEY, HttpStatus.FORBIDDEN),
          Map.entry(NotAuthorizedOnTaskCommentException.ERROR_KEY, HttpStatus.FORBIDDEN),
          Map.entry(NotAuthorizedOnWorkbasketException.ERROR_KEY_ID, HttpStatus.FORBIDDEN),
          Map.entry(NotAuthorizedOnWorkbasketException.ERROR_KEY_KEY_DOMAIN, HttpStatus.FORBIDDEN),
          //
          Map.entry(ClassificationNotFoundException.ERROR_KEY_ID, HttpStatus.NOT_FOUND),
          Map.entry(ClassificationNotFoundException.ERROR_KEY_KEY_DOMAIN, HttpStatus.NOT_FOUND),
          Map.entry(TaskCommentNotFoundException.ERROR_KEY, HttpStatus.NOT_FOUND),
          Map.entry(TaskNotFoundException.ERROR_KEY, HttpStatus.NOT_FOUND),
          Map.entry(UserNotFoundException.ERROR_KEY, HttpStatus.NOT_FOUND),
          Map.entry(WorkbasketNotFoundException.ERROR_KEY_ID, HttpStatus.NOT_FOUND),
          Map.entry(WorkbasketNotFoundException.ERROR_KEY_KEY_DOMAIN, HttpStatus.NOT_FOUND),
          Map.entry(KadaiHistoryEventNotFoundException.ERROR_KEY, HttpStatus.NOT_FOUND),
          //
          Map.entry(AttachmentPersistenceException.ERROR_KEY, HttpStatus.CONFLICT),
          Map.entry(ClassificationAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
          Map.entry(ConcurrencyException.ERROR_KEY, HttpStatus.CONFLICT),
          Map.entry(TaskAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
          Map.entry(UserAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
          Map.entry(WorkbasketAccessItemAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
          Map.entry(WorkbasketAlreadyExistException.ERROR_KEY, HttpStatus.CONFLICT),
          Map.entry(WorkbasketMarkedForDeletionException.ERROR_KEY, HttpStatus.CONFLICT),
          //
          Map.entry(ERROR_KEY_PAYLOAD, HttpStatus.PAYLOAD_TOO_LARGE),
          //
          Map.entry(ClassificationInUseException.ERROR_KEY, HttpStatus.LOCKED),
          Map.entry(WorkbasketInUseException.ERROR_KEY, HttpStatus.LOCKED),
          //
          Map.entry(AutocommitFailedException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR),
          Map.entry(ConnectionNotSetException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR),
          Map.entry(SystemException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR),
          Map.entry(UnsupportedDatabaseException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR),
          Map.entry(ERROR_KEY_UNKNOWN_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));

  @Override
  protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {

    return buildResponse(
        ErrorCode.of(ERROR_KEY_PAYLOAD),
        ex,
        request,
        HTTP_STATUS_BY_ERROR_CODE_KEY.getOrDefault(
            ERROR_KEY_PAYLOAD, HttpStatus.INTERNAL_SERVER_ERROR));
  }

  @ExceptionHandler(BeanInstantiationException.class)
  protected ResponseEntity<Object> handleBeanInstantiationException(
      BeanInstantiationException ex, WebRequest req) {
    if (ex.getCause() instanceof InvalidArgumentException) {
      InvalidArgumentException cause = (InvalidArgumentException) ex.getCause();
      return handleKadaiRuntimeException(cause, req);
    }
    return buildResponse(null, ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // This ExceptionHandler exists to convert IllegalArgumentExceptions to InvalidArgumentExceptions.
  // Once IllegalArgumentExceptions are no longer in use, you can delete this \(*_*)/
  @ExceptionHandler(IllegalArgumentException.class)
  protected ResponseEntity<Object> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest req) {
    HttpStatus status =
        HTTP_STATUS_BY_ERROR_CODE_KEY.getOrDefault(
            InvalidArgumentException.ERROR_KEY, HttpStatus.INTERNAL_SERVER_ERROR);
    return buildResponse(ErrorCode.of(InvalidArgumentException.ERROR_KEY), ex, req, status);
  }

  @ExceptionHandler(KadaiRuntimeException.class)
  protected ResponseEntity<Object> handleKadaiRuntimeException(
      KadaiRuntimeException ex, WebRequest req) {
    HttpStatus status =
        HTTP_STATUS_BY_ERROR_CODE_KEY.getOrDefault(
            ex.getErrorCode().getKey(), HttpStatus.INTERNAL_SERVER_ERROR);
    return buildResponse(ex.getErrorCode(), ex, req, status);
  }

  @ExceptionHandler(KadaiException.class)
  protected ResponseEntity<Object> handleKadaiException(KadaiException ex, WebRequest req) {
    HttpStatus status =
        HTTP_STATUS_BY_ERROR_CODE_KEY.getOrDefault(
            ex.getErrorCode().getKey(), HttpStatus.INTERNAL_SERVER_ERROR);
    return buildResponse(ex.getErrorCode(), ex, req, status);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest req) {
    return buildResponse(
        ErrorCode.of(ERROR_KEY_UNKNOWN_ERROR), ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  @NonNull
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      @NonNull HttpHeaders headers,
      @NonNull HttpStatusCode status,
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
                ERROR_KEY_QUERY_MALFORMED, Map.of("malformedQueryParameters", wrongQueryParameters))
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
                  .map(Enum.class::cast)
                  .map(Enum::name)
                  .toList();
          Set<String> enumConstantSet = new HashSet<>(enumConstants);

          return getRejectedValues(typeMismatchException)
              .filter(not(enumConstantSet::contains))
              .map(value -> new MalformedQueryParameter(queryParameter, value, enumConstants))
              .toList();
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
