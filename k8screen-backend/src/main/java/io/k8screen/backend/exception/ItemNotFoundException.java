package io.k8screen.backend.exception;

import org.jetbrains.annotations.NotNull;

public class ItemNotFoundException extends RuntimeException {

  private static final @NotNull String DEFAULT_MESSAGE = "Item not found";

  public ItemNotFoundException() {
    super(DEFAULT_MESSAGE);
  }

  public ItemNotFoundException(final @NotNull String message) {
    super(message);
  }
}
