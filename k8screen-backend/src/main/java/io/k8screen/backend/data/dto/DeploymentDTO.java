package io.k8screen.backend.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record DeploymentDTO (
  @NotNull String name,
  @NotNull String ready,
  @NotNull @JsonProperty("up_to_date") String upToDate,
  @NotNull String available,
  @NotNull String age) {
}
