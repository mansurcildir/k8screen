package io.k8screen.backend.data.dto;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record PodDTO (
  @NotNull String name,
  @NotNull String ready,
  @NotNull String status,
  @NotNull String restarts,
  @NotNull String age) {
}
