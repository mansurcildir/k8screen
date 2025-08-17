package io.k8screen.backend.subscription.dto;

import org.jetbrains.annotations.NotNull;

public record SubscriptionPlanInfo(@NotNull String name, long maxConfigCount) {}
