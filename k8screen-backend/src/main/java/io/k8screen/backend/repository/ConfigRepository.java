package io.k8screen.backend.repository;

import io.k8screen.backend.data.entity.Config;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConfigRepository extends JpaRepository<Config, String> {

  @NotNull
  @Query("select c from Config c where c.name = :name and c.user.id = :userId")
  Optional<Config> findByNameAndUserId(@NotNull String name, @NotNull String userId);

  @NotNull
  @Query("select c from Config c where c.user.id = :userId")
  List<Config> findAllByUserId(@NotNull String userId);

  @NotNull
  Optional<Void> deleteByName(@NotNull String name);
}
