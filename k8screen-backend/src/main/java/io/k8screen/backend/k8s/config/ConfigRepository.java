package io.k8screen.backend.k8s.config;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ConfigRepository extends JpaRepository<Config, Long> {

  @NotNull
  @Query("select c from Config c where c.name = :name and c.user.uuid = :userUuid")
  Optional<Config> findByNameAndUserUuid(@NotNull String name, @NotNull UUID userUuid);

  @NotNull
  @Query("select c from Config c where c.user.uuid = :userUuid")
  List<Config> findAllByUserUuid(@NotNull UUID userUuid);

  @NotNull
  @Query(
      """
              select c.id from Config c
              where c.user.id = :userId
              order by c.createdAt DESC""")
  List<Long> findIdsByUserIdOrderByCreatedAtDesc(@NotNull Pageable pageable, long userId);

  @Modifying
  @Query(
      """
              update Config c set
              c.deleted = true
              where c.id in :ids
              and c.user.id= :userId""")
  void deleteAllByIdsAndUserId(@NotNull List<Long> ids, long userId);

  long countConfigByUserUuid(@NotNull UUID userUuid);

  void deleteByNameAndUserUuid(@NotNull String name, @NotNull UUID uuid);

  boolean existsByName(@NotNull String name);
}
