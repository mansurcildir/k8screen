package io.k8screen.backend.data.dto;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record SecretDTO (
  @NotNull String name,
  @NotNull String type,
  @NotNull String data,
  @NotNull String age) {
}
