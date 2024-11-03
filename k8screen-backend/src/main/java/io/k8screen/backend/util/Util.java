package io.k8screen.backend.util;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.OffsetDateTime;

public class Util {

  public static String formatDate(final @NotNull OffsetDateTime creationTimestamp) {
    Duration duration = Duration.between(creationTimestamp, OffsetDateTime.now());

    long days = duration.toDays();
    long hours = duration.toHours() % 24;
    long minutes = duration.toMinutes() % 60;
    long seconds = duration.getSeconds() % 60;

    StringBuilder ageBuilder = new StringBuilder();

    if (days > 0) {
      ageBuilder.append(days).append("d");
    }
    if (hours > 0 || ageBuilder.length() > 0) { // Eğer gün varsa, saat göster
      ageBuilder.append(hours).append("h");
    }
    if (days <= 0 && (minutes > 0 || ageBuilder.length() > 0)) { // Eğer saat veya gün varsa, dakika göster
      ageBuilder.append(minutes).append("m");
    }
    if (minutes <= 0 && (seconds > 0 || ageBuilder.length() == 0)) { // Eğer hiçbir şey yoksa, saniye göster
      ageBuilder.append(seconds).append("s");
    }

    return ageBuilder.toString();
  }
}
