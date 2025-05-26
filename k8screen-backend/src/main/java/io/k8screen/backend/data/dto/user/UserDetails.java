package io.k8screen.backend.data.dto.user;

import java.util.UUID;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;

@Builder
public record UserDetails(@NotNull UUID userUuid, @NotNull String username) {}
