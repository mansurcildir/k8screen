package io.k8screen.backend.subscription;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

  @NotNull
  Optional<SubscriptionPlan> findByName(@NotNull String name);
}
