package io.k8screen.backend.data.dto.subscription;

import org.jetbrains.annotations.NotNull;

public record SubscriptionPlanInfo(@NotNull String name, long maxConfigCount) {}
