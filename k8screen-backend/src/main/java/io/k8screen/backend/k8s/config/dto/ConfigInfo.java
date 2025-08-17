package io.k8screen.backend.k8s.config.dto;

import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record ConfigInfo(@NotNull String name) {}
