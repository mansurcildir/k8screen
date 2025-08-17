package io.k8screen.backend.k8s.secret.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record SecretInfo(
    @NotNull String name,
    @NotNull String type,
    @JsonProperty("data_size") int dataSize,
    @NotNull String age) {}
