package io.k8screen.backend.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import org.jetbrains.annotations.NotNull;

public class Util {

  public static String formatAge(final @NotNull OffsetDateTime creationTimestamp) {
    Duration duration = Duration.between(creationTimestamp, OffsetDateTime.now());

    long days = duration.toDays();
    long hours = duration.toHours() % 24;
    long minutes = duration.toMinutes() % 60;
    long seconds = duration.getSeconds() % 60;

    long years = days / 365;
    days = days % 365;

    StringBuilder age = new StringBuilder();

    if (years > 0) {
      age.append(years).append("y");
      if (days > 0) {
        age.append(days).append("d");
      }
    } else if (days > 0) {
      age.append(days).append("d");
      if (hours > 0) {
        age.append(hours).append("h");
      }
    } else if (hours > 0) {
      age.append(hours).append("h");
      if (minutes > 0) {
        age.append(minutes).append("m");
      }
    } else if (minutes > 0) {
      age.append(minutes).append("m");
      if (seconds > 0) {
        age.append(seconds).append("s");
      }
    } else {
      age.append(seconds).append("s");
    }

    return age.toString().trim();
  }
}
