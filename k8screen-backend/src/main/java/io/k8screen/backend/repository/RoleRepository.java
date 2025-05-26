package io.k8screen.backend.repository;

import io.k8screen.backend.data.entity.Role;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
  @NotNull
  Optional<Role> findByName(@NotNull String name);
}
