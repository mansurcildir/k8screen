package io.k8screen.backend.subscription;

import io.k8screen.backend.subscription.dto.SubscriptionPlanInfo;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanConverter {
  @NotNull
  SubscriptionPlanInfo toSubscriptionPlanInfo(@NotNull SubscriptionPlan subscriptionPlan);
}
