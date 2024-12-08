package io.k8screen.backend.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record SecretDTO(
    @NotNull String name,
    @NotNull String type,
    @JsonProperty("data_size") int dataSize,
    @NotNull String age) {}
