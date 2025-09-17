package io.k8screen.backend.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.jetbrains.annotations.NotNull;

public class FormatUtil {

  public static @NotNull String formatAge(final @NotNull OffsetDateTime creationTimestamp) {
    long totalSeconds =
        Duration.between(creationTimestamp, OffsetDateTime.now(ZoneOffset.UTC)).toSeconds();

    final long[][] units = {
      {365L * 24 * 3600, 'y'}, // years
      {24 * 3600, 'd'}, // days
      {3600, 'h'}, // hours
      {60, 'm'} // minutes
    };

    final StringBuilder sb = new StringBuilder();
    for (final long[] unit : units) {
      final long value = totalSeconds / unit[0];
      if (value > 0) {
        sb.append(value).append((char) unit[1]);
        totalSeconds %= unit[0];
      }
    }

    if (sb.isEmpty()) {
      sb.append(totalSeconds).append("s");
    }
    return sb.toString();
  }
}
