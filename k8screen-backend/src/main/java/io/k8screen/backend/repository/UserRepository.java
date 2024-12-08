package io.k8screen.backend.repository;

import io.k8screen.backend.data.entity.User;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
  @NotNull
  Optional<User> findByUsername(@NotNull String username);
}
