package io.k8screen.backend.exception;

import io.k8screen.backend.result.DataResult;
import io.k8screen.backend.result.ResponseFactory;
import io.k8screen.backend.result.Result;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

  private final @NotNull ResponseFactory responseFactory;

  @ExceptionHandler(Exception.class)
  public @NotNull ResponseEntity<Result> handleException(final @NotNull Exception ex) {
    log.error(ex.getMessage());

    final Result result =
        this.responseFactory.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public @NotNull ResponseEntity<Result> handleValidations(
      final @NotNull MethodArgumentNotValidException ex) {
    log.info(ex.getMessage());

    final var errors = new HashMap<String, Object>();

    for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    final DataResult<HashMap<String, Object>> result =
        this.responseFactory.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), errors);
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ItemNotFoundException.class)
  public @NotNull ResponseEntity<Result> handleItemNotFoundException(
      final @NotNull ItemNotFoundException ex) {
    log.info(ex.getMessage());

    final Result result =
        this.responseFactory.error(HttpStatus.NO_CONTENT.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public @NotNull ResponseEntity<Result> handleBadCredentialsException(
      final @NotNull BadCredentialsException ex) {
    log.info(ex.getMessage());

    final Result result =
        this.responseFactory.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public @NotNull ResponseEntity<Result> handleUnauthorizedException(
      final @NotNull UnauthorizedException ex) {
    log.info(ex.getMessage());

    final Result result =
        this.responseFactory.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadRequestException.class)
  public @NotNull ResponseEntity<Result> handleBadRequestException(
      final @NotNull BadRequestException ex) {
    log.info(ex.getMessage());
    final Result result =
        this.responseFactory.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ForbiddenException.class)
  public @NotNull ResponseEntity<Result> handleForbiddenException(
      final @NotNull ForbiddenException ex) {
    log.info(ex.getMessage());
    final Result result = this.responseFactory.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(StripeIsDisabledException.class)
  public @NotNull ResponseEntity<Result> handleStripeIsDisabledException(
      final @NotNull StripeIsDisabledException ex) {
    log.info(ex.getMessage());
    final Result result = this.responseFactory.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(SubscriptionLimitExceed.class)
  public @NotNull ResponseEntity<Result> handleSubscriptionLimitExceedException(
      final @NotNull SubscriptionLimitExceed ex) {
    log.info(ex.getMessage());
    final Result result = this.responseFactory.error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
    return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
  }
}
