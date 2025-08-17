package io.k8screen.backend.user.dto;

import org.jetbrains.annotations.NotNull;

public record GithubEmail(@NotNull String email, boolean primary, boolean verified) {}
