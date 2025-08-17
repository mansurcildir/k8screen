package io.k8screen.backend.user;

import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.k8s.config.ConfigRepository;
import io.k8screen.backend.k8s.config.dto.UserConfig;
import io.k8screen.backend.user.dto.UserInfo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
  private final @NotNull UserRepository userRepository;
  private final @NotNull ConfigRepository configRepository;
  private final @NotNull UserConverter userConverter;

  @Cacheable(value = "users", key = "#userUuid")
  public @NotNull UserInfo getUserInfo(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    return this.userConverter.toUserInfo(user);
  }

  @CacheEvict(value = "users", key = "#userUuid")
  public void changeActiveConfig(
      final @NotNull UserConfig userConfig, final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuid(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final boolean existsConfig = this.configRepository.existsByName(userConfig.getConfig());

    if (!existsConfig) {
      throw new ItemNotFoundException("configNotFound");
    }

    user.setActiveConfig(userConfig.getConfig());
    this.userRepository.save(user);
  }
}
