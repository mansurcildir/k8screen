package io.k8screen.backend.core.exception;

import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public @NotNull ResponseEntity<Object> handleException(final @NotNull Exception ex) {
    log.error("Unexpected Error: {}", ex.getMessage());
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public @NotNull ResponseEntity<Object> handleValidations(
      final @NotNull MethodArgumentNotValidException exception) {
    final var errors = new HashMap<String, Object>();

    for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
      errors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }

    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ItemNotFoundException.class)
  public @NotNull ResponseEntity<Object> handleItemNotFoundException(
      final @NotNull ItemNotFoundException exception) {
    log.error(exception.getMessage());
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
  }
}
