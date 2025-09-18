package io.k8screen.backend.user;

import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  @NotNull
  Optional<User> findByUsername(@NotNull String username);

  @NotNull
  Optional<User> findByUuid(@NotNull UUID uuid);

  @NotNull
  Optional<User> findByStripeCustomerId(@NotNull UUID stripeCustomerId);

  boolean existsByUsername(@NotNull String username);

  boolean existsByEmail(@NotNull String email);
}
