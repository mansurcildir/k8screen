package io.k8screen.backend.exception;

import org.jetbrains.annotations.NotNull;

public class SubscriptionLimitExceed extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Subscription limit exceed";

  public SubscriptionLimitExceed() {
    super(DEFAULT_MESSAGE);
  }

  public SubscriptionLimitExceed(final @NotNull String message) {
    super(message);
  }
}
