package io.k8screen.backend.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import org.jetbrains.annotations.NotNull;

public class Util {

  public static String formatDate(final @NotNull OffsetDateTime creationTimestamp) {
    final Duration duration = Duration.between(creationTimestamp, OffsetDateTime.now());

    final long days = duration.toDays();
    final long hours = duration.toHours() % 24;
    final long minutes = duration.toMinutes() % 60;
    final long seconds = duration.getSeconds() % 60;

    final StringBuilder ageBuilder = new StringBuilder();

    if (days > 0) {
      ageBuilder.append(days).append("d");
    }
    if (hours > 0 || !ageBuilder.isEmpty()) {
      ageBuilder.append(hours).append("h");
    }
    if (days <= 0 && (minutes > 0 || !ageBuilder.isEmpty())) {
      ageBuilder.append(minutes).append("m");
    }
    if (minutes <= 0 && (seconds > 0 || ageBuilder.isEmpty())) {
      ageBuilder.append(seconds).append("s");
    }

    return ageBuilder.toString();
  }
}
