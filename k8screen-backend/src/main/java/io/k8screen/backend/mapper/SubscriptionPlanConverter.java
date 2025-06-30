package io.k8screen.backend.mapper;

import io.k8screen.backend.data.dto.subscription.SubscriptionPlanInfo;
import io.k8screen.backend.data.entity.SubscriptionPlan;
import org.jetbrains.annotations.NotNull;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanConverter {
  @NotNull
  SubscriptionPlanInfo toSubscriptionPlanInfo(@NotNull SubscriptionPlan subscriptionPlan);
}
