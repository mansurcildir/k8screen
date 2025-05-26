package io.k8screen.backend.repository;

import io.k8screen.backend.data.entity.Config;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConfigRepository extends JpaRepository<Config, Long> {

  @NotNull
  @Query(
      "select c from Config c where c.name = :name and c.user.uuid = :userUuid and c.deleted = false")
  Optional<Config> findByNameAndUserUuidAndDeletedFalse(
      @NotNull String name, @NotNull UUID userUuid);

  @NotNull
  @Query("select c from Config c where c.user.id = :userId and c.deleted = false")
  List<Config> findAllByUserIdAndDeletedFalse(long userId);
}
