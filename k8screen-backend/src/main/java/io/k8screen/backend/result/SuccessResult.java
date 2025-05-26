package io.k8screen.backend.result;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class SuccessResult<T> extends Result {
  public SuccessResult(final int status, final @NotNull String message) {
    super(true, status, message);
  }
}
