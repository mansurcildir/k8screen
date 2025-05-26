package io.k8screen.backend.service;

import io.k8screen.backend.data.dto.config.ConfigInfo;
import io.k8screen.backend.data.entity.Config;
import io.k8screen.backend.data.entity.User;
import io.k8screen.backend.exception.ItemNotFoundException;
import io.k8screen.backend.mapper.ConfigConverter;
import io.k8screen.backend.repository.ConfigRepository;
import io.k8screen.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ConfigService {

  private final @NotNull UserRepository userRepository;
  private final @NotNull ConfigRepository configRepository;
  private final @NotNull ConfigConverter configConverter;
  private final @NotNull FileSystemService fileSystemService;

  public @NotNull List<ConfigInfo> findAllConfigs(final @NotNull UUID userUuid) {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final List<Config> configs = this.configRepository.findAllByUserIdAndDeletedFalse(user.getId());
    return configs.stream().map(this.configConverter::toConfigItem).toList();
  }

  public @NotNull ConfigInfo findConfigByName(
      final @NotNull String name, final @NotNull UUID userUuid) {

    final Config config =
        this.configRepository
            .findByNameAndUserUuidAndDeletedFalse(name, userUuid)
            .orElseThrow(() -> new ItemNotFoundException("configNotFound"));

    return ConfigInfo.builder().name(config.getName()).build();
  }

  public void checkConfig(final @NotNull MultipartFile file, final @NotNull UUID userUuid)
      throws IOException {
    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    final Optional<Config> config =
        this.configRepository.findByNameAndUserUuidAndDeletedFalse(
            Objects.requireNonNull(file.getOriginalFilename()), userUuid);

    if (config.isEmpty()) {
      this.createConfig(file, user);
    }
  }

  public void createConfig(final @NotNull MultipartFile file, final @NotNull User user)
      throws IOException {

    this.fileSystemService.uploadConfig(file, user.getUuid());

    final Config config = Config.builder().name(file.getOriginalFilename()).build();
    config.setUuid(UUID.randomUUID());
    config.setUser(user);
    this.configRepository.save(config);
  }

  public void deleteConfigByName(final @NotNull String name, final @NotNull UUID userUuid)
      throws IOException {
    this.fileSystemService.deleteConfig(name, userUuid);

    final User user =
        this.userRepository
            .findByUuidAndDeletedFalse(userUuid)
            .orElseThrow(() -> new ItemNotFoundException("userNotFound"));

    user.setActiveConfig(null);
    this.userRepository.save(user);

    final Config config =
        this.configRepository
            .findByNameAndUserUuidAndDeletedFalse(name, userUuid)
            .orElseThrow(() -> new ItemNotFoundException("configNotFound"));

    config.setDeleted(true);
    this.configRepository.save(config);
  }
}
