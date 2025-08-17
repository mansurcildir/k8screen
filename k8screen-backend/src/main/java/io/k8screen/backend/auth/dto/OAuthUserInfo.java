package io.k8screen.backend.auth.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record OAuthUserInfo(
    @NotNull String sub,
    @Nullable String username,
    @Nullable String email,
    @Nullable String avatarUrl) {}
