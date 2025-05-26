package io.k8screen.backend.repository;

import io.k8screen.backend.data.entity.RefreshToken;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  @NotNull
  Optional<RefreshToken> findByToken(@NotNull String token);

  void deleteAllByUserUuid(@NotNull UUID userUuid);
}
