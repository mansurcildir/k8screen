package io.k8screen.backend.data.dto.config;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record ConfigInfo(@NotNull String name) {}
