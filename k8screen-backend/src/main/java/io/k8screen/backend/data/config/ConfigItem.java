package io.k8screen.backend.data.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record ConfigItem(@NotNull @NotEmpty String id, @NotNull @NotEmpty String name) {}
